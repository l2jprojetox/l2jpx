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
package org.l2jpx.gameserver.network.clientpackets.primeshop;

import java.util.Calendar;

import org.l2jpx.Config;
import org.l2jpx.commons.threads.ThreadPool;
import org.l2jpx.gameserver.data.xml.PrimeShopData;
import org.l2jpx.gameserver.enums.ExBrProductReplyType;
import org.l2jpx.gameserver.model.actor.Player;
import org.l2jpx.gameserver.model.actor.request.PrimeShopRequest;
import org.l2jpx.gameserver.model.itemcontainer.Inventory;
import org.l2jpx.gameserver.model.primeshop.PrimeShopGroup;
import org.l2jpx.gameserver.model.primeshop.PrimeShopItem;
import org.l2jpx.gameserver.model.variables.AccountVariables;
import org.l2jpx.gameserver.network.clientpackets.ClientPacket;
import org.l2jpx.gameserver.network.serverpackets.primeshop.ExBRBuyProduct;
import org.l2jpx.gameserver.network.serverpackets.primeshop.ExBRGamePoint;
import org.l2jpx.gameserver.util.Util;

/**
 * @author Gnacik, UnAfraid
 */
public class RequestBRBuyProduct extends ClientPacket
{
	private static final int HERO_COINS = 23805;
	
	private int _brId;
	private int _count;
	
	@Override
	protected void readImpl()
	{
		_brId = readInt();
		_count = readInt();
	}
	
	@Override
	protected void runImpl()
	{
		final Player player = getPlayer();
		if (player == null)
		{
			return;
		}
		
		if (player.hasItemRequest() || player.hasRequest(PrimeShopRequest.class))
		{
			player.sendPacket(new ExBRBuyProduct(ExBrProductReplyType.INVALID_USER_STATE));
			return;
		}
		
		player.addRequest(new PrimeShopRequest(player));
		
		final PrimeShopGroup item = PrimeShopData.getInstance().getItem(_brId);
		if (validatePlayer(item, _count, player))
		{
			final int price = item.getPrice() * _count;
			if (price < 1)
			{
				player.sendPacket(new ExBRBuyProduct(ExBrProductReplyType.LACK_OF_POINT));
				player.removeRequest(PrimeShopRequest.class);
				return;
			}
			
			final int paymentId = validatePaymentId(item);
			if (paymentId < 0)
			{
				player.sendPacket(new ExBRBuyProduct(ExBrProductReplyType.LACK_OF_POINT));
				player.removeRequest(PrimeShopRequest.class);
				return;
			}
			else if (paymentId > 0)
			{
				if (!player.destroyItemByItemId("PrimeShop-" + item.getBrId(), paymentId, price, player, true))
				{
					player.sendPacket(new ExBRBuyProduct(ExBrProductReplyType.LACK_OF_POINT));
					player.removeRequest(PrimeShopRequest.class);
					return;
				}
			}
			else if (paymentId == 0)
			{
				if (player.getPrimePoints() < price)
				{
					player.sendPacket(new ExBRBuyProduct(ExBrProductReplyType.LACK_OF_POINT));
					player.removeRequest(PrimeShopRequest.class);
					return;
				}
				player.setPrimePoints(player.getPrimePoints() - price);
			}
			
			for (PrimeShopItem subItem : item.getItems())
			{
				player.addItem("PrimeShop", subItem.getId(), subItem.getCount() * _count, player, true);
			}
			
			// Update account variables.
			if (item.getAccountDailyLimit() > 0)
			{
				player.getAccountVariables().set(AccountVariables.PRIME_SHOP_PRODUCT_DAILY_COUNT + item.getBrId(), player.getAccountVariables().getInt(AccountVariables.PRIME_SHOP_PRODUCT_DAILY_COUNT + item.getBrId(), 0) + (item.getCount() * _count));
			}
			else if (item.getAccountBuyLimit() > 0)
			{
				player.getAccountVariables().set(AccountVariables.PRIME_SHOP_PRODUCT_COUNT + item.getBrId(), player.getAccountVariables().getInt(AccountVariables.PRIME_SHOP_PRODUCT_COUNT + item.getBrId(), 0) + (item.getCount() * _count));
			}
			
			player.sendPacket(new ExBRBuyProduct(ExBrProductReplyType.SUCCESS));
			player.sendPacket(new ExBRGamePoint(player));
		}
		
		ThreadPool.schedule(() -> player.removeRequest(PrimeShopRequest.class), 1000);
	}
	
	/**
	 * @param item
	 * @param count
	 * @param player
	 * @return
	 */
	private static boolean validatePlayer(PrimeShopGroup item, int count, Player player)
	{
		final long currentTime = System.currentTimeMillis() / 1000;
		if (item == null)
		{
			player.sendPacket(new ExBRBuyProduct(ExBrProductReplyType.INVALID_PRODUCT));
			Util.handleIllegalPlayerAction(player, player + " tried to buy invalid brId from Prime", Config.DEFAULT_PUNISH);
			return false;
		}
		else if ((count < 1) || (count > 99))
		{
			Util.handleIllegalPlayerAction(player, player + " tried to buy invalid itemcount [" + count + "] from Prime", Config.DEFAULT_PUNISH);
			player.sendPacket(new ExBRBuyProduct(ExBrProductReplyType.INVALID_USER_STATE));
			return false;
		}
		else if ((item.getMinLevel() > 0) && (item.getMinLevel() > player.getLevel()))
		{
			player.sendPacket(new ExBRBuyProduct(ExBrProductReplyType.INVALID_USER));
			return false;
		}
		else if ((item.getMaxLevel() > 0) && (item.getMaxLevel() < player.getLevel()))
		{
			player.sendPacket(new ExBRBuyProduct(ExBrProductReplyType.INVALID_USER));
			return false;
		}
		else if ((item.getMinBirthday() > 0) && (item.getMinBirthday() > player.getBirthdays()))
		{
			player.sendPacket(new ExBRBuyProduct(ExBrProductReplyType.INVALID_USER_STATE));
			return false;
		}
		else if ((item.getMaxBirthday() > 0) && (item.getMaxBirthday() < player.getBirthdays()))
		{
			player.sendPacket(new ExBRBuyProduct(ExBrProductReplyType.INVALID_USER_STATE));
			return false;
		}
		else if ((Calendar.getInstance().get(Calendar.DAY_OF_WEEK) & item.getDaysOfWeek()) == 0)
		{
			player.sendPacket(new ExBRBuyProduct(ExBrProductReplyType.NOT_DAY_OF_WEEK));
			return false;
		}
		else if ((item.getStartSale() > 1) && (item.getStartSale() > currentTime))
		{
			player.sendPacket(new ExBRBuyProduct(ExBrProductReplyType.BEFORE_SALE_DATE));
			return false;
		}
		else if ((item.getEndSale() > 1) && (item.getEndSale() < currentTime))
		{
			player.sendPacket(new ExBRBuyProduct(ExBrProductReplyType.AFTER_SALE_DATE));
			return false;
		}
		else if ((item.getAccountDailyLimit() > 0) && ((count + player.getAccountVariables().getInt(AccountVariables.PRIME_SHOP_PRODUCT_DAILY_COUNT + item.getBrId(), 0)) > item.getAccountDailyLimit()))
		{
			player.sendPacket(new ExBRBuyProduct(ExBrProductReplyType.SOLD_OUT));
			return false;
		}
		else if ((item.getAccountBuyLimit() > 0) && ((count + player.getAccountVariables().getInt(AccountVariables.PRIME_SHOP_PRODUCT_COUNT + item.getBrId(), 0)) > item.getAccountBuyLimit()))
		{
			player.sendPacket(new ExBRBuyProduct(ExBrProductReplyType.SOLD_OUT));
			return false;
		}
		
		final int weight = item.getWeight() * count;
		final long slots = item.getCount() * count;
		if (player.getInventory().validateWeight(weight))
		{
			if (item.getCount() == 1)
			{
				if (!player.getInventory().validateCapacity(slots))
				{
					player.sendPacket(new ExBRBuyProduct(ExBrProductReplyType.INVENTORY_OVERFLOW));
					return false;
				}
			}
			else if (!player.getInventory().validateCapacity(count))
			{
				player.sendPacket(new ExBRBuyProduct(ExBrProductReplyType.INVENTORY_OVERFLOW));
				return false;
			}
		}
		else
		{
			player.sendPacket(new ExBRBuyProduct(ExBrProductReplyType.INVENTORY_OVERFLOW));
			return false;
		}
		
		return true;
	}
	
	private static int validatePaymentId(PrimeShopGroup item)
	{
		switch (item.getPaymentType())
		{
			case 0: // Prime points
			{
				return 0;
			}
			case 1: // Adenas
			{
				return Inventory.ADENA_ID;
			}
			case 2: // Hero coins
			{
				return HERO_COINS;
			}
		}
		return -1;
	}
}
