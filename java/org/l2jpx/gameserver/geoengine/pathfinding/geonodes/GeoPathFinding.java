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

import java.io.File;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.l2jpx.Config;
import org.l2jpx.gameserver.geoengine.GeoEngine;
import org.l2jpx.gameserver.geoengine.pathfinding.AbstractNode;
import org.l2jpx.gameserver.geoengine.pathfinding.AbstractNodeLoc;
import org.l2jpx.gameserver.geoengine.pathfinding.PathFinding;
import org.l2jpx.gameserver.model.Location;
import org.l2jpx.gameserver.model.World;
import org.l2jpx.gameserver.model.instancezone.Instance;

/**
 * @author -Nemesiss-
 */
public class GeoPathFinding extends PathFinding
{
	private static final Logger LOGGER = Logger.getLogger(GeoPathFinding.class.getName());
	
	private static final Map<Short, ByteBuffer> PATH_NODES = new HashMap<>();
	private static final Map<Short, IntBuffer> PATH_NODE_INDEX = new HashMap<>();
	
	@Override
	public boolean pathNodesExist(short regionoffset)
	{
		return PATH_NODE_INDEX.containsKey(regionoffset);
	}
	
	@Override
	public List<AbstractNodeLoc> findPath(int x, int y, int z, int tx, int ty, int tz, Instance instance, boolean playable)
	{
		final int gx = (x - World.WORLD_X_MIN) >> 4;
		final int gy = (y - World.WORLD_Y_MIN) >> 4;
		final short gz = (short) z;
		final int gtx = (tx - World.WORLD_X_MIN) >> 4;
		final int gty = (ty - World.WORLD_Y_MIN) >> 4;
		final short gtz = (short) tz;
		
		final GeoNode start = readNode(gx, gy, gz);
		final GeoNode end = readNode(gtx, gty, gtz);
		if ((start == null) || (end == null))
		{
			return null;
		}
		if (Math.abs(start.getLoc().getZ() - z) > 55)
		{
			return null; // Not correct layer.
		}
		if (Math.abs(end.getLoc().getZ() - tz) > 55)
		{
			return null; // Not correct layer.
		}
		if (start == end)
		{
			return null;
		}
		
		// TODO: Find closest path node we CAN access. Now only checks if we can not reach the closest.
		Location temp = GeoEngine.getInstance().getValidLocation(x, y, z, start.getLoc().getX(), start.getLoc().getY(), start.getLoc().getZ(), instance);
		if ((temp.getX() != start.getLoc().getX()) || (temp.getY() != start.getLoc().getY()))
		{
			return null; // Cannot reach closest...
		}
		
		// TODO: Find closest path node around target, now only checks if final location can be reached.
		temp = GeoEngine.getInstance().getValidLocation(tx, ty, tz, end.getLoc().getX(), end.getLoc().getY(), end.getLoc().getZ(), instance);
		if ((temp.getX() != end.getLoc().getX()) || (temp.getY() != end.getLoc().getY()))
		{
			return null; // Cannot reach closest...
		}
		
		return searchByClosest2(start, end);
	}
	
	public List<AbstractNodeLoc> searchByClosest2(GeoNode start, GeoNode end)
	{
		// Always continues checking from the closest to target non-blocked
		// node from to_visit list. There's extra length in path if needed
		// to go backwards/sideways but when moving generally forwards, this is extra fast
		// and accurate. And can reach insane distances (try it with 800 nodes..).
		// Minimum required node count would be around 300-400.
		// Generally returns a bit (only a bit) more intelligent looking routes than
		// the basic version. Not a true distance image (which would increase CPU
		// load) level of intelligence though.
		
		// List of Visited Nodes.
		final List<GeoNode> visited = new ArrayList<>(550);
		
		// List of Nodes to Visit.
		final LinkedList<GeoNode> toVisit = new LinkedList<>();
		toVisit.add(start);
		final int targetX = end.getLoc().getNodeX();
		final int targetY = end.getLoc().getNodeY();
		
		int dx, dy;
		boolean added;
		int i = 0;
		while (i < 550)
		{
			GeoNode node;
			try
			{
				node = toVisit.removeFirst();
			}
			catch (Exception e)
			{
				// No Path found
				return null;
			}
			if (node.equals(end))
			{
				return constructPath2(node);
			}
			
			i++;
			visited.add(node);
			node.attachNeighbors(readNeighbors(node));
			final GeoNode[] neighbors = node.getNeighbors();
			if (neighbors == null)
			{
				continue;
			}
			for (GeoNode n : neighbors)
			{
				if ((visited.lastIndexOf(n) == -1) && !toVisit.contains(n))
				{
					added = false;
					n.setParent(node);
					dx = targetX - n.getLoc().getNodeX();
					dy = targetY - n.getLoc().getNodeY();
					n.setCost((dx * dx) + (dy * dy));
					for (int index = 0; index < toVisit.size(); index++)
					{
						// Supposed to find it quite early.
						if (toVisit.get(index).getCost() > n.getCost())
						{
							toVisit.add(index, n);
							added = true;
							break;
						}
					}
					if (!added)
					{
						toVisit.addLast(n);
					}
				}
			}
		}
		// No Path found.
		return null;
	}
	
	public List<AbstractNodeLoc> constructPath2(AbstractNode<GeoNodeLoc> node)
	{
		final LinkedList<AbstractNodeLoc> path = new LinkedList<>();
		int previousDirectionX = -1000;
		int previousDirectionY = -1000;
		int directionX;
		int directionY;
		
		AbstractNode<GeoNodeLoc> tempNode = node;
		while (tempNode.getParent() != null)
		{
			// Only add a new route point if moving direction changes.
			directionX = tempNode.getLoc().getNodeX() - tempNode.getParent().getLoc().getNodeX();
			directionY = tempNode.getLoc().getNodeY() - tempNode.getParent().getLoc().getNodeY();
			
			if ((directionX != previousDirectionX) || (directionY != previousDirectionY))
			{
				previousDirectionX = directionX;
				previousDirectionY = directionY;
				path.addFirst(tempNode.getLoc());
			}
			tempNode = tempNode.getParent();
		}
		return path;
	}
	
	private GeoNode[] readNeighbors(GeoNode n)
	{
		if (n.getLoc() == null)
		{
			return null;
		}
		
		int idx = n.getNeighborsIdx();
		
		final int nodeX = n.getLoc().getNodeX();
		final int nodeY = n.getLoc().getNodeY();
		
		final short regoffset = getRegionOffset(getRegionX(nodeX), getRegionY(nodeY));
		final ByteBuffer pn = PATH_NODES.get(regoffset);
		
		final List<AbstractNode<GeoNodeLoc>> neighbors = new ArrayList<>(8);
		GeoNode newNode;
		short newNodeX;
		short newNodeY;
		
		// Region for sure will change, we must read from correct file
		byte neighbor = pn.get(idx++); // N
		if (neighbor > 0)
		{
			neighbor--;
			newNodeX = (short) nodeX;
			newNodeY = (short) (nodeY - 1);
			newNode = readNode(newNodeX, newNodeY, neighbor);
			if (newNode != null)
			{
				neighbors.add(newNode);
			}
		}
		neighbor = pn.get(idx++); // NE
		if (neighbor > 0)
		{
			neighbor--;
			newNodeX = (short) (nodeX + 1);
			newNodeY = (short) (nodeY - 1);
			newNode = readNode(newNodeX, newNodeY, neighbor);
			if (newNode != null)
			{
				neighbors.add(newNode);
			}
		}
		neighbor = pn.get(idx++); // E
		if (neighbor > 0)
		{
			neighbor--;
			newNodeX = (short) (nodeX + 1);
			newNodeY = (short) nodeY;
			newNode = readNode(newNodeX, newNodeY, neighbor);
			if (newNode != null)
			{
				neighbors.add(newNode);
			}
		}
		neighbor = pn.get(idx++); // SE
		if (neighbor > 0)
		{
			neighbor--;
			newNodeX = (short) (nodeX + 1);
			newNodeY = (short) (nodeY + 1);
			newNode = readNode(newNodeX, newNodeY, neighbor);
			if (newNode != null)
			{
				neighbors.add(newNode);
			}
		}
		neighbor = pn.get(idx++); // S
		if (neighbor > 0)
		{
			neighbor--;
			newNodeX = (short) nodeX;
			newNodeY = (short) (nodeY + 1);
			newNode = readNode(newNodeX, newNodeY, neighbor);
			if (newNode != null)
			{
				neighbors.add(newNode);
			}
		}
		neighbor = pn.get(idx++); // SW
		if (neighbor > 0)
		{
			neighbor--;
			newNodeX = (short) (nodeX - 1);
			newNodeY = (short) (nodeY + 1);
			newNode = readNode(newNodeX, newNodeY, neighbor);
			if (newNode != null)
			{
				neighbors.add(newNode);
			}
		}
		neighbor = pn.get(idx++); // W
		if (neighbor > 0)
		{
			neighbor--;
			newNodeX = (short) (nodeX - 1);
			newNodeY = (short) nodeY;
			newNode = readNode(newNodeX, newNodeY, neighbor);
			if (newNode != null)
			{
				neighbors.add(newNode);
			}
		}
		neighbor = pn.get(idx++); // NW
		if (neighbor > 0)
		{
			neighbor--;
			newNodeX = (short) (nodeX - 1);
			newNodeY = (short) (nodeY - 1);
			newNode = readNode(newNodeX, newNodeY, neighbor);
			if (newNode != null)
			{
				neighbors.add(newNode);
			}
		}
		return neighbors.toArray(new GeoNode[neighbors.size()]);
	}
	
	// Private
	
	private GeoNode readNode(short nodeX, short nodeY, byte layer)
	{
		final short regoffset = getRegionOffset(getRegionX(nodeX), getRegionY(nodeY));
		if (!pathNodesExist(regoffset))
		{
			return null;
		}
		final short nbx = getNodeBlock(nodeX);
		final short nby = getNodeBlock(nodeY);
		int idx = PATH_NODE_INDEX.get(regoffset).get((nby << 8) + nbx);
		final ByteBuffer pn = PATH_NODES.get(regoffset);
		// Reading.
		final byte nodes = pn.get(idx);
		idx += (layer * 10) + 1; // byte + layer*10byte
		if (nodes < layer)
		{
			LOGGER.warning("SmthWrong!");
		}
		final short node_z = pn.getShort(idx);
		idx += 2;
		return new GeoNode(new GeoNodeLoc(nodeX, nodeY, node_z), idx);
	}
	
	private GeoNode readNode(int gx, int gy, short z)
	{
		final short nodeX = getNodePos(gx);
		final short nodeY = getNodePos(gy);
		final short regoffset = getRegionOffset(getRegionX(nodeX), getRegionY(nodeY));
		if (!pathNodesExist(regoffset))
		{
			return null;
		}
		final short nbx = getNodeBlock(nodeX);
		final short nby = getNodeBlock(nodeY);
		int idx = PATH_NODE_INDEX.get(regoffset).get((nby << 8) + nbx);
		final ByteBuffer pn = PATH_NODES.get(regoffset);
		// Reading.
		byte nodes = pn.get(idx++);
		int idx2 = 0; // Create index to nearlest node by z.
		short lastZ = Short.MIN_VALUE;
		while (nodes > 0)
		{
			final short node_z = pn.getShort(idx);
			if (Math.abs(lastZ - z) > Math.abs(node_z - z))
			{
				lastZ = node_z;
				idx2 = idx + 2;
			}
			idx += 10; // short + 8 byte
			nodes--;
		}
		return new GeoNode(new GeoNodeLoc(nodeX, nodeY, lastZ), idx2);
	}
	
	protected GeoPathFinding()
	{
		for (int regionX = World.TILE_X_MIN; regionX <= World.TILE_X_MAX; regionX++)
		{
			for (int regionY = World.TILE_Y_MIN; regionY <= World.TILE_Y_MAX; regionY++)
			{
				loadPathNodeFile((byte) regionX, (byte) regionY);
			}
		}
	}
	
	private void loadPathNodeFile(byte rx, byte ry)
	{
		final short regionoffset = getRegionOffset(rx, ry);
		final File file = new File(Config.PATHNODE_PATH.toString(), rx + "_" + ry + ".pn");
		if (!file.exists())
		{
			return;
		}
		
		// LOGGER.info("Path Engine: - Loading: " + file.getName() + " -> region offset: " + regionoffset + " X: " + rx + " Y: " + ry);
		
		int node = 0;
		int size = 0;
		int index = 0;
		
		// Create a read-only memory-mapped file.
		try (RandomAccessFile raf = new RandomAccessFile(file, "r");
			FileChannel roChannel = raf.getChannel())
		{
			size = (int) roChannel.size();
			final MappedByteBuffer nodes = roChannel.map(FileChannel.MapMode.READ_ONLY, 0, size).load();
			
			// Indexing pathnode files, so we will know where each block starts.
			final IntBuffer indexs = IntBuffer.allocate(65536);
			
			while (node < 65536)
			{
				final byte layer = nodes.get(index);
				indexs.put(node++, index);
				index += (layer * 10) + 1;
			}
			PATH_NODE_INDEX.put(regionoffset, indexs);
			PATH_NODES.put(regionoffset, nodes);
		}
		catch (Exception e)
		{
			LOGGER.log(Level.WARNING, "Failed to Load PathNode File: " + file.getAbsolutePath() + " : " + e.getMessage(), e);
		}
	}
	
	public static GeoPathFinding getInstance()
	{
		return SingletonHolder.INSTANCE;
	}
	
	private static class SingletonHolder
	{
		protected static final GeoPathFinding INSTANCE = new GeoPathFinding();
	}
}
