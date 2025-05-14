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
import java.util.Collection;
import java.util.List;

import org.l2jpx.commons.network.WritableBuffer;
import org.l2jpx.gameserver.model.actor.Player;
import org.l2jpx.gameserver.model.item.instance.Item;
import org.l2jpx.gameserver.network.GameClient;
import org.l2jpx.gameserver.network.PacketLogger;
import org.l2jpx.gameserver.network.ServerPackets;

public class WareHouseWithdrawalList extends AbstractItemPacket
{
	public static final int PRIVATE = 1;
	public static final int CLAN = 2;
	public static final int CASTLE = 3; // not sure
	public static final int FREIGHT = 1;
	
	private Player _player;
	private long _playerAdena;
	private final int _invSize;
	private Collection<Item> _items;
	private final List<Integer> _itemsStackable = new ArrayList<>();
	/**
	 * <ul>
	 * <li>0x01-Private Warehouse</li>
	 * <li>0x02-Clan Warehouse</li>
	 * <li>0x03-Castle Warehouse</li>
	 * <li>0x04-Warehouse</li>
	 * </ul>
	 */
	private int _whType;
	
	public WareHouseWithdrawalList(Player player, int type)
	{
		_player = player;
		_whType = type;
		_playerAdena = _player.getAdena();
		_invSize = player.getInventory().getSize();
		if (_player.getActiveWarehouse() == null)
		{
			PacketLogger.warning("error while sending withdraw request to: " + _player.getName());
			return;
		}
		_items = _player.getActiveWarehouse().getItems();
		for (Item item : _items)
		{
			if (item.isStackable())
			{
				_itemsStackable.add(item.getDisplayId());
			}
		}
	}
	
	@Override
	public void writeImpl(GameClient client, WritableBuffer buffer)
	{
		ServerPackets.WAREHOUSE_WITHDRAW_LIST.writeId(this, buffer);
		buffer.writeShort(_whType);
		buffer.writeLong(_playerAdena);
		buffer.writeShort(_items.size());
		buffer.writeShort(_itemsStackable.size());
		for (int itemId : _itemsStackable)
		{
			buffer.writeInt(itemId);
		}
		buffer.writeInt(_invSize);
		for (Item item : _items)
		{
			writeItem(item, buffer);
			buffer.writeInt(item.getObjectId());
			buffer.writeInt(0);
			buffer.writeInt(0);
		}
	}
}
