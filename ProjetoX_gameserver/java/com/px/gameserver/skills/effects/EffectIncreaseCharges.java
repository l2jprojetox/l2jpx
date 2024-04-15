package com.px.gameserver.skills.effects;

import com.px.gameserver.enums.skills.L2EffectType;
import com.px.gameserver.model.L2Effect;
import com.px.gameserver.model.actor.Player;
import com.px.gameserver.skills.Env;

/**
 * Effect will generate charges for Player targets Number of charges in "value", maximum number in "count" effect variables
 * @author DS
 */
public class EffectIncreaseCharges extends L2Effect
{
	public EffectIncreaseCharges(Env env, EffectTemplate template)
	{
		super(env, template);
	}
	
	@Override
	public L2EffectType getEffectType()
	{
		return L2EffectType.INCREASE_CHARGES;
	}
	
	@Override
	public boolean onStart()
	{
		if (getEffected() == null)
			return false;
		
		if (!(getEffected() instanceof Player))
			return false;
		
		((Player) getEffected()).increaseCharges((int) calc(), getCount());
		return true;
	}
	
	@Override
	public boolean onActionTime()
	{
		return false; // abort effect even if count > 1
	}
}