package com.px.gameserver.skills.effects;

import com.px.gameserver.enums.skills.EffectType;
import com.px.gameserver.model.actor.Creature;
import com.px.gameserver.skills.AbstractEffect;
import com.px.gameserver.skills.L2Skill;

public class EffectImobileBuff extends AbstractEffect
{
	public EffectImobileBuff(EffectTemplate template, L2Skill skill, Creature effected, Creature effector)
	{
		super(template, skill, effected, effector);
	}
	
	@Override
	public EffectType getEffectType()
	{
		return EffectType.BUFF;
	}
	
	@Override
	public boolean onStart()
	{
		getEffector().setIsImmobilized(true);
		return true;
	}
	
	@Override
	public void onExit()
	{
		getEffector().setIsImmobilized(false);
	}
	
	@Override
	public boolean onActionTime()
	{
		return false;
	}
}