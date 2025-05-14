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
import org.l2jpx.gameserver.enums.PlayerCondOverride;
import org.l2jpx.gameserver.model.World;
import org.l2jpx.gameserver.model.WorldObject;
import org.l2jpx.gameserver.model.actor.Player;
import org.l2jpx.gameserver.model.effects.AbstractEffect;
import org.l2jpx.gameserver.model.skill.AbnormalType;
import org.l2jpx.gameserver.model.skill.BuffInfo;
import org.l2jpx.gameserver.network.PacketLogger;
import org.l2jpx.gameserver.network.SystemMessageId;
import org.l2jpx.gameserver.network.serverpackets.ActionFailed;

public class Action extends ClientPacket
{
	private int _objectId;
	@SuppressWarnings("unused")
	private int _originX;
	@SuppressWarnings("unused")
	private int _originY;
	@SuppressWarnings("unused")
	private int _originZ;
	private int _actionId;
	
	@Override
	protected void readImpl()
	{
		_objectId = readInt(); // Target object Identifier
		_originX = readInt();
		_originY = readInt();
		_originZ = readInt();
		_actionId = readByte(); // Action identifier : 0-Simple click, 1-Shift click
	}
	
	@Override
	protected void runImpl()
	{
		if (!getClient().getFloodProtectors().canPerformPlayerAction())
		{
			return;
		}
		
		// Get the current Player of the player
		final Player player = getPlayer();
		if (player == null)
		{
			return;
		}
		
		if (player.inObserverMode())
		{
			player.sendPacket(SystemMessageId.OBSERVERS_CANNOT_PARTICIPATE);
			player.sendPacket(ActionFailed.STATIC_PACKET);
			return;
		}
		
		final BuffInfo info = player.getEffectList().getFirstBuffInfoByAbnormalType(AbnormalType.BOT_PENALTY);
		if (info != null)
		{
			for (AbstractEffect effect : info.getEffects())
			{
				if (!effect.checkCondition(-4))
				{
					player.sendPacket(SystemMessageId.YOU_HAVE_BEEN_REPORTED_AS_AN_ILLEGAL_PROGRAM_USER_SO_YOUR_ACTIONS_HAVE_BEEN_RESTRICTED);
					player.sendPacket(ActionFailed.STATIC_PACKET);
					return;
				}
			}
		}
		
		final WorldObject obj;
		if (player.getTargetId() == _objectId)
		{
			obj = player.getTarget();
		}
		else if (player.isInAirShip() && (player.getAirShip().getHelmObjectId() == _objectId))
		{
			obj = player.getAirShip();
		}
		else
		{
			obj = World.getInstance().findObject(_objectId);
		}
		
		// If object requested does not exist, add warn msg into logs
		if (obj == null)
		{
			// pressing e.g. pickup many times quickly would get you here
			player.sendPacket(ActionFailed.STATIC_PACKET);
			return;
		}
		
		if ((!obj.isTargetable() || player.isTargetingDisabled()) && !player.canOverrideCond(PlayerCondOverride.TARGET_ALL))
		{
			player.sendPacket(ActionFailed.STATIC_PACKET);
			return;
		}
		
		// Players can't interact with objects in the other instances
		if (obj.getInstanceWorld() != player.getInstanceWorld())
		{
			player.sendPacket(ActionFailed.STATIC_PACKET);
			return;
		}
		
		// Only GMs can directly interact with invisible characters
		if (!obj.isVisibleFor(player))
		{
			player.sendPacket(ActionFailed.STATIC_PACKET);
			return;
		}
		
		// Check if the target is valid, if the player haven't a shop or isn't the requester of a transaction (ex : FriendInvite, JoinAlly, JoinParty...)
		if (player.getActiveRequester() != null)
		{
			// Actions prohibited when in trade
			player.sendPacket(ActionFailed.STATIC_PACKET);
			return;
		}
		
		player.onActionRequest();
		
		switch (_actionId)
		{
			case 0:
			{
				obj.onAction(player);
				break;
			}
			case 1:
			{
				if (!player.isGM() && (!(obj.isNpc() && Config.ALT_GAME_VIEWNPC) || obj.isFakePlayer()))
				{
					obj.onAction(player, false);
				}
				else
				{
					obj.onActionShift(player);
				}
				break;
			}
			default:
			{
				// Invalid action detected (probably client cheating), log this
				PacketLogger.warning(getClass().getSimpleName() + ": Character: " + player.getName() + " requested invalid action: " + _actionId);
				player.sendPacket(ActionFailed.STATIC_PACKET);
				break;
			}
		}
	}
}
