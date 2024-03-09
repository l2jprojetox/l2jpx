package net.l2jpx.gameserver.skills.conditions;

import java.util.ArrayList;

import net.l2jpx.gameserver.model.actor.instance.L2PcInstance;
import net.l2jpx.gameserver.skills.Env;

/**
 * The Class ConditionPlayerClassIdRestriction. Credits: l2jserver
 */
public class ConditionPlayerClassIdRestriction extends Condition
{
	private final ArrayList<Integer> classIds;
	
	/**
	 * Instantiates a new condition player class id restriction.
	 * @param classId the class id
	 */
	public ConditionPlayerClassIdRestriction(final ArrayList<Integer> classId)
	{
		classIds = classId;
	}
	
	@Override
	public boolean testImpl(final Env env)
	{
		if (!(env.player instanceof L2PcInstance))
		{
			return false;
		}
		return (classIds.contains(((L2PcInstance) env.player).getClassId().getId()));
	}
}