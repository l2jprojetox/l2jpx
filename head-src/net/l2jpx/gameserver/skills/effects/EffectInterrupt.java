package net.l2jpx.gameserver.skills.effects;

import net.l2jpx.gameserver.model.L2Effect;
import net.l2jpx.gameserver.skills.Env;

/**
 * @author KidZor
 */

public class EffectInterrupt extends L2Effect
{
	public EffectInterrupt(final Env env, final EffectTemplate template)
	{
		super(env, template);
	}
	
	@Override
	public EffectType getEffectType()
	{
		return L2Effect.EffectType.INTERRUPT;
	}
	
	@Override
	public void onStart()
	{
		getEffected().abortCast();
	}
	
	@Override
	public void onExit()
	{
		// nothing
	}
	
	@Override
	public boolean onActionTime()
	{
		// nothing
		return false;
	}
}
