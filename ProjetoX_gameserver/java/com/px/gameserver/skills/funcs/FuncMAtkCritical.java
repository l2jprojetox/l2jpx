package com.px.gameserver.skills.funcs;

import com.px.gameserver.enums.skills.Stats;
import com.px.gameserver.model.actor.Creature;
import com.px.gameserver.model.actor.Player;
import com.px.gameserver.skills.Env;
import com.px.gameserver.skills.Formulas;
import com.px.gameserver.skills.basefuncs.Func;

public class FuncMAtkCritical extends Func
{
	static final FuncMAtkCritical _fac_instance = new FuncMAtkCritical();
	
	public static Func getInstance()
	{
		return _fac_instance;
	}
	
	private FuncMAtkCritical()
	{
		super(Stats.MCRITICAL_RATE, 0x09, null, null);
	}
	
	@Override
	public void calc(Env env)
	{
		final Creature player = env.getCharacter();
		if (player instanceof Player)
		{
			if (player.getActiveWeaponInstance() != null)
				env.mulValue(Formulas.WIT_BONUS[player.getWIT()]);
		}
		else
			env.mulValue(Formulas.WIT_BONUS[player.getWIT()]);
		
		env.setBaseValue(env.getValue());
	}
}