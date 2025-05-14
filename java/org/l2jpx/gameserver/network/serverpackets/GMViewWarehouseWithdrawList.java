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

import java.util.Collection;

import org.l2jpx.commons.network.WritableBuffer;
import org.l2jpx.gameserver.model.actor.Player;
import org.l2jpx.gameserver.model.clan.Clan;
import org.l2jpx.gameserver.model.item.instance.Item;
import org.l2jpx.gameserver.network.GameClient;
import org.l2jpx.gameserver.network.ServerPackets;

public class GMViewWarehouseWithdrawList extends AbstractItemPacket
{
	private final Collection<Item> _items;
	private final String _playerName;
	private final long _money;
	
	public GMViewWarehouseWithdrawList(Player player)
	{
		_items = player.getWarehouse().getItems();
		_playerName = player.getName();
		_money = player.getWarehouse().getAdena();
	}
	
	public GMViewWarehouseWithdrawList(Clan clan)
	{
		_playerName = clan.getLeaderName();
		_items = clan.getWarehouse().getItems();
		_money = clan.getWarehouse().getAdena();
	}
	
	@Override
	public void writeImpl(GameClient client, WritableBuffer buffer)
	{
		ServerPackets.GM_VIEW_WAREHOUSE_WITHDRAW_LIST.writeId(this, buffer);
		buffer.writeString(_playerName);
		buffer.writeLong(_money);
		buffer.writeShort(_items.size());
		for (Item item : _items)
		{
			writeItem(item, buffer);
			buffer.writeInt(item.getObjectId());
		}
	}
}
