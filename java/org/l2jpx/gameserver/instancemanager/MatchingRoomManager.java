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
package org.l2jpx.gameserver.instancemanager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import org.l2jpx.gameserver.enums.ClassId;
import org.l2jpx.gameserver.enums.MatchingRoomType;
import org.l2jpx.gameserver.enums.PartyMatchingRoomLevelType;
import org.l2jpx.gameserver.model.actor.Player;
import org.l2jpx.gameserver.model.matching.MatchingRoom;

/**
 * @author Sdw
 */
public class MatchingRoomManager
{
	private Set<Player> _waitingList;
	
	private static final Map<MatchingRoomType, Map<Integer, MatchingRoom>> _rooms = new ConcurrentHashMap<>(2);
	
	private final AtomicInteger _id = new AtomicInteger(0);
	
	public void addToWaitingList(Player player)
	{
		if (_waitingList == null)
		{
			synchronized (this)
			{
				if (_waitingList == null)
				{
					_waitingList = ConcurrentHashMap.newKeySet(1);
				}
			}
		}
		_waitingList.add(player);
	}
	
	public void removeFromWaitingList(Player player)
	{
		getPlayerInWaitingList().remove(player);
	}
	
	public Set<Player> getPlayerInWaitingList()
	{
		return _waitingList == null ? Collections.emptySet() : _waitingList;
	}
	
	public List<Player> getPlayerInWaitingList(int minLevel, int maxLevel, List<ClassId> classIds, String query)
	{
		if (_waitingList == null)
		{
			return Collections.emptyList();
		}
		
		final List<Player> players = new ArrayList<>();
		for (Player player : _waitingList)
		{
			if ((player != null) && (player.getLevel() >= minLevel) && (player.getLevel() <= maxLevel) && ((classIds == null) || classIds.contains(player.getClassId())) && ((query == null) || query.isEmpty() || player.getName().toLowerCase().contains(query)))
			{
				players.add(player);
			}
		}
		return players;
	}
	
	public int addMatchingRoom(MatchingRoom room)
	{
		final int roomId = _id.incrementAndGet();
		_rooms.computeIfAbsent(room.getRoomType(), k -> new ConcurrentHashMap<>()).put(roomId, room);
		return roomId;
	}
	
	public void removeMatchingRoom(MatchingRoom room)
	{
		_rooms.getOrDefault(room.getRoomType(), Collections.emptyMap()).remove(room.getId());
	}
	
	public Map<Integer, MatchingRoom> getPartyMathchingRooms()
	{
		return _rooms.get(MatchingRoomType.PARTY);
	}
	
	public List<MatchingRoom> getPartyMathchingRooms(int location, PartyMatchingRoomLevelType type, int requestorLevel)
	{
		final List<MatchingRoom> result = new ArrayList<>();
		if (_rooms.containsKey(MatchingRoomType.PARTY))
		{
			for (MatchingRoom room : _rooms.get(MatchingRoomType.PARTY).values())
			{
				if (((location < 0) || (room.getLocation() == location)) //
					&& ((type == PartyMatchingRoomLevelType.ALL) || ((room.getMinLevel() >= requestorLevel) && (room.getMaxLevel() <= requestorLevel))))
				{
					result.add(room);
				}
			}
		}
		return result;
	}
	
	public Map<Integer, MatchingRoom> getCCMathchingRooms()
	{
		return _rooms.get(MatchingRoomType.COMMAND_CHANNEL);
	}
	
	public List<MatchingRoom> getCCMathchingRooms(int location, int level)
	{
		final List<MatchingRoom> result = new ArrayList<>();
		if (_rooms.containsKey(MatchingRoomType.COMMAND_CHANNEL))
		{
			for (MatchingRoom room : _rooms.get(MatchingRoomType.COMMAND_CHANNEL).values())
			{
				if ((room.getLocation() == location) //
					&& ((room.getMinLevel() <= level) && (room.getMaxLevel() >= level)))
				{
					result.add(room);
				}
			}
		}
		return result;
	}
	
	public MatchingRoom getCCMatchingRoom(int roomId)
	{
		return _rooms.getOrDefault(MatchingRoomType.COMMAND_CHANNEL, Collections.emptyMap()).get(roomId);
	}
	
	public MatchingRoom getPartyMathchingRoom(int location, int level)
	{
		if (_rooms.containsKey(MatchingRoomType.PARTY))
		{
			for (MatchingRoom room : _rooms.get(MatchingRoomType.PARTY).values())
			{
				if ((room.getLocation() == location) //
					&& ((room.getMinLevel() <= level) && (room.getMaxLevel() >= level)))
				{
					return room;
				}
			}
		}
		return null;
	}
	
	public MatchingRoom getPartyMathchingRoom(int roomId)
	{
		return _rooms.getOrDefault(MatchingRoomType.PARTY, Collections.emptyMap()).get(roomId);
	}
	
	public static MatchingRoomManager getInstance()
	{
		return SingletonHolder.INSTANCE;
	}
	
	private static class SingletonHolder
	{
		protected static final MatchingRoomManager INSTANCE = new MatchingRoomManager();
	}
}
