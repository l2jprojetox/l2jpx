package com.px.gameserver.skills.effects;

import com.px.gameserver.enums.skills.EffectType;
import com.px.gameserver.enums.skills.Stats;
import com.px.gameserver.model.actor.Creature;
import com.px.gameserver.model.actor.Player;
import com.px.gameserver.network.SystemMessageId;
import com.px.gameserver.network.serverpackets.SystemMessage;
import com.px.gameserver.skills.AbstractEffect;
import com.px.gameserver.skills.L2Skill;

public class EffectHeal extends AbstractEffect
{
	public EffectHeal(EffectTemplate template, L2Skill skill, Creature effected, Creature effector)
	{
		super(template, skill, effected, effector);
	}
	
	@Override
	public EffectType getEffectType()
	{
		return EffectType.HEAL;
	}
	
	@Override
	public boolean onStart()
	{
		if (!getEffected().canBeHealed())
			return false;
		
		final double power = getTemplate().getValue() + getEffected().getStatus().calcStat(Stats.HEAL_PROFICIENCY, 0, null, null);
		final double amount = getEffected().getStatus().addHp(power * getEffected().getStatus().calcStat(Stats.HEAL_EFFECTIVNESS, 100, null, null) / 100.);
		
		getEffected().getStatus().addHp(amount);
		
		if (getEffected() instanceof Player)
		{
			if (getEffector() != getEffected())
				getEffected().sendPacket(SystemMessage.getSystemMessage(SystemMessageId.S2_HP_RESTORED_BY_S1).addCharName(getEffector()).addNumber((int) amount));
			else
				getEffected().sendPacket(SystemMessage.getSystemMessage(SystemMessageId.S1_HP_RESTORED).addNumber((int) amount));
		}
		return true;
	}
	
	@Override
	public boolean onActionTime()
	{
		return false;
	}
}