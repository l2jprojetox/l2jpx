package com.l2jpx.gameserver.network.clientpackets;

import java.util.StringTokenizer;
import java.util.logging.Logger;

import com.l2jpx.commons.lang.StringUtil;

import l2jbrasil.AutoFarm.AutofarmPlayerRoutine;

import com.l2jpx.Config;
import com.l2jpx.gameserver.communitybbs.CommunityBoard;
import com.l2jpx.gameserver.data.manager.HeroManager;
import com.l2jpx.gameserver.data.xml.AdminData;
import com.l2jpx.gameserver.enums.FloodProtector;
import com.l2jpx.gameserver.handler.AdminCommandHandler;
import com.l2jpx.gameserver.handler.IAdminCommandHandler;
import com.l2jpx.gameserver.handler.IVoicedCommandHandler;
import com.l2jpx.gameserver.handler.VoicedCommandHandler;
import com.l2jpx.gameserver.model.World;
import com.l2jpx.gameserver.model.WorldObject;
import com.l2jpx.gameserver.model.actor.Npc;
import com.l2jpx.gameserver.model.actor.Player;
import com.l2jpx.gameserver.model.actor.instance.OlympiadManagerNpc;
import com.l2jpx.gameserver.model.olympiad.OlympiadManager;
import com.l2jpx.gameserver.network.SystemMessageId;
import com.l2jpx.gameserver.network.serverpackets.ActionFailed;
import com.l2jpx.gameserver.network.serverpackets.ExShowScreenMessage;
import com.l2jpx.gameserver.network.serverpackets.ExShowScreenMessage.SMPOS;
import com.l2jpx.gameserver.network.serverpackets.NpcHtmlMessage;
import com.l2jpx.gameserver.network.serverpackets.SystemMessage;
import com.l2jpx.gameserver.scripting.QuestState;

public final class RequestBypassToServer extends L2GameClientPacket
{
	private static final Logger GMAUDIT_LOG = Logger.getLogger("gmaudit");
	
	private String _command;
	
	@Override
	protected void readImpl()
	{
		_command = readS();
	}
	
	@Override
	protected void runImpl()
	{
		if (_command.isEmpty())
			return;
		
		if (!getClient().performAction(FloodProtector.SERVER_BYPASS))
			return;
		
		final Player player = getClient().getPlayer();
		if (player == null)
			return;
		
		final AutofarmPlayerRoutine bot = player.getBot();
		if (_command.startsWith("admin_"))
		{
			String command = _command.split(" ")[0];
			
			final IAdminCommandHandler ach = AdminCommandHandler.getInstance().getHandler(command);
			if (ach == null)
			{
				if (player.isGM())
					player.sendMessage("The command " + command.substring(6) + " doesn't exist.");
				
				LOGGER.warn("No handler registered for admin command '{}'.", command);
				return;
			}
			
			if (!AdminData.getInstance().hasAccess(command, player.getAccessLevel()))
			{
				player.sendMessage("You don't have the access rights to use this command.");
				LOGGER.warn("{} tried to use admin command '{}' without proper Access Level.", player.getName(), command);
				return;
			}
			
			if (Config.GMAUDIT)
				GMAUDIT_LOG.info(player.getName() + " [" + player.getObjectId() + "] used '" + _command + "' command on: " + ((player.getTarget() != null) ? player.getTarget().getName() : "none"));
			
			ach.useAdminCommand(_command, player);
		}
		else if (_command.startsWith("player_help "))
		{
			final String path = _command.substring(12);
			if (path.indexOf("..") != -1)
				return;
			
			final StringTokenizer st = new StringTokenizer(path);
			final String[] cmd = st.nextToken().split("#");
			
			final NpcHtmlMessage html = new NpcHtmlMessage(0);
			html.setFile("data/html/help/" + cmd[0]);
			if (cmd.length > 1)
			{
				final int itemId = Integer.parseInt(cmd[1]);
				html.setItemId(itemId);
				
				if (itemId == 7064 && cmd[0].equalsIgnoreCase("lidias_diary/7064-16.htm"))
				{
					final QuestState qs = player.getQuestList().getQuestState("Q023_LidiasHeart");
					if (qs != null && qs.getCond() == 5 && qs.getInteger("diary") == 0)
						qs.set("diary", "1");
				}
			}
			html.disableValidation();
			player.sendPacket(html);
		}
	        else if (_command.startsWith("_infosettings"))
	        {
	            showAutoFarm(player);
	        }

	        
	        
	        else if (_command.startsWith("_autofarm"))
	        {
	            if (player.isAutoFarm())
	            {
	                bot.stop();
	                player.setAutoFarm(false);
	            }
	            else
	            {
	                bot.start();
	                player.setAutoFarm(true);
	            }
	            
	        }
	        
	        if (_command.startsWith("_pageAutoFarm"))
	        {
	            StringTokenizer st = new StringTokenizer(_command, " ");
	            st.nextToken();
	            try
	            {
	                String param = st.nextToken();
	                
	                if (param.startsWith("inc_page") || param.startsWith("dec_page"))
	                {
	                    int newPage;
	                    
	                    if (param.startsWith("inc_page"))
	                    {
	                        newPage = player.getPage() + 1;
	                    }
	                    else
	                    {
	                        newPage = player.getPage() - 1;
	                    }
	                    
	                    if (newPage >= 0 && newPage <= 9)
	                    {
	                        String[] pageStrings =
	                        {
	                            "F1",
	                            "F2",
	                            "F3",
	                            "F4",
	                            "F5",
	                            "F6",
	                            "F7",
	                            "F8",
	                            "F9",
	                            "F10"
	                        };
	                        
	                        player.setPage(newPage);
	                        player.sendPacket(new ExShowScreenMessage("Auto Farm Skill Bar " + pageStrings[newPage], 3 * 1000, SMPOS.TOP_CENTER, false));
	                        player.saveAutoFarmSettings();
	                        
	                    }
	                    
	                }
	                
	            }
	            catch (Exception e)
	            {
	                e.printStackTrace();
	            }
	        }
	        
	        if (_command.startsWith("_enableBuffProtect"))
	        {
	            player.setNoBuffProtection(!player.isNoBuffProtected());
	            if (player.isNoBuffProtected())
	            {
	                player.sendPacket(new ExShowScreenMessage("Auto Farm Buff Protect On", 3 * 1000, SMPOS.TOP_CENTER, false));
	            }
	            else
	            {
	                player.sendPacket(new ExShowScreenMessage("Auto Farm Buff Protect Off", 3 * 1000, SMPOS.TOP_CENTER, false));
	            }
	            player.saveAutoFarmSettings();
	        }
	        if (_command.startsWith("_enableSummonAttack"))
	        {
	            player.setSummonAttack(!player.isSummonAttack());
	            if (player.isSummonAttack())
	            {
	                player.sendPacket(new SystemMessage(SystemMessageId.ACTIVATE_SUMMON_ACTACK));
	                player.sendPacket(new ExShowScreenMessage("Auto Farm Summon Attack On", 3 * 1000, SMPOS.TOP_CENTER, false));
	            }
	            else
	            {
	                player.sendPacket(new SystemMessage(SystemMessageId.DESACTIVATE_SUMMON_ACTACK));
	                player.sendPacket(new ExShowScreenMessage("Auto Farm Summon Attack Off", 3 * 1000, SMPOS.TOP_CENTER, false));
	            }
	            player.saveAutoFarmSettings();
	        }
	        
	        
	          
			
					if (_command.startsWith("_enableRespectHunt"))
			{
				
				player.setAntiKsProtection(!player.isAntiKsProtected());
				if (player.isAntiKsProtected())
				{
					player.sendPacket(new SystemMessage(SystemMessageId.ACTIVATE_RESPECT_HUNT));
					player.sendPacket(new ExShowScreenMessage("Respct Hunt On", 3 * 1000, SMPOS.TOP_CENTER, false));
				}
				else
				{
					player.sendPacket(new SystemMessage(SystemMessageId.DESACTIVATE_RESPECT_HUNT));
					player.sendPacket(new ExShowScreenMessage("Respct Hunt Off", 3 * 1000, SMPOS.TOP_CENTER, false));
				}
				player.saveAutoFarmSettings();
				
			}
			
			
			if (_command.startsWith("_radiusAutoFarm"))
			{
				StringTokenizer st = new StringTokenizer(_command, " ");
				st.nextToken();
				try
				{
					String param = st.nextToken();
					
					if (param.startsWith("inc_radius"))
					{
						player.setRadius(player.getRadius() + 200);
						player.sendPacket(new ExShowScreenMessage("Auto Farm Range: " + player.getRadius(), 3 * 1000, SMPOS.TOP_CENTER, false));
						
					}
					else if (param.startsWith("dec_radius"))
					{
						player.setRadius(player.getRadius() - 200);
						player.sendPacket(new ExShowScreenMessage("Auto Farm Range: " + player.getRadius(), 3 * 1000, SMPOS.TOP_CENTER, false));
						
					}
					player.saveAutoFarmSettings();
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}
			
			
		}else if (_command.startsWith("voiced_"))
		{
			String command = _command.split(" ")[0];

			IVoicedCommandHandler ach = VoicedCommandHandler.getInstance().getHandler(_command.substring(7));

			if (ach == null)
			{
				player.sendMessage("The command " + command.substring(7) + " does not exist!");
				LOGGER.warn("No handler registered for command '" + _command + "'");
				return;
			}

			ach.useVoicedCommand(_command.substring(7), player, null);
		}

		else if (_command.startsWith("npc_"))
		{
			if (!player.validateBypass(_command))
				return;
			
			int endOfId = _command.indexOf('_', 5);
			String id;
			if (endOfId > 0)
				id = _command.substring(4, endOfId);
			else
				id = _command.substring(4);
			
			try
			{
				final WorldObject object = World.getInstance().getObject(Integer.parseInt(id));
				
				if (object instanceof Npc && endOfId > 0 && player.getAI().canDoInteract(object))
					((Npc) object).onBypassFeedback(player, _command.substring(endOfId + 1));
				
				player.sendPacket(ActionFailed.STATIC_PACKET);
			}
			catch (NumberFormatException nfe)
			{
			}
		}
		// Navigate throught Manor windows
		else if (_command.startsWith("manor_menu_select?"))
		{
			WorldObject object = player.getTarget();
			if (object instanceof Npc)
				((Npc) object).onBypassFeedback(player, _command);
		}
		else if (_command.startsWith("bbs_") || _command.startsWith("_bbs") || _command.startsWith("_friend") || _command.startsWith("_mail") || _command.startsWith("_block"))
		{
			CommunityBoard.getInstance().handleCommands(getClient(), _command);
		}
		else if (_command.startsWith("Quest "))
		{
			if (!player.validateBypass(_command))
				return;
			
			String[] str = _command.substring(6).trim().split(" ", 2);
			if (str.length == 1)
				player.getQuestList().processQuestEvent(str[0], "");
			else
				player.getQuestList().processQuestEvent(str[0], str[1]);
		}
		else if (_command.startsWith("_match"))
		{
			String params = _command.substring(_command.indexOf("?") + 1);
			StringTokenizer st = new StringTokenizer(params, "&");
			int heroclass = Integer.parseInt(st.nextToken().split("=")[1]);
			int heropage = Integer.parseInt(st.nextToken().split("=")[1]);
			int heroid = HeroManager.getInstance().getHeroByClass(heroclass);
			if (heroid > 0)
				HeroManager.getInstance().showHeroFights(player, heroclass, heroid, heropage);
		}
		else if (_command.startsWith("_diary"))
		{
			String params = _command.substring(_command.indexOf("?") + 1);
			StringTokenizer st = new StringTokenizer(params, "&");
			int heroclass = Integer.parseInt(st.nextToken().split("=")[1]);
			int heropage = Integer.parseInt(st.nextToken().split("=")[1]);
			int heroid = HeroManager.getInstance().getHeroByClass(heroclass);
			if (heroid > 0)
				HeroManager.getInstance().showHeroDiary(player, heroclass, heroid, heropage);
		}
		else if (_command.startsWith("arenachange")) // change
		{
			final boolean isManager = player.getCurrentFolk() instanceof OlympiadManagerNpc;
			if (!isManager)
			{
				// Without npc, command can be used only in observer mode on arena
				if (!player.isInObserverMode() || player.isInOlympiadMode() || player.getOlympiadGameId() < 0)
					return;
			}
			
			// Olympiad registration check.
			if (OlympiadManager.getInstance().isRegisteredInComp(player))
			{
				player.sendPacket(SystemMessageId.WHILE_YOU_ARE_ON_THE_WAITING_LIST_YOU_ARE_NOT_ALLOWED_TO_WATCH_THE_GAME);
				return;
			}
			
			final int arenaId = Integer.parseInt(_command.substring(12).trim());
			player.enterOlympiadObserverMode(arenaId);
		}
	}
	private static final String ACTIVED = "<font color=00FF00>STARTED</font>";
	private static final String DESATIVED = "<font color=FF0000>STOPPED</font>";
	private static final String STOP = "STOP";
	private static final String START = "START";
	
	public static void showAutoFarm(Player activeChar)
	{
	    NpcHtmlMessage html = new NpcHtmlMessage(0);
	    html.setFile("data/html/mods/menu/AutoFarm.htm");
	    html.replace("%player%", activeChar.getName());
	    html.replace("%page%", StringUtil.formatNumber(activeChar.getPage() + 1));
	    html.replace("%heal%", StringUtil.formatNumber(activeChar.getHealPercent()));
	    html.replace("%radius%", StringUtil.formatNumber(activeChar.getRadius()));
	    html.replace("%summonSkill%", StringUtil.formatNumber(activeChar.getSummonSkillPercent()));
	    html.replace("%hpPotion%", StringUtil.formatNumber(activeChar.getHpPotionPercentage()));
	    html.replace("%mpPotion%", StringUtil.formatNumber(activeChar.getMpPotionPercentage()));
	    html.replace("%noBuff%", activeChar.isNoBuffProtected() ? "back=L2UI.CheckBox_checked fore=L2UI.CheckBox_checked" : "back=L2UI.CheckBox fore=L2UI.CheckBox");
	    html.replace("%summonAtk%", activeChar.isSummonAttack() ? "back=L2UI.CheckBox_checked fore=L2UI.CheckBox_checked" : "back=L2UI.CheckBox fore=L2UI.CheckBox");
	    html.replace("%antiKs%", activeChar.isAntiKsProtected() ? "back=L2UI.CheckBox_checked fore=L2UI.CheckBox_checked" : "back=L2UI.CheckBox fore=L2UI.CheckBox");
	    html.replace("%autofarm%", activeChar.isAutoFarm() ? ACTIVED : DESATIVED);
	    html.replace("%button%", activeChar.isAutoFarm() ? STOP : START);
	    activeChar.sendPacket(html);
	}
}