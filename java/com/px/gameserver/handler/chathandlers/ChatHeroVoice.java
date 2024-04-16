package com.px.gameserver.handler.chathandlers;

import com.px.gameserver.enums.FloodProtector;
import com.px.gameserver.enums.SayType;
import com.px.gameserver.handler.IChatHandler;
import com.px.gameserver.model.World;
import com.px.gameserver.model.actor.Player;
import com.px.gameserver.network.serverpackets.CreatureSay;

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