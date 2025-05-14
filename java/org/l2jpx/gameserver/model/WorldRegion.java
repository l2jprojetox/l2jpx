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
package org.l2jpx.gameserver.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.atomic.AtomicInteger;

import org.l2jpx.Config;
import org.l2jpx.commons.threads.ThreadPool;
import org.l2jpx.gameserver.ai.CtrlIntention;
import org.l2jpx.gameserver.model.actor.Attackable;
import org.l2jpx.gameserver.model.actor.Npc;
import org.l2jpx.gameserver.model.actor.instance.Door;
import org.l2jpx.gameserver.model.actor.instance.Fence;
import org.l2jpx.gameserver.taskmanager.RandomAnimationTaskManager;

public class WorldRegion
{
	/** Set containing visible objects in this world region. */
	private final Set<WorldObject> _visibleObjects = ConcurrentHashMap.newKeySet();
	/** List containing doors in this world region. */
	private final List<Door> _doors = new ArrayList<>(1);
	/** List containing fences in this world region. */
	private final List<Fence> _fences = new ArrayList<>(1);
	/** Array containing nearby regions forming this world region's effective area. */
	private WorldRegion[] _surroundingRegions;
	private final int _regionX;
	private final int _regionY;
	private boolean _active = Config.GRIDS_ALWAYS_ON;
	private ScheduledFuture<?> _neighborsTask = null;
	private final AtomicInteger _activeNeighbors = new AtomicInteger();
	
	public WorldRegion(int regionX, int regionY)
	{
		_regionX = regionX;
		_regionY = regionY;
	}
	
	private void switchAI(boolean isOn)
	{
		if (_visibleObjects.isEmpty())
		{
			return;
		}
		
		if (!isOn)
		{
			for (WorldObject wo : _visibleObjects)
			{
				if (wo.isAttackable())
				{
					final Attackable mob = (Attackable) wo;
					
					// Set target to null and cancel attack or cast.
					mob.setTarget(null);
					
					// Stop movement.
					mob.stopMove(null);
					
					// Stop all active skills effects in progress on the Creature.
					mob.stopAllEffects();
					
					mob.clearAggroList();
					mob.getAttackByList().clear();
					
					// Teleport to spawn when too far away.
					final Spawn spawn = mob.getSpawn();
					if ((spawn != null) && (mob.calculateDistance2D(spawn) > Config.MAX_DRIFT_RANGE))
					{
						mob.teleToLocation(spawn);
					}
					
					// Stop the AI tasks.
					if (mob.hasAI())
					{
						mob.getAI().setIntention(CtrlIntention.AI_INTENTION_IDLE);
						mob.getAI().stopAITask();
					}
					
					// Stop attack task.
					mob.abortAttack();
					
					RandomAnimationTaskManager.getInstance().remove(mob);
				}
				else if (wo instanceof Npc)
				{
					RandomAnimationTaskManager.getInstance().remove((Npc) wo);
				}
			}
		}
		else
		{
			for (WorldObject wo : _visibleObjects)
			{
				if (wo.isAttackable())
				{
					// Start HP/MP/CP regeneration task.
					((Attackable) wo).getStatus().startHpMpRegeneration();
					RandomAnimationTaskManager.getInstance().add((Npc) wo);
				}
				else if (wo.isNpc())
				{
					RandomAnimationTaskManager.getInstance().add((Npc) wo);
				}
			}
		}
	}
	
	public boolean isActive()
	{
		return _active;
	}
	
	public void incrementActiveNeighbors()
	{
		_activeNeighbors.incrementAndGet();
	}
	
	public void decrementActiveNeighbors()
	{
		_activeNeighbors.decrementAndGet();
	}
	
	public boolean areNeighborsActive()
	{
		return Config.GRIDS_ALWAYS_ON || (_activeNeighbors.get() > 0);
	}
	
	public boolean areNeighborsEmpty()
	{
		for (int i = 0; i < _surroundingRegions.length; i++)
		{
			final WorldRegion worldRegion = _surroundingRegions[i];
			if (worldRegion.isActive())
			{
				final Collection<WorldObject> regionObjects = worldRegion.getVisibleObjects();
				if (regionObjects.isEmpty())
				{
					continue;
				}
				
				for (WorldObject wo : regionObjects)
				{
					if ((wo != null) && wo.isPlayable())
					{
						return false;
					}
				}
			}
		}
		return true;
	}
	
	/**
	 * this function turns this region's AI and geodata on or off
	 * @param value
	 */
	public void setActive(boolean value)
	{
		if (_active == value)
		{
			return;
		}
		
		_active = value;
		
		if (value)
		{
			for (int i = 0; i < _surroundingRegions.length; i++)
			{
				_surroundingRegions[i].incrementActiveNeighbors();
			}
		}
		else
		{
			for (int i = 0; i < _surroundingRegions.length; i++)
			{
				_surroundingRegions[i].decrementActiveNeighbors();
			}
		}
		
		// Turn the AI on or off to match the region's activation.
		switchAI(value);
	}
	
	/**
	 * Immediately sets self as active and starts a timer to set neighbors as active this timer is to avoid turning on neighbors in the case when a person just teleported into a region and then teleported out immediately...there is no reason to activate all the neighbors in that case.
	 */
	private void startActivation()
	{
		// First set self to active and do self-tasks...
		setActive(true);
		
		// If the timer to deactivate neighbors is running, cancel it.
		synchronized (this)
		{
			if (_neighborsTask != null)
			{
				_neighborsTask.cancel(true);
				_neighborsTask = null;
			}
			
			// Then, set a timer to activate the neighbors.
			_neighborsTask = ThreadPool.schedule(() ->
			{
				for (int i = 0; i < _surroundingRegions.length; i++)
				{
					_surroundingRegions[i].setActive(true);
				}
			}, 1000 * Config.GRID_NEIGHBOR_TURNON_TIME);
		}
	}
	
	/**
	 * starts a timer to set neighbors (including self) as inactive this timer is to avoid turning off neighbors in the case when a person just moved out of a region that he may very soon return to. There is no reason to turn self & neighbors off in that case.
	 */
	private void startDeactivation()
	{
		// If the timer to activate neighbors is running, cancel it.
		synchronized (this)
		{
			if (_neighborsTask != null)
			{
				_neighborsTask.cancel(true);
				_neighborsTask = null;
			}
			
			// Start a timer to "suggest" a deactivate to self and neighbors.
			// Suggest means: first check if a neighbor has Players in it. If not, deactivate.
			_neighborsTask = ThreadPool.schedule(() ->
			{
				for (int i = 0; i < _surroundingRegions.length; i++)
				{
					final WorldRegion worldRegion = _surroundingRegions[i];
					if (worldRegion.areNeighborsEmpty())
					{
						worldRegion.setActive(false);
					}
				}
			}, 1000 * Config.GRID_NEIGHBOR_TURNOFF_TIME);
		}
	}
	
	/**
	 * Add the WorldObject in the WorldObjectHashSet(WorldObject) _visibleObjects containing WorldObject visible in this WorldRegion<br>
	 * If WorldObject is a Player, Add the Player in the WorldObjectHashSet(Player) _allPlayable containing Player of all player in game in this WorldRegion
	 * @param object
	 */
	public void addVisibleObject(WorldObject object)
	{
		if (object == null)
		{
			return;
		}
		
		_visibleObjects.add(object);
		
		if (object.isDoor())
		{
			for (int i = 0; i < _surroundingRegions.length; i++)
			{
				_surroundingRegions[i].addDoor((Door) object);
			}
		}
		else if (object.isFence())
		{
			for (int i = 0; i < _surroundingRegions.length; i++)
			{
				_surroundingRegions[i].addFence((Fence) object);
			}
		}
		
		// If this is the first player to enter the region, activate self and neighbors.
		if (object.isPlayable() && !_active && !Config.GRIDS_ALWAYS_ON)
		{
			startActivation();
		}
	}
	
	/**
	 * Remove the WorldObject from the WorldObjectHashSet(WorldObject) _visibleObjects in this WorldRegion. If WorldObject is a Player, remove it from the WorldObjectHashSet(Player) _allPlayable of this WorldRegion
	 * @param object
	 */
	public void removeVisibleObject(WorldObject object)
	{
		if (object == null)
		{
			return;
		}
		
		if (_visibleObjects.isEmpty())
		{
			return;
		}
		
		_visibleObjects.remove(object);
		
		if (object.isDoor())
		{
			for (int i = 0; i < _surroundingRegions.length; i++)
			{
				_surroundingRegions[i].removeDoor((Door) object);
			}
		}
		else if (object.isFence())
		{
			for (int i = 0; i < _surroundingRegions.length; i++)
			{
				_surroundingRegions[i].removeFence((Fence) object);
			}
		}
		
		if (object.isPlayable() && areNeighborsEmpty() && !Config.GRIDS_ALWAYS_ON)
		{
			startDeactivation();
		}
	}
	
	public Collection<WorldObject> getVisibleObjects()
	{
		return _visibleObjects;
	}
	
	public synchronized void addDoor(Door door)
	{
		if (!_doors.contains(door))
		{
			_doors.add(door);
		}
	}
	
	private synchronized void removeDoor(Door door)
	{
		_doors.remove(door);
	}
	
	public List<Door> getDoors()
	{
		return _doors;
	}
	
	public synchronized void addFence(Fence fence)
	{
		if (!_fences.contains(fence))
		{
			_fences.add(fence);
		}
	}
	
	private synchronized void removeFence(Fence fence)
	{
		_fences.remove(fence);
	}
	
	public List<Fence> getFences()
	{
		return _fences;
	}
	
	public void setSurroundingRegions(WorldRegion[] regions)
	{
		_surroundingRegions = regions;
		
		// Make sure that this region is always the first region to improve bulk operations when this region should be updated first.
		for (int i = 0; i < _surroundingRegions.length; i++)
		{
			if (_surroundingRegions[i] == this)
			{
				final WorldRegion first = _surroundingRegions[0];
				_surroundingRegions[0] = this;
				_surroundingRegions[i] = first;
			}
		}
	}
	
	public WorldRegion[] getSurroundingRegions()
	{
		return _surroundingRegions;
	}
	
	public boolean isSurroundingRegion(WorldRegion region)
	{
		return (region != null) && (_regionX >= (region.getRegionX() - 1)) && (_regionX <= (region.getRegionX() + 1)) && (_regionY >= (region.getRegionY() - 1)) && (_regionY <= (region.getRegionY() + 1));
	}
	
	public int getRegionX()
	{
		return _regionX;
	}
	
	public int getRegionY()
	{
		return _regionY;
	}
	
	@Override
	public String toString()
	{
		return "(" + _regionX + ", " + _regionY + ")";
	}
}
