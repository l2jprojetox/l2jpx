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
public class PCriticalRateFinalizer implements IStatFunction
{
	@Override
	public double calc(Creature creature, OptionalDouble base, Stat stat)
	{
		throwIfPresent(base);
		
		double baseValue = calcWeaponBaseValue(creature, stat);
		if (creature.isPlayer())
		{
			// Enchanted legs bonus
			baseValue += calcEnchantBodyPart(creature, ItemTemplate.SLOT_LEGS);
		}
		final double dexBonus = creature.getDEX() > 0 ? BaseStat.DEX.calcBonus(creature) : 1;
		
		final double maxPhysicalCritRate;
		if (creature.isPlayable())
		{
			maxPhysicalCritRate = Config.MAX_PCRIT_RATE + creature.getStat().getValue(Stat.ADD_MAX_PHYSICAL_CRITICAL_RATE, 0);
		}
		else
		{
			maxPhysicalCritRate = Double.MAX_VALUE;
		}
		
		return validateValue(creature, Stat.defaultValue(creature, stat, baseValue * dexBonus * 10), 0, maxPhysicalCritRate);
	}
	
	@Override
	public double calcEnchantBodyPartBonus(int enchantLevel, boolean isBlessed)
	{
		if (isBlessed)
		{
			return (0.5 * Math.max(enchantLevel - 3, 0)) + (0.5 * Math.max(enchantLevel - 6, 0));
		}
		return (0.34 * Math.max(enchantLevel - 3, 0)) + (0.34 * Math.max(enchantLevel - 6, 0));
	}
}
