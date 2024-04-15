package com.px.gameserver.network.clientpackets;

import com.px.gameserver.model.actor.Player;

/**
 * @author zabbix
 */
public class GameGuardReply extends L2GameClientPacket
{
	@Override
	protected void readImpl()
	{
	}
	
	@Override
	protected void runImpl()
	{
		final Player activeChar = getClient().getPlayer();
		if (activeChar == null)
			return;
		
		getClient().setGameGuardOk(true);
	}
}