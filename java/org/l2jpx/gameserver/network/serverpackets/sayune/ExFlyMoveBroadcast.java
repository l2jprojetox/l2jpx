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
package org.l2jpx.gameserver.network.serverpackets.sayune;

import org.l2jpx.commons.network.WritableBuffer;
import org.l2jpx.gameserver.enums.SayuneType;
import org.l2jpx.gameserver.model.actor.Player;
import org.l2jpx.gameserver.model.interfaces.ILocational;
import org.l2jpx.gameserver.network.GameClient;
import org.l2jpx.gameserver.network.ServerPackets;
import org.l2jpx.gameserver.network.serverpackets.ServerPacket;

/**
 * @author UnAfraid
 */
public class ExFlyMoveBroadcast extends ServerPacket
{
	private final int _objectId;
	private final int _mapId;
	private final ILocational _currentLoc;
	private final ILocational _targetLoc;
	private final SayuneType _type;
	
	public ExFlyMoveBroadcast(Player player, SayuneType type, int mapId, ILocational targetLoc)
	{
		_objectId = player.getObjectId();
		_type = type;
		_mapId = mapId;
		_currentLoc = player;
		_targetLoc = targetLoc;
	}
	
	@Override
	public void writeImpl(GameClient client, WritableBuffer buffer)
	{
		ServerPackets.EX_FLY_MOVE_BROADCAST.writeId(this, buffer);
		buffer.writeInt(_objectId);
		buffer.writeInt(_type.ordinal());
		buffer.writeInt(_mapId);
		buffer.writeInt(_targetLoc.getX());
		buffer.writeInt(_targetLoc.getY());
		buffer.writeInt(_targetLoc.getZ());
		buffer.writeInt(0); // ?
		buffer.writeInt(_currentLoc.getX());
		buffer.writeInt(_currentLoc.getY());
		buffer.writeInt(_currentLoc.getZ());
	}
}
