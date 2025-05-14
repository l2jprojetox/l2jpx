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
package org.l2jpx.gameserver.geoengine.pathfinding.geonodes;

import org.l2jpx.gameserver.geoengine.pathfinding.AbstractNodeLoc;
import org.l2jpx.gameserver.model.World;

/**
 * @author -Nemesiss-
 */
public class GeoNodeLoc extends AbstractNodeLoc
{
	private final short _x;
	private final short _y;
	private final short _z;
	
	public GeoNodeLoc(short x, short y, short z)
	{
		_x = x;
		_y = y;
		_z = z;
	}
	
	@Override
	public int getX()
	{
		return World.WORLD_X_MIN + (_x * 128) + 48;
	}
	
	@Override
	public int getY()
	{
		return World.WORLD_Y_MIN + (_y * 128) + 48;
	}
	
	@Override
	public int getZ()
	{
		return _z;
	}
	
	@Override
	public void setZ(short z)
	{
	}
	
	@Override
	public int getNodeX()
	{
		return _x;
	}
	
	@Override
	public int getNodeY()
	{
		return _y;
	}
	
	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = (prime * result) + _x;
		result = (prime * result) + _y;
		result = (prime * result) + _z;
		return result;
	}
	
	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
		{
			return true;
		}
		if (obj == null)
		{
			return false;
		}
		if (!(obj instanceof GeoNodeLoc))
		{
			return false;
		}
		final GeoNodeLoc other = (GeoNodeLoc) obj;
		return (_x == other._x) && (_y == other._y) && (_z == other._z);
	}
}
