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

import java.util.HashSet;
import java.util.Set;

import org.l2jpx.commons.network.WritableBuffer;
import org.l2jpx.gameserver.data.xml.ElementalAttributeData;
import org.l2jpx.gameserver.enums.AttributeType;
import org.l2jpx.gameserver.model.actor.Player;
import org.l2jpx.gameserver.model.item.instance.Item;
import org.l2jpx.gameserver.network.GameClient;
import org.l2jpx.gameserver.network.ServerPackets;

/**
 * @author Kerberos
 */
public class ExChooseInventoryAttributeItem extends ServerPacket
{
	private final int _itemId;
	private final long _count;
	private final AttributeType _atribute;
	private final int _level;
	private final Set<Integer> _items = new HashSet<>();
	
	public ExChooseInventoryAttributeItem(Player player, Item stone)
	{
		_itemId = stone.getDisplayId();
		_count = stone.getCount();
		_atribute = ElementalAttributeData.getInstance().getItemElement(_itemId);
		if (_atribute == AttributeType.NONE)
		{
			throw new IllegalArgumentException("Undefined Atribute item: " + stone);
		}
		_level = ElementalAttributeData.getInstance().getMaxElementLevel(_itemId);
		// Register only items that can be put an attribute stone/crystal
		for (Item item : player.getInventory().getItems())
		{
			if (item.isElementable())
			{
				_items.add(item.getObjectId());
			}
		}
	}
	
	@Override
	public void writeImpl(GameClient client, WritableBuffer buffer)
	{
		ServerPackets.EX_CHOOSE_INVENTORY_ATTRIBUTE_ITEM.writeId(this, buffer);
		buffer.writeInt(_itemId);
		buffer.writeLong(_count);
		buffer.writeInt(_atribute == AttributeType.FIRE); // Fire
		buffer.writeInt(_atribute == AttributeType.WATER); // Water
		buffer.writeInt(_atribute == AttributeType.WIND); // Wind
		buffer.writeInt(_atribute == AttributeType.EARTH); // Earth
		buffer.writeInt(_atribute == AttributeType.HOLY); // Holy
		buffer.writeInt(_atribute == AttributeType.DARK); // Unholy
		buffer.writeInt(_level); // Item max attribute level
		buffer.writeInt(_items.size());
		_items.forEach(buffer::writeInt);
	}
}
