package com.px.gameserver.handler.targethandlers;

import com.px.gameserver.enums.skills.SkillTargetType;
import com.px.gameserver.handler.ITargetHandler;
import com.px.gameserver.model.actor.Creature;
import com.px.gameserver.model.actor.Playable;
import com.px.gameserver.model.actor.Summon;
import com.px.gameserver.network.SystemMessageId;
import com.px.gameserver.network.serverpackets.SystemMessage;
import com.px.gameserver.skills.L2Skill;

public class TargetOwnerPet implements ITargetHandler
{
	@Override
	public SkillTargetType getTargetType()
	{
		return SkillTargetType.OWNER_PET;
	}
	
	@Override
	public Creature[] getTargetList(Creature caster, Creature target, L2Skill skill)
	{
		if (!(caster instanceof Summon))
			return EMPTY_TARGET_ARRAY;
		
		return new Creature[]
		{
			caster.getActingPlayer()
		};
	}
	
	@Override
	public Creature getFinalTarget(Creature caster, Creature target, L2Skill skill)
	{
		if (!(caster instanceof Summon))
			return null;
		
		return caster.getActingPlayer();
	}
	
	@Override
	public boolean meetCastConditions(Playable caster, Creature target, L2Skill skill, boolean isCtrlPressed)
	{
		if (target == null || caster.getActingPlayer() != target)
		{
			caster.sendPacket(SystemMessageId.INVALID_TARGET);
			return false;
		}
		
		if (target.isDead())
		{
			caster.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.S1_CANNOT_BE_USED).addSkillName(skill));
			return false;
		}
		return true;
	}
}