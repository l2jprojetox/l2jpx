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

import org.l2jpx.Config;
import org.l2jpx.gameserver.model.actor.Npc;
import org.l2jpx.gameserver.model.actor.Player;
import org.l2jpx.gameserver.model.clan.ClanPrivilege;
import org.l2jpx.gameserver.model.holders.ItemHolder;
import org.l2jpx.gameserver.model.item.instance.Item;
import org.l2jpx.gameserver.model.itemcontainer.ClanWarehouse;
import org.l2jpx.gameserver.model.itemcontainer.ItemContainer;
import org.l2jpx.gameserver.model.itemcontainer.PlayerWarehouse;
import org.l2jpx.gameserver.network.PacketLogger;
import org.l2jpx.gameserver.network.SystemMessageId;
import org.l2jpx.gameserver.util.Util;

/**
 * @version $Revision: 1.2.2.1.2.4 $ $Date: 2005/03/29 23:15:16 $
 */
public class SendWareHouseWithDrawList extends ClientPacket
{
	private static final int BATCH_LENGTH = 12; // length of the one item
	
	private ItemHolder[] _items = null;
	
	@Override
	protected void readImpl()
	{
		final int count = readInt();
		if ((count <= 0) || (count > Config.MAX_ITEM_IN_PACKET) || ((count * BATCH_LENGTH) != remaining()))
		{
			return;
		}
		
		_items = new ItemHolder[count];
		for (int i = 0; i < count; i++)
		{
			final int objId = readInt();
			final long cnt = readLong();
			if ((objId < 1) || (cnt < 0))
			{
				_items = null;
				return;
			}
			_items[i] = new ItemHolder(objId, cnt);
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
			player.sendMessage("You are withdrawing items too fast.");
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
		
		if (!(warehouse instanceof PlayerWarehouse) && !player.getAccessLevel().allowTransaction())
		{
			player.sendMessage("Transactions are disabled for your Access Level.");
			return;
		}
		
		// Alt game - Karma punishment
		if (!Config.ALT_GAME_KARMA_PLAYER_CAN_USE_WAREHOUSE && (player.getReputation() < 0))
		{
			return;
		}
		
		if (Config.ALT_MEMBERS_CAN_WITHDRAW_FROM_CLANWH)
		{
			if ((warehouse instanceof ClanWarehouse) && !player.hasClanPrivilege(ClanPrivilege.CL_VIEW_WAREHOUSE))
			{
				return;
			}
		}
		else if ((warehouse instanceof ClanWarehouse) && !player.isClanLeader())
		{
			player.sendPacket(SystemMessageId.ITEMS_LEFT_AT_THE_CLAN_HALL_WAREHOUSE_CAN_ONLY_BE_RETRIEVED_BY_THE_CLAN_LEADER_DO_YOU_WANT_TO_CONTINUE);
			return;
		}
		
		int weight = 0;
		int slots = 0;
		for (ItemHolder i : _items)
		{
			// Calculate needed slots
			final Item item = warehouse.getItemByObjectId(i.getId());
			if ((item == null) || (item.getCount() < i.getCount()))
			{
				Util.handleIllegalPlayerAction(player, "Warning!! Character " + player.getName() + " of account " + player.getAccountName() + " tried to withdraw non-existent item from warehouse.", Config.DEFAULT_PUNISH);
				return;
			}
			
			weight += i.getCount() * item.getTemplate().getWeight();
			if (!item.isStackable())
			{
				slots += i.getCount();
			}
			else if (player.getInventory().getItemByItemId(item.getId()) == null)
			{
				slots++;
			}
		}
		
		// Item Max Limit Check
		if (!player.getInventory().validateCapacity(slots))
		{
			player.sendPacket(SystemMessageId.YOUR_INVENTORY_IS_FULL);
			return;
		}
		
		// Weight limit Check
		if (!player.getInventory().validateWeight(weight))
		{
			player.sendPacket(SystemMessageId.YOU_HAVE_EXCEEDED_THE_WEIGHT_LIMIT);
			return;
		}
		
		// Proceed to the transfer
		for (ItemHolder i : _items)
		{
			final Item oldItem = warehouse.getItemByObjectId(i.getId());
			if ((oldItem == null) || (oldItem.getCount() < i.getCount()))
			{
				PacketLogger.warning("Error withdrawing a warehouse object for char " + player.getName() + " (olditem == null)");
				return;
			}
			final Item newItem = warehouse.transferItem(warehouse.getName(), i.getId(), i.getCount(), player.getInventory(), player, manager);
			if (newItem == null)
			{
				PacketLogger.warning("Error withdrawing a warehouse object for char " + player.getName() + " (newitem == null)");
				return;
			}
		}
		
		// Send updated item list to the player
		player.sendItemList(false);
	}
}
