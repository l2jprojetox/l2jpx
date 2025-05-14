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
import org.l2jpx.gameserver.model.actor.Player;
import org.l2jpx.gameserver.model.actor.instance.Pet;
import org.l2jpx.gameserver.model.item.instance.Item;
import org.l2jpx.gameserver.network.GameClient;
import org.l2jpx.gameserver.network.ServerPackets;

public class GMViewItemList extends AbstractItemPacket
{
	private final List<Item> _items = new ArrayList<>();
	private final int _limit;
	private final String _playerName;
	
	public GMViewItemList(Player player)
	{
		_playerName = player.getName();
		_limit = player.getInventoryLimit();
		for (Item item : player.getInventory().getItems())
		{
			_items.add(item);
		}
	}
	
	public GMViewItemList(Pet cha)
	{
		_playerName = cha.getName();
		_limit = cha.getInventoryLimit();
		for (Item item : cha.getInventory().getItems())
		{
			_items.add(item);
		}
	}
	
	@Override
	public void writeImpl(GameClient client, WritableBuffer buffer)
	{
		ServerPackets.GM_VIEW_ITEM_LIST.writeId(this, buffer);
		buffer.writeString(_playerName);
		buffer.writeInt(_limit); // inventory limit
		buffer.writeShort(1); // show window ??
		buffer.writeShort(_items.size());
		for (Item item : _items)
		{
			writeItem(item, buffer);
		}
	}
}
