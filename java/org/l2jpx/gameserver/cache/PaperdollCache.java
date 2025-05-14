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
package org.l2jpx.gameserver.cache;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.l2jpx.gameserver.data.xml.ArmorSetData;
import org.l2jpx.gameserver.model.ArmorSet;
import org.l2jpx.gameserver.model.actor.Player;
import org.l2jpx.gameserver.model.item.instance.Item;
import org.l2jpx.gameserver.model.stats.BaseStat;
import org.l2jpx.gameserver.model.stats.Stat;

/**
 * @author Sahar
 */
public class PaperdollCache
{
	private final Set<Item> _paperdollItems = ConcurrentHashMap.newKeySet();
	
	private final Map<BaseStat, Double> _baseStatValues = new ConcurrentHashMap<>();
	private final Map<Stat, Double> _statValues = new ConcurrentHashMap<>();
	private int _armorSetEnchant = -1;
	
	public Set<Item> getPaperdollItems()
	{
		return _paperdollItems;
	}
	
	public void clearCachedStats()
	{
		_baseStatValues.clear();
		_statValues.clear();
		
		clearArmorSetEnchant();
	}
	
	public void clearArmorSetEnchant()
	{
		_armorSetEnchant = -1;
	}
	
	public double getBaseStatValue(Player player, BaseStat stat)
	{
		final Double baseStatValue = _baseStatValues.get(stat);
		if (baseStatValue != null)
		{
			return baseStatValue.doubleValue();
		}
		
		final Set<ArmorSet> appliedSets = new HashSet<>(2);
		double value = 0;
		for (Item item : _paperdollItems)
		{
			for (ArmorSet set : ArmorSetData.getInstance().getSets(item.getId()))
			{
				if ((set.getPieceCount(player) >= set.getMinimumPieces()) && appliedSets.add(set))
				{
					value += set.getStatsBonus(stat);
				}
			}
		}
		
		_baseStatValues.put(stat, value);
		return value;
	}
	
	public int getArmorSetEnchant(Player player)
	{
		int armorSetEnchant = _armorSetEnchant;
		if (armorSetEnchant >= 0)
		{
			return armorSetEnchant;
		}
		
		armorSetEnchant = 0;
		for (Item item : _paperdollItems)
		{
			for (ArmorSet set : ArmorSetData.getInstance().getSets(item.getId()))
			{
				final int enchantEffect = set.getSetEnchant(player);
				if (enchantEffect > armorSetEnchant)
				{
					armorSetEnchant = enchantEffect;
				}
			}
		}
		
		_armorSetEnchant = armorSetEnchant;
		return armorSetEnchant;
	}
	
	public double getStats(Stat stat)
	{
		final Double statValue = _statValues.get(stat);
		if (statValue != null)
		{
			return statValue.doubleValue();
		}
		
		double value = 0;
		for (Item item : _paperdollItems)
		{
			value += item.getTemplate().getStats(stat, 0);
		}
		
		_statValues.put(stat, value);
		return value;
	}
}