package com.px.gameserver.network.clientpackets;

import com.px.gameserver.model.actor.Player;

public class GameGuardReply extends L2GameClientPacket
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
		
		getClient().setGameGuardOk(true);
	}
}