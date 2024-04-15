package com.px.gameserver.network.clientpackets;

import com.px.gameserver.model.actor.Player;
import com.px.gameserver.network.serverpackets.QuestList;

public final class RequestQuestList extends L2GameClientPacket
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
		
		activeChar.sendPacket(new QuestList(activeChar));
	}
}