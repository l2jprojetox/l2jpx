package com.px.gameserver.skills.effects;

import com.px.gameserver.enums.skills.EffectType;
import com.px.gameserver.model.actor.Creature;
import com.px.gameserver.model.actor.Player;
import com.px.gameserver.network.serverpackets.ExRegenMax;
import com.px.gameserver.skills.AbstractEffect;
import com.px.gameserver.skills.L2Skill;

public class EffectHealOverTime extends AbstractEffect
{
	public EffectHealOverTime(EffectTemplate template, L2Skill skill, Creature effected, Creature effector)
	{
		super(template, skill, effected, effector);
	}
	
	@Override
	public EffectType getEffectType()
	{
		return EffectType.HEAL_OVER_TIME;
	}
	
	@Override
	public boolean onStart()
	{
		// If effected is a player, send a hp regen effect packet.
		if (getEffected() instanceof Player && getTemplate().getCounter() > 0 && getPeriod() > 0)
			getEffected().sendPacket(new ExRegenMax(getTemplate().getCounter() * getPeriod(), getPeriod(), getTemplate().getValue()));
		
		return true;
	}
	
	@Override
	public boolean onActionTime()
	{
		if (!getEffected().canBeHealed())
			return false;
		
		getEffected().getStatus().addHp(getTemplate().getValue());
		return true;
	}
}