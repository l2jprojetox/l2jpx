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
package org.l2jpx.gameserver.model.cubic.conditions;

import org.l2jpx.gameserver.model.WorldObject;
import org.l2jpx.gameserver.model.actor.Creature;
import org.l2jpx.gameserver.model.actor.instance.Door;
import org.l2jpx.gameserver.model.cubic.Cubic;

/**
 * @author UnAfraid
 */
public class HpCondition implements ICubicCondition
{
	private final HpConditionType _type;
	private final int _hpPer;
	
	public HpCondition(HpConditionType type, int hpPer)
	{
		_type = type;
		_hpPer = hpPer;
	}
	
	@Override
	public boolean test(Cubic cubic, Creature owner, WorldObject target)
	{
		if (target.isCreature() || target.isDoor())
		{
			final double hpPer = (target.isDoor() ? (Door) target : (Creature) target).getCurrentHpPercent();
			switch (_type)
			{
				case GREATER:
				{
					return hpPer > _hpPer;
				}
				case LESSER:
				{
					return hpPer < _hpPer;
				}
			}
		}
		return false;
	}
	
	@Override
	public String toString()
	{
		return getClass().getSimpleName() + " chance: " + _hpPer;
	}
	
	public enum HpConditionType
	{
		GREATER,
		LESSER;
	}
}
