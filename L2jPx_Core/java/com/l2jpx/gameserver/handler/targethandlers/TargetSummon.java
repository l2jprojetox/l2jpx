package com.l2jpx.gameserver.handler.targethandlers;

import com.l2jpx.gameserver.enums.skills.SkillTargetType;
import com.l2jpx.gameserver.handler.ITargetHandler;
import com.l2jpx.gameserver.model.actor.Creature;
import com.l2jpx.gameserver.model.actor.Playable;
import com.l2jpx.gameserver.model.actor.Summon;
import com.l2jpx.gameserver.network.SystemMessageId;
import com.l2jpx.gameserver.skills.L2Skill;

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