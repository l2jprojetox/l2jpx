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
package org.l2jpx.gameserver.network.clientpackets;

import java.util.HashMap;
import java.util.Map;

import org.l2jpx.Config;
import org.l2jpx.commons.threads.ThreadPool;
import org.l2jpx.gameserver.data.xml.BuyListData;
import org.l2jpx.gameserver.enums.Race;
import org.l2jpx.gameserver.model.WorldObject;
import org.l2jpx.gameserver.model.actor.Npc;
import org.l2jpx.gameserver.model.actor.Player;
import org.l2jpx.gameserver.model.actor.instance.Merchant;
import org.l2jpx.gameserver.model.buylist.Product;
import org.l2jpx.gameserver.model.buylist.ProductList;
import org.l2jpx.gameserver.model.item.Armor;
import org.l2jpx.gameserver.model.item.ItemTemplate;
import org.l2jpx.gameserver.model.item.Weapon;
import org.l2jpx.gameserver.model.item.type.ArmorType;
import org.l2jpx.gameserver.model.item.type.WeaponType;
import org.l2jpx.gameserver.model.itemcontainer.Inventory;
import org.l2jpx.gameserver.network.PacketLogger;
import org.l2jpx.gameserver.network.SystemMessageId;
import org.l2jpx.gameserver.network.serverpackets.ActionFailed;
import org.l2jpx.gameserver.network.serverpackets.ExUserInfoEquipSlot;
import org.l2jpx.gameserver.network.serverpackets.ShopPreviewInfo;
import org.l2jpx.gameserver.util.Util;

/**
 ** @author Gnacik
 */
public class RequestPreviewItem extends ClientPacket
{
	@SuppressWarnings("unused")
	private int _unk;
	private int _listId;
	private int _count;
	private int[] _items;
	
	private class RemoveWearItemsTask implements Runnable
	{
		private final Player _player;
		
		protected RemoveWearItemsTask(Player player)
		{
			_player = player;
		}
		
		@Override
		public void run()
		{
			try
			{
				_player.sendPacket(SystemMessageId.YOU_ARE_NO_LONGER_TRYING_ON_EQUIPMENT_2);
				_player.sendPacket(new ExUserInfoEquipSlot(_player));
			}
			catch (Exception e)
			{
				PacketLogger.warning(getClass().getSimpleName() + ": " + e.getMessage());
			}
		}
	}
	
	@Override
	protected void readImpl()
	{
		_unk = readInt();
		_listId = readInt();
		_count = readInt();
		if (_count < 0)
		{
			_count = 0;
		}
		if (_count > 100)
		{
			return; // prevent too long lists
		}
		
		// Create _items table that will contain all ItemID to Wear
		_items = new int[_count];
		
		// Fill _items table with all ItemID to Wear
		for (int i = 0; i < _count; i++)
		{
			_items[i] = readInt();
		}
	}
	
	@Override
	protected void runImpl()
	{
		if (_items == null)
		{
			return;
		}
		
		// Get the current player and return if null
		final Player player = getPlayer();
		if (player == null)
		{
			return;
		}
		
		if (!getClient().getFloodProtectors().canPerformTransaction())
		{
			player.sendMessage("You are buying too fast.");
			return;
		}
		
		// If Alternate rule Karma punishment is set to true, forbid Wear to player with Karma
		if (!Config.ALT_GAME_KARMA_PLAYER_CAN_SHOP && (player.getReputation() < 0))
		{
			return;
		}
		
		// Check current target of the player and the INTERACTION_DISTANCE
		final WorldObject target = player.getTarget();
		if (!player.isGM() && ((target == null) // No target (i.e. GM Shop)
			|| !(target instanceof Merchant) // Target not a merchant
			|| !player.isInsideRadius2D(target, Npc.INTERACTION_DISTANCE) // Distance is too far
		))
		{
			return;
		}
		
		if ((_count < 1) || (_listId >= 4000000))
		{
			player.sendPacket(ActionFailed.STATIC_PACKET);
			return;
		}
		
		// Get the current merchant targeted by the player
		final Merchant merchant = (target instanceof Merchant) ? (Merchant) target : null;
		if (merchant == null)
		{
			PacketLogger.warning(getClass().getSimpleName() + ": Null merchant!");
			return;
		}
		
		final ProductList buyList = BuyListData.getInstance().getBuyList(_listId);
		if (buyList == null)
		{
			Util.handleIllegalPlayerAction(player, "Warning!! Character " + player.getName() + " of account " + player.getAccountName() + " sent a false BuyList list_id " + _listId, Config.DEFAULT_PUNISH);
			return;
		}
		
		long totalPrice = 0;
		final Map<Integer, Integer> itemList = new HashMap<>();
		for (int i = 0; i < _count; i++)
		{
			final int itemId = _items[i];
			final Product product = buyList.getProductByItemId(itemId);
			if (product == null)
			{
				Util.handleIllegalPlayerAction(player, "Warning!! Character " + player.getName() + " of account " + player.getAccountName() + " sent a false BuyList list_id " + _listId + " and item_id " + itemId, Config.DEFAULT_PUNISH);
				return;
			}
			
			final ItemTemplate template = product.getItem();
			if (template == null)
			{
				continue;
			}
			
			final int slot = Inventory.getPaperdollIndex(template.getBodyPart());
			if (slot < 0)
			{
				continue;
			}
			
			if (template instanceof Weapon)
			{
				if (player.getRace() == Race.KAMAEL)
				{
					if (template.getItemType() == WeaponType.NONE)
					{
						continue;
					}
					else if ((template.getItemType() == WeaponType.RAPIER) || (template.getItemType() == WeaponType.CROSSBOW) || (template.getItemType() == WeaponType.ANCIENTSWORD))
					{
						continue;
					}
				}
			}
			else if (template instanceof Armor)
			{
				if ((player.getRace() == Race.KAMAEL) && ((template.getItemType() == ArmorType.HEAVY) || (template.getItemType() == ArmorType.MAGIC)))
				{
					continue;
				}
			}
			
			if (itemList.containsKey(slot))
			{
				player.sendPacket(SystemMessageId.YOU_CAN_NOT_TRY_THOSE_ITEMS_ON_AT_THE_SAME_TIME);
				return;
			}
			
			itemList.put(slot, itemId);
			totalPrice += Config.WEAR_PRICE;
			if (totalPrice > Inventory.MAX_ADENA)
			{
				Util.handleIllegalPlayerAction(player, "Warning!! Character " + player.getName() + " of account " + player.getAccountName() + " tried to purchase over " + Inventory.MAX_ADENA + " adena worth of goods.", Config.DEFAULT_PUNISH);
				return;
			}
		}
		
		// Charge buyer and add tax to castle treasury if not owned by npc clan because a Try On is not Free
		if ((totalPrice < 0) || !player.reduceAdena("Wear", totalPrice, player.getLastFolkNPC(), true))
		{
			player.sendPacket(SystemMessageId.YOU_DO_NOT_HAVE_ENOUGH_ADENA);
			return;
		}
		
		if (!itemList.isEmpty())
		{
			player.sendPacket(new ShopPreviewInfo(itemList));
			// Schedule task
			ThreadPool.schedule(new RemoveWearItemsTask(player), Config.WEAR_DELAY * 1000);
		}
	}
}
