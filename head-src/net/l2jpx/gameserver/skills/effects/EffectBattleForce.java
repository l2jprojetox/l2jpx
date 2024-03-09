package net.l2jpx.gameserver.skills.effects;

import net.l2jpx.gameserver.skills.Env;

/**
 * @author kombat
 */
public final class EffectBattleForce extends EffectForce
{
	public EffectBattleForce(final Env env, final EffectTemplate template)
	{
		super(env, template);
	}
	
	@Override
	public EffectType getEffectType()
	{
		return EffectType.BATTLE_FORCE;
	}
	
	@Override
	public boolean onActionTime()
	{
		return false;
	}
}
