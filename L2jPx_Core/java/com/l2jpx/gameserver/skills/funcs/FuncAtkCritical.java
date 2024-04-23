package com.l2jpx.gameserver.skills.funcs;

import com.l2jpx.gameserver.enums.skills.Stats;
import com.l2jpx.gameserver.model.actor.Creature;
import com.l2jpx.gameserver.model.actor.Summon;
import com.l2jpx.gameserver.skills.Formulas;
import com.l2jpx.gameserver.skills.L2Skill;
import com.l2jpx.gameserver.skills.basefuncs.Func;

/**
 * @see Func
 */
public class FuncAtkCritical extends Func
{
	private static final FuncAtkCritical INSTANCE = new FuncAtkCritical();
	
	private FuncAtkCritical()
	{
		super(null, Stats.CRITICAL_RATE, 10, 0, null);
	}
	
	@Override
	public double calc(Creature effector, Creature effected, L2Skill skill, double base, double value)
	{
		if (!(effector instanceof Summon))
			value *= Formulas.DEX_BONUS[effector.getStatus().getDEX()];
		
		return value * 10;
	}
	
	public static Func getInstance()
	{
		return INSTANCE;
	}
}