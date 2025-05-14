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

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;

import org.l2jpx.commons.network.WritableBuffer;
import org.l2jpx.gameserver.enums.ClassId;
import org.l2jpx.gameserver.instancemanager.InstanceManager;
import org.l2jpx.gameserver.instancemanager.MatchingRoomManager;
import org.l2jpx.gameserver.model.actor.Player;
import org.l2jpx.gameserver.model.instancezone.Instance;
import org.l2jpx.gameserver.network.GameClient;
import org.l2jpx.gameserver.network.ServerPackets;

/**
 * @author Gnacik
 */
public class ExListPartyMatchingWaitingRoom extends ServerPacket
{
	private static final int NUM_PER_PAGE = 64;
	
	private final int _size;
	private final List<Player> _players = new LinkedList<>();
	
	public ExListPartyMatchingWaitingRoom(int page, int minLevel, int maxLevel, List<ClassId> classIds, String query)
	{
		final List<Player> players = MatchingRoomManager.getInstance().getPlayerInWaitingList(minLevel, maxLevel, classIds, query);
		_size = players.size();
		final int startIndex = (page - 1) * NUM_PER_PAGE;
		int chunkSize = _size - startIndex;
		if (chunkSize > NUM_PER_PAGE)
		{
			chunkSize = NUM_PER_PAGE;
		}
		for (int i = startIndex; i < (startIndex + chunkSize); i++)
		{
			_players.add(players.get(i));
		}
	}
	
	@Override
	public void writeImpl(GameClient client, WritableBuffer buffer)
	{
		ServerPackets.EX_LIST_PARTY_MATCHING_WAITING_ROOM.writeId(this, buffer);
		buffer.writeInt(_size);
		buffer.writeInt(_players.size());
		for (Player player : _players)
		{
			buffer.writeString(player.getName());
			buffer.writeInt(player.getClassId().getId());
			buffer.writeInt(player.getLevel());
			final Instance instance = InstanceManager.getInstance().getPlayerInstance(player, false);
			buffer.writeInt((instance != null) && (instance.getTemplateId() >= 0) ? instance.getTemplateId() : -1);
			final Map<Integer, Long> instanceTimes = InstanceManager.getInstance().getAllInstanceTimes(player);
			buffer.writeInt(instanceTimes.size());
			for (Entry<Integer, Long> entry : instanceTimes.entrySet())
			{
				final long instanceTime = TimeUnit.MILLISECONDS.toSeconds(entry.getValue() - System.currentTimeMillis());
				buffer.writeInt(entry.getKey());
				buffer.writeInt((int) instanceTime);
			}
		}
	}
}
