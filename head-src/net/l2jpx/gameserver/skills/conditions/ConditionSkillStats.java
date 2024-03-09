package net.l2jpx.gameserver.skills.conditions;

import net.l2jpx.gameserver.skills.Env;
import net.l2jpx.gameserver.skills.Stats;

/**
 * @author mkizub
 */
public class ConditionSkillStats extends Condition
{
	
	private final Stats stat;
	
	public ConditionSkillStats(final Stats stat)
	{
		super();
		this.stat = stat;
	}
	
	@Override
	public boolean testImpl(final Env env)
	{
		if (env.skill == null)
		{
			return false;
		}
		return env.skill.getStat() == stat;
	}
}
