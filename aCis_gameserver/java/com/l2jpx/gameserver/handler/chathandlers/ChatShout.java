package com.l2jpx.gameserver.handler.chathandlers;

import com.l2jpx.gameserver.data.xml.MapRegionData;
import com.l2jpx.gameserver.enums.FloodProtector;
import com.l2jpx.gameserver.enums.SayType;
import com.l2jpx.gameserver.handler.IChatHandler;
import com.l2jpx.gameserver.model.World;
import com.l2jpx.gameserver.model.actor.Player;
import com.l2jpx.gameserver.network.serverpackets.CreatureSay;

public class ChatShout implements IChatHandler
{
	private static final SayType[] COMMAND_IDS =
	{
		SayType.SHOUT
	};
	
	@Override
	public void handleChat(SayType type, Player player, String target, String text)
	{
		if (!player.getClient().performAction(FloodProtector.GLOBAL_CHAT))
			return;
		
		final CreatureSay cs = new CreatureSay(player, type, text);
		final int region = MapRegionData.getInstance().getMapRegion(player.getX(), player.getY());
		
		for (Player worldPlayer : World.getInstance().getPlayers())
		{
			if (region == MapRegionData.getInstance().getMapRegion(worldPlayer.getX(), worldPlayer.getY()))
				worldPlayer.sendPacket(cs);
		}
	}
	
	@Override
	public SayType[] getChatTypeList()
	{
		return COMMAND_IDS;
	}
}