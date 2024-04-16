package com.px.gameserver.network.clientpackets;

import com.px.gameserver.model.actor.Player;
import com.px.gameserver.network.serverpackets.ShowMiniMap;

public final class RequestShowMiniMap extends L2GameClientPacket
{
	@Override
	protected void readImpl()
	{
	}
	
	@Override
	protected final void runImpl()
	{
		final Player player = getClient().getPlayer();
		if (player == null)
			return;
		
		player.sendPacket(ShowMiniMap.REGULAR_MAP);
	}
}