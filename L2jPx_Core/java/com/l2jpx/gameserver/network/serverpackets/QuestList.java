package com.l2jpx.gameserver.network.serverpackets;

import java.util.List;

import com.l2jpx.gameserver.model.actor.Player;
import com.l2jpx.gameserver.scripting.QuestState;

public class QuestList extends L2GameServerPacket
{
	private final List<QuestState> _questStates;
	
	public QuestList(Player player)
	{
		_questStates = player.getQuestList().getAllQuests(true);
	}
	
	@Override
	protected final void writeImpl()
	{
		writeC(0x80);
		
		writeH(_questStates.size());
		
		for (QuestState qs : _questStates)
		{
			writeD(qs.getQuest().getQuestId());
			writeD(qs.getFlags());
		}
	}
}