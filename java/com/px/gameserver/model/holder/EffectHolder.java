package com.px.gameserver.model.holder;

import com.px.gameserver.skills.L2Skill;

/**
 * A class extending {@link IntIntHolder} containing all neccessary information to maintain valid effects duration.
 */
public final class EffectHolder extends IntIntHolder
{
	private final int _duration;
	
	public EffectHolder(L2Skill skill, int duration)
	{
		super(skill.getId(), skill.getLevel());
		
		_duration = duration;
	}
	
	public int getDuration()
	{
		return _duration;
	}
}