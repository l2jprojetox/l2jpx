package com.px.gameserver.handler.targethandlers;

import com.px.gameserver.enums.skills.SkillTargetType;
import com.px.gameserver.handler.ITargetHandler;
import com.px.gameserver.model.actor.Creature;
import com.px.gameserver.model.actor.Playable;
import com.px.gameserver.model.actor.Summon;
import com.px.gameserver.network.SystemMessageId;
import com.px.gameserver.skills.L2Skill;

public class TargetSummon implements ITargetHandler
{
	@Override
	public SkillTargetType getTargetType()
	{
		return SkillTargetType.SUMMON;
	}
	
	@Override
	public Creature[] getTargetList(Creature caster, Creature target, L2Skill skill)
	{
		final Summon summon = caster.getSummon();
		if (summon == null)
			return EMPTY_TARGET_ARRAY;
		
		return new Creature[]
		{
			summon
		};
	}
	
	@Override
	public Creature getFinalTarget(Creature caster, Creature target, L2Skill skill)
	{
		final Summon summon = caster.getSummon();
		if (summon == null)
			return null;
		
		return summon;
	}
	
	@Override
	public boolean meetCastConditions(Playable caster, Creature target, L2Skill skill, boolean isCtrlPressed)
	{
		final Summon summon = caster.getSummon();
		if (summon == null || summon.isDead())
		{
			caster.sendPacket(SystemMessageId.INVALID_TARGET);
			return false;
		}
		
		return true;
	}
}