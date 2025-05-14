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
package org.l2jpx.gameserver.instancemanager;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.l2jpx.commons.database.DatabaseFactory;
import org.l2jpx.gameserver.model.WorldObject;
import org.l2jpx.gameserver.model.clan.Clan;
import org.l2jpx.gameserver.model.siege.Fort;

public class FortManager
{
	private static final Logger LOGGER = Logger.getLogger(FortManager.class.getName());
	
	private static final Map<Integer, Fort> _forts = new ConcurrentSkipListMap<>();
	
	public Fort findNearestFort(WorldObject obj)
	{
		return findNearestFort(obj, Long.MAX_VALUE);
	}
	
	public Fort findNearestFort(WorldObject obj, long maxDistanceValue)
	{
		Fort nearestFort = getFort(obj);
		if (nearestFort == null)
		{
			long maxDistance = maxDistanceValue;
			for (Fort fort : _forts.values())
			{
				final double distance = fort.getDistance(obj);
				if (maxDistance > distance)
				{
					maxDistance = (long) distance;
					nearestFort = fort;
				}
			}
		}
		return nearestFort;
	}
	
	public Fort getFortById(int fortId)
	{
		for (Fort f : _forts.values())
		{
			if (f.getResidenceId() == fortId)
			{
				return f;
			}
		}
		return null;
	}
	
	public Fort getFortByOwner(Clan clan)
	{
		if (clan == null)
		{
			return null;
		}
		for (Fort f : _forts.values())
		{
			if (f.getOwnerClan() == clan)
			{
				return f;
			}
		}
		return null;
	}
	
	public Fort getFort(String name)
	{
		for (Fort f : _forts.values())
		{
			if (f.getName().equalsIgnoreCase(name.trim()))
			{
				return f;
			}
		}
		return null;
	}
	
	public Fort getFort(int x, int y, int z)
	{
		for (Fort f : _forts.values())
		{
			if (f.checkIfInZone(x, y, z))
			{
				return f;
			}
		}
		return null;
	}
	
	public Fort getFort(WorldObject activeObject)
	{
		return getFort(activeObject.getX(), activeObject.getY(), activeObject.getZ());
	}
	
	public Collection<Fort> getForts()
	{
		return _forts.values();
	}
	
	public void loadInstances()
	{
		try (Connection con = DatabaseFactory.getConnection();
			Statement s = con.createStatement();
			ResultSet rs = s.executeQuery("SELECT id FROM fort ORDER BY id"))
		{
			while (rs.next())
			{
				final int fortId = rs.getInt("id");
				_forts.put(fortId, new Fort(fortId));
			}
			
			LOGGER.info(getClass().getSimpleName() + ": Loaded " + _forts.values().size() + " fortress.");
			for (Fort fort : _forts.values())
			{
				fort.getSiege().loadSiegeGuard();
			}
		}
		catch (Exception e)
		{
			LOGGER.log(Level.WARNING, "Exception: loadFortData(): " + e.getMessage(), e);
		}
	}
	
	public void activateInstances()
	{
		for (Fort fort : _forts.values())
		{
			fort.activateInstance();
		}
	}
	
	public static FortManager getInstance()
	{
		return SingletonHolder.INSTANCE;
	}
	
	private static class SingletonHolder
	{
		protected static final FortManager INSTANCE = new FortManager();
	}
}
