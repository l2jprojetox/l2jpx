package com.px.gameserver.network.clientpackets;

import com.px.gameserver.enums.MessageType;
import com.px.gameserver.model.actor.Player;
import com.px.gameserver.model.group.Party;
import com.px.gameserver.model.partymatching.PartyMatchRoom;
import com.px.gameserver.model.partymatching.PartyMatchRoomList;
import com.px.gameserver.network.serverpackets.ExClosePartyRoom;
import com.px.gameserver.network.serverpackets.ExPartyRoomMember;
import com.px.gameserver.network.serverpackets.PartyMatchDetail;

public final class RequestWithdrawParty extends L2GameClientPacket
{
	@Override
	protected void readImpl()
	{
	}
	
	@Override
	protected void runImpl()
	{
		final Player player = getClient().getPlayer();
		if (player == null)
			return;
		
		final Party party = player.getParty();
		if (party == null)
			return;
		
		party.removePartyMember(player, MessageType.LEFT);
		
		if (player.isInPartyMatchRoom())
		{
			PartyMatchRoom room = PartyMatchRoomList.getInstance().getPlayerRoom(player);
			if (room != null)
			{
				player.sendPacket(new PartyMatchDetail(room));
				player.sendPacket(new ExPartyRoomMember(room, 0));
				player.sendPacket(ExClosePartyRoom.STATIC_PACKET);
				
				room.deleteMember(player);
			}
			player.setPartyRoom(0);
			player.broadcastUserInfo();
		}
	}
}