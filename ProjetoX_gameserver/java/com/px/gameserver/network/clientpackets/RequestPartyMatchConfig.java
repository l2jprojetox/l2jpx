package com.px.gameserver.network.clientpackets;

import com.px.gameserver.model.actor.Player;
import com.px.gameserver.model.partymatching.PartyMatchRoom;
import com.px.gameserver.model.partymatching.PartyMatchRoomList;
import com.px.gameserver.model.partymatching.PartyMatchWaitingList;
import com.px.gameserver.network.SystemMessageId;
import com.px.gameserver.network.serverpackets.ActionFailed;
import com.px.gameserver.network.serverpackets.ExPartyRoomMember;
import com.px.gameserver.network.serverpackets.PartyMatchDetail;
import com.px.gameserver.network.serverpackets.PartyMatchList;

public final class RequestPartyMatchConfig extends L2GameClientPacket
{
	private int _auto, _loc, _lvl;
	
	@Override
	protected void readImpl()
	{
		_auto = readD();
		_loc = readD();
		_lvl = readD();
	}
	
	@Override
	protected void runImpl()
	{
		final Player activeChar = getClient().getPlayer();
		if (activeChar == null)
			return;
		
		if (!activeChar.isInPartyMatchRoom() && activeChar.getParty() != null && activeChar.getParty().getLeader() != activeChar)
		{
			activeChar.sendPacket(SystemMessageId.CANT_VIEW_PARTY_ROOMS);
			activeChar.sendPacket(ActionFailed.STATIC_PACKET);
			return;
		}
		
		if (activeChar.isInPartyMatchRoom())
		{
			// If Player is in Room show him room, not list
			PartyMatchRoomList list = PartyMatchRoomList.getInstance();
			if (list == null)
				return;
			
			PartyMatchRoom room = list.getPlayerRoom(activeChar);
			if (room == null)
				return;
			
			activeChar.sendPacket(new PartyMatchDetail(room));
			activeChar.sendPacket(new ExPartyRoomMember(room, 2));
			
			activeChar.setPartyRoom(room.getId());
			activeChar.broadcastUserInfo();
		}
		else
		{
			// Add to waiting list
			PartyMatchWaitingList.getInstance().addPlayer(activeChar);
			
			// Send Room list
			activeChar.sendPacket(new PartyMatchList(activeChar, _auto, _loc, _lvl));
		}
	}
}