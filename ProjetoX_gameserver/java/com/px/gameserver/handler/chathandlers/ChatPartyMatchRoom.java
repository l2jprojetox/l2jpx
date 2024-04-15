package com.px.gameserver.handler.chathandlers;

import com.px.gameserver.handler.IChatHandler;
import com.px.gameserver.model.actor.Player;
import com.px.gameserver.model.partymatching.PartyMatchRoom;
import com.px.gameserver.model.partymatching.PartyMatchRoomList;
import com.px.gameserver.network.serverpackets.CreatureSay;

public class ChatPartyMatchRoom implements IChatHandler
{
	private static final int[] COMMAND_IDS =
	{
		14
	};
	
	@Override
	public void handleChat(int type, Player activeChar, String target, String text)
	{
		if (!activeChar.isInPartyMatchRoom())
			return;
		
		final PartyMatchRoom room = PartyMatchRoomList.getInstance().getPlayerRoom(activeChar);
		if (room == null)
			return;
		
		final CreatureSay cs = new CreatureSay(activeChar.getObjectId(), type, activeChar.getName(), text);
		for (Player member : room.getPartyMembers())
			member.sendPacket(cs);
	}
	
	@Override
	public int[] getChatTypeList()
	{
		return COMMAND_IDS;
	}
}