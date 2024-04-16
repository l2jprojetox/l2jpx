package com.px.gameserver.model.spawn;

import java.io.InvalidClassException;

import com.px.commons.random.Rnd;

import com.px.Config;
import com.px.gameserver.data.manager.SpawnManager;
import com.px.gameserver.geoengine.GeoEngine;
import com.px.gameserver.model.WorldObject;
import com.px.gameserver.model.actor.Creature;
import com.px.gameserver.model.actor.Npc;
import com.px.gameserver.model.actor.template.NpcTemplate;
import com.px.gameserver.model.location.Location;
import com.px.gameserver.model.location.SpawnLocation;

/**
 * This class manages the spawn and respawn of a single {@link Npc} at given {@link SpawnLocation}.
 */
public final class Spawn extends ASpawn
{
	private final SpawnLocation _loc = new SpawnLocation(0, 0, 0, 0);
	
	private Npc _npc;
	
	public Spawn(int id) throws SecurityException, ClassNotFoundException, NoSuchMethodException, InvalidClassException
	{
		super(id);
	}
	
	public Spawn(NpcTemplate template) throws SecurityException, ClassNotFoundException, NoSuchMethodException, InvalidClassException
	{
		super(template);
	}
	
	@Override
	public SpawnLocation getSpawnLocation()
	{
		// Create spawn location (this object is directly assigned to Npc, while Spawn is keeping its own).
		final SpawnLocation loc = _loc.clone();
		
		// Get random heading, if not defined.
		if (loc.getHeading() < 0)
			loc.setHeading(Rnd.get(65536));
		
		return loc;
	}
	
	@Override
	public Location getRandomWalkLocation(Npc npc, int offset)
	{
		// Get location object (spawn location).
		final Location loc = _loc.clone();
		
		// Generate random location based on offset.
		loc.addRandomOffset(offset);
		
		// Validate location using geodata.
		loc.set(GeoEngine.getInstance().getValidLocation(npc, loc));
		return loc;
	}
	
	@Override
	public boolean isInMyTerritory(WorldObject worldObject)
	{
		return worldObject.getPosition().isIn3DRadius(_loc, Config.MAX_DRIFT_RANGE);
	}
	
	@Override
	public Npc doSpawn(boolean isSummonSpawn, Creature summoner)
	{
		// Spawn NPC.
		_npc = super.doSpawn(isSummonSpawn, summoner);
		if (_npc == null)
		{
			LOGGER.warn("Can not spawn id {} from loc {}.", getNpcId(), _loc);
		}
		// Add Spawn to SpawnManager.
		else
			SpawnManager.getInstance().addSpawn(this);
		
		return _npc;
	}
	
	@Override
	public void doDelete()
	{
		if (_npc == null)
			return;
		
		// Reset spawn data.
		if (_spawnData != null)
			_spawnData.setStatus((byte) -1);
		
		// Delete privates which were manually spawned via createOnePrivate / createOnePrivateEx.
		if (_npc.isMaster())
			_npc.getMinions().forEach(Npc::deleteMe);
		
		// Cancel respawn task and delete NPC.
		_npc.cancelRespawn();
		_npc.deleteMe();
		_npc = null;
	}
	
	@Override
	public void onDecay(Npc npc)
	{
		// NPC can be respawned -> calculate the random time and schedule respawn.
		if (getRespawnDelay() > 0)
		{
			// Calculate the random delay.
			final long respawnDelay = calculateRespawnDelay() * 1000;
			
			// Check spawn data and set respawn.
			if (_spawnData != null)
				_spawnData.setRespawn(respawnDelay);
		}
		// Npc can't be respawned, it disappears permanently -> Remove Spawn from SpawnManager.
		else
			SpawnManager.getInstance().deleteSpawn(this);
	}
	
	@Override
	public String toString()
	{
		return "Spawn [id=" + getNpcId() + "]";
	}
	
	@Override
	public String getDescription()
	{
		return "Location: " + _loc;
	}
	
	@Override
	public Npc getNpc()
	{
		return _npc;
	}
	
	@Override
	public void updateSpawnData()
	{
		if (_spawnData == null)
			return;
		
		_spawnData.setStats(_npc);
	}
	
	@Override
	public void sendScriptEvent(int eventId, int arg1, int arg2)
	{
		_npc.sendScriptEvent(eventId, arg1, arg2);
	}
	
	/**
	 * Sets the {@link SpawnLocation} of this {@link Spawn}.
	 * @param loc : The SpawnLocation to set.
	 */
	public void setLoc(SpawnLocation loc)
	{
		_loc.set(loc.getX(), loc.getY(), GeoEngine.getInstance().getHeight(loc), loc.getHeading());
	}
	
	/**
	 * Sets the {@link SpawnLocation} of this {@link Spawn} using separate coordinates.
	 * @param x : X coordinate.
	 * @param y : Y coordinate.
	 * @param z : Z coordinate.
	 * @param heading : Heading.
	 */
	public void setLoc(int x, int y, int z, int heading)
	{
		_loc.set(x, y, GeoEngine.getInstance().getHeight(x, y, z), heading);
	}
	
	/**
	 * @return the X coordinate of the {@link SpawnLocation}.
	 */
	public int getLocX()
	{
		return _loc.getX();
	}
	
	/**
	 * @return the Y coordinate of the {@link SpawnLocation}.
	 */
	public int getLocY()
	{
		return _loc.getY();
	}
	
	/**
	 * @return the Z coordinate of the {@link SpawnLocation}.
	 */
	public int getLocZ()
	{
		return _loc.getZ();
	}
	
	/**
	 * @return the heading coordinate of the {@link SpawnLocation}.
	 */
	public int getHeading()
	{
		return _loc.getHeading();
	}
}