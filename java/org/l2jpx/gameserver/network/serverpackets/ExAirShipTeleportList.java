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
import org.l2jpx.gameserver.model.VehiclePathPoint;
import org.l2jpx.gameserver.network.GameClient;
import org.l2jpx.gameserver.network.ServerPackets;

public class ExAirShipTeleportList extends ServerPacket
{
	private final int _dockId;
	private final VehiclePathPoint[][] _teleports;
	private final int[] _fuelConsumption;
	
	public ExAirShipTeleportList(int dockId, VehiclePathPoint[][] teleports, int[] fuelConsumption)
	{
		_dockId = dockId;
		_teleports = teleports;
		_fuelConsumption = fuelConsumption;
	}
	
	@Override
	public void writeImpl(GameClient client, WritableBuffer buffer)
	{
		ServerPackets.EX_AIR_SHIP_TELEPORT_LIST.writeId(this, buffer);
		buffer.writeInt(_dockId);
		if (_teleports != null)
		{
			buffer.writeInt(_teleports.length);
			for (int i = 0; i < _teleports.length; i++)
			{
				buffer.writeInt(i - 1);
				buffer.writeInt(_fuelConsumption[i]);
				final VehiclePathPoint[] path = _teleports[i];
				final VehiclePathPoint dst = path[path.length - 1];
				buffer.writeInt(dst.getX());
				buffer.writeInt(dst.getY());
				buffer.writeInt(dst.getZ());
			}
		}
		else
		{
			buffer.writeInt(0);
		}
	}
}
