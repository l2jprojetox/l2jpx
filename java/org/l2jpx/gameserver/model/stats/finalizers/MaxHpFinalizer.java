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
import org.l2jpx.gameserver.data.xml.EnchantItemHPBonusData;
import org.l2jpx.gameserver.model.actor.Creature;
import org.l2jpx.gameserver.model.actor.Player;
import org.l2jpx.gameserver.model.actor.instance.Pet;
import org.l2jpx.gameserver.model.item.ItemTemplate;
import org.l2jpx.gameserver.model.item.instance.Item;
import org.l2jpx.gameserver.model.itemcontainer.Inventory;
import org.l2jpx.gameserver.model.stats.BaseStat;
import org.l2jpx.gameserver.model.stats.IStatFunction;
import org.l2jpx.gameserver.model.stats.Stat;

/**
 * @author UnAfraid
 */
public class MaxHpFinalizer implements IStatFunction
{
	@Override
	public double calc(Creature creature, OptionalDouble base, Stat stat)
	{
		throwIfPresent(base);
		
		double baseValue = creature.getTemplate().getBaseValue(stat, 0);
		if (creature.isPet())
		{
			final Pet pet = (Pet) creature;
			baseValue = pet.getPetLevelData().getPetMaxHP();
		}
		else if (creature.isPlayer())
		{
			final Player player = creature.getActingPlayer();
			if (player != null)
			{
				baseValue = player.getTemplate().getBaseHpMax(player.getLevel());
			}
		}
		
		final double conBonus = creature.getCON() > 0 ? BaseStat.CON.calcBonus(creature) : 1;
		baseValue *= conBonus;
		
		return defaultValue(creature, stat, baseValue);
	}
	
	private static double defaultValue(Creature creature, Stat stat, double baseValue)
	{
		double mul = creature.getStat().getMul(stat);
		double add = creature.getStat().getAdd(stat);
		
		double maxHp = (mul * baseValue) + add + creature.getStat().getMoveTypeValue(stat, creature.getMoveType());
		final boolean isPlayer = creature.isPlayer();
		
		final Inventory inv = creature.getInventory();
		if (inv == null)
		{
			if (isPlayer)
			{
				if (creature.getActingPlayer().isCursedWeaponEquipped())
				{
					return Double.MAX_VALUE;
				}
				
				mul = creature.getStat().getMul(Stat.HP_LIMIT);
				add = creature.getStat().getAdd(Stat.HP_LIMIT);
				return Math.min(maxHp, (Config.MAX_HP * mul) + add);
			}
			return maxHp;
		}
		
		boolean shouldLiftLimit = false;
		
		// Add maxHP bonus from items
		for (Item item : inv.getPaperdollItems())
		{
			maxHp += item.getTemplate().getStats(stat, 0);
			
			// Apply enchanted item bonus HP
			if (item.isArmor() && item.isEnchanted())
			{
				final int bodyPart = item.getTemplate().getBodyPart();
				if ((bodyPart != ItemTemplate.SLOT_NECK) && (bodyPart != ItemTemplate.SLOT_LR_EAR) && (bodyPart != ItemTemplate.SLOT_LR_FINGER))
				{
					maxHp += EnchantItemHPBonusData.getInstance().getHPBonus(item);
				}
			}
			
			if (item.isWeapon() && item.getWeaponItem().isDragonWeapon())
			{
				shouldLiftLimit = true;
			}
		}
		
		final double hpLimit;
		if (isPlayer && !shouldLiftLimit && !creature.getActingPlayer().isCursedWeaponEquipped())
		{
			mul = creature.getStat().getMul(Stat.HP_LIMIT);
			add = creature.getStat().getAdd(Stat.HP_LIMIT);
			hpLimit = (Config.MAX_HP * mul) + add;
		}
		else
		{
			hpLimit = Double.MAX_VALUE;
		}
		
		return Math.min(maxHp, hpLimit);
	}
}
