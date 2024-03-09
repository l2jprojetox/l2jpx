package net.l2jpx.gameserver.skills.effects;

import net.l2jpx.gameserver.model.L2Effect;
import net.l2jpx.gameserver.skills.Env;

/**
 * @author ProGramMoS, L2JFrozen
 */
final class EffectBuff extends L2Effect
{
	
	public EffectBuff(final Env envbuff, final EffectTemplate template)
	{
		super(envbuff, template);
	}
	
	@Override
	public EffectType getEffectType()
	{
		return EffectType.BUFF;
	}
	
	@Override
	public boolean onActionTime()
	{
		// just stop this effect
		return false;
	}
}
