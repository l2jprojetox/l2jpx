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

import org.l2jpx.commons.network.WritableBuffer;
import org.l2jpx.gameserver.model.Location;
import org.l2jpx.gameserver.model.actor.Player;
import org.l2jpx.gameserver.network.GameClient;
import org.l2jpx.gameserver.network.ServerPackets;

public class ExMoveToLocationInAirShip extends ServerPacket
{
	private final int _objectId;
	private final int _airShipId;
	private final Location _destination;
	private final int _heading;
	
	/**
	 * @param player
	 */
	public ExMoveToLocationInAirShip(Player player)
	{
		_objectId = player.getObjectId();
		_airShipId = player.getAirShip().getObjectId();
		_destination = player.getInVehiclePosition();
		_heading = player.getHeading();
	}
	
	@Override
	public void writeImpl(GameClient client, WritableBuffer buffer)
	{
		ServerPackets.EX_MOVE_TO_LOCATION_IN_AIR_SHIP.writeId(this, buffer);
		buffer.writeInt(_objectId);
		buffer.writeInt(_airShipId);
		buffer.writeInt(_destination.getX());
		buffer.writeInt(_destination.getY());
		buffer.writeInt(_destination.getZ());
		buffer.writeInt(_heading);
	}
}
