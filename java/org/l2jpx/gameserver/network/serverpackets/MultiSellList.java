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

import static org.l2jpx.gameserver.data.xml.MultisellData.PAGE_SIZE;

import org.l2jpx.commons.network.WritableBuffer;
import org.l2jpx.gameserver.data.xml.ItemData;
import org.l2jpx.gameserver.model.ItemInfo;
import org.l2jpx.gameserver.model.actor.Player;
import org.l2jpx.gameserver.model.holders.ItemChanceHolder;
import org.l2jpx.gameserver.model.holders.MultisellEntryHolder;
import org.l2jpx.gameserver.model.holders.PreparedMultisellListHolder;
import org.l2jpx.gameserver.model.item.ItemTemplate;
import org.l2jpx.gameserver.model.item.instance.Item;
import org.l2jpx.gameserver.network.GameClient;
import org.l2jpx.gameserver.network.ServerPackets;

public class MultiSellList extends AbstractItemPacket
{
	private final Player _player;
	private int _size;
	private int _index;
	private final PreparedMultisellListHolder _list;
	private final boolean _finished;
	
	public MultiSellList(Player player, PreparedMultisellListHolder list, int index)
	{
		_player = player;
		_list = list;
		_index = index;
		_size = list.getEntries().size() - index;
		if (_size > PAGE_SIZE)
		{
			_finished = false;
			_size = PAGE_SIZE;
		}
		else
		{
			_finished = true;
		}
	}
	
	@Override
	public void writeImpl(GameClient client, WritableBuffer buffer)
	{
		ServerPackets.MULTI_SELL_LIST.writeId(this, buffer);
		buffer.writeByte(0); // Helios
		buffer.writeInt(_list.getId()); // list id
		buffer.writeByte(0); // GOD Unknown
		buffer.writeInt(1 + (_index / PAGE_SIZE)); // page started from 1
		buffer.writeInt(_finished); // finished
		buffer.writeInt(PAGE_SIZE); // size of pages
		buffer.writeInt(_size); // list length
		buffer.writeByte(0); // Grand Crusade
		buffer.writeByte(_list.isChanceMultisell()); // new multisell window
		buffer.writeInt(32); // Helios - Always 32
		while (_size-- > 0)
		{
			ItemInfo itemEnchantment = _list.getItemEnchantment(_index);
			final MultisellEntryHolder entry = _list.getEntries().get(_index++);
			if ((itemEnchantment == null) && _list.isMaintainEnchantment())
			{
				SEARCH: for (ItemChanceHolder holder : entry.getIngredients())
				{
					final Item item = _player.getInventory().getItemByItemId(holder.getId());
					if ((item != null) && item.isEquipable())
					{
						itemEnchantment = new ItemInfo(item);
						break SEARCH;
					}
				}
			}
			buffer.writeInt(_index); // Entry ID. Start from 1.
			buffer.writeByte(entry.isStackable());
			// Those values will be passed down to MultiSellChoose packet.
			buffer.writeShort(itemEnchantment != null ? itemEnchantment.getEnchantLevel() : 0); // enchant level
			writeItemAugment(itemEnchantment, buffer);
			writeItemElemental(itemEnchantment, buffer);
			buffer.writeByte(0);
			buffer.writeByte(0);
			buffer.writeShort(entry.getProducts().size());
			buffer.writeShort(entry.getIngredients().size());
			for (ItemChanceHolder product : entry.getProducts())
			{
				final ItemTemplate template = ItemData.getInstance().getTemplate(product.getId());
				final ItemInfo displayItemEnchantment = _list.isMaintainEnchantment() && (itemEnchantment != null) && (template != null) && template.getClass().equals(itemEnchantment.getItem().getClass()) ? itemEnchantment : null;
				if (template != null)
				{
					buffer.writeInt(template.getDisplayId());
					buffer.writeLong(template.getBodyPart());
					buffer.writeShort(template.getType2());
				}
				else
				{
					buffer.writeInt(product.getId());
					buffer.writeLong(0);
					buffer.writeShort(65535);
				}
				buffer.writeLong(_list.getProductCount(product));
				buffer.writeShort(product.getEnchantmentLevel() > 0 ? product.getEnchantmentLevel() : displayItemEnchantment != null ? displayItemEnchantment.getEnchantLevel() : 0); // enchant level
				buffer.writeInt((int) Math.ceil(product.getChance())); // chance
				writeItemAugment(displayItemEnchantment, buffer);
				writeItemElemental(displayItemEnchantment, buffer);
				buffer.writeByte(0);
				buffer.writeByte(0);
			}
			for (ItemChanceHolder ingredient : entry.getIngredients())
			{
				final ItemTemplate template = ItemData.getInstance().getTemplate(ingredient.getId());
				final ItemInfo displayItemEnchantment = (itemEnchantment != null) && (template != null) && template.getClass().equals(itemEnchantment.getItem().getClass()) ? itemEnchantment : null;
				if (template != null)
				{
					buffer.writeInt(template.getDisplayId());
					buffer.writeShort(template.getType2());
				}
				else
				{
					buffer.writeInt(ingredient.getId());
					buffer.writeShort(65535);
				}
				buffer.writeLong(_list.getIngredientCount(ingredient));
				buffer.writeShort(ingredient.getEnchantmentLevel() > 0 ? ingredient.getEnchantmentLevel() : displayItemEnchantment != null ? displayItemEnchantment.getEnchantLevel() : 0); // enchant level
				writeItemAugment(displayItemEnchantment, buffer);
				writeItemElemental(displayItemEnchantment, buffer);
				buffer.writeByte(0);
				buffer.writeByte(0);
			}
		}
	}
}