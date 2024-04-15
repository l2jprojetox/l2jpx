package com.px.gameserver.network.clientpackets;

import com.px.gameserver.model.actor.Player;

public final class ObserverReturn extends L2GameClientPacket
{
	@Override
	protected void readImpl()
	{
	}
	
	@Override
	protected void runImpl()
	{
		final Player activeChar = getClient().getPlayer();
		if (activeChar == null)
			return;
		
		if (activeChar.isInObserverMode())
			activeChar.leaveObserverMode();
	}
}