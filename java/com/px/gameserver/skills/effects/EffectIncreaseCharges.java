package com.px.gameserver.skills.effects;

import com.px.gameserver.enums.skills.EffectType;
import com.px.gameserver.model.actor.Creature;
import com.px.gameserver.model.actor.Player;
import com.px.gameserver.skills.AbstractEffect;
import com.px.gameserver.skills.L2Skill;

public class EffectIncreaseCharges extends AbstractEffect
{
	public EffectIncreaseCharges(EffectTemplate template, L2Skill skill, Creature effected, Creature effector)
	{
		super(template, skill, effected, effector);
	}
	
	@Override
	public EffectType getEffectType()
	{
		return EffectType.INCREASE_CHARGES;
	}
	
	@Override
	public boolean onStart()
	{
		if (!(getEffected() instanceof Player))
			return false;
		
		((Player) getEffected()).increaseCharges((int) getTemplate().getValue(), getCount());
		return true;
	}
	
	@Override
	public boolean onActionTime()
	{
		return false; // abort effect even if count > 1
	}
}