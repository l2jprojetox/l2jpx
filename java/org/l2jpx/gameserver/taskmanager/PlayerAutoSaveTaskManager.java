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
package org.l2jpx.gameserver.taskmanager;

import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import org.l2jpx.Config;
import org.l2jpx.commons.threads.ThreadPool;
import org.l2jpx.gameserver.model.actor.Player;

/**
 * @author Mobius
 */
public class PlayerAutoSaveTaskManager implements Runnable
{
	private static final Map<Player, Long> PLAYER_TIMES = new ConcurrentHashMap<>();
	private static boolean _working = false;
	
	protected PlayerAutoSaveTaskManager()
	{
		ThreadPool.scheduleAtFixedRate(this, 1000, 1000);
	}
	
	@Override
	public void run()
	{
		if (_working)
		{
			return;
		}
		_working = true;
		
		if (!PLAYER_TIMES.isEmpty())
		{
			final long currentTime = System.currentTimeMillis();
			final Iterator<Entry<Player, Long>> iterator = PLAYER_TIMES.entrySet().iterator();
			Entry<Player, Long> entry;
			Player player;
			Long time;
			
			while (iterator.hasNext())
			{
				entry = iterator.next();
				player = entry.getKey();
				time = entry.getValue();
				
				if (currentTime > time)
				{
					if ((player != null) && player.isOnline())
					{
						player.autoSave();
						PLAYER_TIMES.put(player, currentTime + Config.CHAR_DATA_STORE_INTERVAL);
						break; // Prevent SQL flood.
					}
					
					iterator.remove();
				}
			}
		}
		
		_working = false;
	}
	
	public void add(Player player)
	{
		PLAYER_TIMES.put(player, System.currentTimeMillis() + Config.CHAR_DATA_STORE_INTERVAL);
	}
	
	public void remove(Player player)
	{
		PLAYER_TIMES.remove(player);
	}
	
	public static PlayerAutoSaveTaskManager getInstance()
	{
		return SingletonHolder.INSTANCE;
	}
	
	private static class SingletonHolder
	{
		protected static final PlayerAutoSaveTaskManager INSTANCE = new PlayerAutoSaveTaskManager();
	}
}
