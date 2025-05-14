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
import org.l2jpx.gameserver.model.actor.Player;
import org.l2jpx.gameserver.model.matching.CommandChannelMatchingRoom;
import org.l2jpx.gameserver.model.matching.MatchingRoom;
import org.l2jpx.gameserver.network.SystemMessageId;
import org.l2jpx.gameserver.network.serverpackets.ExMPCCRoomInfo;

/**
 * @author Sdw
 */
public class RequestExManageMpccRoom extends ClientPacket
{
	private int _roomId;
	private int _maxMembers;
	private int _minLevel;
	private int _maxLevel;
	private String _title;
	
	@Override
	protected void readImpl()
	{
		_roomId = readInt();
		_maxMembers = readInt();
		_minLevel = readInt();
		_maxLevel = readInt();
		readInt(); // Party Distrubtion Type
		_title = readString();
	}
	
	@Override
	protected void runImpl()
	{
		final Player player = getPlayer();
		if (player == null)
		{
			return;
		}
		
		final MatchingRoom room = player.getMatchingRoom();
		if ((room == null) || (room.getId() != _roomId) || (room.getRoomType() != MatchingRoomType.COMMAND_CHANNEL) || (room.getLeader() != player))
		{
			return;
		}
		
		room.setTitle(_title);
		room.setMaxMembers(_maxMembers);
		room.setMinLevel(_minLevel);
		room.setMaxLevel(_maxLevel);
		
		room.getMembers().forEach(p -> p.sendPacket(new ExMPCCRoomInfo((CommandChannelMatchingRoom) room)));
		player.sendPacket(SystemMessageId.THE_COMMAND_CHANNEL_MATCHING_ROOM_INFORMATION_WAS_EDITED);
	}
}
