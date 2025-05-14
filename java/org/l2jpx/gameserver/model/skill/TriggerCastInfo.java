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
package org.l2jpx.gameserver.model.skill;

import org.l2jpx.gameserver.model.WorldObject;
import org.l2jpx.gameserver.model.actor.Creature;
import org.l2jpx.gameserver.model.item.instance.Item;

/**
 * @author Mobius
 */
public class TriggerCastInfo
{
	private final Creature _creature;
	private final WorldObject _target;
	private final Skill _skill;
	private final Item _item;
	private final boolean _ignoreTargetType;
	
	public TriggerCastInfo(Creature creature, WorldObject target, Skill skill, Item item, boolean ignoreTargetType)
	{
		_creature = creature;
		_target = target;
		_skill = skill;
		_item = item;
		_ignoreTargetType = ignoreTargetType;
	}
	
	public Creature getCreature()
	{
		return _creature;
	}
	
	public WorldObject getTarget()
	{
		return _target;
	}
	
	public Skill getSkill()
	{
		return _skill;
	}
	
	public Item getItem()
	{
		return _item;
	}
	
	public boolean isIgnoreTargetType()
	{
		return _ignoreTargetType;
	}
}
