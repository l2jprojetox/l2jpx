package com.px.gameserver.skills.effects;

import com.px.gameserver.enums.skills.EffectType;
import com.px.gameserver.model.actor.Creature;
import com.px.gameserver.model.actor.Player;
import com.px.gameserver.model.actor.Summon;
import com.px.gameserver.skills.AbstractEffect;
import com.px.gameserver.skills.L2Skill;

public class EffectImobilePetBuff extends AbstractEffect
{
	public EffectImobilePetBuff(EffectTemplate template, L2Skill skill, Creature effected, Creature effector)
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
		if (getEffected() instanceof Summon && getEffector() instanceof Player && ((Summon) getEffected()).getOwner() == getEffector())
		{
			getEffected().setIsImmobilized(true);
			return true;
		}
		return false;
	}
	
	@Override
	public void onExit()
	{
		getEffected().setIsImmobilized(false);
	}
	
	@Override
	public boolean onActionTime()
	{
		return false;
	}
}