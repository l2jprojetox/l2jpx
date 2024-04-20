package com.l2jpx.gameserver.network.clientpackets;

import com.l2jpx.gameserver.data.manager.PartyMatchRoomManager;
import com.l2jpx.gameserver.enums.MessageType;
import com.l2jpx.gameserver.model.actor.Player;
import com.l2jpx.gameserver.model.group.Party;
import com.l2jpx.gameserver.model.group.PartyMatchRoom;
import com.l2jpx.gameserver.network.serverpackets.ExPartyRoomMember;
import com.l2jpx.gameserver.network.serverpackets.PartyMatchDetail;

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
			final PartyMatchRoom room = PartyMatchRoomManager.getInstance().getRoom(player.getPartyRoom());
			if (room != null)
			{
				player.sendPacket(new PartyMatchDetail(room));
				player.sendPacket(new ExPartyRoomMember(room, 0));
				
				// Remove PartyMatchRoom member.
				room.removeMember(player);
			}
		}
	}
}