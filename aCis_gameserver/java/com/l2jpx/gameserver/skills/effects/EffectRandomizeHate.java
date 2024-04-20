package com.l2jpx.gameserver.skills.effects;

import com.l2jpx.gameserver.enums.skills.EffectType;
import com.l2jpx.gameserver.model.actor.Attackable;
import com.l2jpx.gameserver.model.actor.Creature;
import com.l2jpx.gameserver.skills.AbstractEffect;
import com.l2jpx.gameserver.skills.L2Skill;

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
		
		((Attackable) getEffected()).getAggroList().randomizeAttack();
		
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