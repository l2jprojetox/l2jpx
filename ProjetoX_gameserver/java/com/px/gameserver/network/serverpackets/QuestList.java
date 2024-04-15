package com.px.gameserver.network.serverpackets;

import java.util.List;

import com.px.gameserver.model.actor.Player;
import com.px.gameserver.scripting.Quest;
import com.px.gameserver.scripting.QuestState;

public class QuestList extends L2GameServerPacket
{
	private final List<Quest> _quests;
	private final Player _activeChar;
	
	public QuestList(Player player)
	{
		_activeChar = player;
		_quests = player.getAllQuests(true);
	}
	
	@Override
	protected final void writeImpl()
	{
		writeC(0x80);
		writeH(_quests.size());
		for (Quest q : _quests)
		{
			writeD(q.getQuestId());
			QuestState qs = _activeChar.getQuestState(q.getName());
			if (qs == null)
			{
				writeD(0);
				continue;
			}
			
			int states = qs.getInt("__compltdStateFlags");
			if (states != 0)
				writeD(states);
			else
				writeD(qs.getInt("cond"));
		}
	}
}