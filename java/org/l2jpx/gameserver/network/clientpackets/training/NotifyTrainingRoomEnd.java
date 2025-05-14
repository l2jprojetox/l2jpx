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
package org.l2jpx.gameserver.network.clientpackets.training;

import org.l2jpx.gameserver.model.actor.Player;
import org.l2jpx.gameserver.model.holders.TrainingHolder;
import org.l2jpx.gameserver.network.clientpackets.ClientPacket;
import org.l2jpx.gameserver.network.serverpackets.training.ExTrainingZone_Leaving;

/**
 * @author Sdw
 */
public class NotifyTrainingRoomEnd extends ClientPacket
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
		
		final TrainingHolder holder = player.getTraingCampInfo();
		if (holder == null)
		{
			return;
		}
		
		if (holder.isTraining())
		{
			holder.setEndTime(System.currentTimeMillis());
			player.setTraingCampInfo(holder);
			player.enableAllSkills();
			player.setInvul(false);
			player.setInvisible(false);
			player.setImmobilized(false);
			player.teleToLocation(player.getLastLocation());
			player.sendPacket(ExTrainingZone_Leaving.STATIC_PACKET);
			holder.setEndTime(System.currentTimeMillis());
			player.setTraingCampInfo(holder);
		}
	}
}
