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
package org.l2jpx.gameserver.model.holders;

import java.util.function.Function;

import org.l2jpx.gameserver.model.ArmorSet;
import org.l2jpx.gameserver.model.actor.Player;
import org.l2jpx.gameserver.model.item.instance.Item;

/**
 * @author UnAfraid
 */
public class ArmorsetSkillHolder extends SkillHolder
{
	private final int _minimumPieces;
	private final int _minEnchant;
	private final boolean _isOptional;
	
	public ArmorsetSkillHolder(int skillId, int skillLevel, int minimumPieces, int minEnchant, boolean isOptional)
	{
		super(skillId, skillLevel);
		_minimumPieces = minimumPieces;
		_minEnchant = minEnchant;
		_isOptional = isOptional;
	}
	
	public int getMinimumPieces()
	{
		return _minimumPieces;
	}
	
	public int getMinEnchant()
	{
		return _minEnchant;
	}
	
	public boolean isOptional()
	{
		return _isOptional;
	}
	
	public boolean validateConditions(Player player, ArmorSet armorSet, Function<Item, Integer> idProvider)
	{
		// Player doesn't have enough items equipped to use this skill
		if (_minimumPieces > armorSet.getPieceCount(player, idProvider))
		{
			return false;
		}
		
		// Player's set enchantment isn't enough to use this skill
		if (_minEnchant > armorSet.getSetEnchant(player))
		{
			return false;
		}
		
		// Player doesn't have the required item to use this skill
		if (_isOptional && !armorSet.hasOptionalEquipped(player, idProvider))
		{
			return false;
		}
		
		// Player already knows that skill
		if (player.getSkillLevel(getSkillId()) == getSkillLevel())
		{
			return false;
		}
		
		return true;
	}
}
