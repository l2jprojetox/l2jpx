package net.l2jpx.gameserver.model.actor.stat;

import net.l2jpx.gameserver.model.L2Summon;

public class SummonStat extends PlayableStat
{
	public SummonStat(final L2Summon activeChar)
	{
		super(activeChar);
	}
	
	@Override
	public L2Summon getActiveChar()
	{
		return (L2Summon) super.getActiveChar();
	}
}
