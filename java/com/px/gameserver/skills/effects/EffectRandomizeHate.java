package com.px.gameserver.skills.effects;

import com.px.gameserver.enums.skills.EffectType;
import com.px.gameserver.model.actor.Attackable;
import com.px.gameserver.model.actor.Creature;
import com.px.gameserver.skills.AbstractEffect;
import com.px.gameserver.skills.L2Skill;

public class EffectRandomizeHate extends AbstractEffect
{
	public EffectRandomizeHate(EffectTemplate template, L2Skill skill, Creature effected, Creature effector)
	{
		super(template, skill, effected, effector);
	}
	
	@Override
	public EffectType getEffectType()
	{
		return EffectType.RANDOMIZE_HATE;
	}
	
	@Override
	public boolean onStart()
	{
		if (!(getEffected() instanceof Attackable))
			return false;
			
		// if (getEffected().isUnresponsive()) TODO
		// return false;
		
		((Attackable) getEffected()).getAI().getAggroList().randomizeAttack();
		
		return true;
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