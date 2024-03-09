package net.l2jpx.gameserver.model.actor.instance;

import net.l2jpx.gameserver.datatables.xml.L2Multisell;
import net.l2jpx.gameserver.templates.L2NpcTemplate;

/**
 * @author zabbix Lets drink to code!
 */
public class L2BlacksmithInstance extends L2FolkInstance
{
	public L2BlacksmithInstance(final int objectId, final L2NpcTemplate template)
	{
		super(objectId, template);
	}
	
	@Override
	public void onBypassFeedback(final L2PcInstance player, final String command)
	{
		if (command.startsWith("multisell"))
		{
			final int listId = Integer.parseInt(command.substring(9).trim());
			L2Multisell.getInstance().separateAndSend(listId, player, false, getCastle().getTaxRate());
		}
		super.onBypassFeedback(player, command);
	}
	
	@Override
	public String getHtmlPath(final int npcId, final int val)
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
		
		return "data/html/blacksmith/" + pom + ".htm";
	}
}
