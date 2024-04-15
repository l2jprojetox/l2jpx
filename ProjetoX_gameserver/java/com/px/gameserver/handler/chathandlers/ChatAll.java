package com.px.gameserver.handler.chathandlers;

import com.px.gameserver.handler.IChatHandler;
import com.px.gameserver.model.actor.Player;
import com.px.gameserver.model.actor.player.BlockList;
import com.px.gameserver.network.FloodProtectors;
import com.px.gameserver.network.FloodProtectors.Action;
import com.px.gameserver.network.serverpackets.CreatureSay;

public class ChatAll implements IChatHandler
{
	private static final int[] COMMAND_IDS =
	{
		0
	};
	
	@Override
	public void handleChat(int type, Player activeChar, String params, String text)
	{
		if (!FloodProtectors.performAction(activeChar.getClient(), Action.GLOBAL_CHAT))
			return;
		
		final CreatureSay cs = new CreatureSay(activeChar.getObjectId(), type, activeChar.getName(), text);
		for (Player player : activeChar.getKnownTypeInRadius(Player.class, 1250))
		{
			if (!BlockList.isBlocked(player, activeChar))
				player.sendPacket(cs);
		}
		activeChar.sendPacket(cs);
	}
	
	@Override
	public int[] getChatTypeList()
	{
		return COMMAND_IDS;
	}
}