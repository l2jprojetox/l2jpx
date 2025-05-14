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
package org.l2jpx.gameserver.model.conditions;

import java.util.Set;

import org.l2jpx.gameserver.model.actor.Creature;
import org.l2jpx.gameserver.model.actor.Player;
import org.l2jpx.gameserver.model.instancezone.Instance;
import org.l2jpx.gameserver.model.item.ItemTemplate;
import org.l2jpx.gameserver.model.skill.Skill;

/**
 * The Class ConditionPlayerInstanceId.
 */
public class ConditionPlayerInstanceId extends Condition
{
	private final Set<Integer> _instanceIds;
	
	/**
	 * Instantiates a new condition player instance id.
	 * @param instanceIds the instance ids
	 */
	public ConditionPlayerInstanceId(Set<Integer> instanceIds)
	{
		_instanceIds = instanceIds;
	}
	
	@Override
	public boolean testImpl(Creature effector, Creature effected, Skill skill, ItemTemplate item)
	{
		final Player player = effector.getActingPlayer();
		if (player == null)
		{
			return false;
		}
		
		final Instance instance = player.getInstanceWorld();
		return (instance != null) && _instanceIds.contains(instance.getTemplateId());
	}
}
