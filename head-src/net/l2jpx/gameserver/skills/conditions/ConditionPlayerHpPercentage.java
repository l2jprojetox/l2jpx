package net.l2jpx.gameserver.skills.conditions;

import net.l2jpx.gameserver.skills.Env;

public class ConditionPlayerHpPercentage extends Condition
{
	private final double percentage;
	
	public ConditionPlayerHpPercentage(final double p)
	{
		percentage = p;
	}
	
	@Override
	public boolean testImpl(final Env env)
	{
		return env.player.getCurrentHp() <= env.player.getMaxHp() * percentage;
	}
}
