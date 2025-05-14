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

import org.l2jpx.Config;
import org.l2jpx.commons.threads.ThreadPool;
import org.l2jpx.gameserver.handler.ItemHandler;
import org.l2jpx.gameserver.model.actor.Player;
import org.l2jpx.gameserver.model.item.instance.Item;

/**
 * @author Mobius, Gigi
 */
public class AutoPotionTaskManager implements Runnable
{
	private static final Set<Player> PLAYERS = ConcurrentHashMap.newKeySet();
	private static boolean _working = false;
	
	protected AutoPotionTaskManager()
	{
		ThreadPool.schedulePriorityTaskAtFixedRate(this, 0, 1000);
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
			PLAYER: for (Player player : PLAYERS)
			{
				if ((player == null) || player.isAlikeDead() || (player.isOnlineInt() != 1) || (!Config.AUTO_POTIONS_IN_OLYMPIAD && player.isInOlympiadMode()))
				{
					remove(player);
					continue PLAYER;
				}
				
				boolean success = false;
				if (Config.AUTO_HP_ENABLED)
				{
					final boolean restoreHP = ((player.getStatus().getCurrentHp() / player.getMaxHp()) * 100) < Config.AUTO_HP_PERCENTAGE;
					HP: for (int itemId : Config.AUTO_HP_ITEM_IDS)
					{
						final Item hpPotion = player.getInventory().getItemByItemId(itemId);
						if ((hpPotion != null) && (hpPotion.getCount() > 0))
						{
							success = true;
							if (restoreHP)
							{
								ItemHandler.getInstance().getHandler(hpPotion.getEtcItem()).useItem(player, hpPotion, false);
								player.sendMessage("Auto potion: Restored HP.");
								break HP;
							}
						}
					}
				}
				if (Config.AUTO_CP_ENABLED)
				{
					final boolean restoreCP = ((player.getStatus().getCurrentCp() / player.getMaxCp()) * 100) < Config.AUTO_CP_PERCENTAGE;
					CP: for (int itemId : Config.AUTO_CP_ITEM_IDS)
					{
						final Item cpPotion = player.getInventory().getItemByItemId(itemId);
						if ((cpPotion != null) && (cpPotion.getCount() > 0))
						{
							success = true;
							if (restoreCP)
							{
								ItemHandler.getInstance().getHandler(cpPotion.getEtcItem()).useItem(player, cpPotion, false);
								player.sendMessage("Auto potion: Restored CP.");
								break CP;
							}
						}
					}
				}
				if (Config.AUTO_MP_ENABLED)
				{
					final boolean restoreMP = ((player.getStatus().getCurrentMp() / player.getMaxMp()) * 100) < Config.AUTO_MP_PERCENTAGE;
					MP: for (int itemId : Config.AUTO_MP_ITEM_IDS)
					{
						final Item mpPotion = player.getInventory().getItemByItemId(itemId);
						if ((mpPotion != null) && (mpPotion.getCount() > 0))
						{
							success = true;
							if (restoreMP)
							{
								ItemHandler.getInstance().getHandler(mpPotion.getEtcItem()).useItem(player, mpPotion, false);
								player.sendMessage("Auto potion: Restored MP.");
								break MP;
							}
						}
					}
				}
				
				if (!success)
				{
					player.sendMessage("Auto potion: You are out of potions!");
				}
			}
		}
		
		_working = false;
	}
	
	public void add(Player player)
	{
		if (!PLAYERS.contains(player))
		{
			PLAYERS.add(player);
		}
	}
	
	public void remove(Player player)
	{
		PLAYERS.remove(player);
	}
	
	public static AutoPotionTaskManager getInstance()
	{
		return SingletonHolder.INSTANCE;
	}
	
	private static class SingletonHolder
	{
		protected static final AutoPotionTaskManager INSTANCE = new AutoPotionTaskManager();
	}
}
