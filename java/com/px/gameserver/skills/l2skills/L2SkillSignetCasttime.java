package com.px.gameserver.skills.l2skills;

import com.px.commons.data.StatSet;

import com.px.gameserver.model.WorldObject;
import com.px.gameserver.model.actor.Creature;
import com.px.gameserver.skills.L2Skill;

public final class L2SkillSignetCasttime extends L2Skill
{
	public final int effectNpcId;
	public final int effectId;
	
	public L2SkillSignetCasttime(StatSet set)
	{
		super(set);
		effectNpcId = set.getInteger("effectNpcId", -1);
		effectId = set.getInteger("effectId", -1);
	}
	
	@Override
	public void useSkill(Creature caster, WorldObject[] targets)
	{
		if (caster.isAlikeDead())
			return;
		
		getEffectsSelf(caster);
	}
}