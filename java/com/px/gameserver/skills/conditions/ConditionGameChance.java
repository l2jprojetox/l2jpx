package com.px.gameserver.skills.conditions;

import com.px.commons.random.Rnd;

import com.px.gameserver.model.actor.Creature;
import com.px.gameserver.model.item.kind.Item;
import com.px.gameserver.skills.L2Skill;

public class ConditionGameChance extends Condition
{
	private final int _chance;
	
	public ConditionGameChance(int chance)
	{
		_chance = chance;
	}
	
	@Override
	public boolean testImpl(Creature effector, Creature effected, L2Skill skill, Item item)
	{
		return Rnd.get(100) < _chance;
	}
}