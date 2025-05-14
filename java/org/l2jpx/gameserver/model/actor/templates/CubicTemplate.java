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
package org.l2jpx.gameserver.model.actor.templates;

import java.util.ArrayList;
import java.util.List;

import org.l2jpx.gameserver.model.StatSet;
import org.l2jpx.gameserver.model.WorldObject;
import org.l2jpx.gameserver.model.actor.Creature;
import org.l2jpx.gameserver.model.cubic.Cubic;
import org.l2jpx.gameserver.model.cubic.CubicSkill;
import org.l2jpx.gameserver.model.cubic.CubicTargetType;
import org.l2jpx.gameserver.model.cubic.ICubicConditionHolder;
import org.l2jpx.gameserver.model.cubic.conditions.ICubicCondition;

/**
 * @author UnAfraid
 */
public class CubicTemplate extends CreatureTemplate implements ICubicConditionHolder
{
	private final int _id;
	private final int _level;
	private final int _slot;
	private final int _duration;
	private final int _delay;
	private final int _maxCount;
	private final int _useUp;
	private final double _power;
	private final CubicTargetType _targetType;
	private final List<ICubicCondition> _conditions = new ArrayList<>();
	private final List<CubicSkill> _skills = new ArrayList<>();
	
	public CubicTemplate(StatSet set)
	{
		super(set);
		_id = set.getInt("id");
		_level = set.getInt("level");
		_slot = set.getInt("slot");
		_duration = set.getInt("duration");
		_delay = set.getInt("delay");
		_maxCount = set.getInt("maxCount");
		_useUp = set.getInt("useUp");
		_power = set.getDouble("power") / 10;
		_targetType = set.getEnum("targetType", CubicTargetType.class, CubicTargetType.TARGET);
	}
	
	public int getId()
	{
		return _id;
	}
	
	public int getLevel()
	{
		return _level;
	}
	
	public int getSlot()
	{
		return _slot;
	}
	
	public int getDuration()
	{
		return _duration;
	}
	
	public int getDelay()
	{
		return _delay;
	}
	
	public int getMaxCount()
	{
		return _maxCount;
	}
	
	public int getUseUp()
	{
		return _useUp;
	}
	
	public CubicTargetType getTargetType()
	{
		return _targetType;
	}
	
	public List<CubicSkill> getCubicSkills()
	{
		return _skills;
	}
	
	@Override
	public int getBasePAtk()
	{
		return (int) _power;
	}
	
	@Override
	public int getBaseMAtk()
	{
		return (int) _power;
	}
	
	@Override
	public boolean validateConditions(Cubic cubic, Creature owner, WorldObject target)
	{
		if (_conditions.isEmpty())
		{
			return true;
		}
		
		for (ICubicCondition condition : _conditions)
		{
			if (!condition.test(cubic, owner, target))
			{
				return false;
			}
		}
		
		return true;
	}
	
	@Override
	public void addCondition(ICubicCondition condition)
	{
		_conditions.add(condition);
	}
	
	@Override
	public String toString()
	{
		return "Cubic id: " + _id + " level: " + _level + " slot: " + _slot + " duration: " + _duration + " delay: " + _delay + " maxCount: " + _maxCount + " useUp: " + _useUp + " power: " + _power + System.lineSeparator() + "skills: " + _skills + System.lineSeparator() + "conditions:" + _conditions + System.lineSeparator();
	}
}
