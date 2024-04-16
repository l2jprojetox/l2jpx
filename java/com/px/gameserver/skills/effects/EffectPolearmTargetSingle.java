package com.px.gameserver.skills.effects;

import com.px.gameserver.enums.skills.EffectType;
import com.px.gameserver.model.actor.Creature;
import com.px.gameserver.skills.AbstractEffect;
import com.px.gameserver.skills.L2Skill;

public class EffectPolearmTargetSingle extends AbstractEffect
{
	public EffectPolearmTargetSingle(EffectTemplate template, L2Skill skill, Creature effected, Creature effector)
	{
		super(template, skill, effected, effector);
	}
	
	@Override
	public EffectType getEffectType()
	{
		return EffectType.POLEARM_TARGET_SINGLE;
	}
	
	@Override
	public boolean onActionTime()
	{
		return false;
	}
}