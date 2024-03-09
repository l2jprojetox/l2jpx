package net.l2jpx.gameserver.model.actor.stat;

import net.l2jpx.gameserver.model.actor.instance.L2DoorInstance;

public class DoorStat extends CharStat
{
	public DoorStat(final L2DoorInstance activeChar)
	{
		super(activeChar);
		
		setLevel((byte) 1);
	}
	
	@Override
	public L2DoorInstance getActiveChar()
	{
		return (L2DoorInstance) super.getActiveChar();
	}
	
	@Override
	public final int getLevel()
	{
		return 1;
	}
}
