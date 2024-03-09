package net.l2jpx.gameserver.skills.effects;

import net.l2jpx.gameserver.model.L2Effect;
import net.l2jpx.gameserver.skills.Env;

/**
 * @author -Nemesiss-
 */
public class EffectPsychicalMute extends L2Effect
{
	
	public EffectPsychicalMute(final Env env, final EffectTemplate template)
	{
		super(env, template);
	}
	
	@Override
	public EffectType getEffectType()
	{
		return L2Effect.EffectType.PSYCHICAL_MUTE;
	}
	
	@Override
	public void onStart()
	{
		getEffected().startPsychicalMuted();
	}
	
	@Override
	public boolean onActionTime()
	{
		// Simply stop the effect
		getEffected().stopPsychicalMuted(this);
		return false;
	}
	
	@Override
	public void onExit()
	{
		getEffected().stopPsychicalMuted(this);
	}
}
