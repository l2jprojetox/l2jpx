package com.px.gameserver.skills.effects;

import com.px.gameserver.enums.skills.EffectType;
import com.px.gameserver.model.actor.Creature;
import com.px.gameserver.model.actor.Player;
import com.px.gameserver.model.actor.ai.type.PlayerAI;
import com.px.gameserver.skills.AbstractEffect;
import com.px.gameserver.skills.L2Skill;

public class EffectTargetMe extends AbstractEffect
{
	public EffectTargetMe(EffectTemplate template, L2Skill skill, Creature effected, Creature effector)
	{
		super(template, skill, effected, effector);
	}
	
	@Override
	public EffectType getEffectType()
	{
		return EffectType.TARGET_ME;
	}
	
	@Override
	public boolean onStart()
	{
		if (getEffected() instanceof Player)
		{
			if (getEffected().getTarget() == getEffector())
				((PlayerAI) getEffected().getAI()).tryToAttack(getEffector());
			else
				getEffected().setTarget(getEffector());
			
			return true;
		}
		return false;
	}
	
	@Override
	public void onExit()
	{
	}
	
	@Override
	public boolean onActionTime()
	{
		return false;
	}
}