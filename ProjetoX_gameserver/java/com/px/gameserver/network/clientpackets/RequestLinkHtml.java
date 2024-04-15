package com.px.gameserver.network.clientpackets;

import com.px.gameserver.model.actor.Player;
import com.px.gameserver.network.serverpackets.NpcHtmlMessage;

/**
 * @author zabbix Lets drink to code!
 */
public final class RequestLinkHtml extends L2GameClientPacket
{
	private String _link;
	
	@Override
	protected void readImpl()
	{
		_link = readS();
	}
	
	@Override
	public void runImpl()
	{
		final Player actor = getClient().getPlayer();
		if (actor == null)
			return;
		
		if (_link.contains("..") || !_link.contains(".htm"))
			return;
		
		final NpcHtmlMessage html = new NpcHtmlMessage(0);
		html.setFile(_link);
		sendPacket(html);
	}
}