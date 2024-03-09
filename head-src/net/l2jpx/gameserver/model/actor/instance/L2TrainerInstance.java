package net.l2jpx.gameserver.model.actor.instance;

import net.l2jpx.gameserver.templates.L2NpcTemplate;

public final class L2TrainerInstance extends L2FolkInstance
{
	public L2TrainerInstance(int objectId, L2NpcTemplate template)
	{
		super(objectId, template);
	}
	
	@Override
	public String getHtmlPath(int npcId, int val)
	{
		String pom = "";
		
		if (val == 0)
		{
			pom = "" + npcId;
		}
		else
		{
			pom = npcId + "-" + val;
		}
		
		return "data/html/trainer/" + pom + ".htm";
	}
	
	@Override
	public void onBypassFeedback(L2PcInstance player, String command)
	{
		super.onBypassFeedback(player, command);
	}
}
