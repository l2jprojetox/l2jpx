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

import java.sql.Connection;
import java.sql.PreparedStatement;

import org.l2jpx.commons.database.DatabaseFactory;
import org.l2jpx.gameserver.model.actor.Player;
import org.l2jpx.gameserver.network.PacketLogger;
import org.l2jpx.gameserver.network.SystemMessageId;
import org.l2jpx.gameserver.network.clientpackets.ClientPacket;
import org.l2jpx.gameserver.network.serverpackets.SystemMessage;
import org.l2jpx.gameserver.network.serverpackets.friend.FriendAddRequestResult;

public class RequestAnswerFriendInvite extends ClientPacket
{
	private int _response;
	
	@Override
	protected void readImpl()
	{
		readByte();
		_response = readInt();
	}
	
	@Override
	protected void runImpl()
	{
		final Player player = getPlayer();
		if (player == null)
		{
			return;
		}
		
		final Player requestor = player.getActiveRequester();
		if (requestor == null)
		{
			return;
		}
		
		if (player == requestor)
		{
			player.sendPacket(SystemMessageId.YOU_CANNOT_ADD_YOURSELF_TO_YOUR_OWN_FRIEND_LIST);
			return;
		}
		
		if (player.getFriendList().contains(requestor.getObjectId()) //
			|| requestor.getFriendList().contains(player.getObjectId()))
		{
			final SystemMessage sm = new SystemMessage(SystemMessageId.C1_IS_ALREADY_ON_YOUR_FRIEND_LIST);
			sm.addString(player.getName());
			requestor.sendPacket(sm);
			return;
		}
		
		if (_response == 1)
		{
			try (Connection con = DatabaseFactory.getConnection();
				PreparedStatement statement = con.prepareStatement("INSERT INTO character_friends (charId, friendId) VALUES (?, ?), (?, ?)"))
			{
				statement.setInt(1, requestor.getObjectId());
				statement.setInt(2, player.getObjectId());
				statement.setInt(3, player.getObjectId());
				statement.setInt(4, requestor.getObjectId());
				statement.execute();
				SystemMessage msg = new SystemMessage(SystemMessageId.THAT_PERSON_HAS_BEEN_SUCCESSFULLY_ADDED_TO_YOUR_FRIEND_LIST);
				requestor.sendPacket(msg);
				
				// Player added to your friend list
				msg = new SystemMessage(SystemMessageId.S1_HAS_BEEN_ADDED_TO_YOUR_FRIENDS_LIST);
				msg.addString(player.getName());
				requestor.sendPacket(msg);
				requestor.getFriendList().add(player.getObjectId());
				
				// has joined as friend.
				msg = new SystemMessage(SystemMessageId.S1_HAS_BEEN_ADDED_TO_YOUR_FRIENDS_LIST_2);
				msg.addString(requestor.getName());
				player.sendPacket(msg);
				player.getFriendList().add(requestor.getObjectId());
				
				// Send notifications for both player in order to show them online
				player.sendPacket(new FriendAddRequestResult(requestor, 1));
				requestor.sendPacket(new FriendAddRequestResult(player, 1));
			}
			catch (Exception e)
			{
				PacketLogger.warning("Could not add friend objectid: " + e.getMessage());
			}
		}
		else
		{
			requestor.sendPacket(new SystemMessage(SystemMessageId.YOU_HAVE_FAILED_TO_ADD_A_FRIEND_TO_YOUR_FRIENDS_LIST));
		}
		
		player.setActiveRequester(null);
		requestor.onTransactionResponse();
	}
}
