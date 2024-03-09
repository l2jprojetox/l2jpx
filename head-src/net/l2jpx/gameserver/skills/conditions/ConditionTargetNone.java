package net.l2jpx.gameserver.skills.conditions;

import net.l2jpx.gameserver.skills.Env;

/**
 * @author mkizub
 */
public class ConditionTargetNone extends Condition
{
	public ConditionTargetNone()
	{
	}
	
	@Override
	public boolean testImpl(final Env env)
	{
		return env.target == null;
	}
}
