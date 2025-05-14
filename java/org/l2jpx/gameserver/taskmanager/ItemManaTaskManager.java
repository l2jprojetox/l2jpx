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

import org.l2jpx.commons.threads.ThreadPool;
import org.l2jpx.gameserver.model.actor.Player;
import org.l2jpx.gameserver.model.item.instance.Item;

/**
 * @author Mobius
 */
public class ItemManaTaskManager implements Runnable
{
	private static final Map<Item, Long> ITEMS = new ConcurrentHashMap<>();
	private static final int MANA_CONSUMPTION_RATE = 60000;
	private static boolean _working = false;
	
	protected ItemManaTaskManager()
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
		
		if (!ITEMS.isEmpty())
		{
			final long currentTime = System.currentTimeMillis();
			final Iterator<Entry<Item, Long>> iterator = ITEMS.entrySet().iterator();
			Entry<Item, Long> entry;
			
			while (iterator.hasNext())
			{
				entry = iterator.next();
				if (currentTime > entry.getValue())
				{
					iterator.remove();
					
					final Item item = entry.getKey();
					final Player player = item.getActingPlayer();
					if ((player == null) || player.isInOfflineMode())
					{
						continue;
					}
					
					item.decreaseMana(item.isEquipped());
				}
			}
		}
		
		_working = false;
	}
	
	public void add(Item item)
	{
		if (!ITEMS.containsKey(item))
		{
			ITEMS.put(item, System.currentTimeMillis() + MANA_CONSUMPTION_RATE);
		}
	}
	
	public static ItemManaTaskManager getInstance()
	{
		return SingletonHolder.INSTANCE;
	}
	
	private static class SingletonHolder
	{
		protected static final ItemManaTaskManager INSTANCE = new ItemManaTaskManager();
	}
}