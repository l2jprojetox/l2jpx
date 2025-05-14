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
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.l2jpx.commons.threads.ThreadPool;
import org.l2jpx.gameserver.enums.ScheduledAttackType;
import org.l2jpx.gameserver.model.actor.Creature;
import org.l2jpx.gameserver.model.item.Weapon;
import org.l2jpx.gameserver.network.serverpackets.Attack;

/**
 * Creature Attack task manager class.
 * @author Mobius
 */
public class CreatureAttackTaskManager
{
	private static final Set<Map<Creature, ScheduledAttack>> ATTACK_POOLS = ConcurrentHashMap.newKeySet();
	private static final Set<Map<Creature, ScheduledFinish>> FINISH_POOLS = ConcurrentHashMap.newKeySet();
	private static final int POOL_SIZE = 300;
	private static final int TASK_DELAY = 10;
	
	protected CreatureAttackTaskManager()
	{
	}
	
	private class ScheduleAttackTask implements Runnable
	{
		private final Map<Creature, ScheduledAttack> _creatureAttackData;
		
		public ScheduleAttackTask(Map<Creature, ScheduledAttack> creatureattackData)
		{
			_creatureAttackData = creatureattackData;
		}
		
		@Override
		public void run()
		{
			if (_creatureAttackData.isEmpty())
			{
				return;
			}
			
			final long currentTime = System.currentTimeMillis();
			final Iterator<Entry<Creature, ScheduledAttack>> iterator = _creatureAttackData.entrySet().iterator();
			Entry<Creature, ScheduledAttack> entry;
			ScheduledAttack scheduledAttack;
			
			while (iterator.hasNext())
			{
				entry = iterator.next();
				scheduledAttack = entry.getValue();
				
				if (currentTime >= scheduledAttack.endTime)
				{
					iterator.remove();
					final Creature creature = entry.getKey();
					TYPE_SELECT: switch (scheduledAttack.type)
					{
						case NORMAL:
						{
							creature.onHitTimeNotDual(scheduledAttack.weapon, scheduledAttack.attack, scheduledAttack.hitTime, scheduledAttack.attackTime);
							break TYPE_SELECT;
						}
						case DUAL_FIRST:
						{
							creature.onFirstHitTimeForDual(scheduledAttack.weapon, scheduledAttack.attack, scheduledAttack.hitTime, scheduledAttack.attackTime, scheduledAttack.delayForSecondAttack);
							break TYPE_SELECT;
						}
						case DUAL_SECOND:
						{
							creature.onSecondHitTimeForDual(scheduledAttack.weapon, scheduledAttack.attack, scheduledAttack.hitTime, scheduledAttack.delayForSecondAttack, scheduledAttack.attackTime);
							break TYPE_SELECT;
						}
					}
				}
			}
		}
	}
	
	private class ScheduleAbortTask implements Runnable
	{
		private final Map<Creature, ScheduledFinish> _creatureFinishData;
		
		public ScheduleAbortTask(Map<Creature, ScheduledFinish> creatureFinishData)
		{
			_creatureFinishData = creatureFinishData;
		}
		
		@Override
		public void run()
		{
			if (_creatureFinishData.isEmpty())
			{
				return;
			}
			
			final long currentTime = System.currentTimeMillis();
			final Iterator<Entry<Creature, ScheduledFinish>> iterator = _creatureFinishData.entrySet().iterator();
			Entry<Creature, ScheduledFinish> entry;
			ScheduledFinish scheduledFinish;
			
			while (iterator.hasNext())
			{
				entry = iterator.next();
				scheduledFinish = entry.getValue();
				
				if (currentTime >= scheduledFinish.endTime)
				{
					iterator.remove();
					final Creature creature = entry.getKey();
					creature.onAttackFinish(scheduledFinish.attack);
				}
			}
		}
	}
	
	public void onHitTimeNotDual(Creature creature, Weapon weapon, Attack attack, int hitTime, int attackTime)
	{
		scheduleAttack(ScheduledAttackType.NORMAL, creature, weapon, attack, hitTime, attackTime, 0, hitTime);
	}
	
	public void onFirstHitTimeForDual(Creature creature, Weapon weapon, Attack attack, int hitTime, int attackTime, int delayForSecondAttack)
	{
		scheduleAttack(ScheduledAttackType.DUAL_FIRST, creature, weapon, attack, hitTime, attackTime, delayForSecondAttack, hitTime);
	}
	
	public void onSecondHitTimeForDual(Creature creature, Weapon weapon, Attack attack, int hitTime, int attackTime, int delayForSecondAttack)
	{
		scheduleAttack(ScheduledAttackType.DUAL_SECOND, creature, weapon, attack, hitTime, attackTime, delayForSecondAttack, delayForSecondAttack);
	}
	
	private void scheduleAttack(ScheduledAttackType type, Creature creature, Weapon weapon, Attack attack, int hitTime, int attackTime, int delayForSecondAttack, int taskDelay)
	{
		final ScheduledAttack scheduledAttack = new ScheduledAttack(type, weapon, attack, hitTime, attackTime, delayForSecondAttack, taskDelay + System.currentTimeMillis());
		
		for (Map<Creature, ScheduledAttack> pool : ATTACK_POOLS)
		{
			if (pool.size() < POOL_SIZE)
			{
				pool.put(creature, scheduledAttack);
				return;
			}
		}
		
		final Map<Creature, ScheduledAttack> pool = new ConcurrentHashMap<>();
		pool.put(creature, scheduledAttack);
		ThreadPool.schedulePriorityTaskAtFixedRate(new ScheduleAttackTask(pool), TASK_DELAY, TASK_DELAY);
		ATTACK_POOLS.add(pool);
	}
	
	public void onAttackFinish(Creature creature, Attack attack, int taskDelay)
	{
		final ScheduledFinish scheduledFinish = new ScheduledFinish(attack, taskDelay + System.currentTimeMillis());
		
		for (Map<Creature, ScheduledFinish> pool : FINISH_POOLS)
		{
			if (pool.size() < POOL_SIZE)
			{
				pool.put(creature, scheduledFinish);
				return;
			}
		}
		
		final Map<Creature, ScheduledFinish> pool = new ConcurrentHashMap<>();
		pool.put(creature, scheduledFinish);
		ThreadPool.schedulePriorityTaskAtFixedRate(new ScheduleAbortTask(pool), TASK_DELAY, TASK_DELAY);
		FINISH_POOLS.add(pool);
	}
	
	public void abortAttack(Creature creature)
	{
		for (Map<Creature, ScheduledAttack> pool : ATTACK_POOLS)
		{
			if (pool.remove(creature) != null)
			{
				break;
			}
		}
		
		for (Map<Creature, ScheduledFinish> pool : FINISH_POOLS)
		{
			if (pool.remove(creature) != null)
			{
				return;
			}
		}
	}
	
	private class ScheduledAttack
	{
		public final ScheduledAttackType type;
		public final Weapon weapon;
		public final Attack attack;
		public final int hitTime;
		public final int attackTime;
		public final int delayForSecondAttack;
		public final long endTime;
		
		public ScheduledAttack(ScheduledAttackType type, Weapon weapon, Attack attack, int hitTime, int attackTime, int delayForSecondAttack, long endTime)
		{
			this.type = type;
			this.weapon = weapon;
			this.attack = attack;
			this.hitTime = hitTime;
			this.attackTime = attackTime;
			this.delayForSecondAttack = delayForSecondAttack;
			this.endTime = endTime;
		}
	}
	
	private class ScheduledFinish
	{
		public final Attack attack;
		public final long endTime;
		
		public ScheduledFinish(Attack attack, long endTime)
		{
			this.attack = attack;
			this.endTime = endTime;
		}
	}
	
	public static final CreatureAttackTaskManager getInstance()
	{
		return SingletonHolder.INSTANCE;
	}
	
	private static class SingletonHolder
	{
		protected static final CreatureAttackTaskManager INSTANCE = new CreatureAttackTaskManager();
	}
}
