package com.px.gameserver.skills.conditions;

import com.px.gameserver.enums.actors.ClassRace;
import com.px.gameserver.model.actor.Creature;
import com.px.gameserver.model.actor.Player;
import com.px.gameserver.model.item.kind.Item;
import com.px.gameserver.skills.L2Skill;

public class ConditionPlayerRace extends Condition
{
	private final ClassRace _race;
	
	public ConditionPlayerRace(ClassRace race)
	{
		_race = race;
	}
	
	@Override
	public boolean testImpl(Creature effector, Creature effected, L2Skill skill, Item item)
	{
		return effector instanceof Player && ((Player) effector).getRace() == _race;
	}
}