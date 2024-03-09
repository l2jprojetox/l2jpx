package net.l2jpx.gameserver.skills.conditions;

import net.l2jpx.gameserver.skills.Env;

/**
 * @author Steuf
 */
public class ConditionWithSkill extends Condition
{
	private final boolean skill;
	
	public ConditionWithSkill(final boolean skill)
	{
		this.skill = skill;
	}
	
	@Override
	public boolean testImpl(final Env env)
	{
		if (!skill && env.skill != null)
		{
			return false;
		}
		return true;
	}
}
