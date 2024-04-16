package com.px.gameserver.skills.effects;

import com.px.gameserver.enums.skills.EffectFlag;
import com.px.gameserver.enums.skills.EffectType;
import com.px.gameserver.model.actor.Creature;
import com.px.gameserver.model.actor.Playable;
import com.px.gameserver.model.actor.ai.type.PlayableAI;
import com.px.gameserver.skills.AbstractEffect;
import com.px.gameserver.skills.L2Skill;

public class EffectStun extends AbstractEffect
{
	public EffectStun(EffectTemplate template, L2Skill skill, Creature effected, Creature effector)
	{
		super(template, skill, effected, effector);
	}
	
	@Override
	public EffectType getEffectType()
	{
		return EffectType.STUN;
	}
	
	@Override
	public boolean onStart()
	{
		// Abort attack, cast and move.
		getEffected().abortAll(false);
		
		if (getEffected() instanceof Playable)
			((PlayableAI<?>) getEffected().getAI()).tryToIdle();
		
		// Refresh abnormal effects.
		getEffected().updateAbnormalEffect();
		
		return true;
	}
	
	@Override
	public void onExit()
	{
		// Refresh abnormal effects.
		getEffected().updateAbnormalEffect();
	}
	
	@Override
	public boolean onActionTime()
	{
		return false;
	}
	
	@Override
	public boolean onSameEffect(AbstractEffect effect)
	{
		return false;
	}
	
	@Override
	public int getEffectFlags()
	{
		return EffectFlag.STUNNED.getMask();
	}
}