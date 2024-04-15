package com.px.gameserver.network.clientpackets;

import com.px.gameserver.data.manager.CastleManager;
import com.px.gameserver.model.actor.Player;
import com.px.gameserver.model.entity.Castle;
import com.px.gameserver.network.serverpackets.SiegeAttackerList;

public final class RequestSiegeAttackerList extends L2GameClientPacket
{
	private int _castleId;
	
	@Override
	protected void readImpl()
	{
		_castleId = readD();
	}
	
	@Override
	protected void runImpl()
	{
		final Player activeChar = getClient().getPlayer();
		if (activeChar == null)
			return;
		
		final Castle castle = CastleManager.getInstance().getCastleById(_castleId);
		if (castle == null)
			return;
		
		sendPacket(new SiegeAttackerList(castle));
	}
}