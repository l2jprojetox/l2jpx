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

import org.l2jpx.gameserver.model.actor.Player;
import org.l2jpx.gameserver.network.clientpackets.ClientPacket;
import org.l2jpx.gameserver.network.serverpackets.dailymission.ExOneDayReceiveRewardList;

/**
 * @author UnAfraid
 */
public class RequestTodoList extends ClientPacket
{
	private int _tab;
	@SuppressWarnings("unused")
	private boolean _showAllLevels;
	
	@Override
	protected void readImpl()
	{
		_tab = readByte(); // Daily Reward = 9, Event = 1, Instance Zone = 2
		_showAllLevels = readByte() == 1; // Disabled = 0, Enabled = 1
	}
	
	@Override
	protected void runImpl()
	{
		final Player player = getPlayer();
		if (player == null)
		{
			return;
		}
		
		switch (_tab)
		{
			// case 1:
			// {
			// player.sendPacket(new ExTodoListInzone());
			// break;
			// }
			// case 2:
			// {
			// player.sendPacket(new ExTodoListInzone());
			// break;
			// }
			case 9: // Daily Rewards
			{
				// Initial EW request should be false
				player.sendPacket(new ExOneDayReceiveRewardList(player, true));
				break;
			}
		}
	}
}
