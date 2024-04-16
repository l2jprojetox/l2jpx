package com.px.gameserver.skills.effects;

import com.px.gameserver.enums.skills.EffectType;
import com.px.gameserver.enums.skills.SkillType;
import com.px.gameserver.model.actor.Creature;
import com.px.gameserver.skills.AbstractEffect;
import com.px.gameserver.skills.L2Skill;

public class EffectNegate extends AbstractEffect
{
	public EffectNegate(EffectTemplate template, L2Skill skill, Creature effected, Creature effector)
	{
		super(template, skill, effected, effector);
	}
	
	@Override
	public EffectType getEffectType()
	{
		return EffectType.NEGATE;
	}
	
	@Override
	public boolean onStart()
	{
		for (int negateSkillId : getSkill().getNegateId())
		{
			if (negateSkillId != 0)
				getEffected().stopSkillEffects(negateSkillId);
		}
		
		for (SkillType negateSkillType : getSkill().getNegateStats())
			getEffected().stopSkillEffects(negateSkillType, getSkill().getNegateLvl());
		
		return true;
	}
	
	@Override
	public boolean onActionTime()
	{
		return false;
	}
}