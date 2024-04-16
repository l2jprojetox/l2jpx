package com.px.gameserver.skills.conditions;

import com.px.gameserver.model.actor.Creature;
import com.px.gameserver.model.actor.Player;
import com.px.gameserver.model.item.kind.Item;
import com.px.gameserver.skills.L2Skill;

public class ConditionPlayerWeight extends Condition
{
	private final int _weight;
	
	public ConditionPlayerWeight(int weight)
	{
		_weight = weight;
	}
	
	@Override
	public boolean testImpl(Creature effector, Creature effected, L2Skill skill, Item item)
	{
		if (effector instanceof Player)
			return ((Player) effector).getWeightPenalty().ordinal() < _weight;
		
		return true;
	}
}