package com.px.gameserver.network.clientpackets;

import com.px.gameserver.model.actor.Player;
import com.px.gameserver.scripting.QuestState;

public class RequestTutorialClientEvent extends L2GameClientPacket
{
	int eventId;
	
	@Override
	protected void readImpl()
	{
		eventId = readD();
	}
	
	@Override
	protected void runImpl()
	{
		final Player player = getClient().getPlayer();
		if (player == null)
			return;
		
		QuestState qs = player.getQuestState("Tutorial");
		if (qs != null)
			qs.getQuest().notifyEvent("CE" + eventId + "", null, player);
	}
}