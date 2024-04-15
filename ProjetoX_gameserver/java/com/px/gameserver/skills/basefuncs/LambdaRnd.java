package com.px.gameserver.skills.basefuncs;

import com.px.commons.random.Rnd;

import com.px.gameserver.skills.Env;

/**
 * @author mkizub
 */
public final class LambdaRnd extends Lambda
{
	private final Lambda _max;
	private final boolean _linear;
	
	public LambdaRnd(Lambda max, boolean linear)
	{
		_max = max;
		_linear = linear;
	}
	
	@Override
	public double calc(Env env)
	{
		return _max.calc(env) * ((_linear) ? Rnd.nextDouble() : Rnd.nextGaussian());
	}
}