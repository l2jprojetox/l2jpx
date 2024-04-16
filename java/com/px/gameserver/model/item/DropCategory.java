package com.px.gameserver.model.item;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.px.commons.random.Rnd;

import com.px.gameserver.enums.DropType;
import com.px.gameserver.model.holder.IntIntHolder;

public class DropCategory
{
	private final DropType _dropType;
	private final double _chance;
	private final List<DropData> _drops;
	
	private double _cumulativeChance;
	
	public DropCategory(DropType dropType, double chance)
	{
		_dropType = dropType;
		_chance = chance;
		_drops = new ArrayList<>(0);
		
		_cumulativeChance = 0;
	}
	
	/**
	 * Adds {@link DropData} to this {@link DropCategory}.
	 * @param drop
	 */
	public void addDropData(DropData drop)
	{
		_drops.add(drop);
		
		_cumulativeChance += drop.getChance();
	}
	
	/**
	 * @return The {@link DropType} of this {@link DropCategory}.
	 */
	public DropType getDropType()
	{
		return _dropType;
	}
	
	/**
	 * @return The {@link DropCategory} chance.
	 */
	public double getChance()
	{
		return _chance;
	}
	
	/**
	 * @return The list of all {@link DropData}, which belongs to this {@link DropCategory}.
	 */
	public List<DropData> getAllDrops()
	{
		return _drops;
	}
	
	public double getCategoryCumulativeChance()
	{
		return (_dropType == DropType.SPOIL) ? 100. : _cumulativeChance;
	}
	
	/**
	 * Calculates drops of this {@link DropCategory}.
	 * @param levelMultiplier : The input level modifier of the last attacker.
	 * @param raid : The NPC is raid boss.
	 * @return The list of {@link IntIntHolder} holding item ID and item count.
	 */
	public List<IntIntHolder> calculateDrop(double levelMultiplier, boolean raid)
	{
		// Get base category chance and apply level multiplier and drop rate config based on type.
		double chance = getChance() * levelMultiplier * getDropType().getDropRate(raid) * DropData.MAX_CHANCE / 100;
		
		// Check chance exceeding 100% limit and calculate drop chance multiplier.
		double multiplier;
		
		// Chance is not over 100% (inclusive).
		if (chance <= DropData.MAX_CHANCE)
		{
			// Check category success, set drop chance multiplier.
			if (Rnd.get(DropData.MAX_CHANCE) < chance)
				multiplier = 1;
			else
				return Collections.emptyList();
		}
		// Chance is over 100%. Category automatically succeed, calculate drop chance multiplier.
		else
			multiplier = chance / DropData.MAX_CHANCE;
		
		// Category success, calculate chance for individual drop and go through drops.
		final List<IntIntHolder> result = new ArrayList<>(1);
		
		// Evaluate all drops if the drop type is SPOIL - each drop has a chance to be dropped.
		if (_dropType == DropType.SPOIL)
		{
			for (DropData dd : getAllDrops())
			{
				// Calculate drop chance and apply drop chance multiplier.
				chance = dd.getChance() * multiplier * DropData.MAX_CHANCE / 100;
				
				// Chance is not over 100% (inclusive).
				if (chance <= DropData.MAX_CHANCE)
				{
					// Calculate drop success, calculate drop using fixed amount multiplier.
					if (Rnd.get(DropData.MAX_CHANCE) < chance)
						result.add(dd.calculateDrop(1));
				}
				// Chance is over 100%. Drop automatically succeed, calculate drop using calculated amount multiplier.
				else
					result.add(dd.calculateDrop(chance / DropData.MAX_CHANCE));
			}
		}
		// Evaluate all drops, pick one drop to be dropped.
		else
		{
			// Calculate category cumulative chance and apply drop chance multiplier.
			chance = getCategoryCumulativeChance() * multiplier * DropData.MAX_CHANCE / 100;
			
			// Chance is not over 100% (inclusive).
			if (chance <= DropData.MAX_CHANCE)
			{
				// Get drop chance and loop for drop.
				chance = Rnd.get(DropData.MAX_CHANCE);
				for (DropData dd : getAllDrops())
				{
					// Check drop chance and evaluate.
					chance -= dd.getChance() * multiplier * DropData.MAX_CHANCE / 100;
					if (chance < 0)
					{
						result.add(dd.calculateDrop(1));
						break;
					}
				}
			}
			// Chance is over 100%. Calculate drop chance multiplier and drop amount multiplier.
			else
			{
				double amount = multiplier;
				multiplier = 100 / getCategoryCumulativeChance();
				amount /= multiplier;
				
				// Get drop chance and loop for drop.
				chance = Rnd.get(DropData.MAX_CHANCE);
				for (DropData dd : getAllDrops())
				{
					// Check drop chance and evaluate.
					chance -= dd.getChance() * multiplier * DropData.MAX_CHANCE / 100;
					if (chance < 0)
					{
						result.add(dd.calculateDrop(amount));
						break;
					}
				}
			}
		}
		
		return result;
	}
}