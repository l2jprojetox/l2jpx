package com.px.gameserver.skills.conditions;

import com.px.gameserver.model.actor.Creature;
import com.px.gameserver.model.item.kind.Item;
import com.px.gameserver.skills.L2Skill;

public class ConditionTargetActiveSkillId extends Condition
{
	private final int _skillId;
	
	public ConditionTargetActiveSkillId(int skillId)
	{
		_skillId = skillId;
	}
	
	@Override
	public boolean testImpl(Creature effector, Creature effected, L2Skill skill, Item item)
	{
		return effected.getSkill(_skillId) != null;
	}
}