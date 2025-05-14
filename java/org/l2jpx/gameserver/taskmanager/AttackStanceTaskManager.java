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
package org.l2jpx.gameserver.taskmanager;

import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.l2jpx.commons.threads.ThreadPool;
import org.l2jpx.gameserver.model.actor.Creature;
import org.l2jpx.gameserver.model.actor.Summon;
import org.l2jpx.gameserver.network.serverpackets.AutoAttackStop;

/**
 * Attack stance task manager.
 * @author Luca Baldi
 */
public class AttackStanceTaskManager implements Runnable
{
	private static final Logger LOGGER = Logger.getLogger(AttackStanceTaskManager.class.getName());
	
	public static final long COMBAT_TIME = 15000;
	
	private static final Map<Creature, Long> CREATURE_ATTACK_STANCES = new ConcurrentHashMap<>();
	private static boolean _working = false;
	
	protected AttackStanceTaskManager()
	{
		ThreadPool.schedulePriorityTaskAtFixedRate(this, 0, 1000);
	}
	
	@Override
	public void run()
	{
		if (_working)
		{
			return;
		}
		_working = true;
		
		if (!CREATURE_ATTACK_STANCES.isEmpty())
		{
			try
			{
				final long currentTime = System.currentTimeMillis();
				final Iterator<Entry<Creature, Long>> iterator = CREATURE_ATTACK_STANCES.entrySet().iterator();
				Entry<Creature, Long> entry;
				Creature creature;
				
				while (iterator.hasNext())
				{
					entry = iterator.next();
					if ((currentTime - entry.getValue()) > COMBAT_TIME)
					{
						creature = entry.getKey();
						if (creature != null)
						{
							creature.broadcastPacket(new AutoAttackStop(creature.getObjectId()));
							creature.getAI().setAutoAttacking(false);
							if (creature.isPlayer() && creature.hasSummon())
							{
								final Summon pet = creature.getPet();
								if (pet != null)
								{
									pet.broadcastPacket(new AutoAttackStop(pet.getObjectId()));
								}
								creature.getServitors().values().forEach(s -> s.broadcastPacket(new AutoAttackStop(s.getObjectId())));
							}
						}
						iterator.remove();
					}
				}
			}
			catch (Exception e)
			{
				// Unless caught here, players remain in attack positions.
				LOGGER.log(Level.WARNING, "Error in AttackStanceTaskManager: " + e.getMessage(), e);
			}
		}
		
		_working = false;
	}
	
	/**
	 * Adds the attack stance task.
	 * @param creature the actor
	 */
	public void addAttackStanceTask(Creature creature)
	{
		if (creature == null)
		{
			return;
		}
		
		CREATURE_ATTACK_STANCES.put(creature, System.currentTimeMillis());
	}
	
	/**
	 * Removes the attack stance task.
	 * @param creature the actor
	 */
	public void removeAttackStanceTask(Creature creature)
	{
		Creature actor = creature;
		if (actor != null)
		{
			if (actor.isSummon())
			{
				actor = actor.getActingPlayer();
			}
			CREATURE_ATTACK_STANCES.remove(actor);
		}
	}
	
	/**
	 * Checks for attack stance task.
	 * @param creature the actor
	 * @return {@code true} if the character has an attack stance task, {@code false} otherwise
	 */
	public boolean hasAttackStanceTask(Creature creature)
	{
		Creature actor = creature;
		if (actor != null)
		{
			if (actor.isSummon())
			{
				actor = actor.getActingPlayer();
			}
			return CREATURE_ATTACK_STANCES.containsKey(actor);
		}
		
		return false;
	}
	
	/**
	 * Gets the single instance of AttackStanceTaskManager.
	 * @return single instance of AttackStanceTaskManager
	 */
	public static AttackStanceTaskManager getInstance()
	{
		return SingletonHolder.INSTANCE;
	}
	
	private static class SingletonHolder
	{
		protected static final AttackStanceTaskManager INSTANCE = new AttackStanceTaskManager();
	}
}
