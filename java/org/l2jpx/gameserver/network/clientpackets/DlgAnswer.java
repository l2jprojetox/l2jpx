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

import org.l2jpx.Config;
import org.l2jpx.gameserver.enums.PlayerAction;
import org.l2jpx.gameserver.handler.AdminCommandHandler;
import org.l2jpx.gameserver.model.actor.Player;
import org.l2jpx.gameserver.model.events.EventDispatcher;
import org.l2jpx.gameserver.model.events.EventType;
import org.l2jpx.gameserver.model.events.impl.creature.player.OnPlayerDlgAnswer;
import org.l2jpx.gameserver.model.events.returns.TerminateReturn;
import org.l2jpx.gameserver.model.holders.DoorRequestHolder;
import org.l2jpx.gameserver.model.holders.SummonRequestHolder;
import org.l2jpx.gameserver.model.olympiad.OlympiadManager;
import org.l2jpx.gameserver.model.zone.ZoneId;
import org.l2jpx.gameserver.network.Disconnection;
import org.l2jpx.gameserver.network.SystemMessageId;
import org.l2jpx.gameserver.network.serverpackets.LeaveWorld;
import org.l2jpx.gameserver.util.OfflineTradeUtil;

/**
 * @author Dezmond_snz
 */
public class DlgAnswer extends ClientPacket
{
	private int _messageId;
	private int _answer;
	private int _requesterId;
	
	@Override
	protected void readImpl()
	{
		_messageId = readInt();
		_answer = readInt();
		_requesterId = readInt();
	}
	
	@Override
	protected void runImpl()
	{
		final Player player = getPlayer();
		if (player == null)
		{
			return;
		}
		
		if (EventDispatcher.getInstance().hasListener(EventType.ON_PLAYER_DLG_ANSWER, player))
		{
			final TerminateReturn term = EventDispatcher.getInstance().notifyEvent(new OnPlayerDlgAnswer(player, _messageId, _answer, _requesterId), player, TerminateReturn.class);
			if ((term != null) && term.terminate())
			{
				return;
			}
		}
		
		if (_messageId == SystemMessageId.S1_3.getId())
		{
			// Custom .offlineplay voiced command dialog.
			if (player.removeAction(PlayerAction.OFFLINE_PLAY))
			{
				if ((_answer == 0) || !Config.ENABLE_OFFLINE_PLAY_COMMAND)
				{
					return;
				}
				
				if (Config.OFFLINE_PLAY_PREMIUM && !player.hasPremiumStatus())
				{
					player.sendMessage("This command is only available to premium players.");
					return;
				}
				
				if (!player.isAutoPlaying())
				{
					player.sendMessage("You need to enable auto play before exiting.");
					return;
				}
				
				if (player.isInVehicle() || player.isInsideZone(ZoneId.PEACE))
				{
					player.sendPacket(SystemMessageId.YOU_MAY_NOT_LOG_OUT_FROM_THIS_LOCATION);
					return;
				}
				
				if (player.isRegisteredOnEvent())
				{
					player.sendMessage("Cannot use this command while registered on an event.");
					return;
				}
				
				// Unregister from olympiad.
				if (OlympiadManager.getInstance().isRegistered(player))
				{
					OlympiadManager.getInstance().unRegisterNoble(player);
				}
				
				player.startOfflinePlay();
				return;
			}
			
			if (player.removeAction(PlayerAction.ADMIN_COMMAND))
			{
				final String cmd = player.getAdminConfirmCmd();
				player.setAdminConfirmCmd(null);
				if (_answer == 0)
				{
					return;
				}
				
				// The 'useConfirm' must be disabled here, as we don't want to repeat that process.
				AdminCommandHandler.getInstance().useAdminCommand(player, cmd, false);
			}
		}
		else if (_messageId == SystemMessageId.DO_YOU_WISH_TO_EXIT_THE_GAME.getId())
		{
			if ((_answer == 0) || !Config.ENABLE_OFFLINE_COMMAND || (!Config.OFFLINE_TRADE_ENABLE && !Config.OFFLINE_CRAFT_ENABLE))
			{
				return;
			}
			
			if (!player.isInStoreMode())
			{
				player.sendPacket(SystemMessageId.PRIVATE_STORE_ALREADY_CLOSED);
				return;
			}
			
			if (player.isInInstance() || player.isInVehicle() || !player.canLogout())
			{
				return;
			}
			
			// Unregister from olympiad.
			if (OlympiadManager.getInstance().isRegistered(player))
			{
				OlympiadManager.getInstance().unRegisterNoble(player);
			}
			
			if (!OfflineTradeUtil.enteredOfflineMode(player))
			{
				Disconnection.of(getClient(), player).defaultSequence(LeaveWorld.STATIC_PACKET);
			}
		}
		else if ((_messageId == SystemMessageId.C1_IS_ATTEMPTING_TO_DO_A_RESURRECTION_THAT_RESTORES_S2_S3_XP_ACCEPT.getId()) || (_messageId == SystemMessageId.YOUR_CHARM_OF_COURAGE_IS_TRYING_TO_RESURRECT_YOU_WOULD_YOU_LIKE_TO_RESURRECT_NOW.getId()))
		{
			player.reviveAnswer(_answer);
		}
		else if (_messageId == SystemMessageId.C1_WISHES_TO_SUMMON_YOU_FROM_S2_DO_YOU_ACCEPT.getId())
		{
			final SummonRequestHolder holder = player.removeScript(SummonRequestHolder.class);
			if ((_answer == 1) && (holder != null) && (holder.getSummoner().getObjectId() == _requesterId))
			{
				player.teleToLocation(holder.getLocation(), true);
			}
		}
		else if (_messageId == SystemMessageId.WOULD_YOU_LIKE_TO_OPEN_THE_GATE.getId())
		{
			final DoorRequestHolder holder = player.removeScript(DoorRequestHolder.class);
			if ((holder != null) && (holder.getDoor() == player.getTarget()) && (_answer == 1))
			{
				holder.getDoor().openMe();
			}
		}
		else if (_messageId == SystemMessageId.WOULD_YOU_LIKE_TO_CLOSE_THE_GATE.getId())
		{
			final DoorRequestHolder holder = player.removeScript(DoorRequestHolder.class);
			if ((holder != null) && (holder.getDoor() == player.getTarget()) && (_answer == 1))
			{
				holder.getDoor().closeMe();
			}
		}
	}
}
