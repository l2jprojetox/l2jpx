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

import org.l2jpx.gameserver.enums.ResidenceType;
import org.l2jpx.gameserver.model.StatSet;
import org.l2jpx.gameserver.model.actor.Npc;
import org.l2jpx.gameserver.model.actor.Player;
import org.l2jpx.gameserver.model.clan.Clan;
import org.l2jpx.gameserver.model.instancezone.InstanceTemplate;

/**
 * Instance residence condition
 * @author malyelfik
 */
public class ConditionHasResidence extends Condition
{
	public ConditionHasResidence(InstanceTemplate template, StatSet parameters, boolean onlyLeader, boolean showMessageAndHtml)
	{
		super(template, parameters, onlyLeader, showMessageAndHtml);
	}
	
	@Override
	protected boolean test(Player player, Npc npc)
	{
		final Clan clan = player.getClan();
		if (clan == null)
		{
			return false;
		}
		
		boolean test = false;
		final StatSet params = getParameters();
		final int id = params.getInt("id");
		final ResidenceType type = params.getEnum("type", ResidenceType.class);
		switch (type)
		{
			case CASTLE:
			{
				test = clan.getCastleId() == id;
				break;
			}
			case FORTRESS:
			{
				test = clan.getFortId() == id;
				break;
			}
			case CLANHALL:
			{
				test = clan.getHideoutId() == id;
				break;
			}
		}
		return test;
	}
}