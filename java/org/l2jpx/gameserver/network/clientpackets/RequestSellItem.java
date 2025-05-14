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
import org.l2jpx.gameserver.model.WorldObject;
import org.l2jpx.gameserver.model.actor.Player;
import org.l2jpx.gameserver.model.actor.instance.Merchant;
import org.l2jpx.gameserver.model.buylist.ProductList;
import org.l2jpx.gameserver.model.holders.UniqueItemHolder;
import org.l2jpx.gameserver.model.item.instance.Item;
import org.l2jpx.gameserver.network.SystemMessageId;
import org.l2jpx.gameserver.network.serverpackets.ActionFailed;
import org.l2jpx.gameserver.network.serverpackets.ExBuySellList;
import org.l2jpx.gameserver.network.serverpackets.ExUserInfoInvenWeight;
import org.l2jpx.gameserver.util.Util;

/**
 * RequestSellItem client packet class.
 */
public class RequestSellItem extends ClientPacket
{
	private static final int BATCH_LENGTH = 16;
	private static final int CUSTOM_CB_SELL_LIST = 423;
	
	private int _listId;
	private List<UniqueItemHolder> _items = null;
	
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
			final int objectId = readInt();
			final int itemId = readInt();
			final long count = readLong();
			if ((objectId < 1) || (itemId < 1) || (count < 1))
			{
				_items = null;
				return;
			}
			_items.add(new UniqueItemHolder(itemId, objectId, count));
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
			if ((target == null) || !player.isInsideRadius3D(target, INTERACTION_DISTANCE) || (player.getInstanceId() != target.getInstanceId()))
			{
				player.sendPacket(ActionFailed.STATIC_PACKET);
				return;
			}
			if (target instanceof Merchant)
			{
				merchant = (Merchant) target;
			}
			else
			{
				player.sendPacket(ActionFailed.STATIC_PACKET);
				return;
			}
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
		
		if ((merchant != null) && !buyList.isNpcAllowed(merchant.getId()))
		{
			player.sendPacket(ActionFailed.STATIC_PACKET);
			return;
		}
		
		long totalPrice = 0;
		// Proceed the sell
		for (UniqueItemHolder i : _items)
		{
			final Item item = player.checkItemManipulation(i.getObjectId(), i.getCount(), "sell");
			if ((item == null) || (!item.isSellable()))
			{
				continue;
			}
			
			final long price = item.getReferencePrice() / 2;
			totalPrice += price * i.getCount();
			if (((MAX_ADENA / i.getCount()) < price) || (totalPrice > MAX_ADENA))
			{
				Util.handleIllegalPlayerAction(player, "Warning!! Character " + player.getName() + " of account " + player.getAccountName() + " tried to purchase over " + MAX_ADENA + " adena worth of goods.", Config.DEFAULT_PUNISH);
				return;
			}
			
			if (Config.ALLOW_REFUND)
			{
				player.getInventory().transferItem("Sell", i.getObjectId(), i.getCount(), player.getRefund(), player, merchant);
			}
			else
			{
				player.getInventory().destroyItem("Sell", i.getObjectId(), i.getCount(), player, merchant);
			}
		}
		
		if (!Config.MERCHANT_ZERO_SELL_PRICE)
		{
			player.addAdena("Sell", totalPrice, merchant, false);
		}
		
		// Update current load as well
		player.sendPacket(new ExUserInfoInvenWeight(player));
		player.sendPacket(new ExBuySellList(player, true));
		player.sendPacket(SystemMessageId.EXCHANGE_IS_SUCCESSFUL);
	}
}
