package com.px.gameserver.network.clientpackets;

import com.px.gameserver.data.sql.ClanTable;
import com.px.gameserver.model.actor.Player;

public final class RequestReplySurrenderPledgeWar extends L2GameClientPacket
{
	private int _answer;
	
	@Override
	protected void readImpl()
	{
		_answer = readD();
	}
	
	@Override
	protected void runImpl()
	{
		final Player activeChar = getClient().getPlayer();
		if (activeChar == null)
			return;
		
		final Player requestor = activeChar.getActiveRequester();
		if (requestor == null)
			return;
		
		if (_answer == 1)
		{
			requestor.deathPenalty(false, false, false);
			ClanTable.getInstance().deleteClansWars(requestor.getClanId(), activeChar.getClanId());
		}
		
		activeChar.onTransactionRequest(requestor);
	}
}