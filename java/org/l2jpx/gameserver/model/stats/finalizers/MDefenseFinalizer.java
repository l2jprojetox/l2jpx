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
import org.l2jpx.gameserver.model.actor.Player;
import org.l2jpx.gameserver.model.actor.instance.Pet;
import org.l2jpx.gameserver.model.item.instance.Item;
import org.l2jpx.gameserver.model.itemcontainer.Inventory;
import org.l2jpx.gameserver.model.stats.BaseStat;
import org.l2jpx.gameserver.model.stats.IStatFunction;
import org.l2jpx.gameserver.model.stats.Stat;

/**
 * @author UnAfraid
 */
public class MDefenseFinalizer implements IStatFunction
{
	private static final int[] SLOTS =
	{
		Inventory.PAPERDOLL_LFINGER,
		Inventory.PAPERDOLL_RFINGER,
		Inventory.PAPERDOLL_LEAR,
		Inventory.PAPERDOLL_REAR,
		Inventory.PAPERDOLL_NECK
	};
	
	@Override
	public double calc(Creature creature, OptionalDouble base, Stat stat)
	{
		throwIfPresent(base);
		double baseValue = creature.getTemplate().getBaseValue(stat, 0);
		if (creature.isPet())
		{
			final Pet pet = (Pet) creature;
			baseValue = pet.getPetLevelData().getPetMDef();
		}
		baseValue += calcEnchantedItemBonus(creature, stat);
		
		final Inventory inv = creature.getInventory();
		if (inv != null)
		{
			for (Item item : inv.getPaperdollItems())
			{
				baseValue += item.getTemplate().getStats(stat, 0);
			}
		}
		
		if (creature.isPlayer())
		{
			final Player player = creature.getActingPlayer();
			for (int slot : SLOTS)
			{
				if (!player.getInventory().isPaperdollSlotEmpty(slot))
				{
					final int defaultStatValue = player.getTemplate().getBaseDefBySlot(slot);
					baseValue -= creature.getTransformation().map(transform -> transform.getBaseDefBySlot(player, slot)).orElse(defaultStatValue);
				}
			}
		}
		else if (creature.isPet() && (creature.getInventory().getPaperdollObjectId(Inventory.PAPERDOLL_NECK) != 0))
		{
			baseValue -= 13;
		}
		if (creature.isRaid())
		{
			baseValue *= Config.RAID_MDEFENCE_MULTIPLIER;
		}
		
		final double bonus = creature.getMEN() > 0 ? BaseStat.MEN.calcBonus(creature) : 1;
		baseValue *= bonus * creature.getLevelMod();
		return defaultValue(creature, stat, baseValue);
	}
	
	private double defaultValue(Creature creature, Stat stat, double baseValue)
	{
		final double mul = Math.max(creature.getStat().getMul(stat), 0.5);
		final double add = creature.getStat().getAdd(stat);
		return Math.max((baseValue * mul) + add + creature.getStat().getMoveTypeValue(stat, creature.getMoveType()), creature.getTemplate().getBaseValue(stat, 0) * 0.2);
	}
}
