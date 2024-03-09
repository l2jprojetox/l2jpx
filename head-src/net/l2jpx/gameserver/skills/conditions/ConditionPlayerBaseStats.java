package net.l2jpx.gameserver.skills.conditions;

import net.l2jpx.gameserver.model.L2Character;
import net.l2jpx.gameserver.model.actor.instance.L2PcInstance;
import net.l2jpx.gameserver.skills.Env;

/**
 * @author mkizub
 */
public class ConditionPlayerBaseStats extends Condition
{
	
	private final BaseStat stat;
	private final int value;
	
	public ConditionPlayerBaseStats(final L2Character player, final BaseStat stat, final int value)
	{
		super();
		this.stat = stat;
		this.value = value;
	}
	
	@Override
	public boolean testImpl(final Env env)
	{
		if (!(env.player instanceof L2PcInstance))
		{
			return false;
		}
		final L2PcInstance player = (L2PcInstance) env.player;
		switch (stat)
		{
			case Int:
				return player.getINT() >= value;
			case Str:
				return player.getSTR() >= value;
			case Con:
				return player.getCON() >= value;
			case Dex:
				return player.getDEX() >= value;
			case Men:
				return player.getMEN() >= value;
			case Wit:
				return player.getWIT() >= value;
		}
		return false;
	}
}

enum BaseStat
{
	Int,
	Str,
	Con,
	Dex,
	Men,
	Wit
}
