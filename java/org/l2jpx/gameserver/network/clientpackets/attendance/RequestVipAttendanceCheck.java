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
package org.l2jpx.gameserver.network.clientpackets.attendance;

import org.l2jpx.Config;
import org.l2jpx.gameserver.data.xml.AttendanceRewardData;
import org.l2jpx.gameserver.data.xml.ItemData;
import org.l2jpx.gameserver.model.actor.Player;
import org.l2jpx.gameserver.model.holders.AttendanceInfoHolder;
import org.l2jpx.gameserver.model.holders.ItemHolder;
import org.l2jpx.gameserver.model.item.ItemTemplate;
import org.l2jpx.gameserver.network.SystemMessageId;
import org.l2jpx.gameserver.network.clientpackets.ClientPacket;
import org.l2jpx.gameserver.network.serverpackets.SystemMessage;
import org.l2jpx.gameserver.network.serverpackets.attendance.ExConfirmVipAttendanceCheck;

/**
 * @author Mobius
 */
public class RequestVipAttendanceCheck extends ClientPacket
{
	@Override
	protected void readImpl()
	{
	}
	
	@Override
	protected void runImpl()
	{
		final Player player = getPlayer();
		if (player == null)
		{
			return;
		}
		
		if (!Config.ENABLE_ATTENDANCE_REWARDS)
		{
			player.sendPacket(SystemMessageId.DUE_TO_A_SYSTEM_ERROR_THE_ATTENDANCE_REWARD_CANNOT_BE_RECEIVED_PLEASE_TRY_AGAIN_LATER_BY_GOING_TO_MENU_ATTENDANCE_CHECK);
			return;
		}
		
		if (Config.PREMIUM_ONLY_ATTENDANCE_REWARDS && !player.hasPremiumStatus())
		{
			player.sendPacket(SystemMessageId.YOUR_VIP_RANK_IS_TOO_LOW_TO_RECEIVE_THE_REWARD);
			return;
		}
		
		// Check login delay.
		if (player.getUptime() < (Config.ATTENDANCE_REWARD_DELAY * 60 * 1000))
		{
			final SystemMessage msg = new SystemMessage(SystemMessageId.YOU_CAN_REDEEM_YOUR_REWARD_S1_MINUTES_AFTER_LOGGING_IN_S2_MINUTES_LEFT);
			msg.addInt(Config.ATTENDANCE_REWARD_DELAY);
			msg.addInt(Config.ATTENDANCE_REWARD_DELAY - Math.toIntExact(player.getUptime() / 1000 / 60));
			player.sendPacket(msg);
			return;
		}
		
		final AttendanceInfoHolder attendanceInfo = player.getAttendanceInfo();
		final boolean isRewardAvailable = attendanceInfo.isRewardAvailable();
		final int rewardIndex = attendanceInfo.getRewardIndex();
		final ItemHolder reward = AttendanceRewardData.getInstance().getRewards().get(rewardIndex);
		final ItemTemplate itemTemplate = ItemData.getInstance().getTemplate(reward.getId());
		
		// Weight check.
		final long weight = itemTemplate.getWeight() * reward.getCount();
		final long slots = itemTemplate.isStackable() ? 1 : reward.getCount();
		if (!player.getInventory().validateWeight(weight) || !player.getInventory().validateCapacity(slots))
		{
			player.sendPacket(SystemMessageId.THE_ATTENDANCE_REWARD_CANNOT_BE_RECEIVED_BECAUSE_THE_INVENTORY_WEIGHT_QUANTITY_LIMIT_HAS_BEEN_EXCEEDED);
			return;
		}
		
		// Reward.
		if (isRewardAvailable)
		{
			// Save date and index.
			player.setAttendanceInfo(rewardIndex + 1);
			// Add items to player.
			player.addItem("Attendance Reward", reward, player, true);
			// Send message.
			final SystemMessage msg = new SystemMessage(SystemMessageId.YOU_VE_RECEIVED_YOUR_VIP_ATTENDANCE_REWARD_FOR_DAY_S1);
			msg.addInt(rewardIndex + 1);
			player.sendPacket(msg);
			// Send confirm 
			player.sendPacket(new ExConfirmVipAttendanceCheck(isRewardAvailable, rewardIndex + 1));
		}
	}
}
