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
package org.l2jpx.gameserver.model.stats.finalizers;

import java.util.OptionalDouble;

import org.l2jpx.Config;
import org.l2jpx.gameserver.model.actor.Creature;
import org.l2jpx.gameserver.model.item.ItemTemplate;
import org.l2jpx.gameserver.model.stats.BaseStat;
import org.l2jpx.gameserver.model.stats.IStatFunction;
import org.l2jpx.gameserver.model.stats.Stat;

/**
 * @author UnAfraid
 */
public class MAttackFinalizer implements IStatFunction
{
	@Override
	public double calc(Creature creature, OptionalDouble base, Stat stat)
	{
		throwIfPresent(base);
		
		double baseValue = calcWeaponBaseValue(creature, stat);
		baseValue += calcEnchantedItemBonus(creature, stat);
		if (creature.isPlayer())
		{
			// Enchanted chest bonus
			baseValue += calcEnchantBodyPart(creature, ItemTemplate.SLOT_CHEST, ItemTemplate.SLOT_FULL_ARMOR);
		}
		
		if (Config.CHAMPION_ENABLE && creature.isChampion())
		{
			baseValue *= Config.CHAMPION_ATK;
		}
		if (creature.isRaid())
		{
			baseValue *= Config.RAID_MATTACK_MULTIPLIER;
		}
		
		// Calculate modifiers Magic Attack
		baseValue *= Math.pow(BaseStat.INT.calcBonus(creature) * creature.getLevelMod(), 2.2072);
		return validateValue(creature, Stat.defaultValue(creature, stat, baseValue), 0, creature.isPlayable() ? Config.MAX_MATK : Double.MAX_VALUE);
	}
	
	@Override
	public double calcEnchantBodyPartBonus(int enchantLevel, boolean isBlessed)
	{
		if (isBlessed)
		{
			return (2 * Math.max(enchantLevel - 3, 0)) + (2 * Math.max(enchantLevel - 6, 0));
		}
		return (1.4 * Math.max(enchantLevel - 3, 0)) + (1.4 * Math.max(enchantLevel - 6, 0));
	}
}
