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
package org.l2jpx.gameserver.model.stats;

import java.util.OptionalDouble;

import org.l2jpx.Config;
import org.l2jpx.gameserver.enums.PlayerCondOverride;
import org.l2jpx.gameserver.model.actor.Creature;
import org.l2jpx.gameserver.model.actor.instance.Pet;
import org.l2jpx.gameserver.model.actor.transform.TransformType;
import org.l2jpx.gameserver.model.item.ItemTemplate;
import org.l2jpx.gameserver.model.item.instance.Item;
import org.l2jpx.gameserver.model.item.type.CrystalType;
import org.l2jpx.gameserver.model.item.type.WeaponType;
import org.l2jpx.gameserver.model.itemcontainer.Inventory;

/**
 * @author UnAfraid
 */
@FunctionalInterface
public interface IStatFunction
{
	default void throwIfPresent(OptionalDouble base)
	{
		if (base.isPresent())
		{
			throw new IllegalArgumentException("base should not be set for " + getClass().getSimpleName());
		}
	}
	
	default double calcEnchantBodyPart(Creature creature, int... slots)
	{
		double value = 0;
		for (int slot : slots)
		{
			final Item item = creature.getInventory().getPaperdollItemByItemId(slot);
			if ((item != null) && (item.getEnchantLevel() >= 4) && (item.getTemplate().getCrystalTypePlus() == CrystalType.R))
			{
				value += calcEnchantBodyPartBonus(item.getEnchantLevel(), item.getTemplate().isBlessed());
			}
		}
		return value;
	}
	
	default double calcEnchantBodyPartBonus(int enchantLevel, boolean isBlessed)
	{
		return 0;
	}
	
	default double calcWeaponBaseValue(Creature creature, Stat stat)
	{
		final double baseTemplateValue = creature.getTemplate().getBaseValue(stat, 0);
		double baseValue = creature.getTransformation().map(transform -> transform.getStats(creature, stat, baseTemplateValue)).orElse(baseTemplateValue);
		if (creature.isPet())
		{
			final Pet pet = (Pet) creature;
			final Item weapon = pet.getActiveWeaponInstance();
			final double baseVal = stat == Stat.PHYSICAL_ATTACK ? pet.getPetLevelData().getPetPAtk() : stat == Stat.MAGIC_ATTACK ? pet.getPetLevelData().getPetMAtk() : baseTemplateValue;
			baseValue = baseVal + (weapon != null ? weapon.getTemplate().getStats(stat, baseVal) : 0);
		}
		else if (creature.isPlayer() && (!creature.isTransformed() || (creature.getTransformation().get().getType() == TransformType.COMBAT) || (creature.getTransformation().get().getType() == TransformType.MODE_CHANGE)))
		{
			final Item weapon = creature.getActiveWeaponInstance();
			baseValue = (weapon != null ? weapon.getTemplate().getStats(stat, baseTemplateValue) : baseTemplateValue);
		}
		
		return baseValue;
	}
	
	default double calcWeaponPlusBaseValue(Creature creature, Stat stat)
	{
		final double baseTemplateValue = creature.getTemplate().getBaseValue(stat, 0);
		double baseValue = creature.getTransformation().filter(transform -> !transform.isStance()).map(transform -> transform.getStats(creature, stat, baseTemplateValue)).orElse(baseTemplateValue);
		
		if (creature.isPlayable())
		{
			final Inventory inv = creature.getInventory();
			if (inv != null)
			{
				baseValue += inv.getPaperdollCache().getStats(stat);
			}
		}
		
		return baseValue;
	}
	
	default double calcEnchantedItemBonus(Creature creature, Stat stat)
	{
		if (!creature.isPlayer())
		{
			return 0;
		}
		
		double value = 0;
		for (Item equippedItem : creature.getInventory().getPaperdollItems(Item::isEnchanted))
		{
			final ItemTemplate item = equippedItem.getTemplate();
			final int bodypart = item.getBodyPart();
			if ((bodypart == ItemTemplate.SLOT_HAIR) || //
				(bodypart == ItemTemplate.SLOT_HAIR2) || //
				(bodypart == ItemTemplate.SLOT_HAIRALL))
			{
				// TODO: Item after enchant shows pDef, but scroll says mDef increase.
				if ((stat != Stat.PHYSICAL_DEFENCE) && (stat != Stat.MAGICAL_DEFENCE))
				{
					continue;
				}
			}
			else if (item.getStats(stat, 0) <= 0)
			{
				continue;
			}
			
			final double blessedBonus = item.isBlessed() ? 1.5 : 1;
			int enchant = equippedItem.getEnchantLevel();
			
			if (creature.getActingPlayer().isInOlympiadMode())
			{
				if (item.isWeapon())
				{
					if ((Config.OLYMPIAD_WEAPON_ENCHANT_LIMIT >= 0) && (enchant > Config.OLYMPIAD_WEAPON_ENCHANT_LIMIT))
					{
						enchant = Config.OLYMPIAD_WEAPON_ENCHANT_LIMIT;
					}
				}
				else
				{
					if ((Config.OLYMPIAD_ARMOR_ENCHANT_LIMIT >= 0) && (enchant > Config.OLYMPIAD_ARMOR_ENCHANT_LIMIT))
					{
						enchant = Config.OLYMPIAD_ARMOR_ENCHANT_LIMIT;
					}
				}
			}
			
			if ((stat == Stat.MAGICAL_DEFENCE) || (stat == Stat.PHYSICAL_DEFENCE))
			{
				value += calcEnchantDefBonus(equippedItem, blessedBonus, enchant);
			}
			else if (stat == Stat.MAGIC_ATTACK)
			{
				value += calcEnchantMatkBonus(equippedItem, blessedBonus, enchant);
			}
			else if ((stat == Stat.PHYSICAL_ATTACK) && equippedItem.isWeapon())
			{
				value += calcEnchantedPAtkBonus(equippedItem, blessedBonus, enchant);
			}
		}
		return value;
	}
	
	/**
	 * @param item
	 * @param blessedBonus
	 * @param enchant
	 * @return
	 */
	static double calcEnchantDefBonus(Item item, double blessedBonus, int enchant)
	{
		switch (item.getTemplate().getCrystalTypePlus())
		{
			case R:
			{
				return ((2 * blessedBonus * enchant) + (6 * blessedBonus * Math.max(0, enchant - 3)));
			}
			default:
			{
				return enchant + (3 * Math.max(0, enchant - 3));
			}
		}
	}
	
	/**
	 * @param item
	 * @param blessedBonus
	 * @param enchant
	 * @return
	 */
	static double calcEnchantMatkBonus(Item item, double blessedBonus, int enchant)
	{
		switch (item.getTemplate().getCrystalTypePlus())
		{
			case R:
			{
				return ((5 * blessedBonus * enchant) + (10 * blessedBonus * Math.max(0, enchant - 3)));
			}
			case S:
			{
				// M. Atk. increases by 4 for all weapons.
				// Starting at +4, M. Atk. bonus double.
				return (4 * enchant) + (8 * Math.max(0, enchant - 3));
			}
			case A:
			case B:
			case C:
			{
				// M. Atk. increases by 3 for all weapons.
				// Starting at +4, M. Atk. bonus double.
				return (3 * enchant) + (6 * Math.max(0, enchant - 3));
			}
			default:
			{
				// M. Atk. increases by 2 for all weapons. Starting at +4, M. Atk. bonus double.
				// Starting at +4, M. Atk. bonus double.
				return (2 * enchant) + (4 * Math.max(0, enchant - 3));
			}
		}
	}
	
	/**
	 * @param item
	 * @param blessedBonus
	 * @param enchant
	 * @return
	 */
	static double calcEnchantedPAtkBonus(Item item, double blessedBonus, int enchant)
	{
		switch (item.getTemplate().getCrystalTypePlus())
		{
			case R:
			{
				if ((item.getWeaponItem().getBodyPart() == ItemTemplate.SLOT_LR_HAND) && (item.getWeaponItem().getItemType() != WeaponType.POLE))
				{
					if (item.getWeaponItem().getItemType().isRanged())
					{
						return (12 * blessedBonus * enchant) + (24 * blessedBonus * Math.max(0, enchant - 3));
					}
					return (7 * blessedBonus * enchant) + (14 * blessedBonus * Math.max(0, enchant - 3));
				}
				return (6 * blessedBonus * enchant) + (12 * blessedBonus * Math.max(0, enchant - 3));
			}
			case S:
			{
				if ((item.getWeaponItem().getBodyPart() == ItemTemplate.SLOT_LR_HAND) && (item.getWeaponItem().getItemType() != WeaponType.POLE))
				{
					if (item.getWeaponItem().getItemType().isRanged())
					{
						// P. Atk. increases by 10 for bows.
						// Starting at +4, P. Atk. bonus double.
						return (10 * enchant) + (20 * Math.max(0, enchant - 3));
					}
					// P. Atk. increases by 6 for two-handed swords, two-handed blunts, dualswords, and two-handed combat weapons.
					// Starting at +4, P. Atk. bonus double.
					return (6 * enchant) + (12 * Math.max(0, enchant - 3));
				}
				// P. Atk. increases by 5 for one-handed swords, one-handed blunts, daggers, spears, and other weapons.
				// Starting at +4, P. Atk. bonus double.
				return (5 * enchant) + (10 * Math.max(0, enchant - 3));
			}
			case A:
			{
				if ((item.getWeaponItem().getBodyPart() == ItemTemplate.SLOT_LR_HAND) && (item.getWeaponItem().getItemType() != WeaponType.POLE))
				{
					if (item.getWeaponItem().getItemType().isRanged())
					{
						// P. Atk. increases by 8 for bows.
						// Starting at +4, P. Atk. bonus double.
						return (8 * enchant) + (16 * Math.max(0, enchant - 3));
					}
					// P. Atk. increases by 5 for two-handed swords, two-handed blunts, dualswords, and two-handed combat weapons.
					// Starting at +4, P. Atk. bonus double.
					return (5 * enchant) + (10 * Math.max(0, enchant - 3));
				}
				// P. Atk. increases by 4 for one-handed swords, one-handed blunts, daggers, spears, and other weapons.
				// Starting at +4, P. Atk. bonus double.
				return (4 * enchant) + (8 * Math.max(0, enchant - 3));
			}
			case B:
			case C:
			{
				if ((item.getWeaponItem().getBodyPart() == ItemTemplate.SLOT_LR_HAND) && (item.getWeaponItem().getItemType() != WeaponType.POLE))
				{
					if (item.getWeaponItem().getItemType().isRanged())
					{
						// P. Atk. increases by 6 for bows.
						// Starting at +4, P. Atk. bonus double.
						return (6 * enchant) + (12 * Math.max(0, enchant - 3));
					}
					// P. Atk. increases by 4 for two-handed swords, two-handed blunts, dualswords, and two-handed combat weapons.
					// Starting at +4, P. Atk. bonus double.
					return (4 * enchant) + (8 * Math.max(0, enchant - 3));
				}
				// P. Atk. increases by 3 for one-handed swords, one-handed blunts, daggers, spears, and other weapons.
				// Starting at +4, P. Atk. bonus double.
				return (3 * enchant) + (6 * Math.max(0, enchant - 3));
			}
			default:
			{
				if (item.getWeaponItem().getItemType().isRanged())
				{
					// Bows increase by 4.
					// Starting at +4, P. Atk. bonus double.
					return (4 * enchant) + (8 * Math.max(0, enchant - 3));
				}
				// P. Atk. increases by 2 for all weapons with the exception of bows.
				// Starting at +4, P. Atk. bonus double.
				return (2 * enchant) + (4 * Math.max(0, enchant - 3));
			}
		}
	}
	
	default double validateValue(Creature creature, double value, double minValue, double maxValue)
	{
		if ((value > maxValue) && !creature.canOverrideCond(PlayerCondOverride.MAX_STATS_VALUE))
		{
			return maxValue;
		}
		
		return Math.max(minValue, value);
	}
	
	double calc(Creature creature, OptionalDouble base, Stat stat);
}
