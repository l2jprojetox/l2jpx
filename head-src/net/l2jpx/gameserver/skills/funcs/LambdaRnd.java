package net.l2jpx.gameserver.skills.funcs;

import net.l2jpx.gameserver.skills.Env;
import net.l2jpx.util.random.Rnd;

/**
 * @author mkizub
 */
public final class LambdaRnd extends Lambda
{
	private final Lambda max;
	private final boolean linear;
	
	public LambdaRnd(final Lambda max, final boolean linear)
	{
		this.max = max;
		this.linear = linear;
	}
	
	@Override
	public double calc(final Env env)
	{
		if (linear)
		{
			return max.calc(env) * Rnd.nextDouble();
		}
		return max.calc(env) * Rnd.nextGaussian();
	}
	
}
