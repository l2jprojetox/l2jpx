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

import static org.l2jpx.gameserver.model.actor.Npc.INTERACTION_DISTANCE;
import static org.l2jpx.gameserver.model.itemcontainer.Inventory.MAX_ADENA;

import java.util.ArrayList;
import java.util.List;

import org.l2jpx.Config;
import org.l2jpx.gameserver.data.xml.BuyListData;
import org.l2jpx.gameserver.enums.TaxType;
import org.l2jpx.gameserver.model.WorldObject;
import org.l2jpx.gameserver.model.actor.Player;
import org.l2jpx.gameserver.model.actor.instance.Merchant;
import org.l2jpx.gameserver.model.buylist.Product;
import org.l2jpx.gameserver.model.buylist.ProductList;
import org.l2jpx.gameserver.model.holders.ItemHolder;
import org.l2jpx.gameserver.network.PacketLogger;
import org.l2jpx.gameserver.network.SystemMessageId;
import org.l2jpx.gameserver.network.serverpackets.ActionFailed;
import org.l2jpx.gameserver.network.serverpackets.ExBuySellList;
import org.l2jpx.gameserver.network.serverpackets.ExUserInfoInvenWeight;
import org.l2jpx.gameserver.util.Util;

public class RequestBuyItem extends ClientPacket
{
	private static final int BATCH_LENGTH = 12;
	private static final int CUSTOM_CB_SELL_LIST = 423;
	
	private int _listId;
	private List<ItemHolder> _items = null;
	
	@Override
	protected void readImpl()
	{
		_listId = readInt();
		final int size = readInt();
		if ((size <= 0) || (size > Config.MAX_ITEM_IN_PACKET) || ((size * BATCH_LENGTH) != remaining()))
		{
			return;
		}
		
		_items = new ArrayList<>(size);
		for (int i = 0; i < size; i++)
		{
			final int itemId = readInt();
			final long count = readLong();
			if ((itemId < 1) || (count < 1))
			{
				_items = null;
				return;
			}
			_items.add(new ItemHolder(itemId, count));
		}
	}
	
	@Override
	protected void runImpl()
	{
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
		
		if (_items == null)
		{
			player.sendPacket(ActionFailed.STATIC_PACKET);
			return;
		}
		
		// Alt game - Karma punishment
		if (!Config.ALT_GAME_KARMA_PLAYER_CAN_SHOP && (player.getReputation() < 0))
		{
			player.sendPacket(ActionFailed.STATIC_PACKET);
			return;
		}
		
		final WorldObject target = player.getTarget();
		Merchant merchant = null;
		if (!player.isGM() && (_listId != CUSTOM_CB_SELL_LIST))
		{
			if (!(target instanceof Merchant) || (!player.isInsideRadius3D(target, INTERACTION_DISTANCE)) || (player.getInstanceWorld() != target.getInstanceWorld()))
			{
				player.sendPacket(ActionFailed.STATIC_PACKET);
				return;
			}
			merchant = (Merchant) target; // FIXME: Doesn't work for GMs.
		}
		
		if ((merchant == null) && !player.isGM() && (_listId != CUSTOM_CB_SELL_LIST))
		{
			player.sendPacket(ActionFailed.STATIC_PACKET);
			return;
		}
		
		final ProductList buyList = BuyListData.getInstance().getBuyList(_listId);
		if (buyList == null)
		{
			Util.handleIllegalPlayerAction(player, "Warning!! Character " + player.getName() + " of account " + player.getAccountName() + " sent a false BuyList list_id " + _listId, Config.DEFAULT_PUNISH);
			return;
		}
		
		double castleTaxRate = 0;
		if (merchant != null)
		{
			if (!buyList.isNpcAllowed(merchant.getId()))
			{
				player.sendPacket(ActionFailed.STATIC_PACKET);
				return;
			}
			castleTaxRate = merchant.getCastleTaxRate(TaxType.BUY);
		}
		
		long subTotal = 0;
		
		// Check for buylist validity and calculates summary values
		long slots = 0;
		long weight = 0;
		for (ItemHolder i : _items)
		{
			final Product product = buyList.getProductByItemId(i.getId());
			if (product == null)
			{
				Util.handleIllegalPlayerAction(player, "Warning!! Character " + player.getName() + " of account " + player.getAccountName() + " sent a false BuyList list_id " + _listId + " and item_id " + i.getId(), Config.DEFAULT_PUNISH);
				return;
			}
			
			if (!product.getItem().isStackable() && (i.getCount() > 1))
			{
				Util.handleIllegalPlayerAction(player, "Warning!! Character " + player.getName() + " of account " + player.getAccountName() + " tried to purchase invalid quantity of items at the same time.", Config.DEFAULT_PUNISH);
				player.sendPacket(SystemMessageId.YOU_HAVE_EXCEEDED_THE_QUANTITY_THAT_CAN_BE_INPUTTED);
				return;
			}
			
			long price = product.getPrice();
			if (price < 0)
			{
				PacketLogger.warning("ERROR, no price found .. wrong buylist ??");
				player.sendPacket(ActionFailed.STATIC_PACKET);
				return;
			}
			
			if ((price == 0) && !player.isGM() && Config.ONLY_GM_ITEMS_FREE)
			{
				player.sendMessage("Ohh Cheat does not work? You have a problem now!");
				Util.handleIllegalPlayerAction(player, "Warning!! Character " + player.getName() + " of account " + player.getAccountName() + " tried buy item for 0 adena.", Config.DEFAULT_PUNISH);
				return;
			}
			
			// trying to buy more then available
			if (product.hasLimitedStock() && (i.getCount() > product.getCount()))
			{
				player.sendPacket(ActionFailed.STATIC_PACKET);
				return;
			}
			
			if ((MAX_ADENA / i.getCount()) < price)
			{
				Util.handleIllegalPlayerAction(player, "Warning!! Character " + player.getName() + " of account " + player.getAccountName() + " tried to purchase over " + MAX_ADENA + " adena worth of goods.", Config.DEFAULT_PUNISH);
				return;
			}
			// first calculate price per item with tax, then multiply by count
			price = (long) (price * (1 + castleTaxRate + product.getBaseTaxRate()));
			subTotal += i.getCount() * price;
			if (subTotal > MAX_ADENA)
			{
				Util.handleIllegalPlayerAction(player, "Warning!! Character " + player.getName() + " of account " + player.getAccountName() + " tried to purchase over " + MAX_ADENA + " adena worth of goods.", Config.DEFAULT_PUNISH);
				return;
			}
			
			weight += i.getCount() * product.getItem().getWeight();
			if (player.getInventory().getItemByItemId(product.getItemId()) == null)
			{
				slots++;
			}
		}
		
		if (!player.isGM() && ((weight > Integer.MAX_VALUE) || (weight < 0) || !player.getInventory().validateWeight((int) weight)))
		{
			player.sendPacket(SystemMessageId.YOU_HAVE_EXCEEDED_THE_WEIGHT_LIMIT);
			player.sendPacket(ActionFailed.STATIC_PACKET);
			return;
		}
		
		if (!player.isGM() && ((slots > Integer.MAX_VALUE) || (slots < 0) || !player.getInventory().validateCapacity((int) slots)))
		{
			player.sendPacket(SystemMessageId.YOUR_INVENTORY_IS_FULL);
			player.sendPacket(ActionFailed.STATIC_PACKET);
			return;
		}
		
		// Charge buyer and add tax to castle treasury if not owned by npc clan
		if ((subTotal < 0) || !player.reduceAdena("Buy", subTotal, player.getLastFolkNPC(), false))
		{
			player.sendPacket(SystemMessageId.YOU_DO_NOT_HAVE_ENOUGH_ADENA);
			player.sendPacket(ActionFailed.STATIC_PACKET);
			return;
		}
		
		// Proceed the purchase
		for (ItemHolder i : _items)
		{
			final Product product = buyList.getProductByItemId(i.getId());
			if (product == null)
			{
				Util.handleIllegalPlayerAction(player, "Warning!! Character " + player.getName() + " of account " + player.getAccountName() + " sent a false BuyList list_id " + _listId + " and item_id " + i.getId(), Config.DEFAULT_PUNISH);
				continue;
			}
			
			if (product.hasLimitedStock())
			{
				if (product.decreaseCount(i.getCount()))
				{
					player.getInventory().addItem("Buy", i.getId(), i.getCount(), player, merchant);
				}
			}
			else
			{
				player.getInventory().addItem("Buy", i.getId(), i.getCount(), player, merchant);
			}
		}
		
		// add to castle treasury
		if (merchant != null)
		{
			merchant.handleTaxPayment((long) (subTotal * castleTaxRate));
		}
		
		player.sendPacket(new ExUserInfoInvenWeight(player));
		player.sendPacket(new ExBuySellList(player, true));
		player.sendPacket(SystemMessageId.EXCHANGE_IS_SUCCESSFUL);
	}
}
