package com.px.gameserver.handler.chathandlers;

import com.px.gameserver.data.manager.PartyMatchRoomManager;
import com.px.gameserver.enums.SayType;
import com.px.gameserver.handler.IChatHandler;
import com.px.gameserver.model.actor.Player;
import com.px.gameserver.model.group.PartyMatchRoom;
import com.px.gameserver.network.serverpackets.CreatureSay;

public class ChatPartyMatchRoom implements IChatHandler
{
	private static final SayType[] COMMAND_IDS =
	{
		SayType.PARTYMATCH_ROOM
	};
	
	@Override
	public void handleChat(SayType type, Player player, String target, String text)
	{
		if (!player.isInPartyMatchRoom())
			return;
		
		final PartyMatchRoom room = PartyMatchRoomManager.getInstance().getRoom(player.getPartyRoom());
		if (room == null)
			return;
		
		room.broadcastPacket(new CreatureSay(player, type, text));
	}
	
	@Override
	public SayType[] getChatTypeList()
	{
		return COMMAND_IDS;
	}
}