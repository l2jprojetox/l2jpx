package com.px.gameserver.network.clientpackets;

import com.px.gameserver.model.World;
import com.px.gameserver.model.actor.Player;
import com.px.gameserver.model.partymatching.PartyMatchRoom;
import com.px.gameserver.model.partymatching.PartyMatchRoomList;
import com.px.gameserver.model.partymatching.PartyMatchWaitingList;
import com.px.gameserver.network.SystemMessageId;
import com.px.gameserver.network.serverpackets.ExManagePartyRoomMember;
import com.px.gameserver.network.serverpackets.ExPartyRoomMember;
import com.px.gameserver.network.serverpackets.PartyMatchDetail;
import com.px.gameserver.network.serverpackets.SystemMessage;

/**
 * Format: (ch) d
 * @author -Wooden-
 */
public final class AnswerJoinPartyRoom extends L2GameClientPacket
{
	private int _answer; // 1 or 0
	
	@Override
	protected void readImpl()
	{
		_answer = readD();
	}
	
	@Override
	protected void runImpl()
	{
		final Player player = getClient().getPlayer();
		if (player == null)
			return;
		
		final Player partner = player.getActiveRequester();
		if (partner == null || World.getInstance().getPlayer(partner.getObjectId()) == null)
		{
			// Partner hasn't be found, cancel the invitation
			player.sendPacket(SystemMessageId.TARGET_IS_NOT_FOUND_IN_THE_GAME);
			player.setActiveRequester(null);
			return;
		}
		
		// If answer is positive, join the requester's PartyRoom.
		if (_answer == 1 && !partner.isRequestExpired())
		{
			PartyMatchRoom _room = PartyMatchRoomList.getInstance().getRoom(partner.getPartyRoom());
			if (_room == null)
				return;
			
			if ((player.getLevel() >= _room.getMinLvl()) && (player.getLevel() <= _room.getMaxLvl()))
			{
				// Remove from waiting list
				PartyMatchWaitingList.getInstance().removePlayer(player);
				
				player.setPartyRoom(partner.getPartyRoom());
				
				player.sendPacket(new PartyMatchDetail(_room));
				player.sendPacket(new ExPartyRoomMember(_room, 0));
				
				for (Player _member : _room.getPartyMembers())
				{
					if (_member == null)
						continue;
					
					_member.sendPacket(new ExManagePartyRoomMember(player, _room, 0));
					_member.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.S1_ENTERED_PARTY_ROOM).addCharName(player));
				}
				_room.addMember(player);
				
				// Info Broadcast
				player.broadcastUserInfo();
			}
			else
				player.sendPacket(SystemMessageId.CANT_ENTER_PARTY_ROOM);
		}
		// Else, send a message to requester.
		else
			partner.sendPacket(SystemMessageId.PARTY_MATCHING_REQUEST_NO_RESPONSE);
		
		// reset transaction timers
		player.setActiveRequester(null);
		partner.onTransactionResponse();
	}
}