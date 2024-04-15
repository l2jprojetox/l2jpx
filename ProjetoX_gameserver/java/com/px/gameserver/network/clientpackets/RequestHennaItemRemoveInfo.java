package com.px.gameserver.network.clientpackets;

import com.px.gameserver.data.xml.HennaData;
import com.px.gameserver.model.actor.Player;
import com.px.gameserver.model.item.Henna;
import com.px.gameserver.network.serverpackets.HennaItemRemoveInfo;

public final class RequestHennaItemRemoveInfo extends L2GameClientPacket
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
		final Player activeChar = getClient().getPlayer();
		if (activeChar == null)
			return;
		
		final Henna template = HennaData.getInstance().getHenna(_symbolId);
		if (template == null)
			return;
		
		activeChar.sendPacket(new HennaItemRemoveInfo(template, activeChar));
	}
}