package com.px.gameserver.handler.chathandlers;

import com.px.gameserver.handler.IChatHandler;
import com.px.gameserver.model.actor.Player;
import com.px.gameserver.model.group.CommandChannel;
import com.px.gameserver.model.group.Party;
import com.px.gameserver.network.serverpackets.CreatureSay;

public class ChatPartyRoomAll implements IChatHandler
{
	private static final int[] COMMAND_IDS =
	{
		16
	};
	
	@Override
	public void handleChat(int type, Player player, String target, String text)
	{
		final Party party = player.getParty();
		if (party == null || !party.isLeader(player))
			return;
		
		final CommandChannel channel = party.getCommandChannel();
		if (channel == null)
			return;
		
		channel.broadcastCreatureSay(new CreatureSay(player.getObjectId(), type, player.getName(), text), player);
	}
	
	@Override
	public int[] getChatTypeList()
	{
		return COMMAND_IDS;
	}
}