package com.px.gameserver.skills.effects;

import com.px.gameserver.enums.skills.EffectFlag;
import com.px.gameserver.enums.skills.EffectType;
import com.px.gameserver.model.actor.Creature;
import com.px.gameserver.model.actor.Playable;
import com.px.gameserver.model.actor.ai.type.PlayableAI;
import com.px.gameserver.skills.AbstractEffect;
import com.px.gameserver.skills.L2Skill;

public class EffectStunSelf extends AbstractEffect
{
	public EffectStunSelf(EffectTemplate template, L2Skill skill, Creature effected, Creature effector)
	{
		super(template, skill, effected, effector);
	}
	
	@Override
	public EffectType getEffectType()
	{
		return EffectType.STUN_SELF;
	}
	
	@Override
	public boolean onStart()
	{
		if (getEffected() instanceof Playable)
			((PlayableAI<?>) getEffected().getAI()).tryToIdle();
		
		// Refresh abnormal effects.
		getEffector().updateAbnormalEffect();
		
		return true;
	}
	
	@Override
	public void onExit()
	{
		// Refresh abnormal effects.
		getEffector().updateAbnormalEffect();
	}
	
	@Override
	public boolean onActionTime()
	{
		return false;
	}
	
	@Override
	public boolean isSelfEffectType()
	{
		return true;
	}
	
	@Override
	public int getEffectFlags()
	{
		return EffectFlag.STUNNED.getMask();
	}
}