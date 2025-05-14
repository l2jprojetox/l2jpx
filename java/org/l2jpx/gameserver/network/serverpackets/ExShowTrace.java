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

import java.util.ArrayList;
import java.util.List;

import org.l2jpx.commons.network.WritableBuffer;
import org.l2jpx.gameserver.model.Location;
import org.l2jpx.gameserver.model.interfaces.ILocational;
import org.l2jpx.gameserver.network.GameClient;
import org.l2jpx.gameserver.network.ServerPackets;

/**
 * This packet shows the mouse click particle for 30 seconds on every location.
 * @author NosBit
 */
public class ExShowTrace extends ServerPacket
{
	private final List<Location> _locations = new ArrayList<>();
	
	public void addLocation(int x, int y, int z)
	{
		_locations.add(new Location(x, y, z));
	}
	
	public void addLocation(ILocational loc)
	{
		addLocation(loc.getX(), loc.getY(), loc.getZ());
	}
	
	@Override
	public void writeImpl(GameClient client, WritableBuffer buffer)
	{
		ServerPackets.EX_SHOW_TRACE.writeId(this, buffer);
		buffer.writeShort(0); // type broken in H5
		buffer.writeInt(0); // time broken in H5
		buffer.writeShort(_locations.size());
		for (Location loc : _locations)
		{
			buffer.writeInt(loc.getX());
			buffer.writeInt(loc.getY());
			buffer.writeInt(loc.getZ());
		}
	}
}
