package com.l2jpx.gameserver.network.clientpackets;

import com.l2jpx.gameserver.model.actor.Player;
import com.l2jpx.gameserver.scripting.QuestState;

public class RequestTutorialPassCmdToServer extends L2GameClientPacket
{
	private String _bypass;
	
	@Override
	protected void readImpl()
	{
		_bypass = readS();
	}
	
	@Override
	protected void runImpl()
	{
		final Player player = getClient().getPlayer();
		if (player == null)
			return;
		
		final QuestState qs = player.getQuestList().getQuestState("Tutorial");
		if (qs != null)
			qs.getQuest().notifyEvent(_bypass, null, player);
	}
}