package com.l2jpx.gameserver.skills.funcs;

import com.l2jpx.gameserver.enums.skills.Stats;
import com.l2jpx.gameserver.model.actor.Creature;
import com.l2jpx.gameserver.skills.Formulas;
import com.l2jpx.gameserver.skills.L2Skill;
import com.l2jpx.gameserver.skills.basefuncs.Func;

/**
 * @see Func
 */
public class FuncMAtkSpeed extends Func
{
	private static final FuncMAtkSpeed INSTANCE = new FuncMAtkSpeed();
	
	private FuncMAtkSpeed()
	{
		super(null, Stats.MAGIC_ATTACK_SPEED, 10, 0, null);
	}
	
	@Override
	public double calc(Creature effector, Creature effected, L2Skill skill, double base, double value)
	{
		return value * Formulas.WIT_BONUS[effector.getStatus().getWIT()];
	}
	
	public static Func getInstance()
	{
		return INSTANCE;
	}
}