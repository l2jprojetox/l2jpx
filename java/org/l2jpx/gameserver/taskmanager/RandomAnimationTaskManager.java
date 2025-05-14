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

import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import org.l2jpx.Config;
import org.l2jpx.commons.threads.ThreadPool;
import org.l2jpx.commons.util.Rnd;
import org.l2jpx.gameserver.model.actor.Npc;

/**
 * @author Mobius
 */
public class RandomAnimationTaskManager implements Runnable
{
	private static final Map<Npc, Long> PENDING_ANIMATIONS = new ConcurrentHashMap<>();
	private static boolean _working = false;
	
	protected RandomAnimationTaskManager()
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
		
		final long currentTime = System.currentTimeMillis();
		for (Entry<Npc, Long> entry : PENDING_ANIMATIONS.entrySet())
		{
			if (currentTime > entry.getValue().longValue())
			{
				final Npc npc = entry.getKey();
				if (npc.isInActiveRegion() && !npc.isDead() && !npc.isInCombat() && !npc.isMoving() && !npc.hasBlockActions())
				{
					npc.onRandomAnimation(Rnd.get(2, 3));
				}
				PENDING_ANIMATIONS.put(npc, currentTime + (Rnd.get((npc.isAttackable() ? Config.MIN_MONSTER_ANIMATION : Config.MIN_NPC_ANIMATION), (npc.isAttackable() ? Config.MAX_MONSTER_ANIMATION : Config.MAX_NPC_ANIMATION)) * 1000));
			}
		}
		
		_working = false;
	}
	
	public void add(Npc npc)
	{
		if (npc.hasRandomAnimation())
		{
			PENDING_ANIMATIONS.putIfAbsent(npc, System.currentTimeMillis() + (Rnd.get((npc.isAttackable() ? Config.MIN_MONSTER_ANIMATION : Config.MIN_NPC_ANIMATION), (npc.isAttackable() ? Config.MAX_MONSTER_ANIMATION : Config.MAX_NPC_ANIMATION)) * 1000));
		}
	}
	
	public void remove(Npc npc)
	{
		PENDING_ANIMATIONS.remove(npc);
	}
	
	public static RandomAnimationTaskManager getInstance()
	{
		return SingletonHolder.INSTANCE;
	}
	
	private static class SingletonHolder
	{
		protected static final RandomAnimationTaskManager INSTANCE = new RandomAnimationTaskManager();
	}
}
