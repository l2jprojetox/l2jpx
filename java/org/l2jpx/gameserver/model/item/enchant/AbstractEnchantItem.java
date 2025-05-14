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
package org.l2jpx.gameserver.model.item.enchant;

import java.util.EnumSet;
import java.util.Set;
import java.util.logging.Logger;

import org.l2jpx.gameserver.data.xml.ItemData;
import org.l2jpx.gameserver.model.StatSet;
import org.l2jpx.gameserver.model.item.ItemTemplate;
import org.l2jpx.gameserver.model.item.instance.Item;
import org.l2jpx.gameserver.model.item.type.CrystalType;
import org.l2jpx.gameserver.model.item.type.EtcItemType;

/**
 * @author UnAfraid
 */
public abstract class AbstractEnchantItem
{
	protected static final Logger LOGGER = Logger.getLogger(AbstractEnchantItem.class.getName());
	
	private static final Set<EtcItemType> ENCHANT_TYPES = EnumSet.noneOf(EtcItemType.class);
	static
	{
		ENCHANT_TYPES.add(EtcItemType.ENCHT_ATTR_ANCIENT_CRYSTAL_ENCHANT_AM);
		ENCHANT_TYPES.add(EtcItemType.ENCHT_ATTR_ANCIENT_CRYSTAL_ENCHANT_WP);
		ENCHANT_TYPES.add(EtcItemType.BLESS_ENCHT_AM);
		ENCHANT_TYPES.add(EtcItemType.BLESS_ENCHT_AM_DOWN);
		ENCHANT_TYPES.add(EtcItemType.BLESS_ENCHT_WP);
		ENCHANT_TYPES.add(EtcItemType.ENCHT_AM);
		ENCHANT_TYPES.add(EtcItemType.ENCHT_WP);
		ENCHANT_TYPES.add(EtcItemType.GIANT_ENCHT_AM);
		ENCHANT_TYPES.add(EtcItemType.GIANT_ENCHT_WP);
		ENCHANT_TYPES.add(EtcItemType.ENCHT_ATTR_INC_PROP_ENCHT_AM);
		ENCHANT_TYPES.add(EtcItemType.ENCHT_ATTR_INC_PROP_ENCHT_WP);
		ENCHANT_TYPES.add(EtcItemType.GIANT_ENCHT_ATTR_INC_PROP_ENCHT_AM);
		ENCHANT_TYPES.add(EtcItemType.GIANT_ENCHT_ATTR_INC_PROP_ENCHT_WP);
		ENCHANT_TYPES.add(EtcItemType.BLESSED_ENCHT_ATTR_INC_PROP_ENCHT_AM);
		ENCHANT_TYPES.add(EtcItemType.BLESSED_ENCHT_ATTR_INC_PROP_ENCHT_WP);
		ENCHANT_TYPES.add(EtcItemType.BLESSED_GIANT_ENCHT_ATTR_INC_PROP_ENCHT_AM);
		ENCHANT_TYPES.add(EtcItemType.BLESSED_GIANT_ENCHT_ATTR_INC_PROP_ENCHT_WP);
	}
	
	private final int _id;
	private final CrystalType _grade;
	private final int _minEnchantLevel;
	private final int _maxEnchantLevel;
	private final int _safeEnchantLevel;
	private final int _randomEnchantMin;
	private final int _randomEnchantMax;
	private final double _bonusRate;
	
	public AbstractEnchantItem(StatSet set)
	{
		_id = set.getInt("id");
		if (getItem() == null)
		{
			throw new NullPointerException();
		}
		else if (!ENCHANT_TYPES.contains(getItem().getItemType()))
		{
			throw new IllegalAccessError();
		}
		_grade = set.getEnum("targetGrade", CrystalType.class, CrystalType.NONE);
		_minEnchantLevel = set.getInt("minEnchant", 0);
		_maxEnchantLevel = set.getInt("maxEnchant", 127);
		_safeEnchantLevel = set.getInt("safeEnchant", 0);
		_randomEnchantMin = set.getInt("randomEnchantMin", 1);
		_randomEnchantMax = set.getInt("randomEnchantMax", _randomEnchantMin);
		_bonusRate = set.getDouble("bonusRate", 0);
	}
	
	/**
	 * @return id of current item
	 */
	public int getId()
	{
		return _id;
	}
	
	/**
	 * @return bonus chance that would be added
	 */
	public double getBonusRate()
	{
		return _bonusRate;
	}
	
	/**
	 * @return {@link ItemTemplate} current item/scroll
	 */
	public ItemTemplate getItem()
	{
		return ItemData.getInstance().getTemplate(_id);
	}
	
	/**
	 * @return grade of the item/scroll.
	 */
	public CrystalType getGrade()
	{
		return _grade;
	}
	
	/**
	 * @return {@code true} if scroll is for weapon, {@code false} for armor
	 */
	public abstract boolean isWeapon();
	
	/**
	 * @return the minimum enchant level that this scroll/item can be used with
	 */
	public int getMinEnchantLevel()
	{
		return _minEnchantLevel;
	}
	
	/**
	 * @return the maximum enchant level that this scroll/item can be used with
	 */
	public int getMaxEnchantLevel()
	{
		return _maxEnchantLevel;
	}
	
	/**
	 * @return the safe enchant level of this scroll/item
	 */
	public int getSafeEnchant()
	{
		return _safeEnchantLevel;
	}
	
	/**
	 * @return the minimum random enchant level of this scroll/item
	 */
	public int getRandomEnchantMin()
	{
		return _randomEnchantMin;
	}
	
	/**
	 * @return the maximum random enchant level of this scroll/item
	 */
	public int getRandomEnchantMax()
	{
		return _randomEnchantMax;
	}
	
	/**
	 * @param itemToEnchant the item to be enchanted
	 * @param supportItem
	 * @return {@code true} if this support item can be used with the item to be enchanted, {@code false} otherwise
	 */
	public boolean isValid(Item itemToEnchant, EnchantSupportItem supportItem)
	{
		if (itemToEnchant == null)
		{
			return false;
		}
		else if (!itemToEnchant.isEnchantable() || (!(itemToEnchant.getTemplate().getEnchantLimit() == 0) && (itemToEnchant.getEnchantLevel() == itemToEnchant.getTemplate().getEnchantLimit())))
		{
			return false;
		}
		else if (!isValidItemType(itemToEnchant.getTemplate().getType2()))
		{
			return false;
		}
		else if (((_minEnchantLevel != 0) && (itemToEnchant.getEnchantLevel() < _minEnchantLevel)) || ((_maxEnchantLevel != 0) && (itemToEnchant.getEnchantLevel() >= _maxEnchantLevel)))
		{
			return false;
		}
		else if (_grade != itemToEnchant.getTemplate().getCrystalTypePlus())
		{
			return false;
		}
		return true;
	}
	
	/**
	 * @param type2
	 * @return {@code true} if current type2 is valid to be enchanted, {@code false} otherwise
	 */
	private boolean isValidItemType(int type2)
	{
		if (type2 == ItemTemplate.TYPE2_WEAPON)
		{
			return isWeapon();
		}
		else if ((type2 == ItemTemplate.TYPE2_SHIELD_ARMOR) || (type2 == ItemTemplate.TYPE2_ACCESSORY))
		{
			return !isWeapon();
		}
		return false;
	}
}
