package com.px.gameserver.network.clientpackets;

import com.px.gameserver.model.actor.Player;
import com.px.gameserver.network.serverpackets.UserInfo;

public final class Appearing extends L2GameClientPacket
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
		
		if (activeChar.isTeleporting())
			activeChar.onTeleported();
		
		sendPacket(new UserInfo(activeChar));
	}
	
	@Override
	protected boolean triggersOnActionRequest()
	{
		return false;
	}
}