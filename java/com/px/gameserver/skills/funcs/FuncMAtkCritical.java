package com.px.gameserver.skills.funcs;

import com.px.gameserver.enums.skills.Stats;
import com.px.gameserver.model.actor.Creature;
import com.px.gameserver.model.actor.Player;
import com.px.gameserver.skills.Formulas;
import com.px.gameserver.skills.L2Skill;
import com.px.gameserver.skills.basefuncs.Func;

/**
 * @see Func
 */
public class FuncMAtkCritical extends Func
{
	private static final FuncMAtkCritical INSTANCE = new FuncMAtkCritical();
	
	private FuncMAtkCritical()
	{
		super(null, Stats.MCRITICAL_RATE, 10, 0, null);
	}
	
	@Override
	public double calc(Creature effector, Creature effected, L2Skill skill, double base, double value)
	{
		if (!(effector instanceof Player) || (effector.getActiveWeaponInstance() != null))
			return value * Formulas.WIT_BONUS[effector.getStatus().getWIT()];
		
		return value;
	}
	
	public static Func getInstance()
	{
		return INSTANCE;
	}
}