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

import org.l2jpx.gameserver.enums.PartyMatchingRoomLevelType;
import org.l2jpx.gameserver.instancemanager.MatchingRoomManager;
import org.l2jpx.gameserver.model.CommandChannel;
import org.l2jpx.gameserver.model.Party;
import org.l2jpx.gameserver.model.actor.Player;
import org.l2jpx.gameserver.model.matching.CommandChannelMatchingRoom;
import org.l2jpx.gameserver.network.SystemMessageId;
import org.l2jpx.gameserver.network.serverpackets.ListPartyWaiting;

public class RequestPartyMatchConfig extends ClientPacket
{
	private int _page;
	private int _location;
	private PartyMatchingRoomLevelType _type;
	
	@Override
	protected void readImpl()
	{
		_page = readInt();
		_location = readInt();
		_type = readInt() == 0 ? PartyMatchingRoomLevelType.MY_LEVEL_RANGE : PartyMatchingRoomLevelType.ALL;
	}
	
	@Override
	protected void runImpl()
	{
		final Player player = getPlayer();
		if (player == null)
		{
			return;
		}
		
		final Party party = player.getParty();
		final CommandChannel cc = party == null ? null : party.getCommandChannel();
		if ((party != null) && (cc != null) && (cc.getLeader() == player))
		{
			if (player.getMatchingRoom() == null)
			{
				player.setMatchingRoom(new CommandChannelMatchingRoom(player.getName(), party.getDistributionType().ordinal(), 1, player.getLevel(), 50, player));
			}
		}
		else if ((cc != null) && (cc.getLeader() != player))
		{
			player.sendPacket(SystemMessageId.THE_COMMAND_CHANNEL_AFFILIATED_PARTY_S_PARTY_MEMBER_CANNOT_USE_THE_MATCHING_SCREEN);
		}
		else if ((party != null) && (party.getLeader() != player))
		{
			player.sendPacket(SystemMessageId.THE_LIST_OF_PARTY_ROOMS_CAN_ONLY_BE_VIEWED_BY_A_PERSON_WHO_IS_NOT_PART_OF_A_PARTY);
		}
		else
		{
			MatchingRoomManager.getInstance().addToWaitingList(player);
			player.sendPacket(new ListPartyWaiting(_type, _location, _page, player.getLevel()));
		}
	}
}
