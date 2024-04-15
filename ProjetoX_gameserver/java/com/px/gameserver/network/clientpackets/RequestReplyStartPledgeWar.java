package com.px.gameserver.network.clientpackets;

import com.px.gameserver.data.sql.ClanTable;
import com.px.gameserver.model.actor.Player;
import com.px.gameserver.network.SystemMessageId;

public final class RequestReplyStartPledgeWar extends L2GameClientPacket
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
			ClanTable.getInstance().storeClansWars(requestor.getClanId(), activeChar.getClanId());
		else
			requestor.sendPacket(SystemMessageId.WAR_PROCLAMATION_HAS_BEEN_REFUSED);
		
		activeChar.setActiveRequester(null);
		requestor.onTransactionResponse();
	}
}