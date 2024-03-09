package net.l2jpx.gameserver.network.serverpackets;

import net.l2jpx.gameserver.model.actor.instance.L2PcInstance;
import net.l2jpx.gameserver.model.quest.Quest;
import net.l2jpx.gameserver.model.quest.QuestState;

/**
 * Sh (dd) h (dddd)
 * @author Tempy
 */
public class GMViewQuestList extends L2GameServerPacket
{
	private final L2PcInstance activeChar;
	
	public GMViewQuestList(final L2PcInstance cha)
	{
		activeChar = cha;
	}
	
	@Override
	protected final void writeImpl()
	{
		writeC(0x93);
		writeS(activeChar.getName());
		
		final Quest[] questList = activeChar.getAllActiveQuests();
		
		if (questList.length == 0)
		{
			writeC(0);
			writeH(0);
			writeH(0);
			return;
		}
		
		writeH(questList.length); // quest count
		
		for (final Quest q : questList)
		{
			writeD(q.getQuestId());
			
			final QuestState qs = activeChar.getQuestState(q.getName());
			
			if (qs == null)
			{
				writeD(0);
				continue;
			}
			
			writeD(qs.getInt("cond")); // stage of quest progress
		}
	}
	
	@Override
	public String getType()
	{
		return "[S] ac GMViewQuestList";
	}
}
