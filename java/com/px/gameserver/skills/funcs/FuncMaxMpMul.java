package com.px.gameserver.skills.funcs;

import com.px.gameserver.enums.skills.Stats;
import com.px.gameserver.model.actor.Creature;
import com.px.gameserver.skills.Formulas;
import com.px.gameserver.skills.L2Skill;
import com.px.gameserver.skills.basefuncs.Func;

/**
 * @see Func
 */
public class FuncMaxMpMul extends Func
{
	private static final FuncMaxMpMul INSTANCE = new FuncMaxMpMul();
	
	private FuncMaxMpMul()
	{
		super(null, Stats.MAX_MP, 10, 0, null);
	}
	
	@Override
	public double calc(Creature effector, Creature effected, L2Skill skill, double base, double value)
	{
		return value * Formulas.MEN_BONUS[effector.getStatus().getMEN()];
	}
	
	public static Func getInstance()
	{
		return INSTANCE;
	}
}