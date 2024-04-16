package com.px.gameserver.model.actor.ai;

import java.util.concurrent.ConcurrentSkipListSet;

import com.px.gameserver.enums.IntentionType;

/**
 * A {@link ConcurrentSkipListSet} of {@link Desire}s, which is used to determine what kind of {@link Intention} the owner should do.<br>
 * <br>
 * If a {@link Desire} already exists, only the weight will be added to the existing one.
 */
public class DesireQueue extends ConcurrentSkipListSet<Desire>
{
	private static final long serialVersionUID = 1L;
	
	private static final int MAX_CAPACITY = 50;
	
	public DesireQueue()
	{
		super();
	}
	
	/**
	 * Add a {@link Desire}. If already existing, add the weight to the existing one.
	 * @param desire : The {@link Desire} to add.
	 */
	public void addOrUpdate(Desire desire)
	{
		final int size = size();
		if (size == 0)
			add(desire);
		else
		{
			final Desire existingDesire = stream().filter(d -> d.equals(desire)).findFirst().orElse(null);
			if (existingDesire != null)
				existingDesire.addWeight(desire.getWeight());
			else if (size < MAX_CAPACITY)
				add(desire);
		}
	}
	
	/**
	 * Decrease all {@link Desire}s weight linked to a {@link IntentionType} by a given amount, based on parameters.
	 * @param intentionType : The {@link IntentionType} of {@link Desire}s to affect.
	 * @param amount : The amount of weight to reduce by.
	 */
	public void decreaseWeightByType(IntentionType intentionType, double amount)
	{
		if (isEmpty())
			return;
		
		stream().filter(d -> d.getType() == intentionType).forEach(d ->
		{
			if (d.getWeight() - amount < 0)
				remove(d);
			else
				d.reduceWeight(amount);
		});
	}
}