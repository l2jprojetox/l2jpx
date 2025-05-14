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

import org.l2jpx.Config;
import org.l2jpx.commons.threads.ThreadPool;
import org.l2jpx.gameserver.model.actor.Attackable;
import org.l2jpx.gameserver.model.actor.Creature;
import org.l2jpx.gameserver.model.actor.Player;
import org.l2jpx.gameserver.model.actor.templates.NpcTemplate;

/**
 * @author Mobius
 */
public class DecayTaskManager implements Runnable
{
	private static final Map<Creature, Long> DECAY_SCHEDULES = new ConcurrentHashMap<>();
	private static boolean _working = false;
	
	protected DecayTaskManager()
	{
		ThreadPool.scheduleAtFixedRate(this, 0, 1000);
	}
	
	@Override
	public void run()
	{
		if (_working)
		{
			return;
		}
		_working = true;
		
		if (!DECAY_SCHEDULES.isEmpty())
		{
			final long currentTime = System.currentTimeMillis();
			final Iterator<Entry<Creature, Long>> iterator = DECAY_SCHEDULES.entrySet().iterator();
			Entry<Creature, Long> entry;
			
			while (iterator.hasNext())
			{
				entry = iterator.next();
				if (currentTime > entry.getValue())
				{
					entry.getKey().onDecay();
					iterator.remove();
				}
			}
		}
		
		_working = false;
	}
	
	/**
	 * Adds a decay task for the specified character.<br>
	 * If the decay task already exists it cancels it and re-adds it.
	 * @param creature the creature
	 */
	public void add(Creature creature)
	{
		if (creature == null)
		{
			return;
		}
		
		long delay;
		if (creature.getTemplate() instanceof NpcTemplate)
		{
			delay = ((NpcTemplate) creature.getTemplate()).getCorpseTime();
		}
		else
		{
			delay = Config.DEFAULT_CORPSE_TIME;
		}
		
		if (creature.isAttackable() && (((Attackable) creature).isSpoiled() || ((Attackable) creature).isSeeded()))
		{
			delay += Config.SPOILED_CORPSE_EXTEND_TIME;
		}
		
		if (creature.isPlayer())
		{
			final Player player = creature.getActingPlayer();
			if (player.isOfflinePlay() && Config.OFFLINE_PLAY_LOGOUT_ON_DEATH)
			{
				delay = 10; // 10 seconds
			}
			else if (Config.DISCONNECT_AFTER_DEATH)
			{
				delay = 3600; // 1 hour
			}
		}
		
		// Add to decay schedules.
		DECAY_SCHEDULES.put(creature, System.currentTimeMillis() + (delay * 1000));
	}
	
	/**
	 * Cancels the decay task of the specified character.
	 * @param creature the creature
	 */
	public void cancel(Creature creature)
	{
		DECAY_SCHEDULES.remove(creature);
	}
	
	/**
	 * Gets the remaining time of the specified character's decay task.
	 * @param creature the creature
	 * @return if a decay task exists the remaining time, {@code Long.MAX_VALUE} otherwise
	 */
	public long getRemainingTime(Creature creature)
	{
		final Long time = DECAY_SCHEDULES.get(creature);
		return time != null ? time.longValue() - System.currentTimeMillis() : Long.MAX_VALUE;
	}
	
	@Override
	public String toString()
	{
		final StringBuilder ret = new StringBuilder();
		ret.append("============= DecayTask Manager Report ============");
		ret.append(System.lineSeparator());
		ret.append("Tasks count: ");
		ret.append(DECAY_SCHEDULES.size());
		ret.append(System.lineSeparator());
		ret.append("Tasks dump:");
		ret.append(System.lineSeparator());
		
		final long time = System.currentTimeMillis();
		for (Entry<Creature, Long> entry : DECAY_SCHEDULES.entrySet())
		{
			ret.append("Class/Name: ");
			ret.append(entry.getKey().getClass().getSimpleName());
			ret.append('/');
			ret.append(entry.getKey().getName());
			ret.append(" decay timer: ");
			ret.append(entry.getValue().longValue() - time);
			ret.append(System.lineSeparator());
		}
		
		return ret.toString();
	}
	
	public static DecayTaskManager getInstance()
	{
		return SingletonHolder.INSTANCE;
	}
	
	private static class SingletonHolder
	{
		protected static final DecayTaskManager INSTANCE = new DecayTaskManager();
	}
}
