package com.px.gameserver.handler.chathandlers;

import com.px.gameserver.enums.SayType;
import com.px.gameserver.handler.IChatHandler;
import com.px.gameserver.model.actor.Player;
import com.px.gameserver.model.group.Party;
import com.px.gameserver.network.serverpackets.CreatureSay;

public class ChatParty implements IChatHandler
{
	private static final SayType[] COMMAND_IDS =
	{
		SayType.PARTY
	};
	
	@Override
	public void handleChat(SayType type, Player player, String target, String text)
	{
		final Party party = player.getParty();
		if (party == null)
			return;
		
		party.broadcastPacket(new CreatureSay(player, type, text));
	}
	
	@Override
	public SayType[] getChatTypeList()
	{
		return COMMAND_IDS;
	}
}