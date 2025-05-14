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
package org.l2jpx.gameserver.model.instancezone;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.l2jpx.Config;
import org.l2jpx.commons.database.DatabaseFactory;
import org.l2jpx.commons.threads.ThreadPool;
import org.l2jpx.commons.util.CommonUtil;
import org.l2jpx.gameserver.data.xml.DoorData;
import org.l2jpx.gameserver.enums.InstanceReenterType;
import org.l2jpx.gameserver.enums.InstanceTeleportType;
import org.l2jpx.gameserver.enums.TeleportWhereType;
import org.l2jpx.gameserver.instancemanager.InstanceManager;
import org.l2jpx.gameserver.model.Location;
import org.l2jpx.gameserver.model.StatSet;
import org.l2jpx.gameserver.model.World;
import org.l2jpx.gameserver.model.WorldObject;
import org.l2jpx.gameserver.model.actor.Creature;
import org.l2jpx.gameserver.model.actor.Npc;
import org.l2jpx.gameserver.model.actor.Player;
import org.l2jpx.gameserver.model.actor.Summon;
import org.l2jpx.gameserver.model.actor.instance.Door;
import org.l2jpx.gameserver.model.actor.templates.DoorTemplate;
import org.l2jpx.gameserver.model.events.EventDispatcher;
import org.l2jpx.gameserver.model.events.EventType;
import org.l2jpx.gameserver.model.events.impl.instance.OnInstanceCreated;
import org.l2jpx.gameserver.model.events.impl.instance.OnInstanceDestroy;
import org.l2jpx.gameserver.model.events.impl.instance.OnInstanceEnter;
import org.l2jpx.gameserver.model.events.impl.instance.OnInstanceLeave;
import org.l2jpx.gameserver.model.events.impl.instance.OnInstanceStatusChange;
import org.l2jpx.gameserver.model.interfaces.IIdentifiable;
import org.l2jpx.gameserver.model.interfaces.ILocational;
import org.l2jpx.gameserver.model.interfaces.INamable;
import org.l2jpx.gameserver.model.spawns.NpcSpawnTemplate;
import org.l2jpx.gameserver.model.spawns.SpawnGroup;
import org.l2jpx.gameserver.model.spawns.SpawnTemplate;
import org.l2jpx.gameserver.model.variables.PlayerVariables;
import org.l2jpx.gameserver.network.SystemMessageId;
import org.l2jpx.gameserver.network.serverpackets.ServerPacket;
import org.l2jpx.gameserver.network.serverpackets.SystemMessage;

/**
 * Instance world.
 * @author malyelfik
 */
public class Instance implements IIdentifiable, INamable
{
	private static final Logger LOGGER = Logger.getLogger(Instance.class.getName());
	
	// Basic instance parameters
	private final int _id;
	private final InstanceTemplate _template;
	private final long _startTime;
	private long _endTime;
	// Advanced instance parameters
	private final Set<Integer> _allowed = ConcurrentHashMap.newKeySet(); // Player ids which can enter to instance
	private final Set<Player> _players = ConcurrentHashMap.newKeySet(); // Players inside instance
	private final Set<Npc> _npcs = ConcurrentHashMap.newKeySet(); // Spawned NPCs inside instance
	private final Map<Integer, Door> _doors = new HashMap<>(); // Spawned doors inside instance
	private final StatSet _parameters = new StatSet();
	// Timers
	private final Map<Integer, ScheduledFuture<?>> _ejectDeadTasks = new ConcurrentHashMap<>();
	private ScheduledFuture<?> _cleanUpTask = null;
	private ScheduledFuture<?> _emptyDestroyTask = null;
	private final List<SpawnTemplate> _spawns;
	
	/**
	 * Create instance world.
	 * @param id ID of instance world
	 * @param template template of instance world
	 * @param player player who create instance world.
	 */
	public Instance(int id, InstanceTemplate template, Player player)
	{
		// Set basic instance info
		_id = id;
		_template = template;
		_startTime = System.currentTimeMillis();
		_spawns = new ArrayList<>(template.getSpawns().size());
		
		// Clone and add the spawn templates
		for (SpawnTemplate spawn : template.getSpawns())
		{
			_spawns.add(spawn.clone());
		}
		
		// Register world to instance manager.
		InstanceManager.getInstance().register(this);
		
		// Set duration, spawns, status, etc..
		setDuration(_template.getDuration());
		setStatus(0);
		spawnDoors();
		
		// Initialize instance spawns.
		for (SpawnTemplate spawnTemplate : _spawns)
		{
			if (spawnTemplate.isSpawningByDefault())
			{
				spawnTemplate.spawnAll(this);
			}
		}
		
		// Notify DP scripts
		if (!isDynamic() && EventDispatcher.getInstance().hasListener(EventType.ON_INSTANCE_CREATED, _template))
		{
			EventDispatcher.getInstance().notifyEventAsync(new OnInstanceCreated(this, player), _template);
		}
	}
	
	@Override
	public int getId()
	{
		return _id;
	}
	
	@Override
	public String getName()
	{
		return _template.getName();
	}
	
	/**
	 * Check if instance has been created dynamically or have XML template.
	 * @return {@code true} if instance is dynamic or {@code false} if instance has static template
	 */
	public boolean isDynamic()
	{
		return _template.getId() == -1;
	}
	
	/**
	 * Set instance world parameter.
	 * @param key parameter name
	 * @param value parameter value
	 */
	public void setParameter(String key, Object value)
	{
		if (value == null)
		{
			_parameters.remove(key);
		}
		else
		{
			_parameters.set(key, value);
		}
	}
	
	/**
	 * Set instance world parameter.
	 * @param key parameter name
	 * @param value parameter value
	 */
	public void setParameter(String key, boolean value)
	{
		_parameters.set(key, value ? Boolean.TRUE : Boolean.FALSE);
	}
	
	/**
	 * Get instance world parameters.
	 * @return instance parameters
	 */
	public StatSet getParameters()
	{
		return _parameters;
	}
	
	/**
	 * Get status of instance world.
	 * @return instance status, otherwise 0
	 */
	public int getStatus()
	{
		return _parameters.getInt("INSTANCE_STATUS", 0);
	}
	
	/**
	 * Check if instance status is equal to {@code status}.
	 * @param status number used for status comparison
	 * @return {@code true} when instance status and {@code status} are equal, otherwise {@code false}
	 */
	public boolean isStatus(int status)
	{
		return getStatus() == status;
	}
	
	/**
	 * Set status of instance world.
	 * @param value new world status
	 */
	public void setStatus(int value)
	{
		_parameters.set("INSTANCE_STATUS", value);
		
		if (EventDispatcher.getInstance().hasListener(EventType.ON_INSTANCE_STATUS_CHANGE, _template))
		{
			EventDispatcher.getInstance().notifyEventAsync(new OnInstanceStatusChange(this, value), _template);
		}
	}
	
	/**
	 * Increment instance world status
	 * @return new world status
	 */
	public int incStatus()
	{
		final int status = getStatus() + 1;
		setStatus(status);
		return status;
	}
	
	/**
	 * Add player who can enter to instance.
	 * @param player player instance
	 */
	public void addAllowed(Player player)
	{
		if (!_allowed.contains(player.getObjectId()))
		{
			_allowed.add(player.getObjectId());
		}
	}
	
	/**
	 * Check if player can enter to instance.
	 * @param player player itself
	 * @return {@code true} when can enter, otherwise {@code false}
	 */
	public boolean isAllowed(Player player)
	{
		return _allowed.contains(player.getObjectId());
	}
	
	/**
	 * Returns all players who can enter to instance.
	 * @return allowed players list
	 */
	public List<Player> getAllowed()
	{
		final List<Player> allowed = new ArrayList<>(_allowed.size());
		for (int playerId : _allowed)
		{
			final Player player = World.getInstance().getPlayer(playerId);
			if (player != null)
			{
				allowed.add(player);
			}
		}
		return allowed;
	}
	
	/**
	 * Add player to instance
	 * @param player player instance
	 */
	public void addPlayer(Player player)
	{
		_players.add(player);
		if (_emptyDestroyTask != null)
		{
			_emptyDestroyTask.cancel(false);
			_emptyDestroyTask = null;
		}
	}
	
	/**
	 * Remove player from instance.
	 * @param player player instance
	 */
	public void removePlayer(Player player)
	{
		_players.remove(player);
		if (_players.isEmpty())
		{
			final long emptyTime = _template.getEmptyDestroyTime();
			if ((_template.getDuration() == 0) || (emptyTime == 0))
			{
				destroy();
			}
			else if ((emptyTime >= 0) && (_emptyDestroyTask == null))
			{
				_emptyDestroyTask = ThreadPool.schedule(this::destroy, emptyTime);
			}
		}
	}
	
	/**
	 * Check if player is inside instance.
	 * @param player player to be checked
	 * @return {@code true} if player is inside, otherwise {@code false}
	 */
	public boolean containsPlayer(Player player)
	{
		return _players.contains(player);
	}
	
	/**
	 * Get all players inside instance.
	 * @return players within instance
	 */
	public Set<Player> getPlayers()
	{
		return _players;
	}
	
	/**
	 * Get count of players inside instance.
	 * @return players count inside instance
	 */
	public int getPlayersCount()
	{
		return _players.size();
	}
	
	/**
	 * Get first found player from instance world.<br>
	 * <i>This method is useful for instances with one player inside.</i>
	 * @return first found player, otherwise {@code null}
	 */
	public Player getFirstPlayer()
	{
		for (Player player : _players)
		{
			return player;
		}
		return null;
	}
	
	/**
	 * Get player by ID from instance.
	 * @param id objectId of player
	 * @return first player by ID, otherwise {@code null}
	 */
	public Player getPlayerById(int id)
	{
		for (Player player : _players)
		{
			if (player.getObjectId() == id)
			{
				return player;
			}
		}
		return null;
	}
	
	/**
	 * Get all players from instance world inside specified radius.
	 * @param object location of target
	 * @param radius radius around target
	 * @return players within radius
	 */
	public List<Player> getPlayersInsideRadius(ILocational object, int radius)
	{
		final List<Player> result = new LinkedList<>();
		for (Player player : _players)
		{
			if (player.isInsideRadius3D(object, radius))
			{
				result.add(player);
			}
		}
		return result;
	}
	
	/**
	 * Spawn doors inside instance world.
	 */
	private void spawnDoors()
	{
		for (DoorTemplate template : _template.getDoors().values())
		{
			// Create new door instance
			_doors.put(template.getId(), DoorData.getInstance().spawnDoor(template, this));
		}
	}
	
	/**
	 * Get all doors spawned inside instance world.
	 * @return collection of spawned doors
	 */
	public Collection<Door> getDoors()
	{
		return _doors.values();
	}
	
	/**
	 * Get spawned door by template ID.
	 * @param id template ID of door
	 * @return instance of door if found, otherwise {@code null}
	 */
	public Door getDoor(int id)
	{
		return _doors.get(id);
	}
	
	/**
	 * Handle open/close status of instance doors.
	 * @param id ID of doors
	 * @param open {@code true} means open door, {@code false} means close door
	 */
	public void openCloseDoor(int id, boolean open)
	{
		final Door door = _doors.get(id);
		if (door != null)
		{
			if (open)
			{
				if (!door.isOpen())
				{
					door.openMe();
				}
			}
			else if (door.isOpen())
			{
				door.closeMe();
			}
		}
	}
	
	/**
	 * Check if spawn group with name {@code name} exists.
	 * @param name name of group to be checked
	 * @return {@code true} if group exist, otherwise {@code false}
	 */
	public boolean isSpawnGroupExist(String name)
	{
		for (SpawnTemplate spawnTemplate : _spawns)
		{
			for (SpawnGroup group : spawnTemplate.getGroups())
			{
				if (name.equalsIgnoreCase(group.getName()))
				{
					return true;
				}
			}
		}
		return false;
	}
	
	/**
	 * Get spawn group by group name.
	 * @param name name of group
	 * @return list which contains spawn data from spawn group
	 */
	public List<SpawnGroup> getSpawnGroup(String name)
	{
		final List<SpawnGroup> spawns = new LinkedList<>();
		for (SpawnTemplate spawnTemplate : _spawns)
		{
			spawns.addAll(spawnTemplate.getGroupsByName(name));
		}
		return spawns;
	}
	
	/**
	 * @param name
	 * @return {@code List} of NPCs that are part of specified group
	 */
	public List<Npc> getNpcsOfGroup(String name)
	{
		return getNpcsOfGroup(name, null);
	}
	
	/**
	 * @param groupName
	 * @param filterValue
	 * @return {@code List} of NPCs that are part of specified group and matches filter specified
	 */
	public List<Npc> getNpcsOfGroup(String groupName, Predicate<Npc> filterValue)
	{
		Predicate<Npc> filter = filterValue;
		if (filter == null)
		{
			filter = Objects::nonNull;
		}
		
		final List<Npc> npcs = new LinkedList<>();
		for (SpawnTemplate spawnTemplate : _spawns)
		{
			for (SpawnGroup group : spawnTemplate.getGroupsByName(groupName))
			{
				for (NpcSpawnTemplate npcTemplate : group.getSpawns())
				{
					for (Npc npc : npcTemplate.getSpawnedNpcs())
					{
						if (filter.test(npc))
						{
							npcs.add(npc);
						}
					}
				}
			}
		}
		return npcs;
	}
	
	/**
	 * @param groupName
	 * @param filterValue
	 * @return {@code Npc} instance of an NPC that is part of a group and matches filter specified
	 */
	public Npc getNpcOfGroup(String groupName, Predicate<Npc> filterValue)
	{
		Predicate<Npc> filter = filterValue;
		if (filter == null)
		{
			filter = Objects::nonNull;
		}
		
		for (SpawnTemplate spawnTemplate : _spawns)
		{
			for (SpawnGroup group : spawnTemplate.getGroupsByName(groupName))
			{
				for (NpcSpawnTemplate npcTemplate : group.getSpawns())
				{
					for (Npc npc : npcTemplate.getSpawnedNpcs())
					{
						if (filter.test(npc))
						{
							return npc;
						}
					}
				}
			}
		}
		return null;
	}
	
	/**
	 * Spawn NPCs from group (defined in XML template) into instance world.
	 * @param name name of group which should be spawned
	 * @return list that contains NPCs spawned by this method
	 */
	public List<Npc> spawnGroup(String name)
	{
		final List<SpawnGroup> spawns = getSpawnGroup(name);
		if (spawns == null)
		{
			LOGGER.warning("Spawn group " + name + " doesn't exist for instance " + _template.getName() + " (" + _id + ")!");
			return Collections.emptyList();
		}
		
		final List<Npc> npcs = new LinkedList<>();
		try
		{
			for (SpawnGroup holder : spawns)
			{
				holder.spawnAll(this);
				holder.getSpawns().forEach(spawn -> npcs.addAll(spawn.getSpawnedNpcs()));
			}
		}
		catch (Exception e)
		{
			LOGGER.warning("Unable to spawn group " + name + " inside instance " + _template.getName() + " (" + _id + ")");
		}
		return npcs;
	}
	
	/**
	 * De-spawns NPCs from group (defined in XML template) from the instance world.
	 * @param name of group which should be de-spawned
	 */
	public void despawnGroup(String name)
	{
		final List<SpawnGroup> spawns = getSpawnGroup(name);
		if (spawns == null)
		{
			LOGGER.warning("Spawn group " + name + " doesn't exist for instance " + _template.getName() + " (" + _id + ")!");
			return;
		}
		
		try
		{
			spawns.forEach(SpawnGroup::despawnAll);
		}
		catch (Exception e)
		{
			LOGGER.warning("Unable to spawn group " + name + " inside instance " + _template.getName() + " (" + _id + ")");
		}
	}
	
	/**
	 * Get spawned NPCs from instance.
	 * @return set of NPCs from instance
	 */
	public Set<Npc> getNpcs()
	{
		return _npcs;
	}
	
	/**
	 * Get spawned NPCs from instance with specific IDs.
	 * @param id IDs of NPCs which should be found
	 * @return list of filtered NPCs from instance
	 */
	public List<Npc> getNpcs(int... id)
	{
		final List<Npc> result = new LinkedList<>();
		for (Npc npc : _npcs)
		{
			if (CommonUtil.contains(id, npc.getId()))
			{
				result.add(npc);
			}
		}
		return result;
	}
	
	/**
	 * Get spawned NPCs from instance with specific IDs and class type.
	 * @param <T>
	 * @param clazz
	 * @param ids IDs of NPCs which should be found
	 * @return list of filtered NPCs from instance
	 */
	@SafeVarargs
	@SuppressWarnings("unchecked")
	public final <T extends Creature> List<T> getNpcs(Class<T> clazz, int... ids)
	{
		final List<T> result = new LinkedList<>();
		for (Npc npc : _npcs)
		{
			if (((ids.length == 0) || CommonUtil.contains(ids, npc.getId())) && clazz.isInstance(npc))
			{
				result.add((T) npc);
			}
		}
		return result;
	}
	
	/**
	 * Get alive NPCs from instance.
	 * @return set of NPCs from instance
	 */
	public List<Npc> getAliveNpcs()
	{
		final List<Npc> result = new LinkedList<>();
		for (Npc npc : _npcs)
		{
			if (npc.getCurrentHp() > 0)
			{
				result.add(npc);
			}
		}
		return result;
	}
	
	/**
	 * Get alive NPCs from instance with specific IDs.
	 * @param id IDs of NPCs which should be found
	 * @return list of filtered NPCs from instance
	 */
	public List<Npc> getAliveNpcs(int... id)
	{
		final List<Npc> result = new LinkedList<>();
		for (Npc npc : _npcs)
		{
			if ((npc.getCurrentHp() > 0) && CommonUtil.contains(id, npc.getId()))
			{
				result.add(npc);
			}
		}
		return result;
	}
	
	/**
	 * Get spawned and alive NPCs from instance with specific IDs and class type.
	 * @param <T>
	 * @param clazz
	 * @param ids IDs of NPCs which should be found
	 * @return list of filtered NPCs from instance
	 */
	@SafeVarargs
	@SuppressWarnings("unchecked")
	public final <T extends Creature> List<T> getAliveNpcs(Class<T> clazz, int... ids)
	{
		final List<T> result = new LinkedList<>();
		for (Npc npc : _npcs)
		{
			if ((((ids.length == 0) || CommonUtil.contains(ids, npc.getId())) && (npc.getCurrentHp() > 0)) && clazz.isInstance(npc))
			{
				result.add((T) npc);
			}
		}
		return result;
	}
	
	/**
	 * Get alive NPC count from instance.
	 * @return count of filtered NPCs from instance
	 */
	public int getAliveNpcCount()
	{
		int count = 0;
		for (Npc npc : _npcs)
		{
			if (npc.getCurrentHp() > 0)
			{
				count++;
			}
		}
		return count;
	}
	
	/**
	 * Get alive NPC count from instance with specific IDs.
	 * @param id IDs of NPCs which should be counted
	 * @return count of filtered NPCs from instance
	 */
	public int getAliveNpcCount(int... id)
	{
		int count = 0;
		for (Npc npc : _npcs)
		{
			if ((npc.getCurrentHp() > 0) && CommonUtil.contains(id, npc.getId()))
			{
				count++;
			}
		}
		return count;
	}
	
	/**
	 * Get first found spawned NPC with specific ID.
	 * @param id ID of NPC to be found
	 * @return first found NPC with specified ID, otherwise {@code null}
	 */
	public Npc getNpc(int id)
	{
		for (Npc npc : _npcs)
		{
			if (npc.getId() == id)
			{
				return npc;
			}
		}
		return null;
	}
	
	public void addNpc(Npc npc)
	{
		_npcs.add(npc);
	}
	
	public void removeNpc(Npc npc)
	{
		_npcs.remove(npc);
	}
	
	/**
	 * Remove all players from instance world.
	 */
	private void removePlayers()
	{
		_players.forEach(this::ejectPlayer);
		_players.clear();
	}
	
	/**
	 * Despawn doors inside instance world.
	 */
	private void removeDoors()
	{
		for (Door door : _doors.values())
		{
			if (door != null)
			{
				door.decayMe();
			}
		}
		_doors.clear();
	}
	
	/**
	 * Despawn NPCs inside instance world.
	 */
	public void removeNpcs()
	{
		_spawns.forEach(SpawnTemplate::despawnAll);
		_npcs.forEach(Npc::deleteMe);
		_npcs.clear();
	}
	
	/**
	 * Change instance duration.
	 * @param minutes remaining time to destroy instance
	 */
	public void setDuration(int minutes)
	{
		// Instance never ends
		if (minutes < 0)
		{
			_endTime = -1;
			return;
		}
		
		// Stop running tasks
		final long millis = TimeUnit.MINUTES.toMillis(minutes);
		if (_cleanUpTask != null)
		{
			_cleanUpTask.cancel(true);
			_cleanUpTask = null;
		}
		
		if ((_emptyDestroyTask != null) && (millis < _emptyDestroyTask.getDelay(TimeUnit.MILLISECONDS)))
		{
			_emptyDestroyTask.cancel(true);
			_emptyDestroyTask = null;
		}
		
		// Set new cleanup task
		_endTime = System.currentTimeMillis() + millis;
		if (minutes < 1) // Destroy instance
		{
			destroy();
		}
		else
		{
			sendWorldDestroyMessage(minutes);
			if (minutes <= 5) // Message 1 minute before destroy
			{
				_cleanUpTask = ThreadPool.schedule(this::cleanUp, millis - 60000);
			}
			else // Message 5 minutes before destroy
			{
				_cleanUpTask = ThreadPool.schedule(this::cleanUp, millis - (5 * 60000));
			}
		}
	}
	
	/**
	 * Destroy current instance world.<br>
	 * <b><font color=red>Use this method to destroy instance world properly.</font></b>
	 */
	public synchronized void destroy()
	{
		if (_cleanUpTask != null)
		{
			_cleanUpTask.cancel(false);
			_cleanUpTask = null;
		}
		
		if (_emptyDestroyTask != null)
		{
			_emptyDestroyTask.cancel(false);
			_emptyDestroyTask = null;
		}
		
		_ejectDeadTasks.values().forEach(t -> t.cancel(true));
		_ejectDeadTasks.clear();
		
		// Notify DP scripts
		if (!isDynamic() && EventDispatcher.getInstance().hasListener(EventType.ON_INSTANCE_DESTROY, _template))
		{
			EventDispatcher.getInstance().notifyEvent(new OnInstanceDestroy(this), _template);
		}
		
		// Set reenter time when empty time is positive and reenter type is ON_FINISH.
		if ((_template.getEmptyDestroyTime() > 0) && (_template.getReenterType() == InstanceReenterType.ON_FINISH))
		{
			setReenterTime();
		}
		
		removePlayers();
		removeDoors();
		removeNpcs();
		
		InstanceManager.getInstance().unregister(getId());
	}
	
	/**
	 * Teleport player out of instance.
	 * @param player player that should be moved out
	 */
	public void ejectPlayer(Player player)
	{
		final Instance world = player.getInstanceWorld();
		if ((world != null) && world.equals(this))
		{
			final Location loc = _template.getExitLocation(player);
			if (loc != null)
			{
				player.teleToLocation(loc, null);
			}
			else
			{
				player.teleToLocation(TeleportWhereType.TOWN, null);
			}
		}
	}
	
	/**
	 * Send packet to each player from instance world.
	 * @param packets packets to be send
	 */
	public void broadcastPacket(ServerPacket... packets)
	{
		for (Player player : _players)
		{
			for (ServerPacket packet : packets)
			{
				player.sendPacket(packet);
			}
		}
	}
	
	/**
	 * Get instance creation time.
	 * @return creation time in milliseconds
	 */
	public long getStartTime()
	{
		return _startTime;
	}
	
	/**
	 * Get elapsed time since instance create.
	 * @return elapsed time in milliseconds
	 */
	public long getElapsedTime()
	{
		return System.currentTimeMillis() - _startTime;
	}
	
	/**
	 * Get remaining time before instance will be destroyed.
	 * @return remaining time in milliseconds if duration is not equal to -1, otherwise -1
	 */
	public long getRemainingTime()
	{
		return (_endTime == -1) ? -1 : (_endTime - System.currentTimeMillis());
	}
	
	/**
	 * Get instance destroy time.
	 * @return destroy time in milliseconds if duration is not equal to -1, otherwise -1
	 */
	public long getEndTime()
	{
		return _endTime;
	}
	
	/**
	 * Set reenter penalty for players associated with current instance.<br>
	 * Penalty time is calculated from XML reenter data.
	 */
	public void setReenterTime()
	{
		setReenterTime(_template.calculateReenterTime());
	}
	
	/**
	 * Set reenter penalty for players associated with current instance.
	 * @param time penalty time in milliseconds since January 1, 1970
	 */
	public void setReenterTime(long time)
	{
		// Cannot store reenter data for instance without template id.
		if ((_template.getId() == -1) && (time > 0))
		{
			return;
		}
		
		try (Connection con = DatabaseFactory.getConnection();
			PreparedStatement ps = con.prepareStatement("INSERT IGNORE INTO character_instance_time (charId,instanceId,time) VALUES (?,?,?)"))
		{
			// Save to database
			for (Integer playerId : _allowed)
			{
				ps.setInt(1, playerId);
				ps.setInt(2, _template.getId());
				ps.setLong(3, time);
				ps.addBatch();
			}
			ps.executeBatch();
			
			// Save to memory and send message to player
			final SystemMessage msg = new SystemMessage(SystemMessageId.INSTANT_ZONE_S1_S_ENTRY_HAS_BEEN_RESTRICTED_YOU_CAN_CHECK_THE_NEXT_POSSIBLE_ENTRY_TIME_BY_USING_THE_COMMAND_INSTANCEZONE);
			if (InstanceManager.getInstance().getInstanceName(getTemplateId()) != null)
			{
				msg.addInstanceName(_template.getId());
			}
			else
			{
				msg.addString(_template.getName());
			}
			_allowed.forEach(playerId ->
			{
				InstanceManager.getInstance().setReenterPenalty(playerId, getTemplateId(), time);
				final Player player = World.getInstance().getPlayer(playerId);
				if ((player != null) && player.isOnline())
				{
					player.sendPacket(msg);
				}
			});
		}
		catch (Exception e)
		{
			LOGGER.log(Level.WARNING, "Could not insert character instance reenter data: ", e);
		}
	}
	
	/**
	 * Set instance world to finish state.<br>
	 * Calls method {@link Instance#finishInstance(int)} with {@link Config#INSTANCE_FINISH_TIME} as argument.
	 */
	public void finishInstance()
	{
		finishInstance(Config.INSTANCE_FINISH_TIME);
	}
	
	/**
	 * Set instance world to finish state.<br>
	 * Set re-enter for allowed players if required data are defined in template.<br>
	 * Change duration of instance and set empty destroy time to 0 (instant effect).
	 * @param delay delay in minutes
	 */
	public void finishInstance(int delay)
	{
		// Set re-enter for players
		if (_template.getReenterType() == InstanceReenterType.ON_FINISH)
		{
			setReenterTime();
		}
		// Change instance duration
		setDuration(delay);
	}
	
	// ---------------------------------------------
	// Listeners
	// ---------------------------------------------
	/**
	 * This method is called when player dies inside instance.
	 * @param player
	 */
	public void onDeath(Player player)
	{
		if (!player.isOnEvent() && (_template.getEjectTime() > 0))
		{
			// Send message
			final SystemMessage sm = new SystemMessage(SystemMessageId.IF_YOU_ARE_NOT_RESURRECTED_WITHIN_S1_MINUTE_S_YOU_WILL_BE_EXPELLED_FROM_THE_INSTANT_ZONE);
			sm.addInt(_template.getEjectTime());
			player.sendPacket(sm);
			
			// Start eject task
			final ScheduledFuture<?> oldTAsk = _ejectDeadTasks.put(player.getObjectId(), ThreadPool.schedule(() ->
			{
				if (player.isDead())
				{
					ejectPlayer(player.getActingPlayer());
				}
			}, _template.getEjectTime() * 60 * 1000)); // minutes to milliseconds
			if (oldTAsk != null)
			{
				oldTAsk.cancel(true);
			}
		}
	}
	
	/**
	 * This method is called when player was resurrected inside instance.
	 * @param player resurrected player
	 */
	public void doRevive(Player player)
	{
		final ScheduledFuture<?> task = _ejectDeadTasks.remove(player.getObjectId());
		if (task != null)
		{
			task.cancel(true);
		}
	}
	
	/**
	 * This method is called when object enter or leave this instance.
	 * @param object instance of object which enters/leaves instance
	 * @param enter {@code true} when object enter, {@code false} when object leave
	 */
	public void onInstanceChange(WorldObject object, boolean enter)
	{
		if (object.isPlayer())
		{
			final Player player = object.getActingPlayer();
			if (enter)
			{
				addPlayer(player);
				
				// Cancel _emptyDestroyTask when remaining time is bigger than empty time.
				final long emptyTime = _template.getEmptyDestroyTime();
				if ((_emptyDestroyTask != null) && (emptyTime > 0) && (getRemainingTime() > emptyTime))
				{
					_emptyDestroyTask.cancel(false);
					_emptyDestroyTask = null;
				}
				
				// Set origin return location if enabled
				if (_template.getExitLocationType() == InstanceTeleportType.ORIGIN)
				{
					player.getVariables().set(PlayerVariables.INSTANCE_ORIGIN, player.getX() + ";" + player.getY() + ";" + player.getZ());
				}
				
				// Remove player buffs
				if (_template.isRemoveBuffEnabled())
				{
					_template.removePlayerBuff(player);
				}
				
				// Notify DP scripts
				if (!isDynamic() && EventDispatcher.getInstance().hasListener(EventType.ON_INSTANCE_ENTER, _template))
				{
					EventDispatcher.getInstance().notifyEventAsync(new OnInstanceEnter(player, this), _template);
				}
			}
			else
			{
				removePlayer(player);
				
				// Notify DP scripts
				if (!isDynamic() && EventDispatcher.getInstance().hasListener(EventType.ON_INSTANCE_LEAVE, _template))
				{
					EventDispatcher.getInstance().notifyEventAsync(new OnInstanceLeave(player, this), _template);
				}
			}
		}
		else if (object.isNpc())
		{
			final Npc npc = (Npc) object;
			if (enter)
			{
				addNpc(npc);
			}
			else
			{
				if (npc.getSpawn() != null)
				{
					npc.getSpawn().stopRespawn();
				}
				removeNpc(npc);
			}
		}
	}
	
	/**
	 * This method is called when player logout inside instance world.
	 * @param player player who logout
	 */
	public void onPlayerLogout(Player player)
	{
		removePlayer(player);
		if (Config.RESTORE_PLAYER_INSTANCE)
		{
			player.getVariables().set(PlayerVariables.INSTANCE_RESTORE, _id);
		}
		else
		{
			final Location loc = getExitLocation(player);
			if (loc != null)
			{
				player.setLocationInvisible(loc);
				// If player has death pet, put him out of instance world
				final Summon pet = player.getPet();
				if (pet != null)
				{
					pet.teleToLocation(loc, true);
				}
			}
		}
	}
	
	// ----------------------------------------------
	// Template methods
	// ----------------------------------------------
	/**
	 * Get parameters from instance template.
	 * @return template parameters
	 */
	public StatSet getTemplateParameters()
	{
		return _template.getParameters();
	}
	
	/**
	 * Get template ID of instance world.
	 * @return instance template ID
	 */
	public int getTemplateId()
	{
		return _template.getId();
	}
	
	/**
	 * Get type of re-enter data.
	 * @return type of re-enter (see {@link InstanceReenterType} for possible values)
	 */
	public InstanceReenterType getReenterType()
	{
		return _template.getReenterType();
	}
	
	/**
	 * Check if instance world is PvP zone.
	 * @return {@code true} when instance is PvP zone, otherwise {@code false}
	 */
	public boolean isPvP()
	{
		return _template.isPvP();
	}
	
	/**
	 * Check if summoning players to instance world is allowed.
	 * @return {@code true} when summon is allowed, otherwise {@code false}
	 */
	public boolean isPlayerSummonAllowed()
	{
		return _template.isPlayerSummonAllowed();
	}
	
	/**
	 * Get enter location for instance world.
	 * @return {@link Location} object if instance has enter location defined, otherwise {@code null}
	 */
	public Location getEnterLocation()
	{
		return _template.getEnterLocation();
	}
	
	/**
	 * Get all enter locations defined in XML template.
	 * @return list of enter locations
	 */
	public List<Location> getEnterLocations()
	{
		return _template.getEnterLocations();
	}
	
	/**
	 * Get exit location for player from instance world.
	 * @param player instance of player who wants to leave instance world
	 * @return {@link Location} object if instance has exit location defined, otherwise {@code null}
	 */
	public Location getExitLocation(Player player)
	{
		return _template.getExitLocation(player);
	}
	
	/**
	 * @return the exp rate of the instance
	 */
	public float getExpRate()
	{
		return _template.getExpRate();
	}
	
	/**
	 * @return the sp rate of the instance
	 */
	public float getSPRate()
	{
		return _template.getSPRate();
	}
	
	/**
	 * @return the party exp rate of the instance
	 */
	public float getExpPartyRate()
	{
		return _template.getExpPartyRate();
	}
	
	/**
	 * @return the party sp rate of the instance
	 */
	public float getSPPartyRate()
	{
		return _template.getSPPartyRate();
	}
	
	// ----------------------------------------------
	// Tasks
	// ----------------------------------------------
	/**
	 * Clean up instance.
	 */
	private void cleanUp()
	{
		if (_cleanUpTask != null)
		{
			_cleanUpTask.cancel(true);
		}
		
		if (getRemainingTime() <= TimeUnit.MINUTES.toMillis(1))
		{
			sendWorldDestroyMessage(1);
			_cleanUpTask = ThreadPool.schedule(this::destroy, 60 * 1000); // 1 minute
		}
		else
		{
			sendWorldDestroyMessage(5);
			_cleanUpTask = ThreadPool.schedule(this::cleanUp, 5 * 60 * 1000); // 5 minutes
		}
	}
	
	/**
	 * Show instance destroy messages to players inside instance world.
	 * @param delay time in minutes
	 */
	private void sendWorldDestroyMessage(int delay)
	{
		// Dimensional wrap does not show timer after 5 minutes.
		if (delay > 5)
		{
			return;
		}
		final SystemMessage sm = new SystemMessage(SystemMessageId.THIS_INSTANT_ZONE_WILL_BE_TERMINATED_IN_S1_MINUTE_S_YOU_WILL_BE_FORCED_OUT_OF_THE_DUNGEON_WHEN_THE_TIME_EXPIRES);
		sm.addInt(delay);
		broadcastPacket(sm);
	}
	
	@Override
	public boolean equals(Object obj)
	{
		return (obj instanceof Instance) && (((Instance) obj).getId() == getId());
	}
	
	@Override
	public String toString()
	{
		return _template.getName() + "(" + _id + ")";
	}
}