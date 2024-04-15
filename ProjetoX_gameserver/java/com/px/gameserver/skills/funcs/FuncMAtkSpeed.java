package com.px.gameserver.skills.funcs;

import com.px.gameserver.enums.skills.Stats;
import com.px.gameserver.skills.Env;
import com.px.gameserver.skills.Formulas;
import com.px.gameserver.skills.basefuncs.Func;

public class FuncMAtkSpeed extends Func
{
	static final FuncMAtkSpeed _fas_instance = new FuncMAtkSpeed();
	
	public static Func getInstance()
	{
		return _fas_instance;
	}
	
	private FuncMAtkSpeed()
	{
		super(Stats.MAGIC_ATTACK_SPEED, 0x20, null, null);
	}
	
	@Override
	public void calc(Env env)
	{
		env.mulValue(Formulas.WIT_BONUS[env.getCharacter().getWIT()]);
	}
}