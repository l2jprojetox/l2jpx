package com.l2jpx.gameserver.handler.targethandlers;

import com.l2jpx.gameserver.enums.skills.SkillTargetType;
import com.l2jpx.gameserver.handler.ITargetHandler;
import com.l2jpx.gameserver.model.actor.Creature;
import com.l2jpx.gameserver.model.actor.Playable;
import com.l2jpx.gameserver.model.actor.instance.Chest;
import com.l2jpx.gameserver.model.actor.instance.Door;
import com.l2jpx.gameserver.network.SystemMessageId;
import com.l2jpx.gameserver.skills.L2Skill;

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