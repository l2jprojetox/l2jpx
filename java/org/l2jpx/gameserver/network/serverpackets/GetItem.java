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
import org.l2jpx.gameserver.model.item.instance.Item;
import org.l2jpx.gameserver.network.GameClient;
import org.l2jpx.gameserver.network.ServerPackets;

public class GetItem extends ServerPacket
{
	private final Item _item;
	private final int _playerId;
	
	public GetItem(Item item, int playerId)
	{
		_item = item;
		_playerId = playerId;
	}
	
	@Override
	public void writeImpl(GameClient client, WritableBuffer buffer)
	{
		ServerPackets.GET_ITEM.writeId(this, buffer);
		buffer.writeInt(_playerId);
		buffer.writeInt(_item.getObjectId());
		buffer.writeInt(_item.getX());
		buffer.writeInt(_item.getY());
		buffer.writeInt(_item.getZ());
	}
}
