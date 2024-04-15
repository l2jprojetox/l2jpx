package com.px.gameserver.skills.effects;

import com.px.gameserver.enums.skills.L2EffectType;
import com.px.gameserver.model.L2Effect;
import com.px.gameserver.model.actor.Player;
import com.px.gameserver.skills.Env;

/**
 * @author Kerberos
 */
public class EffectRecovery extends L2Effect
{
	public EffectRecovery(Env env, EffectTemplate template)
	{
		super(env, template);
	}
	
	@Override
	public L2EffectType getEffectType()
	{
		return L2EffectType.BUFF;
	}
	
	@Override
	public boolean onStart()
	{
		if (getEffected() instanceof Player)
		{
			((Player) getEffected()).reduceDeathPenaltyBuffLevel();
			return true;
		}
		return false;
	}
	
	@Override
	public void onExit()
	{
	}
	
	@Override
	public boolean onActionTime()
	{
		return false;
	}
}