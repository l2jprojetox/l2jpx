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
package org.l2jpx.gameserver.geoengine.geodata;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteOrder;
import java.nio.channels.FileChannel.MapMode;
import java.nio.file.Path;
import java.util.concurrent.atomic.AtomicReferenceArray;

import org.l2jpx.gameserver.geoengine.geodata.regions.NullRegion;
import org.l2jpx.gameserver.geoengine.geodata.regions.Region;

/**
 * @author HorridoJoho
 */
public class GeoData
{
	// World dimensions: 1048576 * 1048576 = 1099511627776
	private static final int WORLD_MIN_X = -655360;
	private static final int WORLD_MIN_Y = -589824;
	// private static final int WORLD_MAX_X = 393215;
	// private static final int WORLD_MAX_Y = 458751;
	
	/** Regions in the world on the x axis */
	public static final int GEO_REGIONS_X = 32;
	/** Regions in the world on the y axis */
	public static final int GEO_REGIONS_Y = 32;
	/** Region in the world */
	public static final int GEO_REGIONS = GEO_REGIONS_X * GEO_REGIONS_Y;
	
	/** Blocks in the world on the x axis */
	// public static final int GEO_BLOCKS_X = GEO_REGIONS_X * IRegion.REGION_BLOCKS_X;
	/** Blocks in the world on the y axis */
	// public static final int GEO_BLOCKS_Y = GEO_REGIONS_Y * IRegion.REGION_BLOCKS_Y;
	/** Blocks in the world */
	// public static final int GEO_BLOCKS = GEO_REGIONS * IRegion.REGION_BLOCKS;
	
	/** Cells in the world on the x axis */
	// public static final int GEO_CELLS_X = GEO_BLOCKS_X * IBlock.BLOCK_CELLS_X;
	/** Cells in the world in the y axis */
	// public static final int GEO_CELLS_Y = GEO_BLOCKS_Y * IBlock.BLOCK_CELLS_Y;
	
	/** The regions array */
	private final AtomicReferenceArray<IRegion> _regions = new AtomicReferenceArray<>(GEO_REGIONS);
	
	public GeoData()
	{
		for (int i = 0; i < GEO_REGIONS; i++)
		{
			_regions.set(i, NullRegion.INSTANCE);
		}
	}
	
	// private void checkGeoX(int geoX)
	// {
	// if ((geoX < 0) || (geoX >= GEO_CELLS_X))
	// {
	// throw new IllegalArgumentException();
	// }
	// }
	
	// private void checkGeoY(int geoY)
	// {
	// if ((geoY < 0) || (geoY >= GEO_CELLS_Y))
	// {
	// throw new IllegalArgumentException();
	// }
	// }
	
	public IRegion getRegion(int geoX, int geoY)
	{
		// checkGeoX(geoX);
		// checkGeoY(geoY);
		return _regions.get(((geoX / IRegion.REGION_CELLS_X) * GEO_REGIONS_Y) + (geoY / IRegion.REGION_CELLS_Y));
	}
	
	public void loadRegion(Path filePath, int regionX, int regionY) throws IOException
	{
		final int regionOffset = (regionX * GEO_REGIONS_Y) + regionY;
		try (RandomAccessFile raf = new RandomAccessFile(filePath.toFile(), "r"))
		{
			_regions.set(regionOffset, new Region(raf.getChannel().map(MapMode.READ_ONLY, 0, raf.length()).order(ByteOrder.LITTLE_ENDIAN)));
		}
	}
	
	public void setRegion(int regionX, int regionY, Region region)
	{
		final int regionOffset = (regionX * GEO_REGIONS_Y) + regionY;
		_regions.set(regionOffset, region);
	}
	
	public void unloadRegion(int regionX, int regionY)
	{
		_regions.set((regionX * GEO_REGIONS_Y) + regionY, NullRegion.INSTANCE);
	}
	
	public boolean hasGeoPos(int geoX, int geoY)
	{
		return getRegion(geoX, geoY).hasGeo();
	}
	
	public boolean checkNearestNswe(int geoX, int geoY, int worldZ, int nswe)
	{
		return getRegion(geoX, geoY).checkNearestNswe(geoX, geoY, worldZ, nswe);
	}
	
	public void setNearestNswe(int geoX, int geoY, int worldZ, byte nswe)
	{
		getRegion(geoX, geoY).setNearestNswe(geoX, geoY, worldZ, nswe);
	}
	
	public void unsetNearestNswe(int geoX, int geoY, int worldZ, byte nswe)
	{
		getRegion(geoX, geoY).unsetNearestNswe(geoX, geoY, worldZ, nswe);
	}
	
	public int getNearestZ(int geoX, int geoY, int worldZ)
	{
		return getRegion(geoX, geoY).getNearestZ(geoX, geoY, worldZ);
	}
	
	public int getNextLowerZ(int geoX, int geoY, int worldZ)
	{
		return getRegion(geoX, geoY).getNextLowerZ(geoX, geoY, worldZ);
	}
	
	public int getNextHigherZ(int geoX, int geoY, int worldZ)
	{
		return getRegion(geoX, geoY).getNextHigherZ(geoX, geoY, worldZ);
	}
	
	public int getGeoX(int worldX)
	{
		// if ((worldX < WORLD_MIN_X) || (worldX > WORLD_MAX_X))
		// {
		// throw new IllegalArgumentException();
		// }
		return (worldX - WORLD_MIN_X) / 16;
	}
	
	public int getGeoY(int worldY)
	{
		// if ((worldY < WORLD_MIN_Y) || (worldY > WORLD_MAX_Y))
		// {
		// throw new IllegalArgumentException();
		// }
		return (worldY - WORLD_MIN_Y) / 16;
	}
	
	public int getWorldX(int geoX)
	{
		// checkGeoX(geoX);
		return (geoX * 16) + WORLD_MIN_X + 8;
	}
	
	public int getWorldY(int geoY)
	{
		// checkGeoY(geoY);
		return (geoY * 16) + WORLD_MIN_Y + 8;
	}
}
