package com.px.gameserver.skills.basefuncs;

import com.px.gameserver.enums.skills.Stats;
import com.px.gameserver.skills.Env;

public class FuncDiv extends Func
{
	public FuncDiv(Stats pStat, int pOrder, Object owner, Lambda lambda)
	{
		super(pStat, pOrder, owner, lambda);
	}
	
	@Override
	public void calc(Env env)
	{
		if (cond == null || cond.test(env))
			env.divValue(_lambda.calc(env));
	}
}