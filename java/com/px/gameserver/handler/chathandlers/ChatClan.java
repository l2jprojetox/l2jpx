package com.px.gameserver.handler.chathandlers;

import com.px.gameserver.enums.SayType;
import com.px.gameserver.handler.IChatHandler;
import com.px.gameserver.model.actor.Player;
import com.px.gameserver.model.pledge.Clan;
import com.px.gameserver.network.serverpackets.CreatureSay;

public class ChatClan implements IChatHandler
{
	private static final SayType[] COMMAND_IDS =
	{
		SayType.CLAN
	};
	
	@Override
	public void handleChat(SayType type, Player player, String target, String text)
	{
		final Clan clan = player.getClan();
		if (clan == null)
			return;
		
		clan.broadcastToMembers(new CreatureSay(player, type, text));
	}
	
	@Override
	public SayType[] getChatTypeList()
	{
		return COMMAND_IDS;
	}
}