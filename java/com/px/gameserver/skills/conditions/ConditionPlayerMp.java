package com.px.gameserver.skills.conditions;

import com.px.gameserver.model.actor.Creature;
import com.px.gameserver.model.item.kind.Item;
import com.px.gameserver.skills.L2Skill;

public class ConditionPlayerMp extends Condition
{
	private final int _mp;
	
	public ConditionPlayerMp(int mp)
	{
		_mp = mp;
	}
	
	@Override
	public boolean testImpl(Creature effector, Creature effected, L2Skill skill, Item item)
	{
		return effector.getStatus().getMpRatio() * 100 <= _mp;
	}
}