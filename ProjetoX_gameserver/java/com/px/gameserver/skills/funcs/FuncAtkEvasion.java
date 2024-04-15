package com.px.gameserver.skills.funcs;

import com.px.gameserver.enums.skills.Stats;
import com.px.gameserver.skills.Env;
import com.px.gameserver.skills.Formulas;
import com.px.gameserver.skills.basefuncs.Func;

public class FuncAtkEvasion extends Func
{
	static final FuncAtkEvasion _fae_instance = new FuncAtkEvasion();
	
	public static Func getInstance()
	{
		return _fae_instance;
	}
	
	private FuncAtkEvasion()
	{
		super(Stats.EVASION_RATE, 0x10, null, null);
	}
	
	@Override
	public void calc(Env env)
	{
		env.addValue(Formulas.BASE_EVASION_ACCURACY[env.getCharacter().getDEX()] + env.getCharacter().getLevel());
	}
}