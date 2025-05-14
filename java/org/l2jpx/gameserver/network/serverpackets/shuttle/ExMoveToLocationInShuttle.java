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
package org.l2jpx.gameserver.network.serverpackets.shuttle;

import org.l2jpx.commons.network.WritableBuffer;
import org.l2jpx.gameserver.model.actor.Player;
import org.l2jpx.gameserver.network.GameClient;
import org.l2jpx.gameserver.network.ServerPackets;
import org.l2jpx.gameserver.network.serverpackets.ServerPacket;

/**
 * @author UnAfraid
 */
public class ExMoveToLocationInShuttle extends ServerPacket
{
	private final int _objectId;
	private final int _airShipId;
	private final int _targetX;
	private final int _targetY;
	private final int _targetZ;
	private final int _fromX;
	private final int _fromY;
	private final int _fromZ;
	
	public ExMoveToLocationInShuttle(Player player, int fromX, int fromY, int fromZ)
	{
		_objectId = player.getObjectId();
		_airShipId = player.getShuttle().getObjectId();
		_targetX = player.getInVehiclePosition().getX();
		_targetY = player.getInVehiclePosition().getY();
		_targetZ = player.getInVehiclePosition().getZ();
		_fromX = fromX;
		_fromY = fromY;
		_fromZ = fromZ;
	}
	
	@Override
	public void writeImpl(GameClient client, WritableBuffer buffer)
	{
		ServerPackets.EX_MOVE_TO_LOCATION_IN_SUTTLE.writeId(this, buffer);
		buffer.writeInt(_objectId);
		buffer.writeInt(_airShipId);
		buffer.writeInt(_targetX);
		buffer.writeInt(_targetY);
		buffer.writeInt(_targetZ);
		buffer.writeInt(_fromX);
		buffer.writeInt(_fromY);
		buffer.writeInt(_fromZ);
	}
}
