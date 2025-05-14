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
package org.l2jpx.gameserver.model.matching;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

import org.l2jpx.commons.database.DatabaseFactory;
import org.l2jpx.gameserver.enums.MatchingMemberType;
import org.l2jpx.gameserver.enums.MatchingRoomType;
import org.l2jpx.gameserver.enums.UserInfoType;
import org.l2jpx.gameserver.instancemanager.MapRegionManager;
import org.l2jpx.gameserver.instancemanager.MatchingRoomManager;
import org.l2jpx.gameserver.model.actor.Player;
import org.l2jpx.gameserver.model.interfaces.IIdentifiable;

/**
 * @author Sdw
 */
public abstract class MatchingRoom implements IIdentifiable
{
	private static final Logger LOGGER = Logger.getLogger(MatchingRoom.class.getName());
	
	private static final String INSERT_PARTY_HISTORY = "INSERT INTO party_matching_history (title,leader) values (?,?)";
	
	private final int _id;
	private String _title;
	private int _loot;
	private int _minLevel;
	private int _maxLevel;
	private int _maxCount;
	private Set<Player> _members;
	private Player _leader;
	
	public MatchingRoom(String title, int loot, int minLevel, int maxLevel, int maxmem, Player leader)
	{
		_id = MatchingRoomManager.getInstance().addMatchingRoom(this);
		_title = title;
		_loot = loot;
		_minLevel = minLevel;
		_maxLevel = maxLevel;
		_maxCount = maxmem;
		_leader = leader;
		addMember(_leader);
		onRoomCreation(leader);
		storeRoomHistory();
	}
	
	public Set<Player> getMembers()
	{
		if (_members == null)
		{
			synchronized (this)
			{
				if (_members == null)
				{
					_members = ConcurrentHashMap.newKeySet(1);
				}
			}
		}
		return _members;
	}
	
	public void addMember(Player player)
	{
		if ((player.getLevel() < _minLevel) || (player.getLevel() > _maxLevel) || ((_members != null) && (_members.size() >= _maxCount)))
		{
			notifyInvalidCondition(player);
			return;
		}
		
		getMembers().add(player);
		MatchingRoomManager.getInstance().removeFromWaitingList(player);
		notifyNewMember(player);
		player.setMatchingRoom(this);
		player.broadcastUserInfo(UserInfoType.CLAN);
	}
	
	public void storeRoomHistory()
	{
		try (Connection con = DatabaseFactory.getConnection();
			PreparedStatement statement = con.prepareStatement(INSERT_PARTY_HISTORY))
		{
			statement.setString(1, _title);
			statement.setString(2, _leader.getName());
			statement.execute();
		}
		catch (Exception e)
		{
			LOGGER.warning("MatchingRoom: Problem restoring room history!");
		}
	}
	
	public void deleteMember(Player player, boolean kicked)
	{
		boolean leaderChanged = false;
		if (player == _leader)
		{
			if (getMembers().isEmpty())
			{
				MatchingRoomManager.getInstance().removeMatchingRoom(this);
			}
			else
			{
				final Iterator<Player> iter = getMembers().iterator();
				if (iter.hasNext())
				{
					_leader = iter.next();
					iter.remove();
					leaderChanged = true;
				}
			}
		}
		else
		{
			getMembers().remove(player);
		}
		
		player.setMatchingRoom(null);
		player.broadcastUserInfo(UserInfoType.CLAN);
		MatchingRoomManager.getInstance().addToWaitingList(player);
		
		notifyRemovedMember(player, kicked, leaderChanged);
	}
	
	@Override
	public int getId()
	{
		return _id;
	}
	
	public int getLootType()
	{
		return _loot;
	}
	
	public int getMinLevel()
	{
		return _minLevel;
	}
	
	public int getMaxLevel()
	{
		return _maxLevel;
	}
	
	public int getLocation()
	{
		return MapRegionManager.getInstance().getBBs(_leader.getLocation());
	}
	
	public int getMembersCount()
	{
		return _members == null ? 0 : _members.size();
	}
	
	public int getMaxMembers()
	{
		return _maxCount;
	}
	
	public String getTitle()
	{
		return _title;
	}
	
	public Player getLeader()
	{
		return _leader;
	}
	
	public boolean isLeader(Player player)
	{
		return player == _leader;
	}
	
	public void setMinLevel(int minLevel)
	{
		_minLevel = minLevel;
	}
	
	public void setMaxLevel(int maxLevel)
	{
		_maxLevel = maxLevel;
	}
	
	public void setLootType(int loot)
	{
		_loot = loot;
	}
	
	public void setMaxMembers(int maxCount)
	{
		_maxCount = maxCount;
	}
	
	public void setTitle(String title)
	{
		_title = title;
	}
	
	protected abstract void onRoomCreation(Player player);
	
	protected abstract void notifyInvalidCondition(Player player);
	
	protected abstract void notifyNewMember(Player player);
	
	protected abstract void notifyRemovedMember(Player player, boolean kicked, boolean leaderChanged);
	
	public abstract void disbandRoom();
	
	public abstract MatchingRoomType getRoomType();
	
	public abstract MatchingMemberType getMemberType(Player player);
}
