package net.l2jpx.gameserver.model.actor.status;

import net.l2jpx.gameserver.model.actor.instance.L2DoorInstance;

public class DoorStatus extends CharStatus
{
	public DoorStatus(final L2DoorInstance activeChar)
	{
		super(activeChar);
	}
	
	@Override
	public L2DoorInstance getActiveChar()
	{
		return (L2DoorInstance) super.getActiveChar();
	}
}
