package com.px.gameserver.skills.l2skills;

import com.px.commons.data.StatSet;

import com.px.gameserver.enums.skills.EffectType;
import com.px.gameserver.enums.skills.SkillTargetType;
import com.px.gameserver.model.WorldObject;
import com.px.gameserver.model.actor.Creature;
import com.px.gameserver.skills.AbstractEffect;
import com.px.gameserver.skills.L2Skill;
import com.px.gameserver.skills.effects.EffectSeed;

public class L2SkillSeed extends L2Skill
{
	public L2SkillSeed(StatSet set)
	{
		super(set);
	}
	
	@Override
	public void useSkill(Creature caster, WorldObject[] targets)
	{
		if (caster.isAlikeDead())
			return;
		
		// Update Seeds Effects
		for (WorldObject obj : targets)
		{
			if (!(obj instanceof Creature))
				continue;
			
			final Creature target = ((Creature) obj);
			if (target.isAlikeDead() && getTargetType() != SkillTargetType.CORPSE_MOB)
				continue;
			
			EffectSeed oldEffect = (EffectSeed) target.getFirstEffect(getId());
			if (oldEffect == null)
				getEffects(caster, target);
			else
				oldEffect.increasePower();
			
			for (AbstractEffect effect : target.getAllEffects())
				if (effect.getEffectType() == EffectType.SEED)
					effect.rescheduleEffect();
		}
	}
}