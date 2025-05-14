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
package org.l2jpx.gameserver.network.clientpackets;

import org.l2jpx.gameserver.enums.MatchingRoomType;
import org.l2jpx.gameserver.model.Party;
import org.l2jpx.gameserver.model.World;
import org.l2jpx.gameserver.model.actor.Player;
import org.l2jpx.gameserver.model.matching.MatchingRoom;
import org.l2jpx.gameserver.network.SystemMessageId;

/**
 * format (ch) d
 * @author -Wooden-
 */
public class RequestOustFromPartyRoom extends ClientPacket
{
	private int _objectId;
	
	@Override
	protected void readImpl()
	{
		_objectId = readInt();
	}
	
	@Override
	protected void runImpl()
	{
		final Player player = getPlayer();
		if (player == null)
		{
			return;
		}
		
		final Player member = World.getInstance().getPlayer(_objectId);
		if (member == null)
		{
			return;
		}
		
		final MatchingRoom room = player.getMatchingRoom();
		if ((room == null) || (room.getRoomType() != MatchingRoomType.PARTY) || (room.getLeader() != player) || (player == member))
		{
			return;
		}
		
		final Party playerParty = player.getParty();
		final Party memberParty = player.getParty();
		if ((playerParty != null) && (memberParty != null) && (playerParty.getLeaderObjectId() == memberParty.getLeaderObjectId()))
		{
			player.sendPacket(SystemMessageId.YOU_CANNOT_DISMISS_A_PARTY_MEMBER_BY_FORCE);
		}
		else
		{
			room.deleteMember(member, true);
		}
	}
}
