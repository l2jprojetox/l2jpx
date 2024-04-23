package com.l2jpx.gameserver.network.clientpackets;

import com.l2jpx.gameserver.data.manager.PartyMatchRoomManager;
import com.l2jpx.gameserver.model.actor.Player;

public final class RequestExitPartyMatchingWaitingRoom extends L2GameClientPacket
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
		
		PartyMatchRoomManager.getInstance().removeWaitingPlayer(player);
	}
}