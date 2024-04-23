package com.l2jpx.gameserver.network.clientpackets;

import com.l2jpx.gameserver.data.xml.AdminData;
import com.l2jpx.gameserver.model.actor.Player;

public final class RequestGmList extends L2GameClientPacket
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
		
		AdminData.getInstance().sendListToPlayer(player);
	}
}