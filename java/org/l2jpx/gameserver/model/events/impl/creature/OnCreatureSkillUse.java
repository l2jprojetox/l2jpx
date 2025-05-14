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

import org.l2jpx.gameserver.model.actor.Creature;
import org.l2jpx.gameserver.model.events.EventType;
import org.l2jpx.gameserver.model.events.impl.IBaseEvent;
import org.l2jpx.gameserver.model.skill.Skill;

/**
 * Executed when the caster Creature tries to use a skill.
 * @author UnAfraid, Nik
 */
public class OnCreatureSkillUse implements IBaseEvent
{
	private Creature _caster;
	private Skill _skill;
	private boolean _simultaneously;
	
	public OnCreatureSkillUse()
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
		return EventType.ON_CREATURE_SKILL_USE;
	}
}