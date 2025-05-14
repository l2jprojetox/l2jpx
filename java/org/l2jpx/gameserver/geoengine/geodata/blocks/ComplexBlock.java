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
public class ComplexBlock implements IBlock
{
	private final short[] _data;
	
	public ComplexBlock(ByteBuffer bb)
	{
		_data = new short[IBlock.BLOCK_CELLS];
		for (int cellOffset = 0; cellOffset < IBlock.BLOCK_CELLS; cellOffset++)
		{
			_data[cellOffset] = bb.getShort();
		}
	}
	
	private short getCellData(int geoX, int geoY)
	{
		return _data[((geoX % IBlock.BLOCK_CELLS_X) * IBlock.BLOCK_CELLS_Y) + (geoY % IBlock.BLOCK_CELLS_Y)];
	}
	
	private byte getCellNSWE(int geoX, int geoY)
	{
		return (byte) (getCellData(geoX, geoY) & 0x000F);
	}
	
	private int getCellHeight(int geoX, int geoY)
	{
		return (short) (getCellData(geoX, geoY) & 0x0FFF0) >> 1;
	}
	
	@Override
	public boolean checkNearestNswe(int geoX, int geoY, int worldZ, int nswe)
	{
		return (getCellNSWE(geoX, geoY) & nswe) == nswe;
	}
	
	@Override
	public void setNearestNswe(int geoX, int geoY, int worldZ, byte nswe)
	{
		final byte currentNswe = getCellNSWE(geoX, geoY);
		if ((currentNswe & nswe) == 0)
		{
			final short currentHeight = (short) getCellHeight(geoX, geoY);
			final short encodedHeight = (short) (currentHeight << 1); // Shift left by 1 bit.
			final short newNswe = (short) (currentNswe | nswe); // Add NSWE.
			final short newCombinedData = (short) (encodedHeight | newNswe); // Combine height and NSWE.
			_data[((geoX % IBlock.BLOCK_CELLS_X) * IBlock.BLOCK_CELLS_Y) + (geoY % IBlock.BLOCK_CELLS_Y)] = (short) (newCombinedData & 0xffff);
		}
	}
	
	@Override
	public void unsetNearestNswe(int geoX, int geoY, int worldZ, byte nswe)
	{
		final byte currentNswe = getCellNSWE(geoX, geoY);
		if ((currentNswe & nswe) != 0)
		{
			final short currentHeight = (short) getCellHeight(geoX, geoY);
			final short encodedHeight = (short) (currentHeight << 1); // Shift left by 1 bit.
			final short newNswe = (short) (currentNswe & ~nswe); // Subtract NSWE.
			final short newCombinedData = (short) (encodedHeight | newNswe); // Combine height and NSWE.
			_data[((geoX % IBlock.BLOCK_CELLS_X) * IBlock.BLOCK_CELLS_Y) + (geoY % IBlock.BLOCK_CELLS_Y)] = (short) (newCombinedData & 0xffff);
		}
	}
	
	@Override
	public int getNearestZ(int geoX, int geoY, int worldZ)
	{
		return getCellHeight(geoX, geoY);
	}
	
	@Override
	public int getNextLowerZ(int geoX, int geoY, int worldZ)
	{
		final int cellHeight = getCellHeight(geoX, geoY);
		return cellHeight <= worldZ ? cellHeight : worldZ;
	}
	
	@Override
	public int getNextHigherZ(int geoX, int geoY, int worldZ)
	{
		final int cellHeight = getCellHeight(geoX, geoY);
		return cellHeight >= worldZ ? cellHeight : worldZ;
	}
	
	public short[] getData()
	{
		return _data;
	}
}
