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
import org.l2jpx.gameserver.model.actor.instance.AirShip;
import org.l2jpx.gameserver.network.GameClient;
import org.l2jpx.gameserver.network.ServerPackets;

public class ExAirShipInfo extends ServerPacket
{
	// store some parameters, because they can be changed during broadcast
	private final AirShip _ship;
	private final int _x;
	private final int _y;
	private final int _z;
	private final int _heading;
	private final int _moveSpeed;
	private final int _rotationSpeed;
	private final int _captain;
	private final int _helm;
	
	public ExAirShipInfo(AirShip ship)
	{
		_ship = ship;
		_x = ship.getX();
		_y = ship.getY();
		_z = ship.getZ();
		_heading = ship.getHeading();
		_moveSpeed = (int) ship.getStat().getMoveSpeed();
		_rotationSpeed = (int) ship.getStat().getRotationSpeed();
		_captain = ship.getCaptainId();
		_helm = ship.getHelmObjectId();
	}
	
	@Override
	public void writeImpl(GameClient client, WritableBuffer buffer)
	{
		ServerPackets.EX_AIR_SHIP_INFO.writeId(this, buffer);
		buffer.writeInt(_ship.getObjectId());
		buffer.writeInt(_x);
		buffer.writeInt(_y);
		buffer.writeInt(_z);
		buffer.writeInt(_heading);
		buffer.writeInt(_captain);
		buffer.writeInt(_moveSpeed);
		buffer.writeInt(_rotationSpeed);
		buffer.writeInt(_helm);
		if (_helm != 0)
		{
			// TODO: unhardcode these!
			buffer.writeInt(0x16e); // Controller X
			buffer.writeInt(0x00); // Controller Y
			buffer.writeInt(0x6b); // Controller Z
			buffer.writeInt(0x15c); // Captain X
			buffer.writeInt(0x00); // Captain Y
			buffer.writeInt(0x69); // Captain Z
		}
		else
		{
			buffer.writeInt(0);
			buffer.writeInt(0);
			buffer.writeInt(0);
			buffer.writeInt(0);
			buffer.writeInt(0);
			buffer.writeInt(0);
		}
		buffer.writeInt(_ship.getFuel());
		buffer.writeInt(_ship.getMaxFuel());
	}
}
