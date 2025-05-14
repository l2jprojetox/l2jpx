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

import java.util.EnumSet;
import java.util.HashSet;
import java.util.Set;

import org.l2jpx.gameserver.data.xml.SkillData;
import org.l2jpx.gameserver.enums.Race;
import org.l2jpx.gameserver.enums.SocialClass;
import org.l2jpx.gameserver.model.holders.ItemHolder;
import org.l2jpx.gameserver.model.holders.SkillHolder;
import org.l2jpx.gameserver.model.skill.Skill;

/**
 * @author Zoey76
 */
public class SkillLearn
{
	private final String _skillName;
	private final int _skillId;
	private final int _skillLevel;
	private final int _getLevel;
	private final int _getDualClassLevel;
	private final boolean _autoGet;
	private final long _levelUpSp;
	private final Set<ItemHolder> _requiredItems = new HashSet<>(1);
	private final Set<Race> _races = EnumSet.noneOf(Race.class);
	private final Set<SkillHolder> _preReqSkills = new HashSet<>(1);
	private SocialClass _socialClass;
	private final boolean _residenceSkill;
	private final Set<Integer> _residenceIds = new HashSet<>(1);
	private final boolean _learnedByNpc;
	private final boolean _learnedByFS;
	private final Set<Integer> _removeSkills = new HashSet<>(1);
	private final int _treeId;
	private final int _row;
	private final int _column;
	private final int _pointsRequired;
	
	/**
	 * Constructor for SkillLearn.
	 * @param set the set with the SkillLearn data.
	 */
	public SkillLearn(StatSet set)
	{
		_skillName = set.getString("skillName");
		_skillId = set.getInt("skillId");
		_skillLevel = set.getInt("skillLevel");
		_getLevel = set.getInt("getLevel");
		_getDualClassLevel = set.getInt("getDualClassLevel", 0);
		_autoGet = set.getBoolean("autoGet", false);
		_levelUpSp = set.getLong("levelUpSp", 0);
		_residenceSkill = set.getBoolean("residenceSkill", false);
		_learnedByNpc = set.getBoolean("learnedByNpc", false);
		_learnedByFS = set.getBoolean("learnedByFS", false);
		_treeId = set.getInt("treeId", 0);
		_row = set.getInt("row", 0);
		_column = set.getInt("row", 0);
		_pointsRequired = set.getInt("pointsRequired", 0);
	}
	
	/**
	 * @return the name of this skill.
	 */
	public String getName()
	{
		return _skillName;
	}
	
	/**
	 * @return the ID of this skill.
	 */
	public int getSkillId()
	{
		return _skillId;
	}
	
	/**
	 * @return the level of this skill.
	 */
	public int getSkillLevel()
	{
		return _skillLevel;
	}
	
	/**
	 * @return the minimum level required to acquire this skill.
	 */
	public int getGetLevel()
	{
		return _getLevel;
	}
	
	/**
	 * @return the minimum level of a character dual class required to acquire this skill.
	 */
	public int getDualClassLevel()
	{
		return _getDualClassLevel;
	}
	
	/**
	 * @return the amount of SP/Clan Reputation to acquire this skill.
	 */
	public long getLevelUpSp()
	{
		return _levelUpSp;
	}
	
	/**
	 * @return {@code true} if the skill is auto-get, this skill is automatically delivered.
	 */
	public boolean isAutoGet()
	{
		return _autoGet;
	}
	
	/**
	 * @return the set with the item holders required to acquire this skill.
	 */
	public Set<ItemHolder> getRequiredItems()
	{
		return _requiredItems;
	}
	
	/**
	 * Adds a required item holder to learn this skill.
	 * @param item the required item holder.
	 */
	public void addRequiredItem(ItemHolder item)
	{
		_requiredItems.add(item);
	}
	
	/**
	 * @return a set with the races that can acquire this skill.
	 */
	public Set<Race> getRaces()
	{
		return _races;
	}
	
	/**
	 * Adds a required race to learn this skill.
	 * @param race the required race.
	 */
	public void addRace(Race race)
	{
		_races.add(race);
	}
	
	/**
	 * @return the set of skill holders required to acquire this skill.
	 */
	public Set<SkillHolder> getPreReqSkills()
	{
		return _preReqSkills;
	}
	
	/**
	 * Adds a required skill holder to learn this skill.
	 * @param skill the required skill holder.
	 */
	public void addPreReqSkill(SkillHolder skill)
	{
		_preReqSkills.add(skill);
	}
	
	/**
	 * @return the social class required to get this skill.
	 */
	public SocialClass getSocialClass()
	{
		return _socialClass;
	}
	
	/**
	 * Sets the social class if hasn't been set before.
	 * @param socialClass the social class to set.
	 */
	public void setSocialClass(SocialClass socialClass)
	{
		if (_socialClass == null)
		{
			_socialClass = socialClass;
		}
	}
	
	/**
	 * @return {@code true} if this skill is a Residence skill.
	 */
	public boolean isResidencialSkill()
	{
		return _residenceSkill;
	}
	
	/**
	 * @return a set with the Ids where this skill is available.
	 */
	public Set<Integer> getResidenceIds()
	{
		return _residenceIds;
	}
	
	/**
	 * Adds a required residence Id.
	 * @param id the residence Id to add.
	 */
	public void addResidenceId(Integer id)
	{
		_residenceIds.add(id);
	}
	
	/**
	 * @return {@code true} if this skill is learned from Npc.
	 */
	public boolean isLearnedByNpc()
	{
		return _learnedByNpc;
	}
	
	/**
	 * @return {@code true} if this skill is learned by Forgotten Scroll.
	 */
	public boolean isLearnedByFS()
	{
		return _learnedByFS;
	}
	
	public void addRemoveSkills(int skillId)
	{
		_removeSkills.add(skillId);
	}
	
	public Set<Integer> getRemoveSkills()
	{
		return _removeSkills;
	}
	
	public int getTreeId()
	{
		return _treeId;
	}
	
	public int getRow()
	{
		return _row;
	}
	
	public int getColumn()
	{
		return _column;
	}
	
	public int getPointsRequired()
	{
		return _pointsRequired;
	}
	
	@Override
	public String toString()
	{
		final Skill skill = SkillData.getInstance().getSkill(_skillId, _skillLevel);
		return "[" + skill + " treeId: " + _treeId + " row: " + _row + " column: " + _column + " pointsRequired:" + _pointsRequired + "]";
	}
}
