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
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;
import java.util.function.Predicate;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.w3c.dom.Document;
import org.w3c.dom.Node;

import org.l2jpx.Config;
import org.l2jpx.commons.threads.ThreadPool;
import org.l2jpx.commons.util.IXmlReader;
import org.l2jpx.gameserver.model.ChanceLocation;
import org.l2jpx.gameserver.model.StatSet;
import org.l2jpx.gameserver.model.actor.templates.NpcTemplate;
import org.l2jpx.gameserver.model.holders.MinionHolder;
import org.l2jpx.gameserver.model.interfaces.IParameterized;
import org.l2jpx.gameserver.model.interfaces.ITerritorized;
import org.l2jpx.gameserver.model.spawns.NpcSpawnTemplate;
import org.l2jpx.gameserver.model.spawns.SpawnGroup;
import org.l2jpx.gameserver.model.spawns.SpawnTemplate;
import org.l2jpx.gameserver.model.zone.ZoneForm;
import org.l2jpx.gameserver.model.zone.form.ZoneCuboid;
import org.l2jpx.gameserver.model.zone.form.ZoneCylinder;
import org.l2jpx.gameserver.model.zone.form.ZoneNPoly;
import org.l2jpx.gameserver.model.zone.type.BannedSpawnTerritory;
import org.l2jpx.gameserver.model.zone.type.SpawnTerritory;

/**
 * @author UnAfraid
 */
public class SpawnData implements IXmlReader
{
	protected static final Logger LOGGER = Logger.getLogger(SpawnData.class.getName());
	
	private final Collection<SpawnTemplate> _spawns = ConcurrentHashMap.newKeySet();
	
	protected SpawnData()
	{
		load();
	}
	
	@Override
	public void load()
	{
		parseDatapackDirectory("data/spawns", true);
		LOGGER.info(getClass().getSimpleName() + ": Loaded " + _spawns.stream().flatMap(c -> c.getGroups().stream()).flatMap(c -> c.getSpawns().stream()).count() + " spawns");
	}
	
	@Override
	public void parseDocument(Document doc, File f)
	{
		forEach(doc, "list", listNode -> forEach(listNode, "spawn", spawnNode ->
		{
			try
			{
				parseSpawn(spawnNode, f, _spawns);
			}
			catch (Exception e)
			{
				LOGGER.log(Level.WARNING, getClass().getSimpleName() + ": Error while processing spawn in file: " + f.getAbsolutePath(), e);
			}
		}));
	}
	
	/**
	 * Initializing all spawns
	 */
	public void init()
	{
		if (Config.ALT_DEV_NO_SPAWNS)
		{
			return;
		}
		
		LOGGER.info(getClass().getSimpleName() + ": Initializing spawns...");
		if (Config.THREADS_FOR_LOADING)
		{
			final Collection<ScheduledFuture<?>> jobs = ConcurrentHashMap.newKeySet();
			for (SpawnTemplate template : _spawns)
			{
				if (template.isSpawningByDefault())
				{
					jobs.add(ThreadPool.schedule(() ->
					{
						template.spawnAll(null);
						template.notifyActivate();
					}, 0));
				}
			}
			while (!jobs.isEmpty())
			{
				for (ScheduledFuture<?> job : jobs)
				{
					if ((job == null) || job.isDone() || job.isCancelled())
					{
						jobs.remove(job);
					}
				}
			}
		}
		else
		{
			for (SpawnTemplate template : _spawns)
			{
				if (template.isSpawningByDefault())
				{
					template.spawnAll(null);
					template.notifyActivate();
				}
			}
		}
		
		LOGGER.info(getClass().getSimpleName() + ": All spawns has been initialized!");
	}
	
	/**
	 * Removing all spawns
	 */
	public void despawnAll()
	{
		LOGGER.info(getClass().getSimpleName() + ": Removing all spawns...");
		_spawns.forEach(SpawnTemplate::despawnAll);
		LOGGER.info(getClass().getSimpleName() + ": All spawns has been removed!");
	}
	
	public Collection<SpawnTemplate> getSpawns()
	{
		return _spawns;
	}
	
	public List<SpawnTemplate> getSpawns(Predicate<SpawnTemplate> condition)
	{
		final List<SpawnTemplate> result = new ArrayList<>();
		for (SpawnTemplate spawnTemplate : _spawns)
		{
			if (condition.test(spawnTemplate))
			{
				result.add(spawnTemplate);
			}
		}
		return result;
	}
	
	public SpawnTemplate getSpawnByName(String name)
	{
		for (SpawnTemplate spawn : _spawns)
		{
			if ((spawn.getName() != null) && spawn.getName().equalsIgnoreCase(name))
			{
				return spawn;
			}
		}
		return null;
	}
	
	public SpawnGroup getSpawnGroupByName(String name)
	{
		for (SpawnTemplate spawnTemplate : _spawns)
		{
			for (SpawnGroup group : spawnTemplate.getGroups())
			{
				if ((group.getName() != null) && group.getName().equalsIgnoreCase(name))
				{
					return group;
				}
			}
		}
		return null;
	}
	
	public List<NpcSpawnTemplate> getNpcSpawns(Predicate<NpcSpawnTemplate> condition)
	{
		final List<NpcSpawnTemplate> result = new ArrayList<>();
		for (SpawnTemplate template : _spawns)
		{
			for (SpawnGroup group : template.getGroups())
			{
				for (NpcSpawnTemplate spawn : group.getSpawns())
				{
					if (condition.test(spawn))
					{
						result.add(spawn);
					}
				}
			}
		}
		return result;
	}
	
	public void parseSpawn(Node spawnsNode, File file, Collection<SpawnTemplate> spawns)
	{
		final SpawnTemplate spawnTemplate = new SpawnTemplate(new StatSet(parseAttributes(spawnsNode)), file);
		SpawnGroup defaultGroup = null;
		for (Node innerNode = spawnsNode.getFirstChild(); innerNode != null; innerNode = innerNode.getNextSibling())
		{
			switch (innerNode.getNodeName())
			{
				case "territories":
				{
					parseTerritories(innerNode, spawnTemplate.getFile(), spawnTemplate);
					break;
				}
				case "group":
				{
					parseGroup(innerNode, spawnTemplate);
					break;
				}
				case "npc":
				{
					if (defaultGroup == null)
					{
						defaultGroup = new SpawnGroup(StatSet.EMPTY_STATSET);
					}
					parseNpc(innerNode, spawnTemplate, defaultGroup);
					break;
				}
				case "parameters":
				{
					parseParameters(innerNode, spawnTemplate);
					break;
				}
			}
		}
		
		// One static group for all npcs outside group scope
		if (defaultGroup != null)
		{
			spawnTemplate.addGroup(defaultGroup);
		}
		spawns.add(spawnTemplate);
	}
	
	/**
	 * @param innerNode
	 * @param file
	 * @param spawnTemplate
	 */
	private void parseTerritories(Node innerNode, File file, ITerritorized spawnTemplate)
	{
		forEach(innerNode, IXmlReader::isNode, territoryNode ->
		{
			final String name = parseString(territoryNode.getAttributes(), "name", file.getName() + "_" + (spawnTemplate.getTerritories().size() + 1));
			final int minZ = parseInteger(territoryNode.getAttributes(), "minZ");
			final int maxZ = parseInteger(territoryNode.getAttributes(), "maxZ");
			final List<Integer> xNodes = new ArrayList<>();
			final List<Integer> yNodes = new ArrayList<>();
			forEach(territoryNode, "node", node ->
			{
				xNodes.add(parseInteger(node.getAttributes(), "x"));
				yNodes.add(parseInteger(node.getAttributes(), "y"));
			});
			final int[] x = xNodes.stream().mapToInt(Integer::valueOf).toArray();
			final int[] y = yNodes.stream().mapToInt(Integer::valueOf).toArray();
			
			// Support for multiple spawn zone types.
			ZoneForm zoneForm = null;
			final String zoneShape = parseString(territoryNode.getAttributes(), "shape", "NPoly");
			switch (zoneShape)
			{
				case "Cuboid":
				{
					zoneForm = new ZoneCuboid(x[0], x[1], y[0], y[1], minZ, maxZ);
					break;
				}
				case "NPoly":
				{
					zoneForm = new ZoneNPoly(x, y, minZ, maxZ);
					break;
				}
				case "Cylinder":
				{
					final int zoneRad = Integer.parseInt(territoryNode.getAttributes().getNamedItem("rad").getNodeValue());
					zoneForm = new ZoneCylinder(x[0], y[0], minZ, maxZ, zoneRad);
					break;
				}
			}
			
			switch (territoryNode.getNodeName())
			{
				case "territory":
				{
					spawnTemplate.addTerritory(new SpawnTerritory(name, zoneForm));
					break;
				}
				case "banned_territory":
				{
					spawnTemplate.addBannedTerritory(new BannedSpawnTerritory(name, zoneForm));
					break;
				}
			}
		});
	}
	
	private void parseGroup(Node n, SpawnTemplate spawnTemplate)
	{
		final SpawnGroup group = new SpawnGroup(new StatSet(parseAttributes(n)));
		forEach(n, IXmlReader::isNode, npcNode ->
		{
			switch (npcNode.getNodeName())
			{
				case "territories":
				{
					parseTerritories(npcNode, spawnTemplate.getFile(), group);
					break;
				}
				case "npc":
				{
					parseNpc(npcNode, spawnTemplate, group);
					break;
				}
			}
		});
		spawnTemplate.addGroup(group);
	}
	
	/**
	 * @param n
	 * @param spawnTemplate
	 * @param group
	 */
	private void parseNpc(Node n, SpawnTemplate spawnTemplate, SpawnGroup group)
	{
		final NpcSpawnTemplate npcTemplate = new NpcSpawnTemplate(spawnTemplate, group, new StatSet(parseAttributes(n)));
		final NpcTemplate template = NpcData.getInstance().getTemplate(npcTemplate.getId());
		if (template == null)
		{
			LOGGER.warning(getClass().getSimpleName() + ": Requested spawn for non existing npc: " + npcTemplate.getId() + " in file: " + spawnTemplate.getFile().getName());
			return;
		}
		
		if (template.isType("Servitor") || template.isType("Pet"))
		{
			LOGGER.warning(getClass().getSimpleName() + ": Requested spawn for " + template.getType() + " " + template.getName() + "(" + template.getId() + ") file: " + spawnTemplate.getFile().getName());
			return;
		}
		
		if (!Config.FAKE_PLAYERS_ENABLED && template.isFakePlayer())
		{
			return;
		}
		
		for (Node d = n.getFirstChild(); d != null; d = d.getNextSibling())
		{
			if ("parameters".equalsIgnoreCase(d.getNodeName()))
			{
				parseParameters(d, npcTemplate);
			}
			else if ("minions".equalsIgnoreCase(d.getNodeName()))
			{
				parseMinions(d, npcTemplate);
			}
			else if ("locations".equalsIgnoreCase(d.getNodeName()))
			{
				parseLocations(d, npcTemplate);
			}
		}
		group.addSpawn(npcTemplate);
	}
	
	/**
	 * @param n
	 * @param npcTemplate
	 */
	private void parseLocations(Node n, NpcSpawnTemplate npcTemplate)
	{
		for (Node d = n.getFirstChild(); d != null; d = d.getNextSibling())
		{
			if ("location".equalsIgnoreCase(d.getNodeName()))
			{
				final int x = parseInteger(d.getAttributes(), "x");
				final int y = parseInteger(d.getAttributes(), "y");
				final int z = parseInteger(d.getAttributes(), "z");
				final int heading = parseInteger(d.getAttributes(), "heading", 0);
				final double chance = parseDouble(d.getAttributes(), "chance");
				npcTemplate.addSpawnLocation(new ChanceLocation(x, y, z, heading, chance));
			}
		}
	}
	
	/**
	 * @param n
	 * @param npcTemplate
	 */
	private void parseParameters(Node n, IParameterized<StatSet> npcTemplate)
	{
		final Map<String, Object> params = parseParameters(n);
		npcTemplate.setParameters(!params.isEmpty() ? new StatSet(Collections.unmodifiableMap(params)) : StatSet.EMPTY_STATSET);
	}
	
	/**
	 * @param n
	 * @param npcTemplate
	 */
	private void parseMinions(Node n, NpcSpawnTemplate npcTemplate)
	{
		forEach(n, "minion", minionNode -> npcTemplate.addMinion(new MinionHolder(new StatSet(parseAttributes(minionNode)))));
	}
	
	/**
	 * Gets the single instance of SpawnsData.
	 * @return single instance of SpawnsData
	 */
	public static SpawnData getInstance()
	{
		return SingletonHolder.INSTANCE;
	}
	
	private static class SingletonHolder
	{
		protected static final SpawnData INSTANCE = new SpawnData();
	}
}
