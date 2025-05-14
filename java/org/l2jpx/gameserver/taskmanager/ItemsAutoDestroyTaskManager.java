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
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.l2jpx.Config;
import org.l2jpx.commons.threads.ThreadPool;
import org.l2jpx.gameserver.enums.ItemLocation;
import org.l2jpx.gameserver.instancemanager.ItemsOnGroundManager;
import org.l2jpx.gameserver.model.item.instance.Item;

public class ItemsAutoDestroyTaskManager implements Runnable
{
	private static final Set<Item> ITEMS = ConcurrentHashMap.newKeySet();
	
	protected ItemsAutoDestroyTaskManager()
	{
		ThreadPool.scheduleAtFixedRate(this, 5000, 5000);
	}
	
	@Override
	public void run()
	{
		if (ITEMS.isEmpty())
		{
			return;
		}
		
		final long currentTime = System.currentTimeMillis();
		final Iterator<Item> iterator = ITEMS.iterator();
		Item itemInstance;
		
		while (iterator.hasNext())
		{
			itemInstance = iterator.next();
			if ((itemInstance.getDropTime() == 0) || (itemInstance.getItemLocation() != ItemLocation.VOID))
			{
				iterator.remove();
			}
			else
			{
				final long autoDestroyTime;
				if (itemInstance.getTemplate().getAutoDestroyTime() > 0)
				{
					autoDestroyTime = itemInstance.getTemplate().getAutoDestroyTime();
				}
				else if (itemInstance.getTemplate().hasExImmediateEffect())
				{
					autoDestroyTime = Config.HERB_AUTO_DESTROY_TIME;
				}
				else
				{
					autoDestroyTime = ((Config.AUTODESTROY_ITEM_AFTER == 0) ? 3600000 : Config.AUTODESTROY_ITEM_AFTER * 1000);
				}
				
				if ((currentTime - itemInstance.getDropTime()) > autoDestroyTime)
				{
					itemInstance.decayMe();
					if (Config.SAVE_DROPPED_ITEM)
					{
						ItemsOnGroundManager.getInstance().removeObject(itemInstance);
					}
					iterator.remove();
				}
			}
		}
	}
	
	public void addItem(Item item)
	{
		item.setDropTime(System.currentTimeMillis());
		ITEMS.add(item);
	}
	
	public static ItemsAutoDestroyTaskManager getInstance()
	{
		return SingletonHolder.INSTANCE;
	}
	
	private static class SingletonHolder
	{
		protected static final ItemsAutoDestroyTaskManager INSTANCE = new ItemsAutoDestroyTaskManager();
	}
}