package com.l2jpx.gameserver.network.clientpackets;

import com.l2jpx.gameserver.model.actor.Player;
import com.l2jpx.gameserver.network.serverpackets.QuestList;

public final class RequestQuestList extends L2GameClientPacket
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
		
		player.sendPacket(new QuestList(player));
	}
}