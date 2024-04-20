package com.l2jpx.gameserver.network.clientpackets;

import com.l2jpx.gameserver.data.manager.SevenSignsManager;
import com.l2jpx.gameserver.model.actor.Player;
import com.l2jpx.gameserver.network.serverpackets.SSQStatus;

public final class RequestSSQStatus extends L2GameClientPacket
{
	private int _page;
	
	@Override
	protected void readImpl()
	{
		_page = readC();
	}
	
	@Override
	protected void runImpl()
	{
		final Player player = getClient().getPlayer();
		if (player == null)
			return;
		
		if ((SevenSignsManager.getInstance().isSealValidationPeriod() || SevenSignsManager.getInstance().isCompResultsPeriod()) && _page == 4)
			return;
		
		player.sendPacket(new SSQStatus(player.getObjectId(), _page));
	}
}