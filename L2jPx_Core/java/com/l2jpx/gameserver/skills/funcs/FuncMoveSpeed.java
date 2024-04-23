package com.l2jpx.gameserver.skills.funcs;

import com.l2jpx.gameserver.enums.skills.Stats;
import com.l2jpx.gameserver.model.actor.Creature;
import com.l2jpx.gameserver.skills.Formulas;
import com.l2jpx.gameserver.skills.L2Skill;
import com.l2jpx.gameserver.skills.basefuncs.Func;

/**
 * @see Func
 */
public class FuncMoveSpeed extends Func
{
	private static final FuncMoveSpeed INSTANCE = new FuncMoveSpeed();
	
	private FuncMoveSpeed()
	{
		super(null, Stats.RUN_SPEED, 10, 0, null);
	}
	
	@Override
	public double calc(Creature effector, Creature effected, L2Skill skill, double base, double value)
	{
		return value * Formulas.DEX_BONUS[effector.getStatus().getDEX()];
	}
	
	public static Func getInstance()
	{
		return INSTANCE;
	}
}