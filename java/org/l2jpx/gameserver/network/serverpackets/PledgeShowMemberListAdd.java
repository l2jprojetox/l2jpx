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
import org.l2jpx.gameserver.model.actor.Player;
import org.l2jpx.gameserver.model.clan.ClanMember;
import org.l2jpx.gameserver.network.GameClient;
import org.l2jpx.gameserver.network.ServerPackets;

public class PledgeShowMemberListAdd extends ServerPacket
{
	private final String _name;
	private final int _level;
	private final int _classId;
	private final int _isOnline;
	private final int _pledgeType;
	
	public PledgeShowMemberListAdd(Player player)
	{
		_name = player.getName();
		_level = player.getLevel();
		_classId = player.getClassId().getId();
		_isOnline = (player.isOnline() ? player.getObjectId() : 0);
		_pledgeType = player.getPledgeType();
	}
	
	public PledgeShowMemberListAdd(ClanMember cm)
	{
		_name = cm.getName();
		_level = cm.getLevel();
		_classId = cm.getClassId();
		_isOnline = (cm.isOnline() ? cm.getObjectId() : 0);
		_pledgeType = cm.getPledgeType();
	}
	
	@Override
	public void writeImpl(GameClient client, WritableBuffer buffer)
	{
		ServerPackets.PLEDGE_SHOW_MEMBER_LIST_ADD.writeId(this, buffer);
		buffer.writeString(_name);
		buffer.writeInt(_level);
		buffer.writeInt(_classId);
		buffer.writeInt(0);
		buffer.writeInt(1);
		buffer.writeInt(_isOnline); // 1 = online 0 = offline
		buffer.writeInt(_pledgeType);
	}
}
