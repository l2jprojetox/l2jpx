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
package org.l2jpx.gameserver.model;

import java.util.Objects;

import org.l2jpx.gameserver.enums.AttributeType;
import org.l2jpx.gameserver.model.buylist.Product;
import org.l2jpx.gameserver.model.item.ItemTemplate;
import org.l2jpx.gameserver.model.item.WarehouseItem;
import org.l2jpx.gameserver.model.item.instance.Item;

/**
 * Get all information from Item to generate ItemInfo.
 */
public class ItemInfo
{
	/** Identifier of the Item */
	private int _objectId;
	
	/** The Item template of the Item */
	private ItemTemplate _item;
	
	/** The level of enchant on the Item */
	private int _enchantLevel;
	
	/** The augmentation of the item */
	private VariationInstance _augmentation;
	
	/** The quantity of Item */
	private long _count;
	
	/** The price of the Item */
	private int _price;
	
	/** The custom Item types (used loto, race tickets) */
	private int _type1;
	private int _type2;
	
	/** If True the Item is equipped */
	private int _equipped;
	
	/** The action to do clientside (1=ADD, 2=MODIFY, 3=REMOVE) */
	private int _change;
	
	/** The mana of this item */
	private int _mana;
	private int _time;
	
	private boolean _available = true;
	
	private int _location;
	
	private byte _elemAtkType = -2;
	private int _elemAtkPower = 0;
	private final int[] _attributeDefence =
	{
		0,
		0,
		0,
		0,
		0,
		0
	};
	
	private int[] _option;
	private int _visualId;
	private long _visualExpiration;
	
	/**
	 * Get all information from Item to generate ItemInfo.
	 * @param item
	 */
	public ItemInfo(Item item)
	{
		Objects.requireNonNull(item);
		
		// Get the Identifier of the Item
		_objectId = item.getObjectId();
		
		// Get the Item of the Item
		_item = item.getTemplate();
		
		// Get the enchant level of the Item
		_enchantLevel = item.getEnchantLevel();
		
		// Get the augmentation bonus
		_augmentation = item.getAugmentation();
		
		// Get the quantity of the Item
		_count = item.getCount();
		
		// Get custom item types (used loto, race tickets)
		_type1 = item.getCustomType1();
		_type2 = item.getCustomType2();
		
		// Verify if the Item is equipped
		_equipped = item.isEquipped() ? 1 : 0;
		
		// Get the action to do clientside
		switch (item.getLastChange())
		{
			case Item.ADDED:
			{
				_change = 1;
				break;
			}
			case Item.MODIFIED:
			{
				_change = 2;
				break;
			}
			case Item.REMOVED:
			{
				_change = 3;
				break;
			}
		}
		
		// Get shadow item mana
		_mana = item.getMana();
		_time = item.isTimeLimitedItem() ? (int) (item.getRemainingTime() / 1000) : -9999;
		_available = item.isAvailable();
		_location = item.getLocationSlot();
		_elemAtkType = item.getAttackAttributeType().getClientId();
		_elemAtkPower = item.getAttackAttributePower();
		for (AttributeType type : AttributeType.ATTRIBUTE_TYPES)
		{
			_attributeDefence[type.getClientId()] = item.getDefenceAttribute(type);
		}
		_option = item.getEnchantOptions();
		_visualId = item.getVisualId();
		_visualExpiration = item.getVisualLifeTime() > 0 ? (item.getVisualLifeTime() - System.currentTimeMillis()) / 1000 : 0;
	}
	
	public ItemInfo(Item item, int change)
	{
		this(item);
		_change = change;
		_visualExpiration = item.getVisualLifeTime() > 0 ? (item.getVisualLifeTime() - System.currentTimeMillis()) / 1000 : 0;
	}
	
	public ItemInfo(TradeItem item)
	{
		if (item == null)
		{
			return;
		}
		
		// Get the Identifier of the Item
		_objectId = item.getObjectId();
		
		// Get the Item of the Item
		_item = item.getItem();
		
		// Get the enchant level of the Item
		_enchantLevel = item.getEnchant();
		
		// Get the augmentation bonus
		if ((item.getAugmentationOption1() >= 0) && (item.getAugmentationOption2() >= 0))
		{
			_augmentation = new VariationInstance(0, item.getAugmentationOption1(), item.getAugmentationOption2());
		}
		
		// Get the quantity of the Item
		_count = item.getCount();
		
		// Get custom item types (used loto, race tickets)
		_type1 = item.getCustomType1();
		_type2 = item.getCustomType2();
		
		// Verify if the Item is equipped
		_equipped = 0;
		
		// Get the action to do clientside
		_change = 0;
		
		// Get shadow item mana
		_mana = -1;
		_time = -9999;
		_location = item.getLocationSlot();
		_elemAtkType = item.getAttackElementType();
		_elemAtkPower = item.getAttackElementPower();
		for (byte i = 0; i < 6; i++)
		{
			_attributeDefence[i] = item.getElementDefAttr(i);
		}
		
		_option = item.getEnchantOptions();
		_visualId = item.getVisualId();
	}
	
	public ItemInfo(Product item)
	{
		if (item == null)
		{
			return;
		}
		
		// Get the Identifier of the Item
		_objectId = 0;
		
		// Get the Item of the Item
		_item = item.getItem();
		
		// Get the enchant level of the Item
		_enchantLevel = 0;
		
		// Get the augmentation bonus
		_augmentation = null;
		
		// Get the quantity of the Item
		_count = item.getCount();
		
		// Get custom item types (used loto, race tickets)
		_type1 = item.getItem().getType1();
		_type2 = item.getItem().getType2();
		
		// Verify if the Item is equipped
		_equipped = 0;
		
		// Get the action to do clientside
		_change = 0;
		
		// Get shadow item mana
		_mana = -1;
		_time = -9999;
		_location = 0;
	}
	
	public ItemInfo(WarehouseItem item)
	{
		if (item == null)
		{
			return;
		}
		
		// Get the Identifier of the Item
		_objectId = item.getObjectId();
		
		// Get the Item of the Item
		_item = item.getItem();
		
		// Get the enchant level of the Item
		_enchantLevel = item.getEnchantLevel();
		
		// Get the augmentation bonus
		_augmentation = item.getAugmentation();
		
		// Get the quantity of the Item
		_count = item.getCount();
		
		// Get custom item types (used loto, race tickets)
		_type1 = item.getCustomType1();
		_type2 = item.getCustomType2();
		
		// Verify if the Item is equipped
		_equipped = 0;
		
		// Get shadow item mana
		_mana = item.getMana();
		_time = item.getTime();
		_location = item.getLocationSlot();
		_elemAtkType = item.getAttackElementType();
		_elemAtkPower = item.getAttackElementPower();
		for (byte i = 0; i < 6; i++)
		{
			_attributeDefence[i] = item.getElementDefAttr(i);
		}
		_option = item.getEnchantOptions();
	}
	
	public int getObjectId()
	{
		return _objectId;
	}
	
	public ItemTemplate getItem()
	{
		return _item;
	}
	
	public int getEnchantLevel()
	{
		return _enchantLevel;
	}
	
	public VariationInstance getAugmentation()
	{
		return _augmentation;
	}
	
	public long getCount()
	{
		return _count;
	}
	
	public int getPrice()
	{
		return _price;
	}
	
	public int getCustomType1()
	{
		return _type1;
	}
	
	public int getCustomType2()
	{
		return _type2;
	}
	
	public int getEquipped()
	{
		return _equipped;
	}
	
	public int getChange()
	{
		return _change;
	}
	
	public int getMana()
	{
		return _mana;
	}
	
	public int getTime()
	{
		return _time > 0 ? _time : _visualExpiration > 0 ? (int) _visualExpiration : -9999;
	}
	
	public boolean isAvailable()
	{
		return _available;
	}
	
	public int getLocation()
	{
		return _location;
	}
	
	public int getAttackElementType()
	{
		return _elemAtkType;
	}
	
	public int getAttackElementPower()
	{
		return _elemAtkPower;
	}
	
	public int getAttributeDefence(AttributeType attribute)
	{
		return _attributeDefence[attribute.getClientId()];
	}
	
	public int[] getEnchantOptions()
	{
		return _option;
	}
	
	public int getVisualId()
	{
		return _visualId;
	}
	
	public long getVisualExpiration()
	{
		return _visualExpiration;
	}
	
	@Override
	public String toString()
	{
		return String.valueOf(_item) + "[objId: " + _objectId + ", count: " + _count + "]";
	}
}
