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
package org.l2jpx.gameserver.geoengine;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.l2jpx.Config;
import org.l2jpx.gameserver.data.xml.DoorData;
import org.l2jpx.gameserver.data.xml.FenceData;
import org.l2jpx.gameserver.geoengine.geodata.Cell;
import org.l2jpx.gameserver.geoengine.geodata.GeoData;
import org.l2jpx.gameserver.geoengine.geodata.IRegion;
import org.l2jpx.gameserver.geoengine.geodata.regions.Region;
import org.l2jpx.gameserver.model.Location;
import org.l2jpx.gameserver.model.World;
import org.l2jpx.gameserver.model.WorldObject;
import org.l2jpx.gameserver.model.instancezone.Instance;
import org.l2jpx.gameserver.model.interfaces.ILocational;
import org.l2jpx.gameserver.util.GeoUtils;
import org.l2jpx.gameserver.util.LinePointIterator;
import org.l2jpx.gameserver.util.LinePointIterator3D;

/**
 * GeoEngine.
 * @author -Nemesiss-, HorridoJoho
 */
public class GeoEngine
{
	private static final Logger LOGGER = Logger.getLogger(GeoEngine.class.getName());
	
	public static final String FILE_NAME_FORMAT = "%d_%d.l2j";
	
	private static final int ELEVATED_SEE_OVER_DISTANCE = 2;
	private static final int MAX_SEE_OVER_HEIGHT = 48;
	private static final int SPAWN_Z_DELTA_LIMIT = 100;
	
	private final GeoData _geodata = new GeoData();
	
	protected GeoEngine()
	{
		int loadedRegions = 0;
		try
		{
			for (int regionX = World.TILE_X_MIN; regionX <= World.TILE_X_MAX; regionX++)
			{
				for (int regionY = World.TILE_Y_MIN; regionY <= World.TILE_Y_MAX; regionY++)
				{
					final Path geoFilePath = Config.GEODATA_PATH.resolve(String.format(FILE_NAME_FORMAT, regionX, regionY));
					if (Files.exists(geoFilePath))
					{
						try
						{
							// LOGGER.info(getClass().getSimpleName() + ": Loading " + geoFilePath.getFileName() + "...");
							_geodata.loadRegion(geoFilePath, regionX, regionY);
							loadedRegions++;
						}
						catch (Exception e)
						{
							LOGGER.log(Level.WARNING, getClass().getSimpleName() + ": Failed to load " + geoFilePath.getFileName() + "!", e);
						}
					}
				}
			}
		}
		catch (Exception e)
		{
			LOGGER.log(Level.SEVERE, getClass().getSimpleName() + ": Failed to load geodata!", e);
			System.exit(1);
		}
		
		LOGGER.info(getClass().getSimpleName() + ": Loaded " + loadedRegions + " regions.");
		
		// Avoid wrong configuration when no files are loaded.
		if ((loadedRegions == 0) && (Config.PATHFINDING > 0))
		{
			Config.PATHFINDING = 0;
			LOGGER.info(getClass().getSimpleName() + ": Pathfinding is disabled.");
		}
	}
	
	public boolean hasGeoPos(int geoX, int geoY)
	{
		return _geodata.hasGeoPos(geoX, geoY);
	}
	
	public boolean checkNearestNswe(int geoX, int geoY, int worldZ, int nswe)
	{
		return _geodata.checkNearestNswe(geoX, geoY, worldZ, nswe);
	}
	
	public boolean checkNearestNsweAntiCornerCut(int geoX, int geoY, int worldZ, int nswe)
	{
		boolean can = true;
		if ((nswe & Cell.NSWE_NORTH_EAST) == Cell.NSWE_NORTH_EAST)
		{
			// can = canEnterNeighbors(prevX, prevY - 1, prevGeoZ, Direction.EAST) && canEnterNeighbors(prevX + 1, prevY, prevGeoZ, Direction.NORTH);
			can = checkNearestNswe(geoX, geoY - 1, worldZ, Cell.NSWE_EAST) && checkNearestNswe(geoX + 1, geoY, worldZ, Cell.NSWE_NORTH);
		}
		
		if (can && ((nswe & Cell.NSWE_NORTH_WEST) == Cell.NSWE_NORTH_WEST))
		{
			// can = canEnterNeighbors(prevX, prevY - 1, prevGeoZ, Direction.WEST) && canEnterNeighbors(prevX - 1, prevY, prevGeoZ, Direction.NORTH);
			can = checkNearestNswe(geoX, geoY - 1, worldZ, Cell.NSWE_WEST) && checkNearestNswe(geoX, geoY - 1, worldZ, Cell.NSWE_NORTH);
		}
		
		if (can && ((nswe & Cell.NSWE_SOUTH_EAST) == Cell.NSWE_SOUTH_EAST))
		{
			// can = canEnterNeighbors(prevX, prevY + 1, prevGeoZ, Direction.EAST) && canEnterNeighbors(prevX + 1, prevY, prevGeoZ, Direction.SOUTH);
			can = checkNearestNswe(geoX, geoY + 1, worldZ, Cell.NSWE_EAST) && checkNearestNswe(geoX + 1, geoY, worldZ, Cell.NSWE_SOUTH);
		}
		
		if (can && ((nswe & Cell.NSWE_SOUTH_WEST) == Cell.NSWE_SOUTH_WEST))
		{
			// can = canEnterNeighbors(prevX, prevY + 1, prevGeoZ, Direction.WEST) && canEnterNeighbors(prevX - 1, prevY, prevGeoZ, Direction.SOUTH);
			can = checkNearestNswe(geoX, geoY + 1, worldZ, Cell.NSWE_WEST) && checkNearestNswe(geoX - 1, geoY, worldZ, Cell.NSWE_SOUTH);
		}
		
		return can && checkNearestNswe(geoX, geoY, worldZ, nswe);
	}
	
	public void setNearestNswe(int geoX, int geoY, int worldZ, byte nswe)
	{
		_geodata.setNearestNswe(geoX, geoY, worldZ, nswe);
	}
	
	public void unsetNearestNswe(int geoX, int geoY, int worldZ, byte nswe)
	{
		_geodata.unsetNearestNswe(geoX, geoY, worldZ, nswe);
	}
	
	public int getNearestZ(int geoX, int geoY, int worldZ)
	{
		return _geodata.getNearestZ(geoX, geoY, worldZ);
	}
	
	public int getNextLowerZ(int geoX, int geoY, int worldZ)
	{
		return _geodata.getNextLowerZ(geoX, geoY, worldZ);
	}
	
	public int getNextHigherZ(int geoX, int geoY, int worldZ)
	{
		return _geodata.getNextHigherZ(geoX, geoY, worldZ);
	}
	
	public int getGeoX(int worldX)
	{
		return _geodata.getGeoX(worldX);
	}
	
	public int getGeoY(int worldY)
	{
		return _geodata.getGeoY(worldY);
	}
	
	public int getWorldX(int geoX)
	{
		return _geodata.getWorldX(geoX);
	}
	
	public int getWorldY(int geoY)
	{
		return _geodata.getWorldY(geoY);
	}
	
	public IRegion getRegion(int geoX, int geoY)
	{
		return _geodata.getRegion(geoX, geoY);
	}
	
	public void setRegion(int regionX, int regionY, Region region)
	{
		_geodata.setRegion(regionX, regionY, region);
	}
	
	/**
	 * Gets the height.
	 * @param x the x coordinate
	 * @param y the y coordinate
	 * @param z the z coordinate
	 * @return the height
	 */
	public int getHeight(int x, int y, int z)
	{
		return getNearestZ(getGeoX(x), getGeoY(y), z);
	}
	
	/**
	 * Gets the spawn height.
	 * @param x the x coordinate
	 * @param y the y coordinate
	 * @param z the the z coordinate
	 * @return the spawn height
	 */
	public int getSpawnHeight(int x, int y, int z)
	{
		final int geoX = getGeoX(x);
		final int geoY = getGeoY(y);
		
		if (!hasGeoPos(geoX, geoY))
		{
			return z;
		}
		
		final int nextLowerZ = getNextLowerZ(geoX, geoY, z + 20);
		return Math.abs(nextLowerZ - z) <= SPAWN_Z_DELTA_LIMIT ? nextLowerZ : z;
	}
	
	/**
	 * Gets the spawn height.
	 * @param location the location
	 * @return the spawn height
	 */
	public int getSpawnHeight(Location location)
	{
		return getSpawnHeight(location.getX(), location.getY(), location.getZ());
	}
	
	/**
	 * Can see target. Doors as target always return true. Checks doors between.
	 * @param cha the character
	 * @param target the target
	 * @return {@code true} if the character can see the target (LOS), {@code false} otherwise
	 */
	public boolean canSeeTarget(WorldObject cha, WorldObject target)
	{
		return (target != null) && (target.isDoor() || canSeeTarget(cha.getX(), cha.getY(), cha.getZ(), cha.getInstanceWorld(), target.getX(), target.getY(), target.getZ(), target.getInstanceWorld()));
	}
	
	/**
	 * Can see target. Checks doors between.
	 * @param cha the character
	 * @param worldPosition the world position
	 * @return {@code true} if the character can see the target at the given world position, {@code false} otherwise
	 */
	public boolean canSeeTarget(WorldObject cha, ILocational worldPosition)
	{
		return canSeeTarget(cha.getX(), cha.getY(), cha.getZ(), worldPosition.getX(), worldPosition.getY(), worldPosition.getZ(), cha.getInstanceWorld());
	}
	
	/**
	 * Can see target. Checks doors between.
	 * @param x the x coordinate
	 * @param y the y coordinate
	 * @param z the z coordinate
	 * @param instance
	 * @param tx the target's x coordinate
	 * @param ty the target's y coordinate
	 * @param tz the target's z coordinate
	 * @param tInstance the target's instance
	 * @return
	 */
	public boolean canSeeTarget(int x, int y, int z, Instance instance, int tx, int ty, int tz, Instance tInstance)
	{
		return (instance == tInstance) && canSeeTarget(x, y, z, tx, ty, tz, instance);
	}
	
	/**
	 * Can see target. Checks doors between.
	 * @param x the x coordinate
	 * @param y the y coordinate
	 * @param z the z coordinate
	 * @param tx the target's x coordinate
	 * @param ty the target's y coordinate
	 * @param tz the target's z coordinate
	 * @param instance
	 * @return {@code true} if there is line of sight between the given coordinate sets, {@code false} otherwise
	 */
	public boolean canSeeTarget(int x, int y, int z, int tx, int ty, int tz, Instance instance)
	{
		// Door checks.
		if (DoorData.getInstance().checkIfDoorsBetween(x, y, z, tx, ty, tz, instance, true))
		{
			return false;
		}
		
		// Fence checks.
		if (FenceData.getInstance().checkIfFenceBetween(x, y, z, tx, ty, tz, instance))
		{
			return false;
		}
		
		return canSeeTarget(x, y, z, tx, ty, tz);
	}
	
	private int getLosGeoZ(int prevX, int prevY, int prevGeoZ, int curX, int curY, int nswe)
	{
		if ((((nswe & Cell.NSWE_NORTH) != 0) && ((nswe & Cell.NSWE_SOUTH) != 0)) || (((nswe & Cell.NSWE_WEST) != 0) && ((nswe & Cell.NSWE_EAST) != 0)))
		{
			throw new RuntimeException("Multiple directions!");
		}
		return checkNearestNsweAntiCornerCut(prevX, prevY, prevGeoZ, nswe) ? getNearestZ(curX, curY, prevGeoZ) : getNextHigherZ(curX, curY, prevGeoZ);
	}
	
	/**
	 * Can see target. Does not check doors between.
	 * @param x the x coordinate
	 * @param y the y coordinate
	 * @param z the z coordinate
	 * @param tx the target's x coordinate
	 * @param ty the target's y coordinate
	 * @param tz the target's z coordinate
	 * @return {@code true} if there is line of sight between the given coordinate sets, {@code false} otherwise
	 */
	public boolean canSeeTarget(int x, int y, int z, int tx, int ty, int tz)
	{
		int geoX = getGeoX(x);
		int geoY = getGeoY(y);
		int tGeoX = getGeoX(tx);
		int tGeoY = getGeoY(ty);
		
		int nearestFromZ = getNearestZ(geoX, geoY, z);
		int nearestToZ = getNearestZ(tGeoX, tGeoY, tz);
		
		// Fastpath.
		if ((geoX == tGeoX) && (geoY == tGeoY))
		{
			return !hasGeoPos(tGeoX, tGeoY) || (nearestFromZ == nearestToZ);
		}
		
		int fromX = tx;
		int fromY = ty;
		int toX = tx;
		int toY = ty;
		if (nearestToZ > nearestFromZ)
		{
			int tmp = toX;
			toX = fromX;
			fromX = tmp;
			
			tmp = toY;
			toY = fromY;
			fromY = tmp;
			
			tmp = nearestToZ;
			nearestToZ = nearestFromZ;
			nearestFromZ = tmp;
			
			tmp = tGeoX;
			tGeoX = geoX;
			geoX = tmp;
			
			tmp = tGeoY;
			tGeoY = geoY;
			geoY = tmp;
		}
		
		final LinePointIterator3D pointIter = new LinePointIterator3D(geoX, geoY, nearestFromZ, tGeoX, tGeoY, nearestToZ);
		// First point is guaranteed to be available, skip it, we can always see our own position.
		pointIter.next();
		int prevX = pointIter.x();
		int prevY = pointIter.y();
		final int prevZ = pointIter.z();
		int prevGeoZ = prevZ;
		int ptIndex = 0;
		while (pointIter.next())
		{
			final int curX = pointIter.x();
			final int curY = pointIter.y();
			
			if ((curX == prevX) && (curY == prevY))
			{
				continue;
			}
			
			final int beeCurZ = pointIter.z();
			int curGeoZ = prevGeoZ;
			
			// Check if the position has geodata.
			if (hasGeoPos(curX, curY))
			{
				final int nswe = GeoUtils.computeNswe(prevX, prevY, curX, curY);
				curGeoZ = getLosGeoZ(prevX, prevY, prevGeoZ, curX, curY, nswe);
				final int maxHeight = ptIndex < ELEVATED_SEE_OVER_DISTANCE ? nearestFromZ + MAX_SEE_OVER_HEIGHT : beeCurZ + MAX_SEE_OVER_HEIGHT;
				boolean canSeeThrough = false;
				if (curGeoZ <= maxHeight)
				{
					if ((nswe & Cell.NSWE_NORTH_EAST) == Cell.NSWE_NORTH_EAST)
					{
						final int northGeoZ = getLosGeoZ(prevX, prevY, prevGeoZ, prevX, prevY - 1, Cell.NSWE_EAST);
						final int eastGeoZ = getLosGeoZ(prevX, prevY, prevGeoZ, prevX + 1, prevY, Cell.NSWE_NORTH);
						canSeeThrough = (northGeoZ <= maxHeight) && (eastGeoZ <= maxHeight) && (northGeoZ <= getNearestZ(prevX, prevY - 1, beeCurZ)) && (eastGeoZ <= getNearestZ(prevX + 1, prevY, beeCurZ));
					}
					else if ((nswe & Cell.NSWE_NORTH_WEST) == Cell.NSWE_NORTH_WEST)
					{
						final int northGeoZ = getLosGeoZ(prevX, prevY, prevGeoZ, prevX, prevY - 1, Cell.NSWE_WEST);
						final int westGeoZ = getLosGeoZ(prevX, prevY, prevGeoZ, prevX - 1, prevY, Cell.NSWE_NORTH);
						canSeeThrough = (northGeoZ <= maxHeight) && (westGeoZ <= maxHeight) && (northGeoZ <= getNearestZ(prevX, prevY - 1, beeCurZ)) && (westGeoZ <= getNearestZ(prevX - 1, prevY, beeCurZ));
					}
					else if ((nswe & Cell.NSWE_SOUTH_EAST) == Cell.NSWE_SOUTH_EAST)
					{
						final int southGeoZ = getLosGeoZ(prevX, prevY, prevGeoZ, prevX, prevY + 1, Cell.NSWE_EAST);
						final int eastGeoZ = getLosGeoZ(prevX, prevY, prevGeoZ, prevX + 1, prevY, Cell.NSWE_SOUTH);
						canSeeThrough = (southGeoZ <= maxHeight) && (eastGeoZ <= maxHeight) && (southGeoZ <= getNearestZ(prevX, prevY + 1, beeCurZ)) && (eastGeoZ <= getNearestZ(prevX + 1, prevY, beeCurZ));
					}
					else if ((nswe & Cell.NSWE_SOUTH_WEST) == Cell.NSWE_SOUTH_WEST)
					{
						final int southGeoZ = getLosGeoZ(prevX, prevY, prevGeoZ, prevX, prevY + 1, Cell.NSWE_WEST);
						final int westGeoZ = getLosGeoZ(prevX, prevY, prevGeoZ, prevX - 1, prevY, Cell.NSWE_SOUTH);
						canSeeThrough = (southGeoZ <= maxHeight) && (westGeoZ <= maxHeight) && (southGeoZ <= getNearestZ(prevX, prevY + 1, beeCurZ)) && (westGeoZ <= getNearestZ(prevX - 1, prevY, beeCurZ));
					}
					else
					{
						canSeeThrough = true;
					}
				}
				
				if (!canSeeThrough)
				{
					return false;
				}
			}
			
			prevX = curX;
			prevY = curY;
			prevGeoZ = curGeoZ;
			++ptIndex;
		}
		
		return true;
	}
	
	/**
	 * Verifies if the is a path between origin's location and destination, if not returns the closest location.
	 * @param origin the origin
	 * @param destination the destination
	 * @return the destination if there is a path or the closes location
	 */
	public Location getValidLocation(ILocational origin, ILocational destination)
	{
		return getValidLocation(origin.getX(), origin.getY(), origin.getZ(), destination.getX(), destination.getY(), destination.getZ(), null);
	}
	
	/**
	 * Move check.
	 * @param x the x coordinate
	 * @param y the y coordinate
	 * @param z the z coordinate
	 * @param tx the target's x coordinate
	 * @param ty the target's y coordinate
	 * @param tz the target's z coordinate
	 * @param instance the instance
	 * @return the last Location (x,y,z) where player can walk - just before wall
	 */
	public Location getValidLocation(int x, int y, int z, int tx, int ty, int tz, Instance instance)
	{
		final int geoX = getGeoX(x);
		final int geoY = getGeoY(y);
		final int nearestFromZ = getNearestZ(geoX, geoY, z);
		final int tGeoX = getGeoX(tx);
		final int tGeoY = getGeoY(ty);
		final int nearestToZ = getNearestZ(tGeoX, tGeoY, tz);
		
		// Door checks.
		if (DoorData.getInstance().checkIfDoorsBetween(x, y, nearestFromZ, tx, ty, nearestToZ, instance, false))
		{
			return new Location(x, y, getHeight(x, y, nearestFromZ));
		}
		
		// Fence checks.
		if (FenceData.getInstance().checkIfFenceBetween(x, y, nearestFromZ, tx, ty, nearestToZ, instance))
		{
			return new Location(x, y, getHeight(x, y, nearestFromZ));
		}
		
		final LinePointIterator pointIter = new LinePointIterator(geoX, geoY, tGeoX, tGeoY);
		// first point is guaranteed to be available
		pointIter.next();
		int prevX = pointIter.x();
		int prevY = pointIter.y();
		int prevZ = nearestFromZ;
		
		while (pointIter.next())
		{
			final int curX = pointIter.x();
			final int curY = pointIter.y();
			final int curZ = getNearestZ(curX, curY, prevZ);
			if (hasGeoPos(prevX, prevY) && !checkNearestNsweAntiCornerCut(prevX, prevY, prevZ, GeoUtils.computeNswe(prevX, prevY, curX, curY)))
			{
				// Can't move, return previous location.
				return new Location(getWorldX(prevX), getWorldY(prevY), prevZ);
			}
			prevX = curX;
			prevY = curY;
			prevZ = curZ;
		}
		return hasGeoPos(prevX, prevY) && (prevZ != nearestToZ) ? new Location(x, y, nearestFromZ) : new Location(tx, ty, nearestToZ);
	}
	
	/**
	 * Checks if it is possible to move from one location to another.
	 * @param fromX the X coordinate to start checking from
	 * @param fromY the Y coordinate to start checking from
	 * @param fromZ the Z coordinate to start checking from
	 * @param toX the X coordinate to end checking at
	 * @param toY the Y coordinate to end checking at
	 * @param toZ the Z coordinate to end checking at
	 * @param instance the instance
	 * @return {@code true} if the character at start coordinates can move to end coordinates, {@code false} otherwise
	 */
	public boolean canMoveToTarget(int fromX, int fromY, int fromZ, int toX, int toY, int toZ, Instance instance)
	{
		final int geoX = getGeoX(fromX);
		final int geoY = getGeoY(fromY);
		final int nearestFromZ = getNearestZ(geoX, geoY, fromZ);
		final int tGeoX = getGeoX(toX);
		final int tGeoY = getGeoY(toY);
		final int nearestToZ = getNearestZ(tGeoX, tGeoY, toZ);
		
		// Door checks.
		if (DoorData.getInstance().checkIfDoorsBetween(fromX, fromY, nearestFromZ, toX, toY, nearestToZ, instance, false))
		{
			return false;
		}
		
		// Fence checks.
		if (FenceData.getInstance().checkIfFenceBetween(fromX, fromY, nearestFromZ, toX, toY, nearestToZ, instance))
		{
			return false;
		}
		
		final LinePointIterator pointIter = new LinePointIterator(geoX, geoY, tGeoX, tGeoY);
		// First point is guaranteed to be available.
		pointIter.next();
		int prevX = pointIter.x();
		int prevY = pointIter.y();
		int prevZ = nearestFromZ;
		
		while (pointIter.next())
		{
			final int curX = pointIter.x();
			final int curY = pointIter.y();
			final int curZ = getNearestZ(curX, curY, prevZ);
			if (hasGeoPos(prevX, prevY) && !checkNearestNsweAntiCornerCut(prevX, prevY, prevZ, GeoUtils.computeNswe(prevX, prevY, curX, curY)))
			{
				return false;
			}
			prevX = curX;
			prevY = curY;
			prevZ = curZ;
		}
		return !hasGeoPos(prevX, prevY) || (prevZ == nearestToZ);
	}
	
	public int traceTerrainZ(int x, int y, int z1, int tx, int ty)
	{
		final int geoX = getGeoX(x);
		final int geoY = getGeoY(y);
		final int nearestFromZ = getNearestZ(geoX, geoY, z1);
		final int tGeoX = getGeoX(tx);
		final int tGeoY = getGeoY(ty);
		
		final LinePointIterator pointIter = new LinePointIterator(geoX, geoY, tGeoX, tGeoY);
		// First point is guaranteed to be available.
		pointIter.next();
		int prevZ = nearestFromZ;
		
		while (pointIter.next())
		{
			prevZ = getNearestZ(pointIter.x(), pointIter.y(), prevZ);
		}
		
		return prevZ;
	}
	
	/**
	 * Checks if it is possible to move from one location to another.
	 * @param from the {@code ILocational} to start checking from
	 * @param toX the X coordinate to end checking at
	 * @param toY the Y coordinate to end checking at
	 * @param toZ the Z coordinate to end checking at
	 * @return {@code true} if the character at start coordinates can move to end coordinates, {@code false} otherwise
	 */
	public boolean canMoveToTarget(ILocational from, int toX, int toY, int toZ)
	{
		return canMoveToTarget(from.getX(), from.getY(), from.getZ(), toX, toY, toZ, null);
	}
	
	/**
	 * Checks if it is possible to move from one location to another.
	 * @param from the {@code ILocational} to start checking from
	 * @param to the {@code ILocational} to end checking at
	 * @return {@code true} if the character at start coordinates can move to end coordinates, {@code false} otherwise
	 */
	public boolean canMoveToTarget(ILocational from, ILocational to)
	{
		return canMoveToTarget(from, to.getX(), to.getY(), to.getZ());
	}
	
	/**
	 * Checks the specified position for available geodata.
	 * @param x the X coordinate
	 * @param y the Y coordinate
	 * @return {@code true} if there is geodata for the given coordinates, {@code false} otherwise
	 */
	public boolean hasGeo(int x, int y)
	{
		return hasGeoPos(getGeoX(x), getGeoY(y));
	}
	
	public static GeoEngine getInstance()
	{
		return SingletonHolder._instance;
	}
	
	private static class SingletonHolder
	{
		protected static final GeoEngine _instance = new GeoEngine();
	}
}
