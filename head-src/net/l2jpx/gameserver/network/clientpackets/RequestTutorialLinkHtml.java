package net.l2jpx.gameserver.network.clientpackets;

import net.l2jpx.gameserver.model.actor.instance.L2PcInstance;
import net.l2jpx.gameserver.model.quest.QuestState;

/**
 * @author ProGramMoS
 */
public class RequestTutorialLinkHtml extends L2GameClientPacket
{
	private String bypass;
	
	@Override
	protected void readImpl()
	{
		bypass = readS();
	}
	
	@Override
	protected void runImpl()
	{
		L2PcInstance player = getClient().getActiveChar();
		if (player == null)
		{
			return;
		}
		
		QuestState qs = player.getQuestState("255_Tutorial");
		if (qs != null)
		{
			qs.getQuest().notifyEvent(bypass, null, player);
		}
		
	}
	
	@Override
	public String getType()
	{
		return "[C] 7b RequestTutorialLinkHtml";
	}
}
