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

import java.util.logging.Logger;

import org.l2jpx.Config;
import org.l2jpx.gameserver.enums.ChatType;
import org.l2jpx.gameserver.handler.ChatHandler;
import org.l2jpx.gameserver.handler.IChatHandler;
import org.l2jpx.gameserver.model.actor.Player;
import org.l2jpx.gameserver.model.effects.EffectFlag;
import org.l2jpx.gameserver.model.events.EventDispatcher;
import org.l2jpx.gameserver.model.events.EventType;
import org.l2jpx.gameserver.model.events.impl.creature.player.OnPlayerChat;
import org.l2jpx.gameserver.model.events.returns.ChatFilterReturn;
import org.l2jpx.gameserver.model.item.instance.Item;
import org.l2jpx.gameserver.model.olympiad.OlympiadManager;
import org.l2jpx.gameserver.network.Disconnection;
import org.l2jpx.gameserver.network.GameClient;
import org.l2jpx.gameserver.network.PacketLogger;
import org.l2jpx.gameserver.network.SystemMessageId;
import org.l2jpx.gameserver.network.serverpackets.ActionFailed;
import org.l2jpx.gameserver.network.serverpackets.LeaveWorld;
import org.l2jpx.gameserver.util.Util;

/**
 * @version $Revision: 1.16.2.12.2.7 $ $Date: 2005/04/11 10:06:11 $
 */
public class Say2 extends ClientPacket
{
	private static Logger LOGGER_CHAT = Logger.getLogger("chat");
	
	private static final String[] WALKER_COMMAND_LIST =
	{
		"USESKILL",
		"USEITEM",
		"BUYITEM",
		"SELLITEM",
		"SAVEITEM",
		"LOADITEM",
		"MSG",
		"DELAY",
		"LABEL",
		"JMP",
		"CALL",
		"RETURN",
		"MOVETO",
		"NPCSEL",
		"NPCDLG",
		"DLGSEL",
		"CHARSTATUS",
		"POSOUTRANGE",
		"POSINRANGE",
		"GOHOME",
		"SAY",
		"EXIT",
		"PAUSE",
		"STRINDLG",
		"STRNOTINDLG",
		"CHANGEWAITTYPE",
		"FORCEATTACK",
		"ISMEMBER",
		"REQUESTJOINPARTY",
		"REQUESTOUTPARTY",
		"QUITPARTY",
		"MEMBERSTATUS",
		"CHARBUFFS",
		"ITEMCOUNT",
		"FOLLOWTELEPORT"
	};
	
	private String _text;
	private int _type;
	private String _target;
	
	@Override
	protected void readImpl()
	{
		_text = readString();
		_type = readInt();
		_target = (_type == ChatType.WHISPER.getClientId()) ? readString() : null;
	}
	
	@Override
	protected void runImpl()
	{
		final Player player = getPlayer();
		if (player == null)
		{
			return;
		}
		
		ChatType chatType = ChatType.findByClientId(_type);
		if (chatType == null)
		{
			PacketLogger.warning("Say2: Invalid type: " + _type + " Player : " + player.getName() + " text: " + _text);
			player.sendPacket(ActionFailed.STATIC_PACKET);
			Disconnection.of(player).defaultSequence(LeaveWorld.STATIC_PACKET);
			return;
		}
		
		if (_text.isEmpty())
		{
			PacketLogger.warning(player.getName() + ": sending empty text. Possible packet hack!");
			player.sendPacket(ActionFailed.STATIC_PACKET);
			Disconnection.of(player).defaultSequence(LeaveWorld.STATIC_PACKET);
			return;
		}
		
		// Even though the client can handle more characters than it's current limit allows, an overflow (critical error) happens if you pass a huge (1000+) message.
		// July 11, 2011 - Verified on High Five 4 official client as 105.
		// Allow higher limit if player shift some item (text is longer then).
		if (!player.isGM() && (((_text.indexOf(8) >= 0) && (_text.length() > 500)) || ((_text.indexOf(8) < 0) && (_text.length() > 105))))
		{
			player.sendPacket(SystemMessageId.WHEN_A_USER_S_KEYBOARD_INPUT_EXCEEDS_A_CERTAIN_CUMULATIVE_SCORE_A_CHAT_BAN_WILL_BE_APPLIED_THIS_IS_DONE_TO_DISCOURAGE_SPAMMING_PLEASE_AVOID_POSTING_THE_SAME_MESSAGE_MULTIPLE_TIMES_DURING_A_SHORT_PERIOD);
			return;
		}
		
		if (Config.L2WALKER_PROTECTION && (chatType == ChatType.WHISPER) && checkBot(_text))
		{
			Util.handleIllegalPlayerAction(player, "Client Emulator Detect: " + player + " using L2Walker.", Config.DEFAULT_PUNISH);
			return;
		}
		
		if (player.isCursedWeaponEquipped() && ((chatType == ChatType.TRADE) || (chatType == ChatType.SHOUT)))
		{
			player.sendPacket(SystemMessageId.SHOUT_AND_TRADE_CHATTING_CANNOT_BE_USED_WHILE_POSSESSING_A_CURSED_WEAPON);
			return;
		}
		
		if (player.isChatBanned() && (_text.charAt(0) != '.'))
		{
			if (player.isAffected(EffectFlag.CHAT_BLOCK))
			{
				player.sendPacket(SystemMessageId.YOU_HAVE_BEEN_REPORTED_AS_AN_ILLEGAL_PROGRAM_USER_SO_CHATTING_IS_NOT_ALLOWED);
			}
			else if (Config.BAN_CHAT_CHANNELS.contains(chatType))
			{
				player.sendPacket(SystemMessageId.CHATTING_IS_CURRENTLY_PROHIBITED_IF_YOU_TRY_TO_CHAT_BEFORE_THE_PROHIBITION_IS_REMOVED_THE_PROHIBITION_TIME_WILL_INCREASE_EVEN_FURTHER);
			}
			return;
		}
		
		if (player.isInOlympiadMode() || OlympiadManager.getInstance().isRegistered(player))
		{
			player.sendPacket(SystemMessageId.YOU_CANNOT_CHAT_WHILE_PARTICIPATING_IN_THE_OLYMPIAD);
			return;
		}
		
		if (player.isJailed() && Config.JAIL_DISABLE_CHAT && ((chatType == ChatType.WHISPER) || (chatType == ChatType.SHOUT) || (chatType == ChatType.TRADE) || (chatType == ChatType.HERO_VOICE)))
		{
			player.sendMessage("You can not chat with players outside of the jail.");
			return;
		}
		
		if ((chatType == ChatType.PETITION_PLAYER) && player.isGM())
		{
			chatType = ChatType.PETITION_GM;
		}
		
		if (Config.LOG_CHAT)
		{
			final StringBuilder sb = new StringBuilder();
			sb.append(chatType.name());
			sb.append(" [");
			sb.append(player);
			if (chatType == ChatType.WHISPER)
			{
				sb.append(" to ");
				sb.append(_target);
				sb.append("] ");
				sb.append(_text);
				LOGGER_CHAT.info(sb.toString());
			}
			else
			{
				sb.append("] ");
				sb.append(_text);
				LOGGER_CHAT.info(sb.toString());
			}
		}
		
		if ((_text.indexOf(8) >= 0) && !parseAndPublishItem(getClient(), player))
		{
			return;
		}
		
		if (EventDispatcher.getInstance().hasListener(EventType.ON_PLAYER_CHAT, player))
		{
			final ChatFilterReturn filter = EventDispatcher.getInstance().notifyEvent(new OnPlayerChat(player, _target, _text, chatType), player, ChatFilterReturn.class);
			if (filter != null)
			{
				_text = filter.getFilteredText();
				chatType = filter.getChatType();
			}
		}
		
		// Say Filter implementation
		if (Config.USE_SAY_FILTER)
		{
			checkText();
		}
		
		final IChatHandler handler = ChatHandler.getInstance().getHandler(chatType);
		if (handler != null)
		{
			handler.handleChat(chatType, player, _target, _text);
		}
		else
		{
			PacketLogger.info("No handler registered for ChatType: " + _type + " Player: " + getClient());
		}
	}
	
	private boolean checkBot(String text)
	{
		for (String botCommand : WALKER_COMMAND_LIST)
		{
			if (text.startsWith(botCommand))
			{
				return true;
			}
		}
		return false;
	}
	
	private void checkText()
	{
		String filteredText = _text;
		for (String pattern : Config.FILTER_LIST)
		{
			filteredText = filteredText.replaceAll("(?i)" + pattern, Config.CHAT_FILTER_CHARS);
		}
		_text = filteredText;
	}
	
	private boolean parseAndPublishItem(GameClient client, Player owner)
	{
		int pos1 = -1;
		while ((pos1 = _text.indexOf(8, pos1)) > -1)
		{
			int pos = _text.indexOf("ID=", pos1);
			if (pos == -1)
			{
				return false;
			}
			final StringBuilder result = new StringBuilder(9);
			pos += 3;
			while (Character.isDigit(_text.charAt(pos)))
			{
				result.append(_text.charAt(pos++));
			}
			final int id = Integer.parseInt(result.toString());
			final Item item = owner.getInventory().getItemByObjectId(id);
			if (item == null)
			{
				PacketLogger.info(client + " trying publish item which does not own! ID:" + id);
				return false;
			}
			item.publish();
			
			pos1 = _text.indexOf(8, pos) + 1;
			if (pos1 == 0) // missing ending tag
			{
				PacketLogger.info(client + " sent invalid publish item msg! ID:" + id);
				return false;
			}
		}
		return true;
	}
}
