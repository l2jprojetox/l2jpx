package com.px.gameserver.handler.chathandlers;

import com.px.gameserver.enums.FloodProtector;
import com.px.gameserver.enums.SayType;
import com.px.gameserver.handler.IChatHandler;
import com.px.gameserver.model.World;
import com.px.gameserver.model.actor.Player;
import com.px.gameserver.network.serverpackets.CreatureSay;

public class ChatTrade implements IChatHandler
{
	private static final SayType[] COMMAND_IDS =
	{
		SayType.TRADE
	};
	
	@Override
	public void handleChat(SayType type, Player player, String target, String text)
	{
		if (!player.getClient().performAction(FloodProtector.TRADE_CHAT))
			return;
		
		World.broadcastToSameRegion(player, new CreatureSay(player, type, text));
	}
	
	@Override
	public SayType[] getChatTypeList()
	{
		return COMMAND_IDS;
	}
}