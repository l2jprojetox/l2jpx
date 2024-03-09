package net.l2jpx.gameserver.network.clientpackets;

import net.l2jpx.gameserver.model.actor.instance.L2PcInstance;
import net.l2jpx.gameserver.model.quest.QuestState;

/**
 * @author ProGramMoS
 */
public class RequestTutorialClientEvent extends L2GameClientPacket
{
	int eventId = 0;
	
	@Override
	protected void readImpl()
	{
		eventId = readD();
	}
	
	@Override
	protected void runImpl()
	{
		final L2PcInstance player = getClient().getActiveChar();
		
		if (player == null)
		{
			return;
		}
		
		final QuestState qs = player.getQuestState("255_Tutorial");
		if (qs != null)
		{
			qs.getQuest().notifyEvent("CE" + eventId + "", null, player);
		}
	}
	
	@Override
	public String getType()
	{
		return "[C] 7e RequestTutorialClientEvent";
	}
}
