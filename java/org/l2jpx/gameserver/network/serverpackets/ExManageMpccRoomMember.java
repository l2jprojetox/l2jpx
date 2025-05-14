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
package org.l2jpx.gameserver.network.serverpackets;

import org.l2jpx.commons.network.WritableBuffer;
import org.l2jpx.gameserver.enums.ExManagePartyRoomMemberType;
import org.l2jpx.gameserver.enums.MatchingMemberType;
import org.l2jpx.gameserver.instancemanager.MapRegionManager;
import org.l2jpx.gameserver.model.actor.Player;
import org.l2jpx.gameserver.model.matching.CommandChannelMatchingRoom;
import org.l2jpx.gameserver.network.GameClient;
import org.l2jpx.gameserver.network.ServerPackets;

/**
 * @author Gnacik
 */
public class ExManageMpccRoomMember extends ServerPacket
{
	private final Player _player;
	private final MatchingMemberType _memberType;
	private final ExManagePartyRoomMemberType _type;
	
	public ExManageMpccRoomMember(Player player, CommandChannelMatchingRoom room, ExManagePartyRoomMemberType mode)
	{
		_player = player;
		_memberType = room.getMemberType(player);
		_type = mode;
	}
	
	@Override
	public void writeImpl(GameClient client, WritableBuffer buffer)
	{
		ServerPackets.EX_MANAGE_PARTY_ROOM_MEMBER.writeId(this, buffer);
		buffer.writeInt(_type.ordinal());
		buffer.writeInt(_player.getObjectId());
		buffer.writeString(_player.getName());
		buffer.writeInt(_player.getClassId().getId());
		buffer.writeInt(_player.getLevel());
		buffer.writeInt(MapRegionManager.getInstance().getBBs(_player.getLocation()));
		buffer.writeInt(_memberType.ordinal());
	}
}
