package com.px.gameserver.handler.targethandlers;

import com.px.gameserver.enums.skills.SkillTargetType;
import com.px.gameserver.handler.ITargetHandler;
import com.px.gameserver.model.actor.Creature;
import com.px.gameserver.model.actor.Playable;
import com.px.gameserver.model.actor.instance.Chest;
import com.px.gameserver.model.actor.instance.Door;
import com.px.gameserver.network.SystemMessageId;
import com.px.gameserver.skills.L2Skill;

public class TargetUnlockable implements ITargetHandler
{
	@Override
	public SkillTargetType getTargetType()
	{
		return SkillTargetType.UNLOCKABLE;
	}
	
	@Override
	public Creature[] getTargetList(Creature caster, Creature target, L2Skill skill)
	{
		return new Creature[]
		{
			target
		};
	}
	
	@Override
	public Creature getFinalTarget(Creature caster, Creature target, L2Skill skill)
	{
		return target;
	}
	
	@Override
	public boolean meetCastConditions(Playable caster, Creature target, L2Skill skill, boolean isCtrlPressed)
	{
		if (!(target instanceof Door) && !(target instanceof Chest))
		{
			caster.sendPacket(SystemMessageId.INVALID_TARGET);
			return false;
		}
		
		if (target instanceof Door && !((Door) target).isUnlockable())
			return false;
		
		return true;
	}
}