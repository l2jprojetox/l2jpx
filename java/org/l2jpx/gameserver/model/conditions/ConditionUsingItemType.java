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
package org.l2jpx.gameserver.model.conditions;

import org.l2jpx.gameserver.model.actor.Creature;
import org.l2jpx.gameserver.model.item.ItemTemplate;
import org.l2jpx.gameserver.model.item.instance.Item;
import org.l2jpx.gameserver.model.item.type.ArmorType;
import org.l2jpx.gameserver.model.itemcontainer.Inventory;
import org.l2jpx.gameserver.model.skill.Skill;

/**
 * The Class ConditionUsingItemType.
 * @author mkizub
 */
public class ConditionUsingItemType extends Condition
{
	private final boolean _armor;
	private final int _mask;
	
	/**
	 * Instantiates a new condition using item type.
	 * @param mask the mask
	 */
	public ConditionUsingItemType(int mask)
	{
		_mask = mask;
		_armor = (_mask & (ArmorType.MAGIC.mask() | ArmorType.LIGHT.mask() | ArmorType.HEAVY.mask())) != 0;
	}
	
	@Override
	public boolean testImpl(Creature effector, Creature effected, Skill skill, ItemTemplate item)
	{
		if (effector == null)
		{
			return false;
		}
		
		if (!effector.isPlayer())
		{
			return !_armor && ((_mask & effector.getAttackType().mask()) != 0);
		}
		
		// If ConditionUsingItemType is one between Light, Heavy or Magic.
		final Inventory inv = effector.getInventory();
		if (_armor)
		{
			// Get the itemMask of the weared chest (if exists).
			final Item chest = inv.getPaperdollItem(Inventory.PAPERDOLL_CHEST);
			if (chest == null)
			{
				return (ArmorType.NONE.mask() & _mask) == ArmorType.NONE.mask();
			}
			
			// If chest armor is different from the condition one return false.
			final int chestMask = chest.getTemplate().getItemMask();
			if ((_mask & chestMask) == 0)
			{
				return false;
			}
			
			// So from here, chest armor matches conditions.
			
			// Return true if chest armor is a Full Armor.
			final int chestBodyPart = chest.getTemplate().getBodyPart();
			if (chestBodyPart == ItemTemplate.SLOT_FULL_ARMOR)
			{
				return true;
			}
			
			// Check legs armor.
			final Item legs = inv.getPaperdollItem(Inventory.PAPERDOLL_LEGS);
			if (legs == null)
			{
				return (ArmorType.NONE.mask() & _mask) == ArmorType.NONE.mask();
			}
			
			// Return true if legs armor matches too.
			final int legMask = legs.getTemplate().getItemMask();
			return (_mask & legMask) != 0;
		}
		
		return (_mask & inv.getWearedMask()) != 0;
	}
}
