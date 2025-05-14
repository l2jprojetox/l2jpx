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
package org.l2jpx.gameserver.network.serverpackets.fishing;

import org.l2jpx.commons.network.WritableBuffer;
import org.l2jpx.gameserver.model.actor.Player;
import org.l2jpx.gameserver.model.interfaces.ILocational;
import org.l2jpx.gameserver.network.GameClient;
import org.l2jpx.gameserver.network.ServerPackets;
import org.l2jpx.gameserver.network.serverpackets.ServerPacket;

/**
 * @author -Wooden-
 */
public class ExFishingStart extends ServerPacket
{
	private final Player _player;
	private final int _fishType;
	private final ILocational _baitLocation;
	
	/**
	 * @param player
	 * @param fishType
	 * @param baitLocation
	 */
	public ExFishingStart(Player player, int fishType, ILocational baitLocation)
	{
		_player = player;
		_fishType = fishType;
		_baitLocation = baitLocation;
	}
	
	@Override
	public void writeImpl(GameClient client, WritableBuffer buffer)
	{
		ServerPackets.EX_FISHING_START.writeId(this, buffer);
		buffer.writeInt(_player.getObjectId());
		buffer.writeByte(_fishType);
		buffer.writeInt(_baitLocation.getX());
		buffer.writeInt(_baitLocation.getY());
		buffer.writeInt(_baitLocation.getZ());
		buffer.writeByte(1); // 0 = newbie, 1 = normal, 2 = night
	}
}
