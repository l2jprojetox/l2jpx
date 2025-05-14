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
package org.l2jpx.gameserver.geoengine.pathfinding.cellnodes;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.l2jpx.Config;
import org.l2jpx.commons.util.StringUtil;
import org.l2jpx.gameserver.geoengine.GeoEngine;
import org.l2jpx.gameserver.geoengine.pathfinding.AbstractNode;
import org.l2jpx.gameserver.geoengine.pathfinding.AbstractNodeLoc;
import org.l2jpx.gameserver.geoengine.pathfinding.PathFinding;
import org.l2jpx.gameserver.instancemanager.IdManager;
import org.l2jpx.gameserver.model.instancezone.Instance;
import org.l2jpx.gameserver.model.item.instance.Item;

/**
 * @author Sami, DS Credits to Diamond
 */
public class CellPathFinding extends PathFinding
{
	private static final Logger LOGGER = Logger.getLogger(CellPathFinding.class.getName());
	
	private BufferInfo[] _allBuffers;
	private int _findSuccess = 0;
	private int _findFails = 0;
	private int _postFilterUses = 0;
	private int _postFilterPlayableUses = 0;
	private int _postFilterPasses = 0;
	private long _postFilterElapsed = 0;
	
	private List<Item> _debugItems = null;
	
	protected CellPathFinding()
	{
		try
		{
			final String[] array = Config.PATHFIND_BUFFERS.split(";");
			
			_allBuffers = new BufferInfo[array.length];
			
			String buf;
			String[] args;
			for (int i = 0; i < array.length; i++)
			{
				buf = array[i];
				args = buf.split("x");
				if (args.length != 2)
				{
					throw new Exception("Invalid buffer definition: " + buf);
				}
				
				_allBuffers[i] = new BufferInfo(Integer.parseInt(args[0]), Integer.parseInt(args[1]));
			}
		}
		catch (Exception e)
		{
			LOGGER.log(Level.WARNING, "CellPathFinding: Problem during buffer init: " + e.getMessage(), e);
			throw new Error("CellPathFinding: load aborted");
		}
	}
	
	@Override
	public boolean pathNodesExist(short regionoffset)
	{
		return false;
	}
	
	@Override
	public List<AbstractNodeLoc> findPath(int x, int y, int z, int tx, int ty, int tz, Instance instance, boolean playable)
	{
		final int gx = GeoEngine.getInstance().getGeoX(x);
		final int gy = GeoEngine.getInstance().getGeoY(y);
		if (!GeoEngine.getInstance().hasGeo(x, y))
		{
			return null;
		}
		final int gz = GeoEngine.getInstance().getHeight(x, y, z);
		final int gtx = GeoEngine.getInstance().getGeoX(tx);
		final int gty = GeoEngine.getInstance().getGeoY(ty);
		if (!GeoEngine.getInstance().hasGeo(tx, ty))
		{
			return null;
		}
		final int gtz = GeoEngine.getInstance().getHeight(tx, ty, tz);
		final CellNodeBuffer buffer = alloc(64 + (2 * Math.max(Math.abs(gx - gtx), Math.abs(gy - gty))), playable);
		if (buffer == null)
		{
			return null;
		}
		
		final boolean debug = Config.DEBUG_PATH && playable;
		
		if (debug)
		{
			if (_debugItems == null)
			{
				_debugItems = new LinkedList<>();
			}
			else
			{
				for (Item item : _debugItems)
				{
					item.decayMe();
				}
				
				_debugItems.clear();
			}
		}
		
		List<AbstractNodeLoc> path = null;
		try
		{
			final CellNode result = buffer.findPath(gx, gy, gz, gtx, gty, gtz);
			
			if (debug)
			{
				for (CellNode n : buffer.debugPath())
				{
					if (n.getCost() < 0)
					{
						dropDebugItem(1831, (int) (-n.getCost() * 10), n.getLoc());
					}
					else
					{
						// Known nodes.
						dropDebugItem(57, (int) (n.getCost() * 10), n.getLoc());
					}
				}
			}
			
			if (result == null)
			{
				_findFails++;
				return null;
			}
			
			path = constructPath(result);
		}
		catch (Exception e)
		{
			LOGGER.log(Level.WARNING, "", e);
			return null;
		}
		finally
		{
			buffer.free();
		}
		
		if ((path.size() < 3) || (Config.MAX_POSTFILTER_PASSES <= 0))
		{
			_findSuccess++;
			return path;
		}
		
		final long timeStamp = System.currentTimeMillis();
		_postFilterUses++;
		if (playable)
		{
			_postFilterPlayableUses++;
		}
		
		ListIterator<AbstractNodeLoc> middlePoint;
		int currentX;
		int currentY;
		int currentZ;
		int pass = 0;
		boolean remove;
		do
		{
			pass++;
			_postFilterPasses++;
			
			remove = false;
			middlePoint = path.listIterator();
			currentX = x;
			currentY = y;
			currentZ = z;
			
			while (middlePoint.hasNext())
			{
				final AbstractNodeLoc locMiddle = middlePoint.next();
				if (!middlePoint.hasNext())
				{
					break;
				}
				
				final AbstractNodeLoc locEnd = path.get(middlePoint.nextIndex());
				if (GeoEngine.getInstance().canMoveToTarget(currentX, currentY, currentZ, locEnd.getX(), locEnd.getY(), locEnd.getZ(), instance))
				{
					middlePoint.remove();
					remove = true;
					if (debug)
					{
						dropDebugItem(735, 1, locMiddle);
					}
				}
				else
				{
					currentX = locMiddle.getX();
					currentY = locMiddle.getY();
					currentZ = locMiddle.getZ();
				}
			}
		}
		// Only one postfilter pass for AI.
		while (playable && remove && (path.size() > 2) && (pass < Config.MAX_POSTFILTER_PASSES));
		
		if (debug)
		{
			path.forEach(n -> dropDebugItem(1061, 1, n));
		}
		
		_findSuccess++;
		_postFilterElapsed += System.currentTimeMillis() - timeStamp;
		return path;
	}
	
	private List<AbstractNodeLoc> constructPath(AbstractNode<NodeLoc> node)
	{
		final LinkedList<AbstractNodeLoc> path = new LinkedList<>();
		int previousDirectionX = Integer.MIN_VALUE;
		int previousDirectionY = Integer.MIN_VALUE;
		int directionX;
		int directionY;
		
		AbstractNode<NodeLoc> tempNode = node;
		while (tempNode.getParent() != null)
		{
			if (!Config.ADVANCED_DIAGONAL_STRATEGY && (tempNode.getParent().getParent() != null))
			{
				final int tmpX = tempNode.getLoc().getNodeX() - tempNode.getParent().getParent().getLoc().getNodeX();
				final int tmpY = tempNode.getLoc().getNodeY() - tempNode.getParent().getParent().getLoc().getNodeY();
				if (Math.abs(tmpX) == Math.abs(tmpY))
				{
					directionX = tmpX;
					directionY = tmpY;
				}
				else
				{
					directionX = tempNode.getLoc().getNodeX() - tempNode.getParent().getLoc().getNodeX();
					directionY = tempNode.getLoc().getNodeY() - tempNode.getParent().getLoc().getNodeY();
				}
			}
			else
			{
				directionX = tempNode.getLoc().getNodeX() - tempNode.getParent().getLoc().getNodeX();
				directionY = tempNode.getLoc().getNodeY() - tempNode.getParent().getLoc().getNodeY();
			}
			
			// Only add a new route point if moving direction changes.
			if ((directionX != previousDirectionX) || (directionY != previousDirectionY))
			{
				previousDirectionX = directionX;
				previousDirectionY = directionY;
				
				path.addFirst(tempNode.getLoc());
				tempNode.setLoc(null);
			}
			
			tempNode = tempNode.getParent();
		}
		
		return path;
	}
	
	private CellNodeBuffer alloc(int size, boolean playable)
	{
		CellNodeBuffer current = null;
		for (BufferInfo i : _allBuffers)
		{
			if (i.mapSize >= size)
			{
				for (CellNodeBuffer buf : i.bufs)
				{
					if (buf.lock())
					{
						i.uses++;
						if (playable)
						{
							i.playableUses++;
						}
						i.elapsed += buf.getElapsedTime();
						current = buf;
						break;
					}
				}
				if (current != null)
				{
					break;
				}
				
				// Not found, allocate temporary buffer.
				current = new CellNodeBuffer(i.mapSize);
				current.lock();
				if (i.bufs.size() < i.count)
				{
					i.bufs.add(current);
					i.uses++;
					if (playable)
					{
						i.playableUses++;
					}
					break;
				}
				
				i.overflows++;
				if (playable)
				{
					i.playableOverflows++;
					// System.err.println("Overflow, size requested: " + size + " playable:"+playable);
				}
			}
		}
		
		return current;
	}
	
	private void dropDebugItem(int itemId, int num, AbstractNodeLoc loc)
	{
		final Item item = new Item(IdManager.getInstance().getNextId(), itemId);
		item.setCount(num);
		item.spawnMe(loc.getX(), loc.getY(), loc.getZ());
		_debugItems.add(item);
	}
	
	private static class BufferInfo
	{
		final int mapSize;
		final int count;
		List<CellNodeBuffer> bufs;
		int uses = 0;
		int playableUses = 0;
		int overflows = 0;
		int playableOverflows = 0;
		long elapsed = 0;
		
		public BufferInfo(int size, int cnt)
		{
			mapSize = size;
			count = cnt;
			bufs = new ArrayList<>(count);
		}
		
		@Override
		public String toString()
		{
			final StringBuilder stat = new StringBuilder(100);
			StringUtil.append(stat, String.valueOf(mapSize), "x", String.valueOf(mapSize), " num:", String.valueOf(bufs.size()), "/", String.valueOf(count), " uses:", String.valueOf(uses), "/", String.valueOf(playableUses));
			if (uses > 0)
			{
				StringUtil.append(stat, " total/avg(ms):", String.valueOf(elapsed), "/", String.format("%1.2f", (double) elapsed / uses));
			}
			
			StringUtil.append(stat, " ovf:", String.valueOf(overflows), "/", String.valueOf(playableOverflows));
			
			return stat.toString();
		}
	}
	
	@Override
	public String[] getStat()
	{
		final String[] result = new String[_allBuffers.length + 1];
		for (int i = 0; i < _allBuffers.length; i++)
		{
			result[i] = _allBuffers[i].toString();
		}
		
		final StringBuilder stat = new StringBuilder(100);
		StringUtil.append(stat, "LOS postfilter uses:", String.valueOf(_postFilterUses), "/", String.valueOf(_postFilterPlayableUses));
		if (_postFilterUses > 0)
		{
			StringUtil.append(stat, " total/avg(ms):", String.valueOf(_postFilterElapsed), "/", String.format("%1.2f", (double) _postFilterElapsed / _postFilterUses), " passes total/avg:", String.valueOf(_postFilterPasses), "/", String.format("%1.1f", (double) _postFilterPasses / _postFilterUses), System.lineSeparator());
		}
		StringUtil.append(stat, "Pathfind success/fail:", String.valueOf(_findSuccess), "/", String.valueOf(_findFails));
		result[result.length - 1] = stat.toString();
		
		return result;
	}
	
	public static CellPathFinding getInstance()
	{
		return SingletonHolder.INSTANCE;
	}
	
	private static class SingletonHolder
	{
		protected static final CellPathFinding INSTANCE = new CellPathFinding();
	}
}
