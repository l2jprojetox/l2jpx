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
import org.l2jpx.gameserver.enums.AttributeType;
import org.l2jpx.gameserver.enums.ItemListType;
import org.l2jpx.gameserver.model.ItemInfo;
import org.l2jpx.gameserver.model.TradeItem;
import org.l2jpx.gameserver.model.buylist.Product;
import org.l2jpx.gameserver.model.item.WarehouseItem;
import org.l2jpx.gameserver.model.item.instance.Item;
import org.l2jpx.gameserver.model.itemcontainer.PlayerInventory;

/**
 * @author UnAfraid
 */
public abstract class AbstractItemPacket extends AbstractMaskPacket<ItemListType>
{
	private static final byte[] MASKS =
	{
		0x00
	};
	
	@Override
	protected byte[] getMasks()
	{
		return MASKS;
	}
	
	protected void writeItem(TradeItem item, WritableBuffer buffer)
	{
		writeItem(new ItemInfo(item), buffer);
	}
	
	protected void writeItem(WarehouseItem item, WritableBuffer buffer)
	{
		writeItem(new ItemInfo(item), buffer);
	}
	
	protected void writeItem(Item item, WritableBuffer buffer)
	{
		writeItem(new ItemInfo(item), buffer);
	}
	
	protected void writeItem(Product item, WritableBuffer buffer)
	{
		writeItem(new ItemInfo(item), buffer);
	}
	
	protected void writeItem(ItemInfo item, WritableBuffer buffer)
	{
		final int mask = calculateMask(item);
		// cddcQcchQccddc
		buffer.writeByte(mask);
		buffer.writeInt(item.getObjectId()); // ObjectId
		buffer.writeInt(item.getItem().getDisplayId()); // ItemId
		buffer.writeByte(item.getItem().isQuestItem() || (item.getEquipped() == 1) ? 0xFF : item.getLocation()); // T1
		buffer.writeLong(item.getCount()); // Quantity
		buffer.writeByte(item.getItem().getType2()); // Item Type 2 : 00-weapon, 01-shield/armor, 02-ring/earring/necklace, 03-questitem, 04-adena, 05-item
		buffer.writeByte(item.getCustomType1()); // Filler (always 0)
		buffer.writeShort(item.getEquipped()); // Equipped : 00-No, 01-yes
		buffer.writeLong(item.getItem().getBodyPart()); // Slot : 0006-lr.ear, 0008-neck, 0030-lr.finger, 0040-head, 0100-l.hand, 0200-gloves, 0400-chest, 0800-pants, 1000-feet, 4000-r.hand, 8000-r.hand
		buffer.writeByte(item.getEnchantLevel()); // Enchant level (pet level shown in control item)
		buffer.writeByte(item.getCustomType2()); // Pet name exists or not shown in control item
		buffer.writeInt(item.getMana());
		buffer.writeInt(item.getTime());
		buffer.writeByte(item.isAvailable()); // GOD Item enabled = 1 disabled (red) = 0
		if (containsMask(mask, ItemListType.AUGMENT_BONUS))
		{
			writeItemAugment(item, buffer);
		}
		if (containsMask(mask, ItemListType.ELEMENTAL_ATTRIBUTE))
		{
			writeItemElemental(item, buffer);
		}
		if (containsMask(mask, ItemListType.ENCHANT_EFFECT))
		{
			writeItemEnchantEffect(item, buffer);
		}
		if (containsMask(mask, ItemListType.VISUAL_ID))
		{
			buffer.writeInt(item.getVisualId()); // Item remodel visual ID
		}
	}
	
	protected static int calculateMask(ItemInfo item)
	{
		int mask = 0;
		if (item.getAugmentation() != null)
		{
			mask |= ItemListType.AUGMENT_BONUS.getMask();
		}
		if ((item.getAttackElementType() >= 0) || (item.getAttributeDefence(AttributeType.FIRE) > 0) || (item.getAttributeDefence(AttributeType.WATER) > 0) || (item.getAttributeDefence(AttributeType.WIND) > 0) || (item.getAttributeDefence(AttributeType.EARTH) > 0) || (item.getAttributeDefence(AttributeType.HOLY) > 0) || (item.getAttributeDefence(AttributeType.DARK) > 0))
		{
			mask |= ItemListType.ELEMENTAL_ATTRIBUTE.getMask();
		}
		if (item.getEnchantOptions() != null)
		{
			for (int id : item.getEnchantOptions())
			{
				if (id > 0)
				{
					mask |= ItemListType.ENCHANT_EFFECT.getMask();
					break;
				}
			}
		}
		if (item.getVisualId() > 0)
		{
			mask |= ItemListType.VISUAL_ID.getMask();
		}
		return mask;
	}
	
	protected void writeItemAugment(ItemInfo item, WritableBuffer buffer)
	{
		if ((item != null) && (item.getAugmentation() != null))
		{
			buffer.writeInt(item.getAugmentation().getOption1Id());
			buffer.writeInt(item.getAugmentation().getOption2Id());
		}
		else
		{
			buffer.writeInt(0);
			buffer.writeInt(0);
		}
	}
	
	protected void writeItemElementalAndEnchant(ItemInfo item, WritableBuffer buffer)
	{
		writeItemElemental(item, buffer);
		writeItemEnchantEffect(item, buffer);
	}
	
	protected void writeItemElemental(ItemInfo item, WritableBuffer buffer)
	{
		if (item != null)
		{
			buffer.writeShort(item.getAttackElementType());
			buffer.writeShort(item.getAttackElementPower());
			buffer.writeShort(item.getAttributeDefence(AttributeType.FIRE));
			buffer.writeShort(item.getAttributeDefence(AttributeType.WATER));
			buffer.writeShort(item.getAttributeDefence(AttributeType.WIND));
			buffer.writeShort(item.getAttributeDefence(AttributeType.EARTH));
			buffer.writeShort(item.getAttributeDefence(AttributeType.HOLY));
			buffer.writeShort(item.getAttributeDefence(AttributeType.DARK));
		}
		else
		{
			buffer.writeShort(0);
			buffer.writeShort(0);
			buffer.writeShort(0);
			buffer.writeShort(0);
			buffer.writeShort(0);
			buffer.writeShort(0);
			buffer.writeShort(0);
			buffer.writeShort(0);
		}
	}
	
	protected void writeItemEnchantEffect(ItemInfo item, WritableBuffer buffer)
	{
		// Enchant Effects
		for (int op : item.getEnchantOptions())
		{
			buffer.writeInt(op);
		}
	}
	
	protected void writeInventoryBlock(PlayerInventory inventory, WritableBuffer buffer)
	{
		if (inventory.hasInventoryBlock())
		{
			buffer.writeShort(inventory.getBlockItems().size());
			buffer.writeByte(inventory.getBlockMode().getClientId());
			for (int id : inventory.getBlockItems())
			{
				buffer.writeInt(id);
			}
		}
		else
		{
			buffer.writeShort(0);
		}
	}
}
