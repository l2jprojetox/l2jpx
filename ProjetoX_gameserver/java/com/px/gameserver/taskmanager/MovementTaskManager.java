package com.px.gameserver.taskmanager;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import com.px.commons.concurrent.ThreadPool;

import com.px.gameserver.enums.AiEventType;
import com.px.gameserver.model.actor.Creature;
import com.px.gameserver.model.actor.ai.type.CreatureAI;

/**
 * Updates position of moving {@link Creature} periodically.
 */
public final class MovementTaskManager implements Runnable
{
	// Update the position of all moving characters each MILLIS_PER_UPDATE.
	private static final int MILLIS_PER_UPDATE = 100;
	
	private final Set<Creature> _characters = ConcurrentHashMap.newKeySet();
	
	private long _ticks;
	
	protected MovementTaskManager()
	{
		// Run task each 100 ms.
		ThreadPool.scheduleAtFixedRate(this, MILLIS_PER_UPDATE, MILLIS_PER_UPDATE);
	}
	
	@Override
	public final void run()
	{
		_ticks++;
		
		// For all moving characters.
		for (Creature character : _characters)
		{
			// Update character position, final position isn't reached yet.
			if (!character.updatePosition())
				continue;
			
			// Destination reached, remove from map.
			_characters.remove(character);
			
			// Get character AI, if AI doesn't exist, skip.
			final CreatureAI ai = character.getAI();
			if (ai == null)
				continue;
			
			// Inform AI about arrival.
			ThreadPool.execute(() -> ai.notifyEvent(AiEventType.ARRIVED));
		}
	}
	
	/**
	 * Add a {@link Creature} to MovementTask in order to update its location every MILLIS_PER_UPDATE ms.
	 * @param cha The Creature to add to movingObjects of GameTimeController
	 */
	public final void add(final Creature cha)
	{
		_characters.add(cha);
	}
	
	/**
	 * @return the current number of ticks. Used as a monotonic clock wall with 100ms timelapse.
	 */
	public final long getTicks()
	{
		return _ticks;
	}
	
	public static final MovementTaskManager getInstance()
	{
		return SingletonHolder.INSTANCE;
	}
	
	private static class SingletonHolder
	{
		protected static final MovementTaskManager INSTANCE = new MovementTaskManager();
	}
}