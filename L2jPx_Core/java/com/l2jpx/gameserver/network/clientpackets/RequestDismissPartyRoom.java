package com.l2jpx.gameserver.network.clientpackets;

import com.l2jpx.gameserver.data.manager.PartyMatchRoomManager;
import com.l2jpx.gameserver.model.actor.Player;

public class RequestDismissPartyRoom extends L2GameClientPacket
{
	private int _roomId;
	
	@Override
	protected void readImpl()
	{
		_roomId = readD();
		readD(); // Not used.
	}
	
	@Override
	protected void runImpl()
	{
		final Player player = getClient().getPlayer();
		if (player == null)
			return;
		
		PartyMatchRoomManager.getInstance().deleteRoom(_roomId);
	}
}