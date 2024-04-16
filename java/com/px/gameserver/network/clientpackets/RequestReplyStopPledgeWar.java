package com.px.gameserver.network.clientpackets;

import com.px.gameserver.data.sql.ClanTable;
import com.px.gameserver.model.actor.Player;
import com.px.gameserver.network.SystemMessageId;

public final class RequestReplyStopPledgeWar extends L2GameClientPacket
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
		final Player player = getClient().getPlayer();
		if (player == null)
			return;
		
		final Player requestor = player.getActiveRequester();
		if (requestor == null)
			return;
		
		if (_answer == 1)
			ClanTable.getInstance().deleteClansWars(requestor.getClanId(), player.getClanId());
		else
			requestor.sendPacket(SystemMessageId.REQUEST_TO_END_WAR_HAS_BEEN_DENIED);
		
		player.setActiveRequester(null);
		requestor.onTransactionResponse();
	}
}