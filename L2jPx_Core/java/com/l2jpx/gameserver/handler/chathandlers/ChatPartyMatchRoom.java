package com.l2jpx.gameserver.handler.chathandlers;

import com.l2jpx.gameserver.data.manager.PartyMatchRoomManager;
import com.l2jpx.gameserver.enums.SayType;
import com.l2jpx.gameserver.handler.IChatHandler;
import com.l2jpx.gameserver.model.actor.Player;
import com.l2jpx.gameserver.model.group.PartyMatchRoom;
import com.l2jpx.gameserver.network.serverpackets.CreatureSay;

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