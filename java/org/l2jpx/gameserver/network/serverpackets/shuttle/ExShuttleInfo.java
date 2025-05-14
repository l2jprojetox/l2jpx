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

import java.util.List;

import org.l2jpx.commons.network.WritableBuffer;
import org.l2jpx.gameserver.model.Location;
import org.l2jpx.gameserver.model.actor.instance.Shuttle;
import org.l2jpx.gameserver.model.shuttle.ShuttleStop;
import org.l2jpx.gameserver.network.GameClient;
import org.l2jpx.gameserver.network.ServerPackets;
import org.l2jpx.gameserver.network.serverpackets.ServerPacket;

/**
 * @author UnAfraid
 */
public class ExShuttleInfo extends ServerPacket
{
	private final Shuttle _shuttle;
	private final List<ShuttleStop> _stops;
	
	public ExShuttleInfo(Shuttle shuttle)
	{
		_shuttle = shuttle;
		_stops = shuttle.getStops();
	}
	
	@Override
	public void writeImpl(GameClient client, WritableBuffer buffer)
	{
		ServerPackets.EX_SHUTTLE_INFO.writeId(this, buffer);
		buffer.writeInt(_shuttle.getObjectId());
		buffer.writeInt(_shuttle.getX());
		buffer.writeInt(_shuttle.getY());
		buffer.writeInt(_shuttle.getZ());
		buffer.writeInt(_shuttle.getHeading());
		buffer.writeInt(_shuttle.getId());
		buffer.writeInt(_stops.size());
		for (ShuttleStop stop : _stops)
		{
			buffer.writeInt(stop.getId());
			for (Location loc : stop.getDimensions())
			{
				buffer.writeInt(loc.getX());
				buffer.writeInt(loc.getY());
				buffer.writeInt(loc.getZ());
			}
			buffer.writeInt(stop.isDoorOpen());
			buffer.writeInt(stop.hasDoorChanged());
		}
	}
}
