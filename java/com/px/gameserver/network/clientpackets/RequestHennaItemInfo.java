package com.px.gameserver.network.clientpackets;

import com.px.gameserver.data.xml.HennaData;
import com.px.gameserver.model.actor.Player;
import com.px.gameserver.model.item.Henna;
import com.px.gameserver.network.serverpackets.HennaItemInfo;

public final class RequestHennaItemInfo extends L2GameClientPacket
{
	private int _symbolId;
	
	@Override
	protected void readImpl()
	{
		_symbolId = readD();
	}
	
	@Override
	protected void runImpl()
	{
		final Player player = getClient().getPlayer();
		if (player == null)
			return;
		
		final Henna henna = HennaData.getInstance().getHenna(_symbolId);
		if (henna == null)
			return;
		
		player.sendPacket(new HennaItemInfo(henna, player));
	}
}