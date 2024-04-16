package com.px.gameserver.skills.conditions;

import com.px.gameserver.model.actor.Creature;
import com.px.gameserver.model.actor.Player;
import com.px.gameserver.model.item.kind.Item;
import com.px.gameserver.skills.L2Skill;

public class ConditionPlayerCharges extends Condition
{
	private final int _charges;
	
	public ConditionPlayerCharges(int charges)
	{
		_charges = charges;
	}
	
	@Override
	public boolean testImpl(Creature effector, Creature effected, L2Skill skill, Item item)
	{
		return effector instanceof Player && ((Player) effector).getCharges() >= _charges;
	}
}