package com.px.gameserver.network.clientpackets;

import com.px.gameserver.data.manager.CastleManager;
import com.px.gameserver.data.manager.ClanHallManager;
import com.px.gameserver.model.actor.Player;
import com.px.gameserver.model.residence.castle.Castle;
import com.px.gameserver.model.residence.clanhall.SiegableHall;
import com.px.gameserver.network.serverpackets.SiegeAttackerList;

public final class RequestSiegeAttackerList extends L2GameClientPacket
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
		if (castle != null)
		{
			sendPacket(new SiegeAttackerList(castle));
			return;
		}
		
		// Check SiegableHall entity associated to the id.
		final SiegableHall sh = ClanHallManager.getInstance().getSiegableHall(_id);
		if (sh != null)
			sendPacket(new SiegeAttackerList(sh));
	}
}