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
package org.l2jpx.gameserver.model.holders;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Mobius
 */
public class AutoUseSettingsHolder
{
	private final Collection<Integer> _autoSupplyItems = ConcurrentHashMap.newKeySet();
	private final Collection<Integer> _autoActions = ConcurrentHashMap.newKeySet();
	private final Collection<Integer> _autoBuffs = ConcurrentHashMap.newKeySet();
	private final List<Integer> _autoSkills = new CopyOnWriteArrayList<>();
	private final AtomicInteger _autoPotionItem = new AtomicInteger();
	private int _skillIndex = 0;
	
	public AutoUseSettingsHolder()
	{
	}
	
	public Collection<Integer> getAutoSupplyItems()
	{
		return _autoSupplyItems;
	}
	
	public Collection<Integer> getAutoActions()
	{
		return _autoActions;
	}
	
	public Collection<Integer> getAutoBuffs()
	{
		return _autoBuffs;
	}
	
	public List<Integer> getAutoSkills()
	{
		return _autoSkills;
	}
	
	public int getAutoPotionItem()
	{
		return _autoPotionItem.get();
	}
	
	public void setAutoPotionItem(int itemId)
	{
		_autoPotionItem.set(itemId);
	}
	
	public boolean isAutoSkill(int skillId)
	{
		return _autoSkills.contains(skillId) || _autoBuffs.contains(skillId);
	}
	
	public Integer getNextSkillId()
	{
		if (_skillIndex >= _autoSkills.size())
		{
			_skillIndex = 0;
		}
		
		Integer skillId = Integer.MIN_VALUE;
		try
		{
			skillId = _autoSkills.get(_skillIndex);
		}
		catch (Exception e)
		{
			resetSkillOrder();
		}
		
		return skillId;
	}
	
	public void incrementSkillOrder()
	{
		_skillIndex++;
	}
	
	public void resetSkillOrder()
	{
		_skillIndex = 0;
	}
	
	public boolean isEmpty()
	{
		return _autoSupplyItems.isEmpty() && (_autoPotionItem.get() == 0) && _autoSkills.isEmpty() && _autoBuffs.isEmpty() && _autoActions.isEmpty();
	}
}
