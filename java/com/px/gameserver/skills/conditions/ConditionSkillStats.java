package com.px.gameserver.skills.conditions;

import com.px.gameserver.enums.skills.Stats;
import com.px.gameserver.model.actor.Creature;
import com.px.gameserver.model.item.kind.Item;
import com.px.gameserver.skills.L2Skill;

public class ConditionSkillStats extends Condition
{
	private final Stats _stat;
	
	public ConditionSkillStats(Stats stat)
	{
		super();
		
		_stat = stat;
	}
	
	@Override
	public boolean testImpl(Creature effector, Creature effected, L2Skill skill, Item item)
	{
		return skill != null && skill.getStat() == _stat;
	}
}