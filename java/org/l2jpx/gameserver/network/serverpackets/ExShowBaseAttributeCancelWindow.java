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
import org.l2jpx.gameserver.model.item.instance.Item;
import org.l2jpx.gameserver.network.GameClient;
import org.l2jpx.gameserver.network.ServerPackets;

public class ExShowBaseAttributeCancelWindow extends ServerPacket
{
	private final List<Item> _items = new ArrayList<>();
	private long _price;
	
	public ExShowBaseAttributeCancelWindow(Player player)
	{
		for (Item item : player.getInventory().getItems())
		{
			if (item.hasAttributes())
			{
				_items.add(item);
			}
		}
	}
	
	@Override
	public void writeImpl(GameClient client, WritableBuffer buffer)
	{
		ServerPackets.EX_SHOW_BASE_ATTRIBUTE_CANCEL_WINDOW.writeId(this, buffer);
		buffer.writeInt(_items.size());
		for (Item item : _items)
		{
			buffer.writeInt(item.getObjectId());
			buffer.writeLong(getPrice(item));
		}
	}
	
	/**
	 * TODO: Unhardcode! Update prices for Top/Mid/Low S80/S84
	 * @param item
	 * @return
	 */
	private long getPrice(Item item)
	{
		switch (item.getTemplate().getCrystalType())
		{
			case S:
			{
				if (item.isWeapon())
				{
					_price = 50000;
				}
				else
				{
					_price = 40000;
				}
				break;
			}
			case S80:
			{
				if (item.isWeapon())
				{
					_price = 100000;
				}
				else
				{
					_price = 80000;
				}
				break;
			}
			case S84:
			{
				if (item.isWeapon())
				{
					_price = 200000;
				}
				else
				{
					_price = 160000;
				}
				break;
			}
		}
		return _price;
	}
}
