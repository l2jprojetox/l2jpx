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
package org.l2jpx.gameserver.network.serverpackets.mentoring;

import java.util.ArrayList;
import java.util.List;

import org.l2jpx.commons.network.WritableBuffer;
import org.l2jpx.gameserver.enums.CategoryType;
import org.l2jpx.gameserver.model.World;
import org.l2jpx.gameserver.model.actor.Player;
import org.l2jpx.gameserver.network.GameClient;
import org.l2jpx.gameserver.network.ServerPackets;
import org.l2jpx.gameserver.network.serverpackets.ServerPacket;

/**
 * @author UnAfraid
 */
public class ListMenteeWaiting extends ServerPacket
{
	private static final int PLAYERS_PER_PAGE = 64;
	
	private final List<Player> _possibleCandiates = new ArrayList<>();
	private final int _page;
	
	public ListMenteeWaiting(int page, int minLevel, int maxLevel)
	{
		_page = page;
		for (Player player : World.getInstance().getPlayers())
		{
			if ((player.getLevel() >= minLevel) && (player.getLevel() <= maxLevel) && !player.isMentee() && !player.isMentor() && !player.isInCategory(CategoryType.SIXTH_CLASS_GROUP))
			{
				_possibleCandiates.add(player);
			}
		}
	}
	
	@Override
	public void writeImpl(GameClient client, WritableBuffer buffer)
	{
		ServerPackets.LIST_MENTEE_WAITING.writeId(this, buffer);
		buffer.writeInt(1); // always 1 in retail
		if (_possibleCandiates.isEmpty())
		{
			buffer.writeInt(0);
			buffer.writeInt(0);
			return;
		}
		
		buffer.writeInt(_possibleCandiates.size());
		buffer.writeInt(_possibleCandiates.size() % PLAYERS_PER_PAGE);
		for (Player player : _possibleCandiates)
		{
			if ((1 <= (PLAYERS_PER_PAGE * _page)) && (1 > (PLAYERS_PER_PAGE * (_page - 1))))
			{
				buffer.writeString(player.getName());
				buffer.writeInt(player.getActiveClass());
				buffer.writeInt(player.getLevel());
			}
		}
	}
}
