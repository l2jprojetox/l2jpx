package com.l2jpx.gameserver.network.clientpackets;

import com.l2jpx.gameserver.data.manager.CastleManager;
import com.l2jpx.gameserver.data.manager.ClanHallManager;
import com.l2jpx.gameserver.model.actor.Player;
import com.l2jpx.gameserver.model.clanhall.SiegableHall;
import com.l2jpx.gameserver.model.entity.Castle;
import com.l2jpx.gameserver.network.serverpackets.SiegeAttackerList;

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