package com.px.gameserver.handler.chathandlers;

import com.px.gameserver.handler.IChatHandler;
import com.px.gameserver.model.actor.Player;
import com.px.gameserver.network.serverpackets.CreatureSay;

public class ChatAlliance implements IChatHandler
{
	private static final int[] COMMAND_IDS =
	{
		9
	};
	
	@Override
	public void handleChat(int type, Player activeChar, String target, String text)
	{
		if (activeChar.getClan() == null || activeChar.getClan().getAllyId() == 0)
			return;
		
		activeChar.getClan().broadcastToOnlineAllyMembers(new CreatureSay(activeChar.getObjectId(), type, activeChar.getName(), text));
	}
	
	@Override
	public int[] getChatTypeList()
	{
		return COMMAND_IDS;
	}
}