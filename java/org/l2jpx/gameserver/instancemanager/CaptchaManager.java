/*
 * This file is part of the L2J Mobius project.
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.l2jpx.gameserver.instancemanager;

import java.awt.image.BufferedImage;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

import org.l2jpx.Config;
import org.l2jpx.commons.threads.ThreadPool;
import org.l2jpx.commons.util.Rnd;
import org.l2jpx.commons.util.StringUtil;
import org.l2jpx.gameserver.data.xml.AdminData;
import org.l2jpx.gameserver.enums.ChatType;
import org.l2jpx.gameserver.enums.QuestSound;
import org.l2jpx.gameserver.enums.TeleportWhereType;
import org.l2jpx.gameserver.model.actor.Creature;
import org.l2jpx.gameserver.model.actor.Player;
import org.l2jpx.gameserver.model.actor.instance.Monster;
import org.l2jpx.gameserver.model.captcha.Captcha;
import org.l2jpx.gameserver.model.captcha.ImageData;
import org.l2jpx.gameserver.model.events.Containers;
import org.l2jpx.gameserver.model.events.EventType;
import org.l2jpx.gameserver.model.events.impl.creature.OnCreatureKilled;
import org.l2jpx.gameserver.model.events.listeners.ConsumerEventListener;
import org.l2jpx.gameserver.model.punishment.PunishmentAffect;
import org.l2jpx.gameserver.model.punishment.PunishmentTask;
import org.l2jpx.gameserver.model.punishment.PunishmentType;
import org.l2jpx.gameserver.network.Disconnection;
import org.l2jpx.gameserver.network.PacketLogger;
import org.l2jpx.gameserver.network.serverpackets.CreatureSay;
import org.l2jpx.gameserver.network.serverpackets.LeaveWorld;
import org.l2jpx.gameserver.network.serverpackets.NpcHtmlMessage;
import org.l2jpx.gameserver.network.serverpackets.PledgeCrest;
import org.l2jpx.gameserver.util.DDSConverter;

/**
 * @author Skache
 */
public class CaptchaManager
{
	private static final Map<Integer, Integer> JAIL_COUNT = new ConcurrentHashMap<>();
	private static final Map<Integer, Integer> RETRIES = new ConcurrentHashMap<>();
	private static final Map<Integer, Long> LAST_JAIL_TIME = new ConcurrentHashMap<>();
	private static final int MAX_JAIL_TIME = 24 * 60; // Maximum (double) jail time in minutes (24 hours).
	
	protected static Map<Integer, ImageData> IMAGES;
	protected static Map<Integer, Integer> MONSTER_COUNTER = new ConcurrentHashMap<>();
	protected static Map<Integer, Long> LAST_KILL_TIME = new ConcurrentHashMap<>();
	protected static Map<Integer, Future<?>> BEGIN_VALIDATION = new ConcurrentHashMap<>();
	protected static Map<Integer, PlayerData> VALIDATION = new ConcurrentHashMap<>();
	protected static int WINDOW_DELAY = 3; // Delay used to generate new window if previous have been closed.
	private static int USED_ID = 0;
	
	private final Consumer<OnCreatureKilled> _onCreatureKilled = event -> updateCounter(event.getAttacker(), event.getTarget());
	
	public class PlayerData
	{
		public PlayerData()
		{
			firstWindow = true;
		}
		
		public boolean firstWindow;
		public BufferedImage image;
		public String captchaText = "";
		public int captchaId = 0;
	}
	
	protected CaptchaManager()
	{
		if (Config.ENABLE_CAPTCHA)
		{
			IMAGES = Captcha.getInstance().createImageList();
			Containers.Players().addListener(new ConsumerEventListener(Containers.Players(), EventType.ON_CREATURE_KILLED, _onCreatureKilled, this));
		}
	}
	
	public void updateCounter(Creature player, Creature monster)
	{
		if (!Config.ENABLE_CAPTCHA)
		{
			return;
		}
		
		if (!(player instanceof Player) || !(monster instanceof Monster))
		{
			return;
		}
		
		// Check if auto-play is enabled and player is auto-playing.
		final Player killer = player.getActingPlayer();
		if (Config.ENABLE_AUTO_PLAY && killer.isAutoPlaying())
		{
			return; // Don't count kills when auto-play is enabled.
		}
		
		if (VALIDATION.get(killer.getObjectId()) != null)
		{
			return;
		}
		
		if (Config.KILL_COUNTER_RESET)
		{
			final long currentTime = System.currentTimeMillis();
			final long previousKillTime = LAST_KILL_TIME.getOrDefault(killer.getObjectId(), currentTime);
			if ((currentTime - previousKillTime) > Config.KILL_COUNTER_RESET_TIME)
			{
				MONSTER_COUNTER.put(killer.getObjectId(), 0);
			}
			LAST_KILL_TIME.put(killer.getObjectId(), currentTime);
		}
		
		int count = 1;
		if (MONSTER_COUNTER.get(killer.getObjectId()) != null)
		{
			count = MONSTER_COUNTER.get(killer.getObjectId()) + 1;
		}
		
		final int next = Rnd.get(Config.KILL_COUNTER_RANDOMIZATION);
		if ((Config.KILL_COUNTER + next) < count)
		{
			validationTasks(killer);
			MONSTER_COUNTER.remove(killer.getObjectId());
		}
		else
		{
			MONSTER_COUNTER.put(killer.getObjectId(), count);
		}
	}
	
	public void preValidationWindow(Player player)
	{
		RETRIES.put(player.getObjectId(), 0);
		final NpcHtmlMessage html = new NpcHtmlMessage(1);
		final StringBuilder sb = new StringBuilder();
		StringUtil.append(sb, "<html>");
		StringUtil.append(sb, "<title>Bot Protection</title>");
		StringUtil.append(sb, "<body><center><br><br><img src=\"L2UI_CH3.herotower_deco\" width=\"256\" height=\"32\">");
		StringUtil.append(sb, "<br><br><font color=\"a2a0a2\">If such window appears it means server suspect,<br1>that you may using cheating software.</font>");
		StringUtil.append(sb, "<br><br><font color=\"b09979\">If given answer results are incorrect or no action is made server is going to punish character instantly.</font>");
		StringUtil.append(sb, "<br><br><button value=\"CONTINUE\" action=\"bypass report_continue\" width=\"100\" height=\"27\" back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\">");
		StringUtil.append(sb, "</center></body>");
		StringUtil.append(sb, "</html>");
		html.setHtml(sb.toString());
		player.sendPacket(html);
	}
	
	private static void validationWindow(Player player)
	{
		final PlayerData data = VALIDATION.get(player.getObjectId());
		final NpcHtmlMessage html = new NpcHtmlMessage(1);
		final StringBuilder sb = new StringBuilder();
		StringUtil.append(sb, "<html>");
		StringUtil.append(sb, "<title>Bot Protection</title>");
		StringUtil.append(sb, "<body><center><br><br><img src=\"L2UI_CH3.herotower_deco\" width=\"256\" height=\"32\">");
		StringUtil.append(sb, "<br><br><font color=\"a2a0a2\">in order to prove you are a human being<br1>you've to</font> <font color=\"b09979\">enter the code from picture:</font>");
		
		// Generated main pattern.
		StringUtil.append(sb, "<br><br><img src=\"Crest.pledge_crest_" + Config.SERVER_ID + "_" + data.captchaId + "\" width=256 height=64></td></tr>");
		StringUtil.append(sb, "<br>");
		StringUtil.append(sb, "<br><br><edit var=\"answer\" width=110>");
		StringUtil.append(sb, "<br><button value=\"Confirm\" action=\"bypass -h report_ $answer\" width=\"100\" height=\"27\" back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\"><br/>");
		StringUtil.append(sb, "</center></body>");
		StringUtil.append(sb, "</html>");
		
		html.setHtml(sb.toString());
		player.sendPacket(html);
		QuestSound soundToPlay = QuestSound.SKILLSOUND_HORROR_1;
		player.sendPacket(soundToPlay.getPacket());
	}
	
	public void punishmentWindow(Player player)
	{
		final NpcHtmlMessage html = new NpcHtmlMessage(1);
		final StringBuilder sb = new StringBuilder();
		StringUtil.append(sb, "<html>");
		StringUtil.append(sb, "<title>Bot Protection</title>");
		StringUtil.append(sb, "<body><center><br><br><img src=\"L2UI_CH3.herotower_deco\" width=\"256\" height=\"32\">");
		StringUtil.append(sb, "<br><br><font color=\"a2a0a2\">If such window appears, it means character haven't passed through prevention system.");
		StringUtil.append(sb, "<br><br><font color=\"b09979\">In such case character get moved to nearest town.</font>");
		StringUtil.append(sb, "</center></body>");
		StringUtil.append(sb, "</html>");
		html.setHtml(sb.toString());
		player.sendPacket(html);
	}
	
	public void validationTasks(Player player)
	{
		final PlayerData data = new PlayerData();
		Captcha.getInstance().generateCaptcha(data, player);
		data.image = IMAGES.get(USED_ID).image;
		data.captchaId = IMAGES.get(USED_ID).captchaID;
		data.captchaText = IMAGES.get(USED_ID).captchaText;
		final PledgeCrest packet = new PledgeCrest(data.captchaId, DDSConverter.convertToDDS(data.image).array());
		player.sendPacket(packet);
		USED_ID++;
		
		if (CaptchaManager.USED_ID == 998)
		{
			CaptchaManager.USED_ID = 0;
			ThreadPool.schedule(() -> IMAGES = Captcha.getInstance().createImageList(), 100);
		}
		VALIDATION.put(player.getObjectId(), data);
		
		final Future<?> newTask = ThreadPool.schedule(new ReportCheckTask(player), Config.VALIDATION_TIME * 1000);
		ThreadPool.schedule(new countdown(player, Config.VALIDATION_TIME), 0);
		BEGIN_VALIDATION.put(player.getObjectId(), newTask);
	}
	
	protected void banPunishment(Player player)
	{
		VALIDATION.remove(player.getObjectId());
		BEGIN_VALIDATION.get(player.getObjectId()).cancel(true);
		BEGIN_VALIDATION.remove(player.getObjectId());
		
		// 0 = move character to the closest village.
		// 1 = kick characters from the server.
		// 2 = put character to jail.
		// 3 = ban character from the server.
		switch (Config.PUNISHMENT)
		{
			case 0:
			{
				player.stopMove(null);
				player.teleToLocation(TeleportWhereType.TOWN);
				punishmentWindow(player);
				QuestSound soundForTeleport = QuestSound.ITEMSOUND_QUEST_GIVEUP;
				player.sendPacket(soundForTeleport.getPacket());
				AdminData.getInstance().broadcastToGMs(new CreatureSay(null, ChatType.ALLIANCE, "Punishment", player.getName() + " has been teleported."));
				PacketLogger.warning(player + "[TELEPORT] Wrong Captcha maybe boting?");
				break;
			}
			case 1:
			{
				if (player.isOnline())
				{
					Disconnection.of(null, player).defaultSequence(LeaveWorld.STATIC_PACKET);
					AdminData.getInstance().broadcastToGMs(new CreatureSay(null, ChatType.ALLIANCE, "Punishment", player.getName() + " has been kicked."));
					PacketLogger.warning(player + "[KICK] Wrong Captcha maybe boting?");
					return;
				}
				break;
			}
			case 2:
			{
				applyJailPunishment(player);
				QuestSound soundForJail = QuestSound.ETCSOUND_ELROKI_SONG_FULL;
				player.sendPacket(soundForJail.getPacket());
				AdminData.getInstance().broadcastToGMs(new CreatureSay(null, ChatType.ALLIANCE, "Punishment", player.getName() + " has been jailed."));
				PacketLogger.warning(player + "[JAIL] Wrong Captcha maybe boting?");
				break;
			}
			case 3:
			{
				changeAccessLevel(player);
				AdminData.getInstance().broadcastToGMs(new CreatureSay(null, ChatType.ALLIANCE, "Punishment", player.getName() + " has been banned."));
				PacketLogger.warning(player + "[BAN] Wrong Captcha maybe boting?");
				break;
			}
		}
		
		player.sendMessage("Unfortunately, code doesn't match.");
	}
	
	private int getPlayerJailCount(Player player)
	{
		return JAIL_COUNT.getOrDefault(player.getObjectId(), 0);
	}
	
	private void incrementPlayerJailCount(Player player)
	{
		JAIL_COUNT.put(player.getObjectId(), getPlayerJailCount(player) + 1);
	}
	
	private void applyJailPunishment(Player player)
	{
		if (player.isOnline())
		{
			final long currentTime = System.currentTimeMillis();
			int jailTime;
			
			if (Config.DOUBLE_JAIL_TIME)
			{
				final long lastJailTime = LAST_JAIL_TIME.getOrDefault(player.getObjectId(), 0L);
				final int previousJailCount = getPlayerJailCount(player);
				
				// Use multiplier if enabled.
				if ((currentTime - lastJailTime) >= TimeUnit.HOURS.toMillis(24))
				{
					jailTime = Config.JAIL_TIME; // Initial jail time from Config.
				}
				else
				{
					// Double the previous jail time, capped at maximum jail time.
					jailTime = Math.min((int) Math.pow(2, previousJailCount) * Config.JAIL_TIME, MAX_JAIL_TIME);
				}
				
				// Increment the jail count for the player.
				incrementPlayerJailCount(player);
				
				// Send a generic message to the player about the increased punishment next time with a delay.
				ThreadPool.schedule(() -> player.sendMessage("Warning: Your next jail time will be increased."), 2000);
			}
			else
			{
				// If multiplier is disabled, use the initial jail time from Config.
				jailTime = Config.JAIL_TIME;
			}
			
			// Calculate punishment time.
			final long punishmentTime;
			if (jailTime == 0)
			{
				punishmentTime = 0;
			}
			else
			{
				punishmentTime = currentTime + TimeUnit.MINUTES.toMillis(jailTime);
			}
			
			// Create a PunishmentTask instance.
			final PunishmentTask punishmentTask = new PunishmentTask(player.getObjectId(), PunishmentAffect.CHARACTER, PunishmentType.JAIL, punishmentTime, "Bot Protection", "System");
			PunishmentManager.getInstance().startPunishment(punishmentTask);
			
			// Update the last jail time for the player if applicable.
			if (jailTime != 0)
			{
				LAST_JAIL_TIME.put(player.getObjectId(), currentTime);
				if (Config.DOUBLE_JAIL_TIME)
				{
					LAST_JAIL_TIME.put(player.getObjectId(), currentTime);
				}
			}
			
			// Message to inform the player about the punishment.
			// player.sendMessage("You've been jailed for not completing bot protection");
		}
	}
	
	private static void changeAccessLevel(Player player)
	{
		if (player.isOnline())
		{
			final PunishmentTask punishmentTask = new PunishmentTask(player.getObjectId(), PunishmentAffect.CHARACTER, PunishmentType.BAN, 0, "Bot Protection", "System");
			PunishmentManager.getInstance().startPunishment(punishmentTask);
		}
	}
	
	public void analyseBypass(String command, Player player)
	{
		if (!VALIDATION.containsKey(player.getObjectId()))
		{
			return;
		}
		
		final String params = command.substring(command.indexOf("_") + 1);
		if (params.startsWith("continue"))
		{
			validationWindow(player);
			VALIDATION.get(player.getObjectId()).firstWindow = false;
			return;
		}
		
		final PlayerData data = VALIDATION.get(player.getObjectId());
		if (!params.trim().equalsIgnoreCase(data.captchaText))
		{
			int retries = RETRIES.getOrDefault(player.getObjectId(), 0) + 1;
			RETRIES.put(player.getObjectId(), retries);
			
			if (retries >= Config.CAPTCHA_ATTEMPTS)
			{
				banPunishment(player);
			}
			else
			{
				player.sendMessage("Incorrect code. You have " + (Config.CAPTCHA_ATTEMPTS - retries) + " more attempt(s) left.");
				validationWindow(player); // Provide another chance.
				// Play sound when doesn't match.
				player.sendPacket(QuestSound.ITEMSOUND_BROKEN_KEY.getPacket());
			}
		}
		else
		{
			player.sendMessage("Congratulations, code matches!");
			VALIDATION.remove(player.getObjectId());
			BEGIN_VALIDATION.get(player.getObjectId()).cancel(true);
			BEGIN_VALIDATION.remove(player.getObjectId());
			// Play sound when code matches.
			player.sendPacket(QuestSound.ITEMSOUND_SOW_SUCCESS.getPacket());
		}
	}
	
	protected class countdown implements Runnable
	{
		private final Player _player;
		private final int _time;
		
		public countdown(Player player, int time)
		{
			_time = time;
			_player = player;
		}
		
		@Override
		public void run()
		{
			if (_player.isOnline())
			{
				if (VALIDATION.containsKey(_player.getObjectId()) && VALIDATION.get(_player.getObjectId()).firstWindow)
				{
					if ((_time % WINDOW_DELAY) == 0)
					{
						preValidationWindow(_player);
					}
				}
				
				switch (_time)
				{
					case 300:
					case 240:
					case 180:
					case 120:
					case 60:
					{
						_player.sendMessage((_time / 60) + " minute(s) to enter the code.");
						break;
					}
					case 30:
					case 10:
					case 5:
					case 4:
					case 3:
					case 2:
					case 1:
					{
						_player.sendMessage(_time + " second(s) to enter the code!");
						break;
					}
				}
				if ((_time > 1) && VALIDATION.containsKey(_player.getObjectId()))
				{
					ThreadPool.schedule(new countdown(_player, _time - 1), 1000);
				}
			}
		}
	}
	
	public void captchaSuccessfull(Player player)
	{
		if (VALIDATION.get(player.getObjectId()) != null)
		{
			VALIDATION.remove(player.getObjectId());
		}
	}
	
	public boolean isAlredyInReportMode(Player player)
	{
		return VALIDATION.get(player.getObjectId()) != null;
	}
	
	private class ReportCheckTask implements Runnable
	{
		private final Player _player;
		
		public ReportCheckTask(Player player)
		{
			_player = player;
		}
		
		@Override
		public void run()
		{
			if (VALIDATION.get(_player.getObjectId()) != null)
			{
				banPunishment(_player);
			}
		}
	}
	
	public static final CaptchaManager getInstance()
	{
		return SingletonHolder.INSTANCE;
	}
	
	private static class SingletonHolder
	{
		protected static final CaptchaManager INSTANCE = new CaptchaManager();
	}
}
