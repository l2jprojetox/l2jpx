package net.l2jpx.gameserver.skills.effects;

import net.l2jpx.gameserver.model.L2Effect;
import net.l2jpx.gameserver.skills.Env;

/**
 * @author mkizub
 */
final class EffectRoot extends L2Effect
{
	
	public EffectRoot(final Env env, final EffectTemplate template)
	{
		super(env, template);
	}
	
	@Override
	public EffectType getEffectType()
	{
		return EffectType.ROOT;
	}
	
	/** Notify started */
	@Override
	public void onStart()
	{
		getEffected().startRooted();
	}
	
	/** Notify exited */
	@Override
	public void onExit()
	{
		getEffected().stopRooting(this);
	}
	
	@Override
	public boolean onActionTime()
	{
		// just stop this effect
		return false;
	}
}
