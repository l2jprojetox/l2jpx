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
package org.l2jpx.gameserver.model.options;

import org.l2jpx.gameserver.model.skill.Skill;

/**
 * @author UnAfraid, Mobius
 */
public class OptionSkillHolder
{
	private final Skill _skill;
	private final double _chance;
	private final OptionSkillType _type;
	
	/**
	 * @param skill
	 * @param type
	 * @param chance
	 */
	public OptionSkillHolder(Skill skill, double chance, OptionSkillType type)
	{
		_skill = skill;
		_chance = chance;
		_type = type;
	}
	
	public Skill getSkill()
	{
		return _skill;
	}
	
	public double getChance()
	{
		return _chance;
	}
	
	public OptionSkillType getSkillType()
	{
		return _type;
	}
}
