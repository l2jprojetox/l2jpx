package com.l2jpx.gameserver.skills.conditions;

import com.l2jpx.commons.random.Rnd;

import com.l2jpx.gameserver.model.actor.Creature;
import com.l2jpx.gameserver.model.item.kind.Item;
import com.l2jpx.gameserver.skills.L2Skill;

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