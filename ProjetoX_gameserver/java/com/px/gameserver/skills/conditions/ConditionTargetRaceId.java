package com.px.gameserver.skills.conditions;

import java.util.List;

import com.px.gameserver.model.actor.Npc;
import com.px.gameserver.skills.Env;

/**
 * @author nBd
 */
public class ConditionTargetRaceId extends Condition
{
	private final List<Integer> _raceIds;
	
	public ConditionTargetRaceId(List<Integer> raceId)
	{
		_raceIds = raceId;
	}
	
	@Override
	public boolean testImpl(Env env)
	{
		if (!(env.getTarget() instanceof Npc))
			return false;
		
		return _raceIds.contains(((Npc) env.getTarget()).getTemplate().getRace().ordinal());
	}
}