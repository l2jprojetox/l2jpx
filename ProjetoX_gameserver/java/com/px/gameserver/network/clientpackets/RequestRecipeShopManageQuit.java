package com.px.gameserver.network.clientpackets;

import com.px.gameserver.model.actor.Player;

public final class RequestRecipeShopManageQuit extends L2GameClientPacket
{
	@Override
	protected void readImpl()
	{
	}
	
	@Override
	protected void runImpl()
	{
		Player player = getClient().getPlayer();
		if (player == null)
			return;
		
		player.forceStandUp();
	}
}