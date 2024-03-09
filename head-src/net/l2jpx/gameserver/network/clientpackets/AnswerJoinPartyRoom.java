package net.l2jpx.gameserver.network.clientpackets;

import net.l2jpx.gameserver.model.L2World;
import net.l2jpx.gameserver.model.PartyMatchRoom;
import net.l2jpx.gameserver.model.PartyMatchRoomList;
import net.l2jpx.gameserver.model.PartyMatchWaitingList;
import net.l2jpx.gameserver.model.actor.instance.L2PcInstance;
import net.l2jpx.gameserver.network.SystemMessageId;
import net.l2jpx.gameserver.network.serverpackets.ExManagePartyRoomMember;
import net.l2jpx.gameserver.network.serverpackets.ExPartyRoomMember;
import net.l2jpx.gameserver.network.serverpackets.PartyMatchDetail;
import net.l2jpx.gameserver.network.serverpackets.SystemMessage;

public final class AnswerJoinPartyRoom extends L2GameClientPacket
{
	private int answer; // 1 or 0
	
	@Override
	protected void readImpl()
	{
		answer = readD();
	}
	
	@Override
	protected void runImpl()
	{
		final L2PcInstance player = getClient().getActiveChar();
		if (player == null)
		{
			return;
		}
		
		final L2PcInstance partner = player.getActiveRequester();
		if (partner == null)
		{
			// Partner hasn't be found, cancel the invitation
			player.sendPacket(new SystemMessage(SystemMessageId.THAT_PLAYER_IS_NOT_ONLINE));
			player.setActiveRequester(null);
			return;
		}
		else if (L2World.getInstance().getPlayer(partner.getObjectId()) == null)
		{
			// Partner hasn't be found, cancel the invitation
			player.sendPacket(new SystemMessage(SystemMessageId.THAT_PLAYER_IS_NOT_ONLINE));
			player.setActiveRequester(null);
			return;
		}
		
		// If answer is positive, join the requester's PartyRoom.
		if (answer == 1 && !partner.isRequestExpired())
		{
			final PartyMatchRoom room = PartyMatchRoomList.getInstance().getRoom(partner.getPartyRoom());
			if (room == null)
			{
				return;
			}
			
			if ((player.getLevel() >= room.getMinLvl()) && (player.getLevel() <= room.getMaxLvl()))
			{
				// Remove from waiting list
				PartyMatchWaitingList.getInstance().removePlayer(player);
				
				player.setPartyRoom(partner.getPartyRoom());
				
				player.sendPacket(new PartyMatchDetail(player, room));
				player.sendPacket(new ExPartyRoomMember(player, room, 0));
				
				for (final L2PcInstance member : room.getPartyMembers())
				{
					if (member == null)
					{
						continue;
					}
					
					member.sendPacket(new ExManagePartyRoomMember(player, room, 0));
					member.sendPacket(new SystemMessage(SystemMessageId.S1_ENTERED_PARTY_ROOM).addString(player.getName()));
				}
				room.addMember(player);
				
				// Info Broadcast
				player.broadcastUserInfo();
			}
			else
			{
				player.sendPacket(new SystemMessage(SystemMessageId.CANT_ENTER_PARTY_ROOM));
			}
		}
		// Else, send a message to requester.
		else
		{
			partner.sendPacket(new SystemMessage(SystemMessageId.PARTY_MATCHING_REQUEST_NO_RESPONSE));
		}
		
		// reset transaction timers
		player.setActiveRequester(null);
		partner.onTransactionResponse();
	}
	
	@Override
	public String getType()
	{
		return "[C] D0:15 AnswerJoinPartyRoom";
	}
}