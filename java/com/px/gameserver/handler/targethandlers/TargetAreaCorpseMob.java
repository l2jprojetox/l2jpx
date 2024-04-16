package com.px.gameserver.handler.targethandlers;

import java.util.ArrayList;
import java.util.List;

import com.px.gameserver.enums.skills.SkillTargetType;
import com.px.gameserver.enums.skills.SkillType;
import com.px.gameserver.geoengine.GeoEngine;
import com.px.gameserver.handler.ITargetHandler;
import com.px.gameserver.model.actor.Attackable;
import com.px.gameserver.model.actor.Creature;
import com.px.gameserver.model.actor.Playable;
import com.px.gameserver.model.actor.instance.Monster;
import com.px.gameserver.model.actor.instance.Pet;
import com.px.gameserver.model.actor.template.NpcTemplate;
import com.px.gameserver.network.SystemMessageId;
import com.px.gameserver.skills.L2Skill;
import com.px.gameserver.taskmanager.DecayTaskManager;

public class TargetAreaCorpseMob implements ITargetHandler
{
	@Override
	public SkillTargetType getTargetType()
	{
		return SkillTargetType.AREA_CORPSE_MOB;
	}
	
	@Override
	public Creature[] getTargetList(Creature caster, Creature target, L2Skill skill)
	{
		final List<Creature> list = new ArrayList<>();
		list.add(target);
		
		for (Creature creature : target.getKnownTypeInRadius(Creature.class, skill.getSkillRadius()))
		{
			if (creature == caster || !GeoEngine.getInstance().canSeeTarget(target, creature))
				continue;
			
			if (skill.getId() == 444)
			{
				if (creature instanceof Attackable && creature.isDead())
					list.add(creature);
				
				continue;
			}
			else if (creature.isDead())
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
		
		return list.toArray(new Creature[list.size()]);
	}
	
	@Override
	public Creature getFinalTarget(Creature caster, Creature target, L2Skill skill)
	{
		return target;
	}
	
	@Override
	public boolean meetCastConditions(Playable caster, Creature target, L2Skill skill, boolean isCtrlPressed)
	{
		// Verify if a corpse exists. Can't do anything on Pets.
		final Long time = DecayTaskManager.getInstance().get(target);
		if (time == null || target instanceof Pet)
		{
			caster.sendPacket(SystemMessageId.INVALID_TARGET);
			return false;
		}
		
		// Harvest can be done anytime.
		if (skill.getSkillType() == SkillType.HARVEST)
		{
			if (!(target instanceof Monster))
			{
				caster.sendPacket(SystemMessageId.THE_HARVEST_FAILED_BECAUSE_THE_SEED_WAS_NOT_SOWN);
				return false;
			}
			
			return true;
		}
		
		// Cast NpcTemplate over Creature template since only pets, servitors and NPCs are stored into DecayTaskManager, and all inherits from NpcTemplate.
		final NpcTemplate template = (NpcTemplate) target.getTemplate();
		
		// Check last corpse action time. That behavior exists whenever the target isn't seeded or spoiled.
		final boolean isSeededOrSpoiled = target instanceof Monster && (((Monster) target).getSeedState().isSeeded() || ((Monster) target).getSpoilState().isSpoiled());
		if (!isSeededOrSpoiled && System.currentTimeMillis() >= time - (template.getCorpseTime() * 1000 / 2))
		{
			caster.sendPacket(SystemMessageId.CORPSE_TOO_OLD_SKILL_NOT_USED);
			return false;
		}
		
		// Sweep always end as true if the target is a Monster ; otherwise, it's always false.
		if (skill.getSkillType() == SkillType.SWEEP && !(target instanceof Monster))
		{
			caster.sendPacket(SystemMessageId.SWEEPER_FAILED_TARGET_NOT_SPOILED);
			return false;
		}
		
		return true;
	}
}