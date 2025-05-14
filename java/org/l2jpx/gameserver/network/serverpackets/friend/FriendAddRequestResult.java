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
package org.l2jpx.gameserver.network.serverpackets.friend;

import org.l2jpx.commons.network.WritableBuffer;
import org.l2jpx.gameserver.model.actor.Player;
import org.l2jpx.gameserver.network.GameClient;
import org.l2jpx.gameserver.network.ServerPackets;
import org.l2jpx.gameserver.network.serverpackets.ServerPacket;

/**
 * @author UnAfraid
 */
public class FriendAddRequestResult extends ServerPacket
{
	private final int _result;
	private final int _charId;
	private final String _charName;
	private final int _isOnline;
	private final int _charObjectId;
	private final int _charLevel;
	private final int _charClassId;
	
	public FriendAddRequestResult(Player player, int result)
	{
		_result = result;
		_charId = player.getObjectId();
		_charName = player.getName();
		_isOnline = player.isOnlineInt();
		_charObjectId = player.getObjectId();
		_charLevel = player.getLevel();
		_charClassId = player.getActiveClass();
	}
	
	@Override
	public void writeImpl(GameClient client, WritableBuffer buffer)
	{
		ServerPackets.FRIEND_ADD_REQUEST_RESULT.writeId(this, buffer);
		buffer.writeInt(_result);
		buffer.writeInt(_charId);
		buffer.writeString(_charName);
		buffer.writeInt(_isOnline);
		buffer.writeInt(_charObjectId);
		buffer.writeInt(_charLevel);
		buffer.writeInt(_charClassId);
		buffer.writeShort(0); // Always 0 on retail
	}
}
