package com.l2jpx.gameserver.skills.conditions;

import com.l2jpx.gameserver.model.actor.Creature;
import com.l2jpx.gameserver.model.item.kind.Item;
import com.l2jpx.gameserver.skills.L2Skill;

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