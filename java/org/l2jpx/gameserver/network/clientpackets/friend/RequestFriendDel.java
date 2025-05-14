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
import org.l2jpx.gameserver.data.sql.CharInfoTable;
import org.l2jpx.gameserver.model.World;
import org.l2jpx.gameserver.model.actor.Player;
import org.l2jpx.gameserver.network.PacketLogger;
import org.l2jpx.gameserver.network.SystemMessageId;
import org.l2jpx.gameserver.network.clientpackets.ClientPacket;
import org.l2jpx.gameserver.network.serverpackets.SystemMessage;
import org.l2jpx.gameserver.network.serverpackets.friend.FriendRemove;

/**
 * @version $Revision: 1.3.4.2 $ $Date: 2005/03/27 15:29:30 $
 */
public class RequestFriendDel extends ClientPacket
{
	private String _name;
	
	@Override
	protected void readImpl()
	{
		_name = readString();
	}
	
	@Override
	protected void runImpl()
	{
		SystemMessage sm;
		
		final Player player = getPlayer();
		if (player == null)
		{
			return;
		}
		
		final int id = CharInfoTable.getInstance().getIdByName(_name);
		if (id == -1)
		{
			sm = new SystemMessage(SystemMessageId.C1_IS_NOT_ON_YOUR_FRIEND_LIST);
			sm.addString(_name);
			player.sendPacket(sm);
			return;
		}
		
		if (!player.getFriendList().contains(id))
		{
			sm = new SystemMessage(SystemMessageId.C1_IS_NOT_ON_YOUR_FRIEND_LIST);
			sm.addString(_name);
			player.sendPacket(sm);
			return;
		}
		
		try (Connection con = DatabaseFactory.getConnection();
			PreparedStatement statement = con.prepareStatement("DELETE FROM character_friends WHERE (charId=? AND friendId=?) OR (charId=? AND friendId=?)"))
		{
			statement.setInt(1, player.getObjectId());
			statement.setInt(2, id);
			statement.setInt(3, id);
			statement.setInt(4, player.getObjectId());
			statement.execute();
			
			// Player deleted from your friend list
			sm = new SystemMessage(SystemMessageId.S1_HAS_BEEN_REMOVED_FROM_YOUR_FRIENDS_LIST_2);
			sm.addString(_name);
			player.sendPacket(sm);
			
			player.getFriendList().remove(Integer.valueOf(id));
			player.sendPacket(new FriendRemove(_name, 1));
			
			final Player target = World.getInstance().getPlayer(_name);
			if (target != null)
			{
				target.getFriendList().remove(Integer.valueOf(player.getObjectId()));
				target.sendPacket(new FriendRemove(player.getName(), 1));
			}
			
			CharInfoTable.getInstance().removeFriendMemo(player.getObjectId(), id);
		}
		catch (Exception e)
		{
			PacketLogger.warning("Could not del friend objectid: " + e.getMessage());
		}
	}
}
