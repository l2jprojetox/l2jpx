package com.l2jpx.gameserver.skills.basefuncs;

import com.l2jpx.gameserver.enums.skills.Stats;
import com.l2jpx.gameserver.model.actor.Creature;
import com.l2jpx.gameserver.skills.L2Skill;
import com.l2jpx.gameserver.skills.conditions.Condition;

/**
 * @see Func
 */
public class FuncSubDiv extends Func
{
	public FuncSubDiv(Object owner, Stats stat, double value, Condition cond)
	{
		super(owner, stat, 40, value, cond);
	}
	
	@Override
	public double calc(Creature effector, Creature effected, L2Skill skill, double base, double value)
	{
		// Condition does not exist or it fails, no change.
		if (getCond() != null && !getCond().test(effector, effected, skill))
			return value;
		
		// Update value.
		return value / (1 - (getValue() / 100));
	}
}