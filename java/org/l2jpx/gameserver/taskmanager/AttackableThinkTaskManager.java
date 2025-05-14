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
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.l2jpx.commons.threads.ThreadPool;
import org.l2jpx.gameserver.ai.CreatureAI;
import org.l2jpx.gameserver.model.actor.Attackable;

/**
 * @author Mobius
 */
public class AttackableThinkTaskManager
{
	private static final Set<Set<Attackable>> POOLS = ConcurrentHashMap.newKeySet();
	private static final int POOL_SIZE = 1000;
	private static final int TASK_DELAY = 1000;
	
	protected AttackableThinkTaskManager()
	{
	}
	
	private class AttackableThink implements Runnable
	{
		private final Set<Attackable> _attackables;
		
		public AttackableThink(Set<Attackable> attackables)
		{
			_attackables = attackables;
		}
		
		@Override
		public void run()
		{
			if (_attackables.isEmpty())
			{
				return;
			}
			
			CreatureAI ai;
			Attackable attackable;
			final Iterator<Attackable> iterator = _attackables.iterator();
			while (iterator.hasNext())
			{
				attackable = iterator.next();
				if (attackable.hasAI())
				{
					ai = attackable.getAI();
					if (ai != null)
					{
						ai.onEvtThink();
					}
					else
					{
						iterator.remove();
					}
				}
				else
				{
					iterator.remove();
				}
			}
		}
	}
	
	public synchronized void add(Attackable attackable)
	{
		for (Set<Attackable> pool : POOLS)
		{
			if (pool.contains(attackable))
			{
				return;
			}
		}
		
		for (Set<Attackable> pool : POOLS)
		{
			if (pool.size() < POOL_SIZE)
			{
				pool.add(attackable);
				return;
			}
		}
		
		final Set<Attackable> pool = ConcurrentHashMap.newKeySet(POOL_SIZE);
		pool.add(attackable);
		ThreadPool.schedulePriorityTaskAtFixedRate(new AttackableThink(pool), TASK_DELAY, TASK_DELAY);
		POOLS.add(pool);
	}
	
	public void remove(Attackable attackable)
	{
		for (Set<Attackable> pool : POOLS)
		{
			if (pool.remove(attackable))
			{
				return;
			}
		}
	}
	
	public static AttackableThinkTaskManager getInstance()
	{
		return SingletonHolder.INSTANCE;
	}
	
	private static class SingletonHolder
	{
		protected static final AttackableThinkTaskManager INSTANCE = new AttackableThinkTaskManager();
	}
}
