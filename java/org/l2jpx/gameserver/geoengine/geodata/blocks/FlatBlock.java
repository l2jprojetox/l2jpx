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
package org.l2jpx.gameserver.geoengine.geodata.blocks;

import java.nio.ByteBuffer;

import org.l2jpx.gameserver.geoengine.geodata.IBlock;

/**
 * @author HorridoJoho
 */
public class FlatBlock implements IBlock
{
	private final short _height;
	
	public FlatBlock(ByteBuffer bb)
	{
		_height = bb.getShort();
	}
	
	@Override
	public boolean checkNearestNswe(int geoX, int geoY, int worldZ, int nswe)
	{
		return true;
	}
	
	@Override
	public void setNearestNswe(int geoX, int geoY, int worldZ, byte nswe)
	{
		throw new RuntimeException("Cannot set NSWE on a flat block!");
	}
	
	@Override
	public void unsetNearestNswe(int geoX, int geoY, int worldZ, byte nswe)
	{
		throw new RuntimeException("Cannot unset NSWE on a flat block!");
	}
	
	@Override
	public int getNearestZ(int geoX, int geoY, int worldZ)
	{
		return _height;
	}
	
	@Override
	public int getNextLowerZ(int geoX, int geoY, int worldZ)
	{
		return _height <= worldZ ? _height : worldZ;
	}
	
	@Override
	public int getNextHigherZ(int geoX, int geoY, int worldZ)
	{
		return _height >= worldZ ? _height : worldZ;
	}
	
	public short getHeight()
	{
		return _height;
	}
}
