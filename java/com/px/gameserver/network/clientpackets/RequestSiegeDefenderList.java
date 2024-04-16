package com.px.gameserver.network.clientpackets;

import com.px.gameserver.data.manager.CastleManager;
import com.px.gameserver.model.actor.Player;
import com.px.gameserver.model.residence.castle.Castle;
import com.px.gameserver.network.serverpackets.SiegeDefenderList;

public final class RequestSiegeDefenderList extends L2GameClientPacket
{
	private int _id;
	
	@Override
	protected void readImpl()
	{
		_id = readD();
	}
	
	@Override
	protected void runImpl()
	{
		final Player player = getClient().getPlayer();
		if (player == null)
			return;
		
		// Check Castle entity associated to the id.
		final Castle castle = CastleManager.getInstance().getCastleById(_id);
		if (castle == null)
			return;
		
		sendPacket(new SiegeDefenderList(castle));
	}
}