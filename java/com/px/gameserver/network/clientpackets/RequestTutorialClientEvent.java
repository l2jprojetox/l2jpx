package com.px.gameserver.network.clientpackets;

import com.px.gameserver.model.actor.Player;
import com.px.gameserver.scripting.QuestState;

public class RequestTutorialClientEvent extends L2GameClientPacket
{
	private int _eventId;
	
	@Override
	protected void readImpl()
	{
		_eventId = readD();
	}
	
	@Override
	protected void runImpl()
	{
		final Player player = getClient().getPlayer();
		if (player == null)
			return;
		
		final QuestState qs = player.getQuestList().getQuestState("Tutorial");
		if (qs != null)
			qs.getQuest().notifyEvent("CE" + _eventId + "", null, player);
	}
}