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
package org.l2jpx.gameserver.data.xml;

import java.io.File;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Logger;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import org.l2jpx.commons.util.IXmlReader;
import org.l2jpx.gameserver.model.Location;
import org.l2jpx.gameserver.model.StatSet;
import org.l2jpx.gameserver.model.World;
import org.l2jpx.gameserver.model.WorldRegion;
import org.l2jpx.gameserver.model.actor.instance.Door;
import org.l2jpx.gameserver.model.actor.templates.DoorTemplate;
import org.l2jpx.gameserver.model.instancezone.Instance;

/**
 * This class loads and hold info about doors.
 * @author JIV, GodKratos, UnAfraid
 */
public class DoorData implements IXmlReader
{
	private static final Logger LOGGER = Logger.getLogger(DoorData.class.getName());
	
	// Info holders
	private final Map<String, Set<Integer>> _groups = new HashMap<>();
	private final Map<Integer, Door> _doors = new HashMap<>();
	private final Map<Integer, StatSet> _templates = new HashMap<>();
	
	protected DoorData()
	{
		load();
	}
	
	@Override
	public void load()
	{
		_doors.clear();
		_groups.clear();
		parseDatapackFile("data/DoorData.xml");
	}
	
	@Override
	public void parseDocument(Document doc, File f)
	{
		forEach(doc, "list", listNode -> forEach(listNode, "door", doorNode -> spawnDoor(parseDoor(doorNode))));
		LOGGER.info(getClass().getSimpleName() + ": Loaded " + _doors.size() + " doors.");
	}
	
	public StatSet parseDoor(Node doorNode)
	{
		final StatSet params = new StatSet(parseAttributes(doorNode));
		params.set("baseHpMax", 1); // Avoid doors without HP value created dead due to default value 0 in CreatureTemplate
		
		forEach(doorNode, IXmlReader::isNode, innerDoorNode ->
		{
			final NamedNodeMap attrs = innerDoorNode.getAttributes();
			if (innerDoorNode.getNodeName().equals("nodes"))
			{
				params.set("nodeZ", parseInteger(attrs, "nodeZ"));
				
				final AtomicInteger count = new AtomicInteger();
				forEach(innerDoorNode, IXmlReader::isNode, nodes ->
				{
					final NamedNodeMap nodeAttrs = nodes.getAttributes();
					if ("node".equals(nodes.getNodeName()))
					{
						params.set("nodeX_" + count.get(), parseInteger(nodeAttrs, "x"));
						params.set("nodeY_" + count.getAndIncrement(), parseInteger(nodeAttrs, "y"));
					}
				});
			}
			else if (attrs != null)
			{
				for (int i = 0; i < attrs.getLength(); i++)
				{
					final Node att = attrs.item(i);
					params.set(att.getNodeName(), att.getNodeValue());
				}
			}
		});
		
		applyCollisions(params);
		return params;
	}
	
	/**
	 * @param set
	 */
	private void applyCollisions(StatSet set)
	{
		// Insert Collision data
		if (set.contains("nodeX_0") && set.contains("nodeY_0") && set.contains("nodeX_1") && set.contains("nodeY_1"))
		{
			final int height = set.getInt("height", 150);
			final int nodeX = set.getInt("nodeX_0");
			final int nodeY = set.getInt("nodeY_0");
			final int posX = set.getInt("nodeX_1");
			final int posY = set.getInt("nodeY_1");
			int collisionRadius; // (max) radius for movement checks
			collisionRadius = Math.min(Math.abs(nodeX - posX), Math.abs(nodeY - posY));
			if (collisionRadius < 20)
			{
				collisionRadius = 20;
			}
			
			set.set("collision_radius", collisionRadius);
			set.set("collision_height", height);
		}
	}
	
	/**
	 * Spawns the door, adds the group name and registers it to templates, regions and doors also inserts collisions data
	 * @param set
	 * @return
	 */
	public Door spawnDoor(StatSet set)
	{
		// Create door template + door instance
		final DoorTemplate template = new DoorTemplate(set);
		final Door door = spawnDoor(template, null);
		
		// Register the door
		_templates.put(door.getId(), set);
		_doors.put(door.getId(), door);
		
		return door;
	}
	
	/**
	 * Spawns the door, adds the group name and registers it to templates
	 * @param template
	 * @param instance
	 * @return a new door instance based on provided template
	 */
	public Door spawnDoor(DoorTemplate template, Instance instance)
	{
		final Door door = new Door(template);
		door.setCurrentHp(door.getMaxHp());
		
		// Set instance world if provided
		if (instance != null)
		{
			door.setInstance(instance);
		}
		
		// Spawn the door on the world
		door.spawnMe(template.getX(), template.getY(), template.getZ());
		
		// Register door's group
		if (template.getGroupName() != null)
		{
			_groups.computeIfAbsent(door.getGroupName(), key -> new HashSet<>()).add(door.getId());
		}
		return door;
	}
	
	public StatSet getDoorTemplate(int doorId)
	{
		return _templates.get(doorId);
	}
	
	public Door getDoor(int doorId)
	{
		return _doors.get(doorId);
	}
	
	public Set<Integer> getDoorsByGroup(String groupName)
	{
		return _groups.getOrDefault(groupName, Collections.emptySet());
	}
	
	public Collection<Door> getDoors()
	{
		return _doors.values();
	}
	
	public boolean checkIfDoorsBetween(Location start, Location end, Instance instance)
	{
		return checkIfDoorsBetween(start.getX(), start.getY(), start.getZ(), end.getX(), end.getY(), end.getZ(), instance);
	}
	
	public boolean checkIfDoorsBetween(int x, int y, int z, int tx, int ty, int tz, Instance instance)
	{
		return checkIfDoorsBetween(x, y, z, tx, ty, tz, instance, false);
	}
	
	public boolean checkIfDoorsBetween(int x, int y, int z, int tx, int ty, int tz, Instance instance, boolean doubleFaceCheck)
	{
		final Collection<Door> doors;
		if (instance == null)
		{
			final WorldRegion region = World.getInstance().getRegion(x, y);
			if (region != null)
			{
				doors = region.getDoors();
			}
			else
			{
				doors = null;
			}
		}
		else
		{
			doors = instance.getDoors();
		}
		if ((doors == null) || doors.isEmpty())
		{
			return false;
		}
		
		for (Door doorInst : doors)
		{
			// check dead and open
			if ((instance != doorInst.getInstanceWorld()) || doorInst.isDead() || doorInst.isOpen() || !doorInst.checkCollision() || (doorInst.getX(0) == 0))
			{
				continue;
			}
			
			boolean intersectFace = false;
			for (int i = 0; i < 4; i++)
			{
				final int j = (i + 1) < 4 ? i + 1 : 0;
				// lower part of the multiplier fraction, if it is 0 we avoid an error and also know that the lines are parallel
				final int denominator = ((ty - y) * (doorInst.getX(i) - doorInst.getX(j))) - ((tx - x) * (doorInst.getY(i) - doorInst.getY(j)));
				if (denominator == 0)
				{
					continue;
				}
				
				// multipliers to the equations of the lines. If they are lower than 0 or bigger than 1, we know that segments don't intersect
				final float multiplier1 = (float) (((doorInst.getX(j) - doorInst.getX(i)) * (y - doorInst.getY(i))) - ((doorInst.getY(j) - doorInst.getY(i)) * (x - doorInst.getX(i)))) / denominator;
				final float multiplier2 = (float) (((tx - x) * (y - doorInst.getY(i))) - ((ty - y) * (x - doorInst.getX(i)))) / denominator;
				if ((multiplier1 >= 0) && (multiplier1 <= 1) && (multiplier2 >= 0) && (multiplier2 <= 1))
				{
					final int intersectZ = Math.round(z + (multiplier1 * (tz - z)));
					// now checking if the resulting point is between door's min and max z
					if ((intersectZ > doorInst.getZMin()) && (intersectZ < doorInst.getZMax()))
					{
						if (!doubleFaceCheck || intersectFace)
						{
							return true;
						}
						intersectFace = true;
					}
				}
			}
		}
		return false;
	}
	
	public static DoorData getInstance()
	{
		return SingletonHolder.INSTANCE;
	}
	
	private static class SingletonHolder
	{
		protected static final DoorData INSTANCE = new DoorData();
	}
}
