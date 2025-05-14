/*
 * This file is part of the L2J Mobius project.
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.l2jpx.gameserver.model.matching;

import org.l2jpx.gameserver.enums.MatchingMemberType;
import org.l2jpx.gameserver.enums.MatchingRoomType;
import org.l2jpx.gameserver.enums.PartyMatchingRoomLevelType;
import org.l2jpx.gameserver.enums.UserInfoType;
import org.l2jpx.gameserver.instancemanager.MatchingRoomManager;
import org.l2jpx.gameserver.model.Party;
import org.l2jpx.gameserver.model.actor.Player;
import org.l2jpx.gameserver.network.SystemMessageId;
import org.l2jpx.gameserver.network.serverpackets.ExClosePartyRoom;
import org.l2jpx.gameserver.network.serverpackets.ExPartyRoomMember;
import org.l2jpx.gameserver.network.serverpackets.ListPartyWaiting;
import org.l2jpx.gameserver.network.serverpackets.PartyRoomInfo;
import org.l2jpx.gameserver.network.serverpackets.SystemMessage;

/**
 * @author Sdw
 */
public class PartyMatchingRoom extends MatchingRoom
{
	public PartyMatchingRoom(String title, int loot, int minLevel, int maxLevel, int maxmem, Player leader)
	{
		super(title, loot, minLevel, maxLevel, maxmem, leader);
	}
	
	@Override
	protected void onRoomCreation(Player player)
	{
		player.broadcastUserInfo(UserInfoType.CLAN);
		player.sendPacket(new ListPartyWaiting(PartyMatchingRoomLevelType.ALL, -1, 1, player.getLevel()));
		player.sendPacket(SystemMessageId.YOU_HAVE_CREATED_A_PARTY_ROOM);
	}
	
	@Override
	protected void notifyInvalidCondition(Player player)
	{
		player.sendPacket(SystemMessageId.YOU_DO_NOT_MEET_THE_REQUIREMENTS_TO_ENTER_THAT_PARTY_ROOM);
	}
	
	@Override
	protected void notifyNewMember(Player player)
	{
		// Update other players
		for (Player member : getMembers())
		{
			if (member != player)
			{
				member.sendPacket(new ExPartyRoomMember(member, this));
			}
		}
		
		// Send SystemMessage to other players
		final SystemMessage sm = new SystemMessage(SystemMessageId.C1_HAS_ENTERED_THE_PARTY_ROOM);
		sm.addPcName(player);
		for (Player member : getMembers())
		{
			if (member != player)
			{
				member.sendPacket(sm);
			}
		}
		
		// Update new player
		player.sendPacket(new PartyRoomInfo(this));
		player.sendPacket(new ExPartyRoomMember(player, this));
	}
	
	@Override
	protected void notifyRemovedMember(Player player, boolean kicked, boolean leaderChanged)
	{
		final SystemMessage sm = new SystemMessage(kicked ? SystemMessageId.C1_HAS_BEEN_KICKED_FROM_THE_PARTY_ROOM : SystemMessageId.C1_HAS_LEFT_THE_PARTY_ROOM);
		sm.addPcName(player);
		
		getMembers().forEach(p ->
		{
			p.sendPacket(new PartyRoomInfo(this));
			p.sendPacket(new ExPartyRoomMember(player, this));
			p.sendPacket(sm);
			p.sendPacket(SystemMessageId.THE_LEADER_OF_THE_PARTY_ROOM_HAS_CHANGED);
		});
		
		player.sendPacket(new SystemMessage(kicked ? SystemMessageId.YOU_HAVE_BEEN_OUSTED_FROM_THE_PARTY_ROOM : SystemMessageId.YOU_HAVE_EXITED_THE_PARTY_ROOM));
		player.sendPacket(ExClosePartyRoom.STATIC_PACKET);
	}
	
	@Override
	public void disbandRoom()
	{
		getMembers().forEach(p ->
		{
			p.sendPacket(SystemMessageId.THE_PARTY_ROOM_HAS_BEEN_DISBANDED);
			p.sendPacket(ExClosePartyRoom.STATIC_PACKET);
			p.setMatchingRoom(null);
			p.broadcastUserInfo(UserInfoType.CLAN);
			MatchingRoomManager.getInstance().addToWaitingList(p);
		});
		
		getMembers().clear();
		
		MatchingRoomManager.getInstance().removeMatchingRoom(this);
	}
	
	@Override
	public MatchingRoomType getRoomType()
	{
		return MatchingRoomType.PARTY;
	}
	
	@Override
	public MatchingMemberType getMemberType(Player player)
	{
		if (isLeader(player))
		{
			return MatchingMemberType.PARTY_LEADER;
		}
		
		final Party leaderParty = getLeader().getParty();
		final Party playerParty = player.getParty();
		if ((leaderParty != null) && (playerParty != null) && (playerParty == leaderParty))
		{
			return MatchingMemberType.PARTY_MEMBER;
		}
		
		return MatchingMemberType.WAITING_PLAYER;
	}
}
