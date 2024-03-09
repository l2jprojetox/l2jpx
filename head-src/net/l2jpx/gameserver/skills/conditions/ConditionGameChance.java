package net.l2jpx.gameserver.skills.conditions;

import net.l2jpx.gameserver.skills.Env;
import net.l2jpx.util.random.Rnd;

/**
 * @author Advi
 */
public class ConditionGameChance extends Condition
{
	private final int chance;
	
	public ConditionGameChance(final int chance)
	{
		this.chance = chance;
	}
	
	@Override
	public boolean testImpl(final Env env)
	{
		return Rnd.get(100) < chance;
	}
}
