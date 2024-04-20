package com.l2jpx.gameserver.skills.funcs;

import com.l2jpx.gameserver.enums.skills.Stats;
import com.l2jpx.gameserver.model.actor.Creature;
import com.l2jpx.gameserver.skills.Formulas;
import com.l2jpx.gameserver.skills.L2Skill;
import com.l2jpx.gameserver.skills.basefuncs.Func;

/**
 * @see Func
 */
public class FuncMaxHpMul extends Func
{
	private static final FuncMaxHpMul INSTANCE = new FuncMaxHpMul();
	
	private FuncMaxHpMul()
	{
		super(null, Stats.MAX_HP, 10, 0, null);
	}
	
	@Override
	public double calc(Creature effector, Creature effected, L2Skill skill, double base, double value)
	{
		return value * Formulas.CON_BONUS[effector.getStatus().getCON()];
	}
	
	public static Func getInstance()
	{
		return INSTANCE;
	}
}