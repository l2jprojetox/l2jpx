package com.l2jpx.gameserver.skills.basefuncs;

import com.l2jpx.gameserver.enums.skills.Stats;
import com.l2jpx.gameserver.model.actor.Creature;
import com.l2jpx.gameserver.skills.L2Skill;
import com.l2jpx.gameserver.skills.conditions.Condition;

/**
 * @see Func
 */
public class FuncSet extends Func
{
	public FuncSet(Object owner, Stats stat, double value, Condition cond)
	{
		super(owner, stat, 0, value, cond);
	}
	
	@Override
	public double calc(Creature effector, Creature effected, L2Skill skill, double base, double value)
	{
		// Condition does not exist or it fails, no change.
		if (getCond() != null && !getCond().test(effector, effected, skill))
			return value;
		
		// Update value.
		return getValue();
	}
}