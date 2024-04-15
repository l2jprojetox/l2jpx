package com.px.gameserver.network.clientpackets;

import com.px.gameserver.model.actor.Player;
import com.px.gameserver.model.partymatching.PartyMatchWaitingList;

public final class RequestExitPartyMatchingWaitingRoom extends L2GameClientPacket
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
		
		PartyMatchWaitingList.getInstance().removePlayer(activeChar);
	}
}