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
package org.l2jpx.gameserver.network.serverpackets.dailymission;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Collections;

import org.l2jpx.commons.network.WritableBuffer;
import org.l2jpx.commons.time.SchedulingPattern;
import org.l2jpx.gameserver.data.xml.DailyMissionData;
import org.l2jpx.gameserver.model.DailyMissionDataHolder;
import org.l2jpx.gameserver.model.actor.Player;
import org.l2jpx.gameserver.network.GameClient;
import org.l2jpx.gameserver.network.ServerPackets;
import org.l2jpx.gameserver.network.serverpackets.ServerPacket;

/**
 * @author Sdw
 */
public class ExOneDayReceiveRewardList extends ServerPacket
{
	private static final SchedulingPattern DAILY_REUSE_PATTERN = new SchedulingPattern("30 6 * * *");
	private static final SchedulingPattern WEEKLY_REUSE_PATTERN = new SchedulingPattern("30 6 * * 1");
	private static final SchedulingPattern MONTHLY_REUSE_PATTERN = new SchedulingPattern("30 6 1 * *");
	
	private final Player _player;
	private final Collection<DailyMissionDataHolder> _rewards;
	private final int _dayRemainTime;
	private final int _weekRemainTime;
	private final int _monthRemainTime;
	
	public ExOneDayReceiveRewardList(Player player, boolean sendRewards)
	{
		_player = player;
		_rewards = sendRewards ? DailyMissionData.getInstance().getDailyMissionData(player) : Collections.emptyList();
		_dayRemainTime = (int) ((DAILY_REUSE_PATTERN.next(System.currentTimeMillis()) - System.currentTimeMillis()) / 1000);
		_weekRemainTime = (int) ((WEEKLY_REUSE_PATTERN.next(System.currentTimeMillis()) - System.currentTimeMillis()) / 1000);
		_monthRemainTime = (int) ((MONTHLY_REUSE_PATTERN.next(System.currentTimeMillis()) - System.currentTimeMillis()) / 1000);
	}
	
	@Override
	public void writeImpl(GameClient client, WritableBuffer buffer)
	{
		if (!DailyMissionData.getInstance().isAvailable())
		{
			return;
		}
		
		ServerPackets.EX_ONE_DAY_RECEIVE_REWARD_LIST.writeId(this, buffer);
		buffer.writeInt(_dayRemainTime);
		buffer.writeInt(_weekRemainTime);
		buffer.writeInt(_monthRemainTime);
		buffer.writeByte(0x17);
		buffer.writeInt(_player.getClassId().getId());
		buffer.writeInt(LocalDate.now().getDayOfWeek().ordinal()); // Day of week
		buffer.writeInt(_rewards.size());
		for (DailyMissionDataHolder reward : _rewards)
		{
			buffer.writeShort(reward.getId());
			buffer.writeByte(reward.getStatus(_player));
			buffer.writeByte(reward.getRequiredCompletions() > 1);
			buffer.writeInt(Math.min(reward.getProgress(_player), _player.getLevel()));
			buffer.writeInt(reward.getRequiredCompletions());
		}
	}
}
