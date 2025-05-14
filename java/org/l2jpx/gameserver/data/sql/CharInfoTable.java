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
package org.l2jpx.gameserver.data.sql;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Calendar;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.l2jpx.commons.database.DatabaseFactory;
import org.l2jpx.gameserver.model.actor.Player;

public class CharInfoTable
{
	private static final Logger LOGGER = Logger.getLogger(CharInfoTable.class.getName());
	
	private final Map<Integer, String> _names = new ConcurrentHashMap<>();
	private final Map<Integer, Integer> _accessLevels = new ConcurrentHashMap<>();
	private final Map<Integer, Integer> _levels = new ConcurrentHashMap<>();
	private final Map<Integer, Integer> _classes = new ConcurrentHashMap<>();
	private final Map<Integer, Integer> _clans = new ConcurrentHashMap<>();
	private final Map<Integer, Map<Integer, String>> _memos = new ConcurrentHashMap<>();
	private final Map<Integer, Calendar> _creationDates = new ConcurrentHashMap<>();
	private final Map<Integer, Long> _lastAccess = new ConcurrentHashMap<>();
	
	protected CharInfoTable()
	{
		try (Connection con = DatabaseFactory.getConnection();
			Statement s = con.createStatement();
			ResultSet rs = s.executeQuery("SELECT charId, char_name, accesslevel FROM characters"))
		{
			while (rs.next())
			{
				final int id = rs.getInt("charId");
				_names.put(id, rs.getString("char_name"));
				_accessLevels.put(id, rs.getInt("accesslevel"));
			}
		}
		catch (SQLException e)
		{
			LOGGER.log(Level.WARNING, getClass().getSimpleName() + ": Couldn't retrieve all char id/name/access: " + e.getMessage(), e);
		}
		LOGGER.info(getClass().getSimpleName() + ": Loaded " + _names.size() + " char names.");
	}
	
	public void addName(Player player)
	{
		if (player != null)
		{
			addName(player.getObjectId(), player.getName());
			_accessLevels.put(player.getObjectId(), player.getAccessLevel().getLevel());
		}
	}
	
	private void addName(int objectId, String name)
	{
		if ((name != null) && !name.equals(_names.get(objectId)))
		{
			_names.put(objectId, name);
		}
	}
	
	public void removeName(int objId)
	{
		_names.remove(objId);
		_accessLevels.remove(objId);
	}
	
	public int getIdByName(String name)
	{
		if ((name == null) || name.isEmpty())
		{
			return -1;
		}
		
		for (Entry<Integer, String> entry : _names.entrySet())
		{
			if (entry.getValue().equalsIgnoreCase(name))
			{
				return entry.getKey();
			}
		}
		
		// Should not continue after the above?
		
		int id = -1;
		int accessLevel = 0;
		
		try (Connection con = DatabaseFactory.getConnection();
			PreparedStatement ps = con.prepareStatement("SELECT charId,accesslevel FROM characters WHERE char_name=?"))
		{
			ps.setString(1, name);
			try (ResultSet rs = ps.executeQuery())
			{
				while (rs.next())
				{
					id = rs.getInt("charId");
					accessLevel = rs.getInt("accesslevel");
				}
			}
		}
		catch (SQLException e)
		{
			LOGGER.log(Level.WARNING, getClass().getSimpleName() + ": Could not check existing char name: " + e.getMessage(), e);
		}
		
		if (id > 0)
		{
			_names.put(id, name);
			_accessLevels.put(id, accessLevel);
			return id;
		}
		
		return -1; // Not found.
	}
	
	public String getNameById(int id)
	{
		if (id <= 0)
		{
			return null;
		}
		
		String name = _names.get(id);
		if (name != null)
		{
			return name;
		}
		
		try (Connection con = DatabaseFactory.getConnection();
			PreparedStatement ps = con.prepareStatement("SELECT char_name,accesslevel FROM characters WHERE charId=?"))
		{
			ps.setInt(1, id);
			try (ResultSet rset = ps.executeQuery())
			{
				if (rset.next())
				{
					name = rset.getString("char_name");
					_names.put(id, name);
					_accessLevels.put(id, rset.getInt("accesslevel"));
					return name;
				}
			}
		}
		catch (SQLException e)
		{
			LOGGER.log(Level.WARNING, getClass().getSimpleName() + ": Could not check existing char id: " + e.getMessage(), e);
		}
		
		return null; // not found
	}
	
	public int getAccessLevelById(int objectId)
	{
		return getNameById(objectId) != null ? _accessLevels.get(objectId) : 0;
	}
	
	public synchronized boolean doesCharNameExist(String name)
	{
		boolean result = false;
		try (Connection con = DatabaseFactory.getConnection();
			PreparedStatement ps = con.prepareStatement("SELECT COUNT(*) as count FROM characters WHERE char_name=?"))
		{
			ps.setString(1, name);
			try (ResultSet rs = ps.executeQuery())
			{
				if (rs.next())
				{
					result = rs.getInt("count") > 0;
				}
			}
		}
		catch (SQLException e)
		{
			LOGGER.log(Level.WARNING, getClass().getSimpleName() + ": Could not check existing charname: " + e.getMessage(), e);
		}
		return result;
	}
	
	public int getAccountCharacterCount(String account)
	{
		try (Connection con = DatabaseFactory.getConnection();
			PreparedStatement ps = con.prepareStatement("SELECT COUNT(char_name) as count FROM characters WHERE account_name=?"))
		{
			ps.setString(1, account);
			try (ResultSet rset = ps.executeQuery())
			{
				if (rset.next())
				{
					return rset.getInt("count");
				}
			}
		}
		catch (SQLException e)
		{
			LOGGER.log(Level.WARNING, "Couldn't retrieve account for id: " + e.getMessage(), e);
		}
		return 0;
	}
	
	public void setLevel(int objectId, int level)
	{
		_levels.put(objectId, level);
	}
	
	public int getLevelById(int objectId)
	{
		final Integer level = _levels.get(objectId);
		if (level != null)
		{
			return level;
		}
		
		try (Connection con = DatabaseFactory.getConnection();
			PreparedStatement ps = con.prepareStatement("SELECT level FROM characters WHERE charId = ?"))
		{
			ps.setInt(1, objectId);
			try (ResultSet rset = ps.executeQuery())
			{
				if (rset.next())
				{
					final int dbLevel = rset.getInt("level");
					_levels.put(objectId, dbLevel);
					return dbLevel;
				}
			}
		}
		catch (SQLException e)
		{
			LOGGER.log(Level.WARNING, getClass().getSimpleName() + ": Could not check existing char count: " + e.getMessage(), e);
		}
		return 0;
	}
	
	public void setClassId(int objectId, int classId)
	{
		_classes.put(objectId, classId);
	}
	
	public int getClassIdById(int objectId)
	{
		final Integer classId = _classes.get(objectId);
		if (classId != null)
		{
			return classId;
		}
		
		try (Connection con = DatabaseFactory.getConnection();
			PreparedStatement ps = con.prepareStatement("SELECT classid FROM characters WHERE charId = ?"))
		{
			ps.setInt(1, objectId);
			try (ResultSet rset = ps.executeQuery())
			{
				if (rset.next())
				{
					final int dbClassId = rset.getInt("classid");
					_classes.put(objectId, dbClassId);
					return dbClassId;
				}
			}
		}
		catch (SQLException e)
		{
			LOGGER.log(Level.WARNING, getClass().getSimpleName() + ": Couldn't retrieve class for id: " + e.getMessage(), e);
		}
		return 0;
	}
	
	public void setClanId(int objectId, int clanId)
	{
		_clans.put(objectId, clanId);
	}
	
	public void removeClanId(int objectId)
	{
		_clans.remove(objectId);
	}
	
	public int getClanIdById(int objectId)
	{
		final Integer clanId = _clans.get(objectId);
		if (clanId != null)
		{
			return clanId;
		}
		
		try (Connection con = DatabaseFactory.getConnection();
			PreparedStatement ps = con.prepareStatement("SELECT clanId FROM characters WHERE charId = ?"))
		{
			ps.setInt(1, objectId);
			try (ResultSet rset = ps.executeQuery())
			{
				while (rset.next())
				{
					final int dbClanId = rset.getInt("clanId");
					_clans.put(objectId, dbClanId);
					return dbClanId;
				}
			}
		}
		catch (SQLException e)
		{
			LOGGER.log(Level.WARNING, getClass().getSimpleName() + ": Could not check existing char count: " + e.getMessage(), e);
		}
		
		// Prevent searching again.
		_clans.put(objectId, 0);
		return 0;
	}
	
	public void setFriendMemo(int charId, int friendId, String memo)
	{
		Map<Integer, String> memos = _memos.get(charId);
		if (memos == null)
		{
			memos = new ConcurrentHashMap<>();
			_memos.put(charId, memos);
		}
		
		if (memo == null)
		{
			memos.put(friendId, "");
			return;
		}
		
		// Bypass exploit check.
		final String text = memo.toLowerCase();
		if (text.contains("action") && text.contains("bypass"))
		{
			memos.put(friendId, "");
			return;
		}
		
		// Add text without tags.
		memos.put(friendId, memo.replaceAll("<.*?>", ""));
	}
	
	public void removeFriendMemo(int charId, int friendId)
	{
		final Map<Integer, String> memos = _memos.get(charId);
		if (memos == null)
		{
			return;
		}
		
		memos.remove(friendId);
	}
	
	public String getFriendMemo(int charId, int friendId)
	{
		Map<Integer, String> memos = _memos.get(charId);
		if (memos == null)
		{
			memos = new ConcurrentHashMap<>();
			_memos.put(charId, memos);
		}
		else if (memos.containsKey(friendId))
		{
			return memos.get(friendId);
		}
		
		try (Connection con = DatabaseFactory.getConnection();
			PreparedStatement statement = con.prepareStatement("SELECT memo FROM character_friends WHERE charId=? AND friendId=?"))
		{
			statement.setInt(1, charId);
			statement.setInt(2, friendId);
			
			try (ResultSet rset = statement.executeQuery())
			{
				if (rset.next())
				{
					final String dbMemo = rset.getString("memo");
					memos.put(friendId, dbMemo == null ? "" : dbMemo);
					return dbMemo;
				}
			}
		}
		catch (Exception e)
		{
			LOGGER.log(Level.WARNING, "Error occurred while retrieving memo: " + e.getMessage(), e);
		}
		
		// Prevent searching again.
		memos.put(friendId, "");
		return null;
	}
	
	public Calendar getCharacterCreationDate(int objectId)
	{
		final Calendar calendar = _creationDates.get(objectId);
		if (calendar != null)
		{
			return calendar;
		}
		
		try (Connection con = DatabaseFactory.getConnection();
			PreparedStatement ps = con.prepareStatement("SELECT createDate FROM characters WHERE charId = ?"))
		{
			ps.setInt(1, objectId);
			try (ResultSet rset = ps.executeQuery())
			{
				if (rset.next())
				{
					final Date createDate = rset.getDate("createDate");
					final Calendar newCalendar = Calendar.getInstance();
					newCalendar.setTime(createDate);
					_creationDates.put(objectId, newCalendar);
					return newCalendar;
				}
			}
		}
		catch (SQLException e)
		{
			LOGGER.log(Level.WARNING, getClass().getSimpleName() + ": Could not retrieve character creation date: " + e.getMessage(), e);
		}
		return null;
	}
	
	public void setLastAccess(int objectId, long lastAccess)
	{
		_lastAccess.put(objectId, lastAccess);
	}
	
	public int getLastAccessDelay(int objectId)
	{
		final Long lastAccess = _lastAccess.get(objectId);
		if (lastAccess != null)
		{
			final long currentTime = System.currentTimeMillis();
			final long timeDifferenceInMillis = currentTime - lastAccess;
			return (int) (timeDifferenceInMillis / 1000);
		}
		
		try (Connection con = DatabaseFactory.getConnection();
			PreparedStatement ps = con.prepareStatement("SELECT lastAccess FROM characters WHERE charId = ?"))
		{
			ps.setInt(1, objectId);
			try (ResultSet rset = ps.executeQuery())
			{
				if (rset.next())
				{
					final long dbLastAccess = rset.getLong("lastAccess");
					_lastAccess.put(objectId, dbLastAccess);
					
					final long currentTime = System.currentTimeMillis();
					final long timeDifferenceInMillis = currentTime - dbLastAccess;
					return (int) (timeDifferenceInMillis / 1000);
				}
			}
		}
		catch (SQLException e)
		{
			LOGGER.log(Level.WARNING, getClass().getSimpleName() + ": Could not retrieve lastAccess timestamp: " + e.getMessage(), e);
		}
		return 0;
	}
	
	public static CharInfoTable getInstance()
	{
		return SingletonHolder.INSTANCE;
	}
	
	private static class SingletonHolder
	{
		protected static final CharInfoTable INSTANCE = new CharInfoTable();
	}
}
