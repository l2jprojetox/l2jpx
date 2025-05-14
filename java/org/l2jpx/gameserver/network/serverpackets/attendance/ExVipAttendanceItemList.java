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
package org.l2jpx.gameserver.network.serverpackets.attendance;

import org.l2jpx.commons.network.WritableBuffer;
import org.l2jpx.gameserver.data.xml.AttendanceRewardData;
import org.l2jpx.gameserver.model.actor.Player;
import org.l2jpx.gameserver.model.holders.AttendanceInfoHolder;
import org.l2jpx.gameserver.model.holders.ItemHolder;
import org.l2jpx.gameserver.network.GameClient;
import org.l2jpx.gameserver.network.ServerPackets;
import org.l2jpx.gameserver.network.serverpackets.ServerPacket;

/**
 * @author Mobius
 */
public class ExVipAttendanceItemList extends ServerPacket
{
	boolean _available;
	int _index;
	
	public ExVipAttendanceItemList(Player player)
	{
		final AttendanceInfoHolder attendanceInfo = player.getAttendanceInfo();
		_available = attendanceInfo.isRewardAvailable();
		_index = attendanceInfo.getRewardIndex();
	}
	
	@Override
	public void writeImpl(GameClient client, WritableBuffer buffer)
	{
		ServerPackets.EX_VIP_ATTENDANCE_ITEM_LIST.writeId(this, buffer);
		buffer.writeByte(_available ? _index + 1 : _index); // index to receive?
		buffer.writeByte(_index); // last received index?
		buffer.writeInt(0);
		buffer.writeInt(0);
		buffer.writeByte(1);
		buffer.writeByte(_available); // player can receive reward today?
		buffer.writeByte(250);
		buffer.writeByte(AttendanceRewardData.getInstance().getRewardsCount()); // reward size
		int rewardCounter = 0;
		for (ItemHolder reward : AttendanceRewardData.getInstance().getRewards())
		{
			rewardCounter++;
			buffer.writeInt(reward.getId());
			buffer.writeLong(reward.getCount());
			buffer.writeByte(1); // is unknown?
			buffer.writeByte((rewardCounter % 7) == 0); // is last in row?
		}
		buffer.writeByte(0);
		buffer.writeInt(0);
	}
}
