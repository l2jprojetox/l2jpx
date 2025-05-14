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
package org.l2jpx.gameserver.model.zone.type;

import org.l2jpx.Config;
import org.l2jpx.commons.threads.ThreadPool;
import org.l2jpx.gameserver.enums.CategoryType;
import org.l2jpx.gameserver.model.actor.Creature;
import org.l2jpx.gameserver.model.actor.tasks.player.FlyMoveStartTask;
import org.l2jpx.gameserver.model.zone.ZoneId;
import org.l2jpx.gameserver.model.zone.ZoneType;

/**
 * @author UnAfraid
 */
public class SayuneZone extends ZoneType
{
	private int _mapId = -1;
	
	public SayuneZone(int id)
	{
		super(id);
	}
	
	@Override
	public void setParameter(String name, String value)
	{
		switch (name)
		{
			case "mapId":
			{
				_mapId = Integer.parseInt(value);
				break;
			}
			default:
			{
				super.setParameter(name, value);
			}
		}
	}
	
	@Override
	protected void onEnter(Creature creature)
	{
		if (creature.isPlayer() && (creature.isInCategory(CategoryType.SIXTH_CLASS_GROUP) || Config.FREE_JUMPS_FOR_ALL) && !creature.getActingPlayer().isMounted() && !creature.isTransformed())
		{
			creature.setInsideZone(ZoneId.SAYUNE, true);
			ThreadPool.execute(new FlyMoveStartTask(this, creature.getActingPlayer()));
		}
	}
	
	@Override
	protected void onExit(Creature creature)
	{
		if (creature.isPlayer())
		{
			creature.setInsideZone(ZoneId.SAYUNE, false);
		}
	}
	
	public int getMapId()
	{
		return _mapId;
	}
}
