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

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

import org.l2jpx.commons.util.CommonUtil;
import org.l2jpx.gameserver.model.actor.Player;
import org.l2jpx.gameserver.model.holders.ArmorsetSkillHolder;
import org.l2jpx.gameserver.model.item.instance.Item;
import org.l2jpx.gameserver.model.itemcontainer.Inventory;
import org.l2jpx.gameserver.model.itemcontainer.PlayerInventory;
import org.l2jpx.gameserver.model.stats.BaseStat;

/**
 * @author UnAfraid
 */
public class ArmorSet
{
	private final int _id;
	private final int _minimumPieces;
	private final boolean _isVisual;
	
	private final int[] _requiredItems;
	private final int[] _optionalItems;
	
	private final List<ArmorsetSkillHolder> _skills;
	private final Map<BaseStat, Double> _stats;
	
	private static final int[] ARMORSET_SLOTS = new int[]
	{
		Inventory.PAPERDOLL_CHEST,
		Inventory.PAPERDOLL_LEGS,
		Inventory.PAPERDOLL_HEAD,
		Inventory.PAPERDOLL_GLOVES,
		Inventory.PAPERDOLL_FEET
	};
	
	public ArmorSet(int id, int minimumPieces, boolean isVisual, Set<Integer> requiredItems, Set<Integer> optionalItems, List<ArmorsetSkillHolder> skills, Map<BaseStat, Double> stats)
	{
		_id = id;
		_minimumPieces = minimumPieces;
		_isVisual = isVisual;
		_requiredItems = requiredItems.stream().mapToInt(x -> x).toArray();
		_optionalItems = optionalItems.stream().mapToInt(x -> x).toArray();
		_skills = skills;
		_stats = stats;
	}
	
	public int getId()
	{
		return _id;
	}
	
	/**
	 * @return the minimum amount of pieces equipped to form a set
	 */
	public int getMinimumPieces()
	{
		return _minimumPieces;
	}
	
	/**
	 * @return {@code true} if the set is visual only, {@code} otherwise
	 */
	public boolean isVisual()
	{
		return _isVisual;
	}
	
	/**
	 * @return the set of items that can form a set
	 */
	public int[] getRequiredItems()
	{
		return _requiredItems;
	}
	
	/**
	 * @return the set of shields
	 */
	public int[] getOptionalItems()
	{
		return _optionalItems;
	}
	
	/**
	 * The list of skills that are activated when set reaches it's minimum equipped items condition
	 * @return
	 */
	public List<ArmorsetSkillHolder> getSkills()
	{
		return _skills;
	}
	
	/**
	 * @param stat
	 * @return the stats bonus value or 0 if doesn't exist
	 */
	public double getStatsBonus(BaseStat stat)
	{
		return _stats.getOrDefault(stat, 0d);
	}
	
	/**
	 * @param shieldId
	 * @return {@code true} if player has the shield of this set equipped, {@code false} in case set doesn't have a shield or player doesn't
	 */
	public boolean containOptionalItem(int shieldId)
	{
		return CommonUtil.contains(_optionalItems, shieldId);
	}
	
	/**
	 * @param player
	 * @return true if all parts of set are enchanted to +6 or more
	 */
	public int getSetEnchant(Player player)
	{
		// Player don't have full set
		if (getPieceCount(player) < _minimumPieces)
		{
			return 0;
		}
		
		final PlayerInventory inv = player.getInventory();
		int enchantLevel = Byte.MAX_VALUE;
		for (int armorSlot : ARMORSET_SLOTS)
		{
			final Item itemPart = inv.getPaperdollItem(armorSlot);
			if ((itemPart != null) && CommonUtil.contains(_requiredItems, itemPart.getId()) && (enchantLevel > itemPart.getEnchantLevel()))
			{
				enchantLevel = itemPart.getEnchantLevel();
			}
		}
		if (enchantLevel == Byte.MAX_VALUE)
		{
			enchantLevel = 0;
		}
		
		return enchantLevel;
	}
	
	public boolean hasOptionalEquipped(Player player, Function<Item, Integer> idProvider)
	{
		for (Item item : player.getInventory().getPaperdollItems())
		{
			if (CommonUtil.contains(_optionalItems, idProvider.apply(item)))
			{
				return true;
			}
		}
		return false;
	}
	
	/**
	 * @param player
	 * @param idProvider
	 * @return the amount of set visual items that player has equipped
	 */
	public long getPieceCount(Player player, Function<Item, Integer> idProvider)
	{
		return player.getInventory().getPaperdollItemCount(item -> CommonUtil.contains(_requiredItems, idProvider.apply(item)));
	}
	
	public long getPieceCount(Player player)
	{
		return player.getInventory().getPaperdollItemCount(item -> CommonUtil.contains(_requiredItems, item.getId()));
	}
}
