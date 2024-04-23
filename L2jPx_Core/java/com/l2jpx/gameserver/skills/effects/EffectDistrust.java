package com.l2jpx.gameserver.skills.effects;

import java.util.List;

import com.l2jpx.commons.random.Rnd;

import com.l2jpx.gameserver.enums.skills.EffectType;
import com.l2jpx.gameserver.model.actor.Creature;
import com.l2jpx.gameserver.model.actor.instance.Chest;
import com.l2jpx.gameserver.model.actor.instance.Monster;
import com.l2jpx.gameserver.skills.AbstractEffect;
import com.l2jpx.gameserver.skills.L2Skill;

public class EffectDistrust extends AbstractEffect
{
	public EffectDistrust(EffectTemplate template, L2Skill skill, Creature effected, Creature effector)
	{
		super(template, skill, effected, effector);
	}
	
	@Override
	public EffectType getEffectType()
	{
		return EffectType.DISTRUST;
	}
	
	@Override
	public boolean onStart()
	{
		if (!(getEffected() instanceof Monster))
			return false;
		
		final List<Monster> targetList = getEffected().getKnownTypeInRadius(Monster.class, 600, a -> !(a instanceof Chest));
		if (targetList.isEmpty())
			return true;
		
		// Choosing randomly a new target
		final Monster target = Rnd.get(targetList);
		if (target == null)
			return true;
		
		// Add aggro to that target aswell. The aggro power is random.
		final int aggro = (5 + Rnd.get(5)) * getEffector().getStatus().getLevel();
		((Monster) getEffected()).getAggroList().addDamageHate(target, 0, aggro);
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