package com.l2jpx.gameserver.skills.conditions;

import com.l2jpx.gameserver.model.actor.Creature;
import com.l2jpx.gameserver.model.item.kind.Item;
import com.l2jpx.gameserver.skills.L2Skill;

public class ConditionPlayerHp extends Condition
{
	private final int _hp;
	
	public ConditionPlayerHp(int hp)
	{
		_hp = hp;
	}
	
	@Override
	public boolean testImpl(Creature effector, Creature effected, L2Skill skill, Item item)
	{
		return effector.getStatus().getHpRatio() * 100 <= _hp;
	}
}