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

import org.l2jpx.gameserver.data.xml.ClanHallData;
import org.l2jpx.gameserver.model.Location;
import org.l2jpx.gameserver.model.actor.Creature;
import org.l2jpx.gameserver.model.residences.ClanHall;
import org.l2jpx.gameserver.model.zone.ZoneId;

/**
 * A clan hall zone
 * @author durgus
 */
public class ClanHallZone extends ResidenceZone
{
	public ClanHallZone(int id)
	{
		super(id);
	}
	
	@Override
	public void setParameter(String name, String value)
	{
		if (name.equals("clanHallId"))
		{
			setResidenceId(Integer.parseInt(value));
		}
		else
		{
			super.setParameter(name, value);
		}
	}
	
	@Override
	protected void onEnter(Creature creature)
	{
		if (creature.isPlayer())
		{
			creature.setInsideZone(ZoneId.CLAN_HALL, true);
		}
	}
	
	@Override
	protected void onExit(Creature creature)
	{
		if (creature.isPlayer())
		{
			creature.setInsideZone(ZoneId.CLAN_HALL, false);
		}
	}
	
	@Override
	public Location getBanishSpawnLoc()
	{
		final ClanHall clanHall = ClanHallData.getInstance().getClanHallById(getResidenceId());
		if (clanHall == null)
		{
			return null;
		}
		return clanHall.getBanishLocation();
	}
}