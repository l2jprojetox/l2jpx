package net.l2jpx.gameserver.skills.effects;

import net.l2jpx.gameserver.model.L2Effect;
import net.l2jpx.gameserver.skills.Env;

public class EffectMeditation extends L2Effect
{
	
	public EffectMeditation(final Env env, final EffectTemplate template)
	{
		super(env, template);
	}
	
	@Override
	public EffectType getEffectType()
	{
		return L2Effect.EffectType.MEDITATION;
	}
	
	@Override
	public void onStart()
	{
		effected.block();
		effected.setMeditated(true);
	}
	
	@Override
	public void onExit()
	{
		effected.unblock();
		effected.setMeditated(false);
	}
	
	@Override
	public boolean onActionTime()
	{
		// stop effect
		return false;
	}
}
