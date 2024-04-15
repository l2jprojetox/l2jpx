package com.px.gameserver.model.actor.instance;

import com.px.gameserver.model.actor.Player;
import com.px.gameserver.model.actor.template.NpcTemplate;
import com.px.gameserver.network.serverpackets.ActionFailed;
import com.px.gameserver.network.serverpackets.NpcHtmlMessage;
import com.px.gameserver.network.serverpackets.SiegeInfo;

public class SiegeNpc extends Folk
{
	public SiegeNpc(int objectId, NpcTemplate template)
	{
		super(objectId, template);
	}
	
	@Override
	public void showChatWindow(Player player)
	{
		if (!getCastle().getSiege().isInProgress())
			player.sendPacket(new SiegeInfo(getCastle()));
		else
		{
			final NpcHtmlMessage html = new NpcHtmlMessage(getObjectId());
			html.setFile("data/html/siege/" + getNpcId() + "-busy.htm");
			html.replace("%castlename%", getCastle().getName());
			html.replace("%objectId%", getObjectId());
			player.sendPacket(html);
			player.sendPacket(ActionFailed.STATIC_PACKET);
		}
	}
}