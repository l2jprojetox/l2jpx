package net.l2jpx.gameserver.skills.conditions;

import net.l2jpx.gameserver.skills.Env;

/**
 * @author mr
 */
public class ConditionPlayerHp extends Condition
{
	private final int hp;
	
	public ConditionPlayerHp(final int hp)
	{
		this.hp = hp;
	}
	
	@Override
	public boolean testImpl(final Env env)
	{
		return env.player.getCurrentHp() * 100 / env.player.getMaxHp() <= hp;
	}
}
