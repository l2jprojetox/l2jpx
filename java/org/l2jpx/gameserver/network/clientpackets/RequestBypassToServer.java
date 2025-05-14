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
package org.l2jpx.gameserver.network.clientpackets;

import java.util.StringTokenizer;

import org.l2jpx.Config;
import org.l2jpx.commons.util.CommonUtil;
import org.l2jpx.gameserver.ai.CtrlIntention;
import org.l2jpx.gameserver.data.xml.MultisellData;
import org.l2jpx.gameserver.handler.AdminCommandHandler;
import org.l2jpx.gameserver.handler.BypassHandler;
import org.l2jpx.gameserver.handler.CommunityBoardHandler;
import org.l2jpx.gameserver.handler.IBypassHandler;
import org.l2jpx.gameserver.instancemanager.CaptchaManager;
import org.l2jpx.gameserver.model.World;
import org.l2jpx.gameserver.model.WorldObject;
import org.l2jpx.gameserver.model.actor.Creature;
import org.l2jpx.gameserver.model.actor.Npc;
import org.l2jpx.gameserver.model.actor.Player;
import org.l2jpx.gameserver.model.events.EventDispatcher;
import org.l2jpx.gameserver.model.events.EventType;
import org.l2jpx.gameserver.model.events.impl.creature.npc.OnNpcManorBypass;
import org.l2jpx.gameserver.model.events.impl.creature.npc.OnNpcMenuSelect;
import org.l2jpx.gameserver.model.events.impl.creature.player.OnPlayerBypass;
import org.l2jpx.gameserver.model.events.returns.TerminateReturn;
import org.l2jpx.gameserver.model.item.instance.Item;
import org.l2jpx.gameserver.model.olympiad.Hero;
import org.l2jpx.gameserver.network.Disconnection;
import org.l2jpx.gameserver.network.PacketLogger;
import org.l2jpx.gameserver.network.serverpackets.ActionFailed;
import org.l2jpx.gameserver.network.serverpackets.LeaveWorld;
import org.l2jpx.gameserver.network.serverpackets.NpcHtmlMessage;
import org.l2jpx.gameserver.util.Util;

/**
 * RequestBypassToServer client packet implementation.
 * @author HorridoJoho
 */
public class RequestBypassToServer extends ClientPacket
{
	// FIXME: This is for compatibility, will be changed when bypass functionality got an overhaul by NosBit
	private static final String[] _possibleNonHtmlCommands =
	{
		"_bbs",
		"bbs",
		"_mail",
		"_friend",
		"_match",
		"_diary",
		"_olympiad?command",
		"menu_select",
		"manor_menu_select",
		"pccafe",
		"report"
	};
	
	// S
	private String _command;
	
	@Override
	protected void readImpl()
	{
		_command = readString();
	}
	
	@Override
	protected void runImpl()
	{
		final Player player = getPlayer();
		if (player == null)
		{
			return;
		}
		
		if (_command.isEmpty())
		{
			PacketLogger.warning(player + " sent empty bypass!");
			Disconnection.of(getClient(), player).defaultSequence(LeaveWorld.STATIC_PACKET);
			return;
		}
		
		boolean requiresBypassValidation = true;
		for (String possibleNonHtmlCommand : _possibleNonHtmlCommands)
		{
			if (_command.startsWith(possibleNonHtmlCommand))
			{
				requiresBypassValidation = false;
				break;
			}
		}
		
		int bypassOriginId = 0;
		if (requiresBypassValidation)
		{
			bypassOriginId = player.validateHtmlAction(_command);
			if (bypassOriginId == -1)
			{
				return;
			}
			
			if ((bypassOriginId > 0) && !Util.isInsideRangeOfObjectId(player, bypassOriginId, Npc.INTERACTION_DISTANCE))
			{
				// No logging here, this could be a common case where the player has the html still open and run too far away and then clicks a html action
				return;
			}
		}
		
		if (!getClient().getFloodProtectors().canUseServerBypass())
		{
			return;
		}
		
		if (EventDispatcher.getInstance().hasListener(EventType.ON_PLAYER_BYPASS, player))
		{
			final TerminateReturn terminateReturn = EventDispatcher.getInstance().notifyEvent(new OnPlayerBypass(player, _command), player, TerminateReturn.class);
			if ((terminateReturn != null) && terminateReturn.terminate())
			{
				return;
			}
		}
		
		try
		{
			if (_command.startsWith("admin_"))
			{
				AdminCommandHandler.getInstance().useAdminCommand(player, _command, true);
			}
			else if (CommunityBoardHandler.getInstance().isCommunityBoardCommand(_command))
			{
				CommunityBoardHandler.getInstance().handleParseCommand(_command, player);
			}
			else if (_command.equals("come_here") && player.isGM())
			{
				comeHere(player);
			}
			else if (_command.startsWith("npc_"))
			{
				final int endOfId = _command.indexOf('_', 5);
				String id;
				if (endOfId > 0)
				{
					id = _command.substring(4, endOfId);
				}
				else
				{
					id = _command.substring(4);
				}
				
				if (Util.isDigit(id))
				{
					final WorldObject object = World.getInstance().findObject(Integer.parseInt(id));
					if ((object != null) && object.isNpc() && (endOfId > 0) && player.isInsideRadius2D(object, Npc.INTERACTION_DISTANCE))
					{
						((Npc) object).onBypassFeedback(player, _command.substring(endOfId + 1));
					}
				}
				
				player.sendPacket(ActionFailed.STATIC_PACKET);
			}
			else if (_command.startsWith("item_"))
			{
				final int endOfId = _command.indexOf('_', 5);
				String id;
				if (endOfId > 0)
				{
					id = _command.substring(5, endOfId);
				}
				else
				{
					id = _command.substring(5);
				}
				try
				{
					final Item item = player.getInventory().getItemByObjectId(Integer.parseInt(id));
					if ((item != null) && (endOfId > 0))
					{
						item.onBypassFeedback(player, _command.substring(endOfId + 1));
					}
					
					player.sendPacket(ActionFailed.STATIC_PACKET);
				}
				catch (NumberFormatException nfe)
				{
					PacketLogger.warning("NFE for command [" + _command + "] " + nfe.getMessage());
				}
			}
			else if (_command.startsWith("_match"))
			{
				final String params = _command.substring(_command.indexOf('?') + 1);
				final StringTokenizer st = new StringTokenizer(params, "&");
				final int heroclass = Integer.parseInt(st.nextToken().split("=")[1]);
				final int heropage = Integer.parseInt(st.nextToken().split("=")[1]);
				final int heroid = Hero.getInstance().getHeroByClass(heroclass);
				if (heroid > 0)
				{
					Hero.getInstance().showHeroFights(player, heroclass, heroid, heropage);
				}
			}
			else if (_command.startsWith("_diary"))
			{
				final String params = _command.substring(_command.indexOf('?') + 1);
				final StringTokenizer st = new StringTokenizer(params, "&");
				final int heroclass = Integer.parseInt(st.nextToken().split("=")[1]);
				final int heropage = Integer.parseInt(st.nextToken().split("=")[1]);
				final int heroid = Hero.getInstance().getHeroByClass(heroclass);
				if (heroid > 0)
				{
					Hero.getInstance().showHeroDiary(player, heroclass, heroid, heropage);
				}
			}
			else if (_command.startsWith("_olympiad?command"))
			{
				final int arenaId = Integer.parseInt(_command.split("=")[2]);
				final IBypassHandler handler = BypassHandler.getInstance().getHandler("arenachange");
				if (handler != null)
				{
					handler.useBypass("arenachange " + (arenaId - 1), player, null);
				}
			}
			else if (_command.startsWith("menu_select"))
			{
				final Npc lastNpc = player.getLastFolkNPC();
				if ((lastNpc != null) && lastNpc.canInteract(player) && EventDispatcher.getInstance().hasListener(EventType.ON_NPC_MENU_SELECT, lastNpc))
				{
					final String[] split = _command.substring(_command.indexOf('?') + 1).split("&");
					final int ask = Integer.parseInt(split[0].split("=")[1]);
					final int reply = Integer.parseInt(split[1].split("=")[1]);
					EventDispatcher.getInstance().notifyEventAsync(new OnNpcMenuSelect(player, lastNpc, ask, reply), lastNpc);
				}
			}
			else if (_command.startsWith("manor_menu_select"))
			{
				final Npc lastNpc = player.getLastFolkNPC();
				if (Config.ALLOW_MANOR && (lastNpc != null) && lastNpc.canInteract(player) && EventDispatcher.getInstance().hasListener(EventType.ON_NPC_MANOR_BYPASS, lastNpc))
				{
					final String[] split = _command.substring(_command.indexOf('?') + 1).split("&");
					final int ask = Integer.parseInt(split[0].split("=")[1]);
					final int state = Integer.parseInt(split[1].split("=")[1]);
					final boolean time = split[2].split("=")[1].equals("1");
					EventDispatcher.getInstance().notifyEventAsync(new OnNpcManorBypass(player, lastNpc, ask, state, time), lastNpc);
				}
			}
			else if (_command.startsWith("pccafe"))
			{
				if (!Config.PC_CAFE_ENABLED)
				{
					return;
				}
				final int multisellId = Integer.parseInt(_command.substring(10).trim());
				MultisellData.getInstance().separateAndSend(multisellId, player, null, false);
			}
			else if (_command.startsWith("report"))
			{
				CaptchaManager.getInstance().analyseBypass(_command, player);
			}
			else
			{
				final IBypassHandler handler = BypassHandler.getInstance().getHandler(_command);
				if (handler != null)
				{
					if (bypassOriginId > 0)
					{
						final WorldObject bypassOrigin = World.getInstance().findObject(bypassOriginId);
						if ((bypassOrigin != null) && bypassOrigin.isCreature())
						{
							handler.useBypass(_command, player, (Creature) bypassOrigin);
						}
						else
						{
							handler.useBypass(_command, player, null);
						}
					}
					else
					{
						handler.useBypass(_command, player, null);
					}
				}
				else
				{
					PacketLogger.warning(getClient() + " sent not handled RequestBypassToServer: [" + _command + "]");
				}
			}
		}
		catch (Exception e)
		{
			PacketLogger.warning("Exception processing bypass from " + player + ": " + _command + " " + e.getMessage());
			PacketLogger.warning(CommonUtil.getStackTrace(e));
			if (player.isGM())
			{
				final StringBuilder sb = new StringBuilder(200);
				sb.append("<html><body>");
				sb.append("Bypass error: " + e + "<br1>");
				sb.append("Bypass command: " + _command + "<br1>");
				sb.append("StackTrace:<br1>");
				for (StackTraceElement ste : e.getStackTrace())
				{
					sb.append(ste + "<br1>");
				}
				sb.append("</body></html>");
				// item html
				final NpcHtmlMessage msg = new NpcHtmlMessage(0, 1, sb.toString());
				msg.disableValidation();
				player.sendPacket(msg);
			}
		}
	}
	
	/**
	 * @param player
	 */
	private void comeHere(Player player)
	{
		final WorldObject obj = player.getTarget();
		if (obj == null)
		{
			return;
		}
		if (obj.isNpc())
		{
			final Npc temp = (Npc) obj;
			temp.setTarget(player);
			temp.getAI().setIntention(CtrlIntention.AI_INTENTION_MOVE_TO, player.getLocation());
		}
	}
}
