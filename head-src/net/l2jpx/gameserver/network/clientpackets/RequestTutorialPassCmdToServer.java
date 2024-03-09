package net.l2jpx.gameserver.network.clientpackets;

import net.l2jpx.gameserver.model.actor.instance.L2PcInstance;
import net.l2jpx.gameserver.model.quest.QuestState;

public class RequestTutorialPassCmdToServer extends L2GameClientPacket
{
	private String bypass = null;
	
	@Override
	protected void readImpl()
	{
		bypass = readS();
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
			qs.getQuest().notifyEvent(bypass, null, player);
		}
	}
	
	@Override
	public String getType()
	{
		return "[C] 7c RequestTutorialPassCmdToServer";
	}
}
