package com.l2jpx.gameserver.network.clientpackets;

import com.l2jpx.gameserver.data.xml.HennaData;
import com.l2jpx.gameserver.model.actor.Player;
import com.l2jpx.gameserver.model.item.Henna;
import com.l2jpx.gameserver.network.serverpackets.HennaItemUnequipInfo;

public final class RequestHennaUnequipInfo extends L2GameClientPacket
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
		
		player.sendPacket(new HennaItemUnequipInfo(henna, player));
	}
}