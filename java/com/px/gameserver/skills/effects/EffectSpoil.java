package com.px.gameserver.skills.effects;

import com.px.gameserver.enums.skills.EffectType;
import com.px.gameserver.model.actor.Creature;
import com.px.gameserver.model.actor.Player;
import com.px.gameserver.model.actor.instance.Monster;
import com.px.gameserver.network.SystemMessageId;
import com.px.gameserver.network.serverpackets.SystemMessage;
import com.px.gameserver.skills.AbstractEffect;
import com.px.gameserver.skills.Formulas;
import com.px.gameserver.skills.L2Skill;

public class EffectSpoil extends AbstractEffect
{
	public EffectSpoil(EffectTemplate template, L2Skill skill, Creature effected, Creature effector)
	{
		super(template, skill, effected, effector);
	}
	
	@Override
	public EffectType getEffectType()
	{
		return EffectType.SPOIL;
	}
	
	@Override
	public boolean onStart()
	{
		if (!(getEffector() instanceof Player))
			return false;
		
		if (!(getEffected() instanceof Monster))
			return false;
		
		final Monster target = (Monster) getEffected();
		if (target.isDead())
			return false;
		
		if (target.getSpoilState().isSpoiled())
		{
			getEffector().sendPacket(SystemMessage.getSystemMessage(SystemMessageId.ALREADY_SPOILED));
			return false;
		}
		
		if (Formulas.calcMagicSuccess(getEffector(), target, getSkill()))
		{
			target.getSpoilState().setSpoilerId(getEffector().getObjectId());
			getEffector().sendPacket(SystemMessage.getSystemMessage(SystemMessageId.SPOIL_SUCCESS));
		}
		
		return true;
	}
	
	@Override
	public boolean onActionTime()
	{
		return false;
	}
}