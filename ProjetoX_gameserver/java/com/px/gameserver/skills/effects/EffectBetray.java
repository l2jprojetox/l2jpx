package com.px.gameserver.skills.effects;

import com.px.gameserver.enums.IntentionType;
import com.px.gameserver.enums.skills.L2EffectFlag;
import com.px.gameserver.enums.skills.L2EffectType;
import com.px.gameserver.model.L2Effect;
import com.px.gameserver.model.actor.Player;
import com.px.gameserver.model.actor.Summon;
import com.px.gameserver.skills.Env;

/**
 * @author decad
 */
final class EffectBetray extends L2Effect
{
	public EffectBetray(Env env, EffectTemplate template)
	{
		super(env, template);
	}
	
	@Override
	public L2EffectType getEffectType()
	{
		return L2EffectType.BETRAY;
	}
	
	/** Notify started */
	@Override
	public boolean onStart()
	{
		if (getEffector() instanceof Player && getEffected() instanceof Summon)
		{
			Player targetOwner = getEffected().getActingPlayer();
			getEffected().getAI().setIntention(IntentionType.ATTACK, targetOwner);
			return true;
		}
		return false;
	}
	
	/** Notify exited */
	@Override
	public void onExit()
	{
		getEffected().getAI().setIntention(IntentionType.IDLE);
	}
	
	@Override
	public boolean onActionTime()
	{
		return false;
	}
	
	@Override
	public int getEffectFlags()
	{
		return L2EffectFlag.BETRAYED.getMask();
	}
}