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
import org.l2jpx.gameserver.model.Location;
import org.l2jpx.gameserver.model.actor.Player;
import org.l2jpx.gameserver.model.actor.instance.Shuttle;
import org.l2jpx.gameserver.network.GameClient;
import org.l2jpx.gameserver.network.ServerPackets;
import org.l2jpx.gameserver.network.serverpackets.ServerPacket;

/**
 * @author UnAfraid
 */
public class ExShuttleGetOn extends ServerPacket
{
	private final int _playerObjectId;
	private final int _shuttleObjectId;
	private final Location _pos;
	
	public ExShuttleGetOn(Player player, Shuttle shuttle)
	{
		_playerObjectId = player.getObjectId();
		_shuttleObjectId = shuttle.getObjectId();
		_pos = player.getInVehiclePosition();
	}
	
	@Override
	public void writeImpl(GameClient client, WritableBuffer buffer)
	{
		ServerPackets.EX_SUTTLE_GET_ON.writeId(this, buffer);
		buffer.writeInt(_playerObjectId);
		buffer.writeInt(_shuttleObjectId);
		buffer.writeInt(_pos.getX());
		buffer.writeInt(_pos.getY());
		buffer.writeInt(_pos.getZ());
	}
}
