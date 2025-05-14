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

import org.l2jpx.gameserver.model.actor.Creature;

/**
 * @author xban1x
 */
public class AggroInfo
{
	private static final long MAX_VALUE = 1000000000000000L;
	
	private final Creature _attacker;
	private long _hate = 0;
	private long _damage = 0;
	
	public AggroInfo(Creature pAttacker)
	{
		_attacker = pAttacker;
	}
	
	public Creature getAttacker()
	{
		return _attacker;
	}
	
	public long getHate()
	{
		return _hate;
	}
	
	public long checkHate(Creature owner)
	{
		if (_attacker.isAlikeDead() || !_attacker.isSpawned() || !owner.isInSurroundingRegion(_attacker))
		{
			_hate = 0;
		}
		return _hate;
	}
	
	public void addHate(long value)
	{
		_hate = Math.min(_hate + value, MAX_VALUE);
	}
	
	public void stopHate()
	{
		_hate = 0;
	}
	
	public long getDamage()
	{
		return _damage;
	}
	
	public void addDamage(long value)
	{
		_damage = Math.min(_damage + value, MAX_VALUE);
	}
	
	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
		{
			return true;
		}
		
		if (obj instanceof AggroInfo)
		{
			return (((AggroInfo) obj).getAttacker() == _attacker);
		}
		
		return false;
	}
	
	@Override
	public int hashCode()
	{
		return _attacker.getObjectId();
	}
	
	@Override
	public String toString()
	{
		return "AggroInfo [attacker=" + _attacker + ", hate=" + _hate + ", damage=" + _damage + "]";
	}
}
