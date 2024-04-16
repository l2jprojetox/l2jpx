package com.px.gameserver.skills.conditions;

import java.util.List;

import com.px.gameserver.model.actor.Creature;
import com.px.gameserver.model.actor.Npc;
import com.px.gameserver.model.item.kind.Item;
import com.px.gameserver.skills.L2Skill;

public class ConditionTargetRaceId extends Condition
{
	private final List<Integer> _raceIds;
	
	public ConditionTargetRaceId(List<Integer> raceId)
	{
		_raceIds = raceId;
	}
	
	@Override
	public boolean testImpl(Creature effector, Creature effected, L2Skill skill, Item item)
	{
		return effected instanceof Npc && _raceIds.contains(((Npc) effected).getTemplate().getRace().ordinal());
	}
}