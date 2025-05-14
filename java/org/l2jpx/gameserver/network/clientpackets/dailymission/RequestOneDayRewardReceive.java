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
package org.l2jpx.gameserver.network.clientpackets.dailymission;

import java.util.Collection;

import org.l2jpx.commons.threads.ThreadPool;
import org.l2jpx.gameserver.data.xml.DailyMissionData;
import org.l2jpx.gameserver.model.DailyMissionDataHolder;
import org.l2jpx.gameserver.model.actor.Player;
import org.l2jpx.gameserver.model.actor.request.RewardRequest;
import org.l2jpx.gameserver.network.clientpackets.ClientPacket;
import org.l2jpx.gameserver.network.serverpackets.dailymission.ExConnectedTimeAndGettableReward;
import org.l2jpx.gameserver.network.serverpackets.dailymission.ExOneDayReceiveRewardList;

/**
 * @author Sdw
 */
public class RequestOneDayRewardReceive extends ClientPacket
{
	private int _id;
	
	@Override
	protected void readImpl()
	{
		_id = readShort();
	}
	
	@Override
	protected void runImpl()
	{
		if (!getClient().getFloodProtectors().canPerformPlayerAction())
		{
			return;
		}
		
		final Player player = getPlayer();
		if (player == null)
		{
			return;
		}
		
		if (player.hasRequest(RewardRequest.class))
		{
			return;
		}
		player.addRequest(new RewardRequest(player));
		
		final Collection<DailyMissionDataHolder> rewards = DailyMissionData.getInstance().getDailyMissionData(_id);
		if ((rewards == null) || rewards.isEmpty())
		{
			player.removeRequest(RewardRequest.class);
			return;
		}
		
		for (DailyMissionDataHolder holder : rewards)
		{
			if (holder.isDisplayable(player))
			{
				holder.requestReward(player);
			}
		}
		
		player.sendPacket(new ExOneDayReceiveRewardList(player, true));
		player.sendPacket(new ExConnectedTimeAndGettableReward(player));
		
		ThreadPool.schedule(() -> player.removeRequest(RewardRequest.class), 300);
	}
}
