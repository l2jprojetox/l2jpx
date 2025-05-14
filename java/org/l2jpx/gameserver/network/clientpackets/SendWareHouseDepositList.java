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

import static org.l2jpx.gameserver.model.itemcontainer.Inventory.ADENA_ID;

import java.util.ArrayList;
import java.util.List;

import org.l2jpx.Config;
import org.l2jpx.gameserver.model.actor.Npc;
import org.l2jpx.gameserver.model.actor.Player;
import org.l2jpx.gameserver.model.holders.ItemHolder;
import org.l2jpx.gameserver.model.item.instance.Item;
import org.l2jpx.gameserver.model.itemcontainer.ItemContainer;
import org.l2jpx.gameserver.model.itemcontainer.PlayerWarehouse;
import org.l2jpx.gameserver.network.PacketLogger;
import org.l2jpx.gameserver.network.SystemMessageId;
import org.l2jpx.gameserver.network.serverpackets.InventoryUpdate;
import org.l2jpx.gameserver.util.Util;

/**
 * SendWareHouseDepositList client packet class.
 */
public class SendWareHouseDepositList extends ClientPacket
{
	private static final int BATCH_LENGTH = 12;
	
	private List<ItemHolder> _items = null;
	
	@Override
	protected void readImpl()
	{
		final int size = readInt();
		if ((size <= 0) || (size > Config.MAX_ITEM_IN_PACKET) || ((size * BATCH_LENGTH) != remaining()))
		{
			return;
		}
		
		_items = new ArrayList<>(size);
		for (int i = 0; i < size; i++)
		{
			final int objId = readInt();
			final long count = readLong();
			if ((objId < 1) || (count < 1))
			{
				_items = null;
				return;
			}
			_items.add(new ItemHolder(objId, count));
		}
	}
	
	@Override
	protected void runImpl()
	{
		if (_items == null)
		{
			return;
		}
		
		final Player player = getPlayer();
		if (player == null)
		{
			return;
		}
		
		if (!getClient().getFloodProtectors().canPerformTransaction())
		{
			player.sendMessage("You are depositing items too fast.");
			return;
		}
		
		final ItemContainer warehouse = player.getActiveWarehouse();
		if (warehouse == null)
		{
			return;
		}
		
		final Npc manager = player.getLastFolkNPC();
		if (((manager == null) || !manager.isWarehouse() || !manager.canInteract(player)) && !player.isGM())
		{
			player.sendPacket(SystemMessageId.YOU_FAILED_AT_SENDING_THE_PACKAGE_BECAUSE_YOU_ARE_TOO_FAR_FROM_THE_WAREHOUSE);
			return;
		}
		
		final boolean isPrivate = warehouse instanceof PlayerWarehouse;
		if (!isPrivate && !player.getAccessLevel().allowTransaction())
		{
			player.sendMessage("Transactions are disabled for your Access Level.");
			return;
		}
		
		if (player.hasItemRequest())
		{
			Util.handleIllegalPlayerAction(player, player + " tried to use enchant Exploit!", Config.DEFAULT_PUNISH);
			return;
		}
		
		// Alt game - Karma punishment
		if (!Config.ALT_GAME_KARMA_PLAYER_CAN_USE_WAREHOUSE && (player.getReputation() < 0))
		{
			return;
		}
		
		// Freight price from config or normal price per item slot (30)
		final long fee = _items.size() * 30;
		long currentAdena = player.getAdena();
		int slots = 0;
		for (ItemHolder itemHolder : _items)
		{
			final Item item = player.checkItemManipulation(itemHolder.getId(), itemHolder.getCount(), "deposit");
			if (item == null)
			{
				PacketLogger.warning("Error depositing a warehouse object for char " + player.getName() + " (validity check)");
				return;
			}
			
			// Calculate needed adena and slots
			if (item.getId() == ADENA_ID)
			{
				currentAdena -= itemHolder.getCount();
			}
			if (!item.isStackable())
			{
				slots += itemHolder.getCount();
			}
			else if (warehouse.getItemByItemId(item.getId()) == null)
			{
				slots++;
			}
		}
		
		// Item Max Limit Check
		if (!warehouse.validateCapacity(slots))
		{
			player.sendPacket(SystemMessageId.YOU_HAVE_EXCEEDED_THE_QUANTITY_THAT_CAN_BE_INPUTTED);
			return;
		}
		
		// Check if enough adena and charge the fee
		if ((currentAdena < fee) || !player.reduceAdena(warehouse.getName(), fee, manager, false))
		{
			player.sendPacket(SystemMessageId.YOU_DO_NOT_HAVE_ENOUGH_ADENA);
			return;
		}
		
		// get current tradelist if any
		if (player.getActiveTradeList() != null)
		{
			return;
		}
		
		// Proceed to the transfer
		final InventoryUpdate playerIU = new InventoryUpdate();
		for (ItemHolder itemHolder : _items)
		{
			// Check validity of requested item
			final Item oldItem = player.checkItemManipulation(itemHolder.getId(), itemHolder.getCount(), "deposit");
			if (oldItem == null)
			{
				PacketLogger.warning("Error depositing a warehouse object for char " + player.getName() + " (olditem == null)");
				return;
			}
			
			if (!oldItem.isDepositable(isPrivate) || !oldItem.isAvailable(player, true, isPrivate))
			{
				continue;
			}
			
			final Item newItem = player.getInventory().transferItem(warehouse.getName(), itemHolder.getId(), itemHolder.getCount(), warehouse, player, manager);
			if (newItem == null)
			{
				PacketLogger.warning("Error depositing a warehouse object for char " + player.getName() + " (newitem == null)");
				continue;
			}
			
			if ((oldItem.getCount() > 0) && (oldItem != newItem))
			{
				playerIU.addModifiedItem(oldItem);
			}
			else
			{
				playerIU.addRemovedItem(oldItem);
			}
		}
		
		// Send updated item list to the player
		player.sendInventoryUpdate(playerIU);
	}
}
