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
package org.l2jpx.gameserver.model.events.impl.creature;

import org.l2jpx.gameserver.model.WorldObject;
import org.l2jpx.gameserver.model.actor.Creature;
import org.l2jpx.gameserver.model.events.EventType;
import org.l2jpx.gameserver.model.events.impl.IBaseEvent;
import org.l2jpx.gameserver.model.skill.Skill;

/**
 * An instantly executed event when Caster has finished using a skill.
 * @author Nik
 */
public class OnCreatureSkillFinishCast implements IBaseEvent
{
	private Creature _caster;
	private WorldObject _target;
	private Skill _skill;
	private boolean _simultaneously;
	
	public OnCreatureSkillFinishCast()
	{
	}
	
	public Creature getCaster()
	{
		return _caster;
	}
	
	public synchronized void setCaster(Creature caster)
	{
		_caster = caster;
	}
	
	public WorldObject getTarget()
	{
		return _target;
	}
	
	public synchronized void setTarget(WorldObject target)
	{
		_target = target;
	}
	
	public Skill getSkill()
	{
		return _skill;
	}
	
	public synchronized void setSkill(Skill skill)
	{
		_skill = skill;
	}
	
	public boolean isSimultaneously()
	{
		return _simultaneously;
	}
	
	public synchronized void setSimultaneously(boolean simultaneously)
	{
		_simultaneously = simultaneously;
	}
	
	@Override
	public EventType getType()
	{
		return EventType.ON_CREATURE_SKILL_FINISH_CAST;
	}
}