package com.px.gameserver.skills.conditions;

import com.px.gameserver.model.actor.Creature;
import com.px.gameserver.model.item.kind.Item;
import com.px.gameserver.skills.L2Skill;

public class ConditionPlayerLevel extends Condition
{
	private final int _level;
	
	public ConditionPlayerLevel(int level)
	{
		_level = level;
	}
	
	@Override
	public boolean testImpl(Creature effector, Creature effected, L2Skill skill, Item item)
	{
		return effector.getStatus().getLevel() >= _level;
	}
}