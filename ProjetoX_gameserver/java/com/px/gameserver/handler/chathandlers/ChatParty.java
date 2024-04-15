package com.px.gameserver.handler.chathandlers;

import com.px.gameserver.handler.IChatHandler;
import com.px.gameserver.model.actor.Player;
import com.px.gameserver.model.group.Party;
import com.px.gameserver.network.serverpackets.CreatureSay;

public class ChatParty implements IChatHandler
{
	private static final int[] COMMAND_IDS =
	{
		3
	};
	
	@Override
	public void handleChat(int type, Player player, String target, String text)
	{
		final Party party = player.getParty();
		if (party == null)
			return;
		
		party.broadcastPacket(new CreatureSay(player.getObjectId(), type, player.getName(), text));
	}
	
	@Override
	public int[] getChatTypeList()
	{
		return COMMAND_IDS;
	}
}