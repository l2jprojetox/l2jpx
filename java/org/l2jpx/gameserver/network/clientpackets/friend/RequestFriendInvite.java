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
package org.l2jpx.gameserver.network.clientpackets.friend;

import org.l2jpx.commons.threads.ThreadPool;
import org.l2jpx.gameserver.data.xml.FakePlayerData;
import org.l2jpx.gameserver.model.BlockList;
import org.l2jpx.gameserver.model.World;
import org.l2jpx.gameserver.model.actor.Player;
import org.l2jpx.gameserver.network.SystemMessageId;
import org.l2jpx.gameserver.network.clientpackets.ClientPacket;
import org.l2jpx.gameserver.network.serverpackets.SystemMessage;
import org.l2jpx.gameserver.network.serverpackets.friend.FriendAddRequest;

public class RequestFriendInvite extends ClientPacket
{
	private String _name;
	
	@Override
	protected void readImpl()
	{
		_name = readString();
	}
	
	private void scheduleDeny(Player player)
	{
		if (player != null)
		{
			player.sendPacket(SystemMessageId.YOU_HAVE_FAILED_TO_ADD_A_FRIEND_TO_YOUR_FRIENDS_LIST);
			player.onTransactionResponse();
		}
	}
	
	@Override
	protected void runImpl()
	{
		final Player player = getPlayer();
		if (player == null)
		{
			return;
		}
		
		if (FakePlayerData.getInstance().isTalkable(_name))
		{
			if (!player.isProcessingRequest())
			{
				final SystemMessage sm = new SystemMessage(SystemMessageId.YOU_VE_REQUESTED_C1_TO_BE_ON_YOUR_FRIENDS_LIST);
				sm.addString(_name);
				player.sendPacket(sm);
				ThreadPool.schedule(() -> scheduleDeny(player), 10000);
				player.blockRequest();
			}
			else
			{
				final SystemMessage sm = new SystemMessage(SystemMessageId.C1_IS_ON_ANOTHER_TASK_PLEASE_TRY_AGAIN_LATER);
				sm.addString(_name);
				player.sendPacket(sm);
			}
			return;
		}
		
		final Player friend = World.getInstance().getPlayer(_name);
		
		// Target is not found in the game.
		if ((friend == null) || !friend.isOnline() || friend.isInvisible())
		{
			player.sendPacket(SystemMessageId.THE_USER_WHO_REQUESTED_TO_BECOME_FRIENDS_IS_NOT_FOUND_IN_THE_GAME);
			return;
		}
		// You cannot add yourself to your own friend list.
		if (friend == player)
		{
			player.sendPacket(SystemMessageId.YOU_CANNOT_ADD_YOURSELF_TO_YOUR_OWN_FRIEND_LIST);
			return;
		}
		// Target is in olympiad.
		if (player.isInOlympiadMode() || friend.isInOlympiadMode())
		{
			player.sendPacket(SystemMessageId.A_USER_CURRENTLY_PARTICIPATING_IN_THE_OLYMPIAD_CANNOT_SEND_PARTY_AND_FRIEND_INVITATIONS);
			return;
		}
		
		// Cannot request friendship in an event.
		if (player.isOnEvent())
		{
			player.sendMessage("You cannot request friendship while participating in an event.");
			return;
		}
		
		// Target blocked active player.
		if (BlockList.isBlocked(friend, player))
		{
			player.sendMessage("You are in target's block list.");
			return;
		}
		SystemMessage sm;
		// Target is blocked.
		if (BlockList.isBlocked(player, friend))
		{
			sm = new SystemMessage(SystemMessageId.YOU_HAVE_BLOCKED_C1);
			sm.addString(friend.getName());
			player.sendPacket(sm);
			return;
		}
		
		// Target already in friend list.
		if (player.getFriendList().contains(friend.getObjectId()))
		{
			player.sendPacket(SystemMessageId.THIS_PLAYER_IS_ALREADY_REGISTERED_ON_YOUR_FRIENDS_LIST);
			return;
		}
		// Target is busy.
		if (friend.isProcessingRequest())
		{
			sm = new SystemMessage(SystemMessageId.C1_IS_ON_ANOTHER_TASK_PLEASE_TRY_AGAIN_LATER);
			sm.addString(_name);
			player.sendPacket(sm);
			return;
		}
		// Friend request sent.
		player.onTransactionRequest(friend);
		friend.sendPacket(new FriendAddRequest(player.getName()));
		sm = new SystemMessage(SystemMessageId.YOU_VE_REQUESTED_C1_TO_BE_ON_YOUR_FRIENDS_LIST);
		sm.addString(_name);
		player.sendPacket(sm);
	}
}
