package com.px.gameserver.handler.targethandlers;

import java.util.ArrayList;
import java.util.List;

import com.px.gameserver.enums.ZoneId;
import com.px.gameserver.enums.skills.SkillTargetType;
import com.px.gameserver.geoengine.GeoEngine;
import com.px.gameserver.model.actor.Attackable;
import com.px.gameserver.model.actor.Creature;
import com.px.gameserver.model.actor.Playable;
import com.px.gameserver.network.SystemMessageId;
import com.px.gameserver.skills.L2Skill;

public class TargetFrontAura extends TargetAura
{
	@Override
	public SkillTargetType getTargetType()
	{
		return SkillTargetType.FRONT_AURA;
	}
	
	@Override
	public Creature[] getTargetList(Creature caster, Creature target, L2Skill skill)
	{
		final List<Creature> list = new ArrayList<>();
		for (Creature creature : caster.getKnownTypeInRadius(Creature.class, skill.getSkillRadius()))
		{
			if (creature.isDead() || !creature.isInFrontOf(caster) || !GeoEngine.getInstance().canSeeTarget(caster, creature))
				continue;
			
			if (caster instanceof Playable && (creature instanceof Attackable || creature instanceof Playable))
			{
				if (creature.isAttackableWithoutForceBy((Playable) caster))
					list.add(creature);
			}
			else if (caster instanceof Attackable && creature instanceof Playable)
			{
				if (creature.isAttackableBy(caster))
					list.add(creature);
			}
		}
		
		if (list.isEmpty())
			return EMPTY_TARGET_ARRAY;
		
		return list.toArray(new Creature[list.size()]);
	}
	
	@Override
	public Creature getFinalTarget(Creature caster, Creature target, L2Skill skill)
	{
		return caster;
	}
	
	@Override
	public boolean meetCastConditions(Playable caster, Creature target, L2Skill skill, boolean isCtrlPressed)
	{
		if (skill.isOffensive() && caster.isInsideZone(ZoneId.PEACE))
		{
			caster.sendPacket(SystemMessageId.CANT_ATK_PEACEZONE);
			return false;
		}
		return true;
	}
}