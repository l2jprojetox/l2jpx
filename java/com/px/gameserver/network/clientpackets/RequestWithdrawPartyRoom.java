package com.px.gameserver.network.clientpackets;

import com.px.gameserver.data.manager.PartyMatchRoomManager;
import com.px.gameserver.model.actor.Player;
import com.px.gameserver.model.group.PartyMatchRoom;
import com.px.gameserver.network.SystemMessageId;

public final class RequestWithdrawPartyRoom extends L2GameClientPacket
{
	private int _roomId;
	@SuppressWarnings("unused")
	private int _unk1;
	
	@Override
	protected void readImpl()
	{
		_roomId = readD();
		_unk1 = readD();
	}
	
	@Override
	protected void runImpl()
	{
		final Player player = getClient().getPlayer();
		if (player == null)
			return;
		
		final PartyMatchRoom room = PartyMatchRoomManager.getInstance().getRoom(_roomId);
		if (room == null)
			return;
		
		if (player.isInParty() && room.getLeader().isInParty() && player.getParty().getLeaderObjectId() == room.getLeader().getParty().getLeaderObjectId())
		{
			// If user is in party with Room Owner is not removed from Room
		}
		else
		{
			// Remove PartyMatchRoom member.
			room.removeMember(player);
			
			player.sendPacket(SystemMessageId.PARTY_ROOM_EXITED);
		}
	}
}