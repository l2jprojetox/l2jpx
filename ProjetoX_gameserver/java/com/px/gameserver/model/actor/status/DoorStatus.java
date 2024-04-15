package com.px.gameserver.model.actor.status;

import com.px.gameserver.model.actor.instance.Door;

public class DoorStatus extends CreatureStatus
{
	public DoorStatus(Door activeChar)
	{
		super(activeChar);
	}
	
	@Override
	public Door getActiveChar()
	{
		return (Door) super.getActiveChar();
	}
}