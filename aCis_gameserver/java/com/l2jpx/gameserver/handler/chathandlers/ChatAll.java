package com.l2jpx.gameserver.handler.chathandlers;

import com.l2jpx.gameserver.enums.FloodProtector;
import com.l2jpx.gameserver.enums.SayType;
import com.l2jpx.gameserver.handler.IChatHandler;
import com.l2jpx.gameserver.model.actor.Player;
import com.l2jpx.gameserver.network.serverpackets.CreatureSay;

public class ChatAll implements IChatHandler
{
	private static final SayType[] COMMAND_IDS =
	{
		SayType.ALL
	};
	
	@Override
	public void handleChat(SayType type, Player player, String target, String text)
	{
		if (!player.getClient().performAction(FloodProtector.GLOBAL_CHAT))
			return;
		
		final CreatureSay cs = new CreatureSay(player, type, text);
		for (Player knownPlayer : player.getKnownTypeInRadius(Player.class, 1250))
			knownPlayer.sendPacket(cs);
		
		player.sendPacket(cs);
	}
	
	@Override
	public SayType[] getChatTypeList()
	{
		return COMMAND_IDS;
	}
}