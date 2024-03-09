package net.l2jpx.gameserver.network.clientpackets;

import org.apache.log4j.Logger;

import net.l2jpx.Config;
import net.l2jpx.gameserver.managers.QuestManager;
import net.l2jpx.gameserver.model.actor.instance.L2PcInstance;
import net.l2jpx.gameserver.model.quest.Quest;
import net.l2jpx.gameserver.model.quest.QuestState;
import net.l2jpx.gameserver.network.SystemMessageId;
import net.l2jpx.gameserver.network.serverpackets.QuestList;
import net.l2jpx.gameserver.network.serverpackets.SystemMessage;

public final class RequestQuestAbort extends L2GameClientPacket
{
	private static Logger LOGGER = Logger.getLogger(RequestQuestAbort.class);
	
	private int questId;
	
	@Override
	protected void readImpl()
	{
		questId = readD();
	}
	
	@Override
	protected void runImpl()
	{
		final L2PcInstance activeChar = getClient().getActiveChar();
		if (activeChar == null)
		{
			return;
		}
		
		Quest qe = null;
		if (!Config.ALT_DEV_NO_QUESTS)
		{
			qe = QuestManager.getInstance().getQuest(questId);
		}
		
		if (qe != null)
		{
			final QuestState qs = activeChar.getQuestState(qe.getName());
			if (qs != null)
			{
				qs.exitQuest(true);
				SystemMessage sm = new SystemMessage(SystemMessageId.S1_S2);
				sm.addString("Quest aborted.");
				activeChar.sendPacket(sm);
				sm = null;
				final QuestList ql = new QuestList();
				activeChar.sendPacket(ql);
			}
			else
			{
				if (Config.DEBUG)
				{
					LOGGER.info("Player '" + activeChar.getName() + "' try to abort quest " + qe.getName() + " but he didn't have it started.");
				}
			}
		}
		else
		{
			if (Config.DEBUG)
			{
				LOGGER.warn("Quest (id='" + questId + "') not found.");
			}
		}
	}
	
	@Override
	public String getType()
	{
		return "[C] 64 RequestQuestAbort";
	}
}
