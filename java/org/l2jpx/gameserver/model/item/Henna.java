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
package org.l2jpx.gameserver.model.item;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.l2jpx.gameserver.enums.ClassId;
import org.l2jpx.gameserver.model.StatSet;
import org.l2jpx.gameserver.model.skill.Skill;
import org.l2jpx.gameserver.model.stats.BaseStat;

/**
 * Class for the Henna object.
 * @author Zoey76
 */
public class Henna
{
	private final int _dyeId;
	private final int _dyeItemId;
	private final Map<BaseStat, Integer> _baseStats = new EnumMap<>(BaseStat.class);
	private final int _wearFee;
	private final int _wearCount;
	private final int _cancelFee;
	private final int _cancelCount;
	private final int _duration;
	private final List<Skill> _skills;
	private final Set<ClassId> _wearClass;
	
	public Henna(StatSet set)
	{
		_dyeId = set.getInt("dyeId");
		_dyeItemId = set.getInt("dyeItemId");
		_baseStats.put(BaseStat.STR, set.getInt("str", 0));
		_baseStats.put(BaseStat.CON, set.getInt("con", 0));
		_baseStats.put(BaseStat.DEX, set.getInt("dex", 0));
		_baseStats.put(BaseStat.INT, set.getInt("int", 0));
		_baseStats.put(BaseStat.MEN, set.getInt("men", 0));
		_baseStats.put(BaseStat.WIT, set.getInt("wit", 0));
		_wearFee = set.getInt("wear_fee");
		_wearCount = set.getInt("wear_count");
		_cancelFee = set.getInt("cancel_fee");
		_cancelCount = set.getInt("cancel_count");
		_duration = set.getInt("duration", -1);
		_skills = new ArrayList<>();
		_wearClass = new HashSet<>();
	}
	
	/**
	 * @return the dye Id.
	 */
	public int getDyeId()
	{
		return _dyeId;
	}
	
	/**
	 * @return the item Id, required for this dye.
	 */
	public int getDyeItemId()
	{
		return _dyeItemId;
	}
	
	public int getBaseStats(BaseStat stat)
	{
		return !_baseStats.containsKey(stat) ? 0 : _baseStats.get(stat).intValue();
	}
	
	public Map<BaseStat, Integer> getBaseStats()
	{
		return _baseStats;
	}
	
	/**
	 * @return the wear fee, cost for adding this dye to the player.
	 */
	public int getWearFee()
	{
		return _wearFee;
	}
	
	/**
	 * @return the wear count, the required count to add this dye to the player.
	 */
	public int getWearCount()
	{
		return _wearCount;
	}
	
	/**
	 * @return the cancel fee, cost for removing this dye from the player.
	 */
	public int getCancelFee()
	{
		return _cancelFee;
	}
	
	/**
	 * @return the cancel count, the retrieved amount of dye items after removing the dye.
	 */
	public int getCancelCount()
	{
		return _cancelCount;
	}
	
	/**
	 * @return the duration of this dye.
	 */
	public int getDuration()
	{
		return _duration;
	}
	
	/**
	 * @param skillList the list of skills related to this dye.
	 */
	public void setSkills(List<Skill> skillList)
	{
		_skills.addAll(skillList);
	}
	
	/**
	 * @return the skills related to this dye.
	 */
	public List<Skill> getSkills()
	{
		return _skills;
	}
	
	/**
	 * @return the list with the allowed classes to wear this dye.
	 */
	public Set<ClassId> getAllowedWearClass()
	{
		return _wearClass;
	}
	
	/**
	 * @param classId the class trying to wear this dye.
	 * @return {@code true} if the player is allowed to wear this dye, {@code false} otherwise.
	 */
	public boolean isAllowedClass(ClassId classId)
	{
		return _wearClass.contains(classId);
	}
	
	/**
	 * @param wearClassIds the list of classes that can wear this dye.
	 */
	public void setWearClassIds(List<ClassId> wearClassIds)
	{
		_wearClass.addAll(wearClassIds);
	}
}