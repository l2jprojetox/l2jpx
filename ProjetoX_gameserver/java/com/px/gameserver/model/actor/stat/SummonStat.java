package com.px.gameserver.model.actor.stat;

import com.px.gameserver.model.actor.Summon;

public class SummonStat extends PlayableStat
{
	public SummonStat(Summon activeChar)
	{
		super(activeChar);
	}
	
	@Override
	public Summon getActiveChar()
	{
		return (Summon) super.getActiveChar();
	}
}