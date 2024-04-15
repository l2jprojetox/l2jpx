package com.px.gameserver.network.serverpackets;

import java.util.List;

import com.px.gameserver.model.actor.Player;
import com.px.gameserver.scripting.Quest;
import com.px.gameserver.scripting.QuestState;

/**
 * Sh (dd) h (dddd)
 * @author Tempy
 */
public class GMViewQuestList extends L2GameServerPacket
{
	private final Player _activeChar;
	
	public GMViewQuestList(Player cha)
	{
		_activeChar = cha;
	}
	
	@Override
	protected final void writeImpl()
	{
		writeC(0x93);
		writeS(_activeChar.getName());
		
		List<Quest> quests = _activeChar.getAllQuests(true);
		
		writeH(quests.size());
		for (Quest q : quests)
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