package com.px.gameserver.network.clientpackets;

import com.px.gameserver.model.actor.Player;
import com.px.gameserver.scripting.QuestState;

public class RequestTutorialQuestionMark extends L2GameClientPacket
{
	int _number;
	
	@Override
	protected void readImpl()
	{
		_number = readD();
	}
	
	@Override
	protected void runImpl()
	{
		final Player player = getClient().getPlayer();
		if (player == null)
			return;
		
		QuestState qs = player.getQuestState("Tutorial");
		if (qs != null)
			qs.getQuest().notifyEvent("QM" + _number + "", null, player);
	}
}