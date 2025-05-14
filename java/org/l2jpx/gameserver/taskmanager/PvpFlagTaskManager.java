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

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.l2jpx.commons.threads.ThreadPool;
import org.l2jpx.gameserver.model.actor.Player;

/**
 * @author Mobius
 */
public class PvpFlagTaskManager implements Runnable
{
	private static final Set<Player> PLAYERS = ConcurrentHashMap.newKeySet();
	private static boolean _working = false;
	
	protected PvpFlagTaskManager()
	{
		ThreadPool.schedulePriorityTaskAtFixedRate(this, 1000, 1000);
	}
	
	@Override
	public void run()
	{
		if (_working)
		{
			return;
		}
		_working = true;
		
		if (!PLAYERS.isEmpty())
		{
			final long currentTime = System.currentTimeMillis();
			for (Player player : PLAYERS)
			{
				if (currentTime > player.getPvpFlagLasts())
				{
					player.stopPvPFlag();
				}
				else if (currentTime > (player.getPvpFlagLasts() - 20000))
				{
					player.updatePvPFlag(2);
				}
				else
				{
					player.updatePvPFlag(1);
				}
			}
		}
		
		_working = false;
	}
	
	public void add(Player player)
	{
		PLAYERS.add(player);
	}
	
	public void remove(Player player)
	{
		PLAYERS.remove(player);
	}
	
	public static PvpFlagTaskManager getInstance()
	{
		return SingletonHolder.INSTANCE;
	}
	
	private static class SingletonHolder
	{
		protected static final PvpFlagTaskManager INSTANCE = new PvpFlagTaskManager();
	}
}
