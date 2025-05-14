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
package org.l2jpx.gameserver.network.serverpackets;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.l2jpx.commons.network.WritableBuffer;
import org.l2jpx.commons.database.DatabaseFactory;
import org.l2jpx.gameserver.network.GameClient;
import org.l2jpx.gameserver.network.PacketLogger;
import org.l2jpx.gameserver.network.ServerPackets;

/**
 * @author Atronic
 */
public class ExRequestPartyMatchingHistory extends ServerPacket
{
	private static final String GET_HISTORY = "SELECT title, leader FROM party_matching_history ORDER BY id DESC LIMIT 100";
	
	public ExRequestPartyMatchingHistory()
	{
	}
	
	@Override
	public void writeImpl(GameClient client, WritableBuffer buffer)
	{
		ServerPackets.EX_PARTY_MATCHING_ROOM_HISTORY.writeId(this, buffer);
		buffer.writeInt(100); // Maximum size according to retail.
		try (Connection con = DatabaseFactory.getConnection();
			PreparedStatement statement = con.prepareStatement(GET_HISTORY))
		{
			final ResultSet rset = statement.executeQuery();
			while (rset.next())
			{
				buffer.writeString(rset.getString("title"));
				buffer.writeString(rset.getString("leader"));
			}
		}
		catch (Exception e)
		{
			PacketLogger.warning("ExRequestPartyMatchingHistory: Could not load data: " + e.getMessage());
		}
	}
}
