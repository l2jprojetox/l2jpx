package net.l2jpx.gameserver.skills.conditions;

import net.l2jpx.gameserver.skills.Env;

/**
 * @author mkizub
 */
public class ConditionTargetLevel extends Condition
{
	private final int level;
	
	public ConditionTargetLevel(final int level)
	{
		this.level = level;
	}
	
	@Override
	public boolean testImpl(final Env env)
	{
		if (env.target == null)
		{
			return false;
		}
		return env.target.getLevel() >= level;
	}
}
