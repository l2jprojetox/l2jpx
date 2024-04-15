package com.px.gameserver.skills.l2skills;

import com.px.commons.util.StatsSet;

import com.px.gameserver.model.L2Skill;
import com.px.gameserver.model.WorldObject;
import com.px.gameserver.model.actor.Creature;

public final class L2SkillSignetCasttime extends L2Skill
{
	public int _effectNpcId;
	public int effectId;
	
	public L2SkillSignetCasttime(StatsSet set)
	{
		super(set);
		_effectNpcId = set.getInteger("effectNpcId", -1);
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