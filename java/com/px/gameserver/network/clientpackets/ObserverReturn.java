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
		final Player player = getClient().getPlayer();
		if (player == null)
			return;
		
		if (player.isInObserverMode())
			player.leaveObserverMode();
	}
}