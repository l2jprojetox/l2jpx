package com.l2jpx.gameserver.network.clientpackets;

import com.l2jpx.gameserver.enums.actors.OperateType;
import com.l2jpx.gameserver.model.actor.Player;

public final class RequestPrivateStoreQuitSell extends L2GameClientPacket
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
		
		player.setOperateType(OperateType.NONE);
		player.broadcastUserInfo();
	}
}