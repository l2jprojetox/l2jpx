package com.l2jpx.gameserver.skills.conditions;

import com.l2jpx.gameserver.model.actor.Creature;
import com.l2jpx.gameserver.model.actor.Player;
import com.l2jpx.gameserver.model.item.kind.Item;
import com.l2jpx.gameserver.skills.L2Skill;

public class ConditionPlayerSex extends Condition
{
	private final int _sex;
	
	public ConditionPlayerSex(int sex)
	{
		_sex = sex;
	}
	
	@Override
	public boolean testImpl(Creature effector, Creature effected, L2Skill skill, Item item)
	{
		return effector instanceof Player && ((Player) effector).getAppearance().getSex().ordinal() == _sex;
	}
}