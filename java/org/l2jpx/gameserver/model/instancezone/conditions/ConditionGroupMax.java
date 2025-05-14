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
package org.l2jpx.gameserver.model.instancezone.conditions;

import java.util.List;

import org.l2jpx.gameserver.model.StatSet;
import org.l2jpx.gameserver.model.actor.Npc;
import org.l2jpx.gameserver.model.actor.Player;
import org.l2jpx.gameserver.model.instancezone.InstanceTemplate;
import org.l2jpx.gameserver.network.SystemMessageId;

/**
 * Instance enter group max size
 * @author malyelfik
 */
public class ConditionGroupMax extends Condition
{
	public ConditionGroupMax(InstanceTemplate template, StatSet parameters, boolean onlyLeader, boolean showMessageAndHtml)
	{
		super(template, parameters, true, showMessageAndHtml);
		setSystemMessage(SystemMessageId.YOU_CANNOT_ENTER_DUE_TO_THE_PARTY_HAVING_EXCEEDED_THE_LIMIT);
	}
	
	@Override
	protected boolean test(Player player, Npc npc, List<Player> group)
	{
		return group.size() <= getLimit();
	}
	
	public int getLimit()
	{
		return getParameters().getInt("limit");
	}
}