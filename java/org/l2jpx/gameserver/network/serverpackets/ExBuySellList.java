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

import org.l2jpx.Config;
import org.l2jpx.commons.network.WritableBuffer;
import org.l2jpx.gameserver.model.actor.Player;
import org.l2jpx.gameserver.model.actor.Summon;
import org.l2jpx.gameserver.model.item.instance.Item;
import org.l2jpx.gameserver.network.GameClient;
import org.l2jpx.gameserver.network.ServerPackets;

/**
 * @author ShanSoft
 */
public class ExBuySellList extends AbstractItemPacket
{
	private final List<Item> _sellList = new ArrayList<>();
	private Collection<Item> _refundList = null;
	private final boolean _done;
	private final int _inventorySlots;
	
	public ExBuySellList(Player player, boolean done)
	{
		final Summon pet = player.getPet();
		for (Item item : player.getInventory().getItems())
		{
			if (!item.isEquipped() && item.isSellable() && ((pet == null) || (item.getObjectId() != pet.getControlObjectId())))
			{
				_sellList.add(item);
			}
		}
		_inventorySlots = player.getInventory().getNonQuestSize();
		if (player.hasRefund())
		{
			_refundList = player.getRefund().getItems();
		}
		_done = done;
	}
	
	@Override
	public void writeImpl(GameClient client, WritableBuffer buffer)
	{
		ServerPackets.EX_BUY_SELL_LIST.writeId(this, buffer);
		buffer.writeInt(1); // Type SELL
		buffer.writeInt(_inventorySlots);
		if ((_sellList != null))
		{
			buffer.writeShort(_sellList.size());
			for (Item item : _sellList)
			{
				writeItem(item, buffer);
				buffer.writeLong(Config.MERCHANT_ZERO_SELL_PRICE ? 0 : item.getTemplate().getReferencePrice() / 2);
			}
		}
		else
		{
			buffer.writeShort(0);
		}
		if ((_refundList != null) && !_refundList.isEmpty())
		{
			buffer.writeShort(_refundList.size());
			int i = 0;
			for (Item item : _refundList)
			{
				writeItem(item, buffer);
				buffer.writeInt(i++);
				buffer.writeLong(Config.MERCHANT_ZERO_SELL_PRICE ? 0 : (item.getTemplate().getReferencePrice() / 2) * item.getCount());
			}
		}
		else
		{
			buffer.writeShort(0);
		}
		buffer.writeByte(_done);
	}
}
