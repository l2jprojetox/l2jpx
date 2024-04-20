package com.l2jpx.gameserver.handler.chathandlers;

import com.l2jpx.gameserver.enums.FloodProtector;
import com.l2jpx.gameserver.enums.SayType;
import com.l2jpx.gameserver.handler.IChatHandler;
import com.l2jpx.gameserver.model.World;
import com.l2jpx.gameserver.model.actor.Player;
import com.l2jpx.gameserver.network.serverpackets.CreatureSay;

public class ChatHeroVoice implements IChatHandler
{
	private static final SayType[] COMMAND_IDS =
	{
		SayType.HERO_VOICE
	};
	
	@Override
	public void handleChat(SayType type, Player player, String target, String text)
	{
		if (!player.isHero())
			return;
		
		if (!player.getClient().performAction(FloodProtector.HERO_VOICE))
			return;
		
		World.toAllOnlinePlayers(new CreatureSay(player, type, text));
	}
	
	@Override
	public SayType[] getChatTypeList()
	{
		return COMMAND_IDS;
	}
}