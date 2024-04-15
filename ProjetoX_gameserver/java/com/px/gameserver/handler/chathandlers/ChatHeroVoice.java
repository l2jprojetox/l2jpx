package com.px.gameserver.handler.chathandlers;

import com.px.gameserver.handler.IChatHandler;
import com.px.gameserver.model.World;
import com.px.gameserver.model.actor.Player;
import com.px.gameserver.network.FloodProtectors;
import com.px.gameserver.network.FloodProtectors.Action;
import com.px.gameserver.network.serverpackets.CreatureSay;

public class ChatHeroVoice implements IChatHandler
{
	private static final int[] COMMAND_IDS =
	{
		17
	};
	
	@Override
	public void handleChat(int type, Player activeChar, String target, String text)
	{
		if (!activeChar.isHero())
			return;
		
		if (!FloodProtectors.performAction(activeChar.getClient(), Action.HERO_VOICE))
			return;
		
		final CreatureSay cs = new CreatureSay(activeChar.getObjectId(), type, activeChar.getName(), text);
		for (Player player : World.getInstance().getPlayers())
			player.sendPacket(cs);
	}
	
	@Override
	public int[] getChatTypeList()
	{
		return COMMAND_IDS;
	}
}