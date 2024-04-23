package com.l2jpx.gameserver.handler.targethandlers;

import com.l2jpx.gameserver.enums.ZoneId;
import com.l2jpx.gameserver.enums.skills.SkillTargetType;
import com.l2jpx.gameserver.handler.ITargetHandler;
import com.l2jpx.gameserver.model.actor.Creature;
import com.l2jpx.gameserver.model.actor.Playable;
import com.l2jpx.gameserver.model.actor.Summon;
import com.l2jpx.gameserver.network.SystemMessageId;
import com.l2jpx.gameserver.skills.L2Skill;

public class TargetEnemySummon implements ITargetHandler
{
	@Override
	public SkillTargetType getTargetType()
	{
		return SkillTargetType.ENEMY_SUMMON;
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
		if (!(target instanceof Summon) || target.isDead() || caster.getSummon() == target || !target.getActingPlayer().isAttackableWithoutForceBy(caster))
		{
			caster.sendPacket(SystemMessageId.INVALID_TARGET);
			return false;
		}
		
		if (caster.isInsideZone(ZoneId.PEACE))
		{
			caster.sendPacket(SystemMessageId.CANT_ATK_PEACEZONE);
			return false;
		}
		
		final Summon summon = (Summon) target;
		if (summon.isInsideZone(ZoneId.PEACE))
		{
			caster.sendPacket(SystemMessageId.TARGET_IN_PEACEZONE);
			return false;
		}
		
		return true;
	}
}