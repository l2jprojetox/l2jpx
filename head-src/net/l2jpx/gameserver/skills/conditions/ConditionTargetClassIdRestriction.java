package net.l2jpx.gameserver.skills.conditions;

import java.util.List;

import net.l2jpx.gameserver.model.actor.instance.L2PcInstance;
import net.l2jpx.gameserver.skills.Env;

public class ConditionTargetClassIdRestriction extends Condition
{
	private final List<Integer> classIds;
	
	public ConditionTargetClassIdRestriction(final List<Integer> classId)
	{
		classIds = classId;
	}
	
	@Override
	public boolean testImpl(final Env env)
	{
		if (!(env.target instanceof L2PcInstance))
		{
			return true;
		}
		return !classIds.contains(((L2PcInstance) env.target).getClassId().getId());
	}
}
