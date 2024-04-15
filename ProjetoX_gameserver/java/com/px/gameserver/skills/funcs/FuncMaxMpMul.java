package com.px.gameserver.skills.funcs;

import com.px.gameserver.enums.skills.Stats;
import com.px.gameserver.skills.Env;
import com.px.gameserver.skills.Formulas;
import com.px.gameserver.skills.basefuncs.Func;

public class FuncMaxMpMul extends Func
{
	static final FuncMaxMpMul _fmmm_instance = new FuncMaxMpMul();
	
	public static Func getInstance()
	{
		return _fmmm_instance;
	}
	
	private FuncMaxMpMul()
	{
		super(Stats.MAX_MP, 0x20, null, null);
	}
	
	@Override
	public void calc(Env env)
	{
		env.mulValue(Formulas.MEN_BONUS[env.getCharacter().getMEN()]);
	}
}