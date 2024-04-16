package com.px.gameserver.network.clientpackets;

import com.px.gameserver.data.manager.PartyMatchRoomManager;
import com.px.gameserver.model.actor.Player;

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