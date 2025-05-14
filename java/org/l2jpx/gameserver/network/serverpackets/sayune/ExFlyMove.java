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

import java.util.List;

import org.l2jpx.commons.network.WritableBuffer;
import org.l2jpx.gameserver.enums.SayuneType;
import org.l2jpx.gameserver.model.SayuneEntry;
import org.l2jpx.gameserver.model.actor.Player;
import org.l2jpx.gameserver.network.GameClient;
import org.l2jpx.gameserver.network.ServerPackets;
import org.l2jpx.gameserver.network.serverpackets.ServerPacket;

/**
 * @author UnAfraid
 */
public class ExFlyMove extends ServerPacket
{
	private final int _objectId;
	private final SayuneType _type;
	private final int _mapId;
	private final List<SayuneEntry> _locations;
	
	public ExFlyMove(Player player, SayuneType type, int mapId, List<SayuneEntry> locations)
	{
		_objectId = player.getObjectId();
		_type = type;
		_mapId = mapId;
		_locations = locations;
	}
	
	@Override
	public void writeImpl(GameClient client, WritableBuffer buffer)
	{
		ServerPackets.EX_FLY_MOVE.writeId(this, buffer);
		buffer.writeInt(_objectId);
		buffer.writeInt(_type.ordinal());
		buffer.writeInt(0); // ??
		buffer.writeInt(_mapId);
		buffer.writeInt(_locations.size());
		for (SayuneEntry loc : _locations)
		{
			buffer.writeInt(loc.getId());
			buffer.writeInt(0); // ??
			buffer.writeInt(loc.getX());
			buffer.writeInt(loc.getY());
			buffer.writeInt(loc.getZ());
		}
	}
}
