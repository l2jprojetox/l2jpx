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

import static org.l2jpx.gameserver.model.itemcontainer.Inventory.MAX_ADENA;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.l2jpx.Config;
import org.l2jpx.gameserver.data.xml.ItemData;
import org.l2jpx.gameserver.instancemanager.CastleManager;
import org.l2jpx.gameserver.instancemanager.CastleManorManager;
import org.l2jpx.gameserver.model.SeedProduction;
import org.l2jpx.gameserver.model.actor.Npc;
import org.l2jpx.gameserver.model.actor.Player;
import org.l2jpx.gameserver.model.actor.instance.Merchant;
import org.l2jpx.gameserver.model.holders.ItemHolder;
import org.l2jpx.gameserver.model.item.ItemTemplate;
import org.l2jpx.gameserver.model.siege.Castle;
import org.l2jpx.gameserver.network.SystemMessageId;
import org.l2jpx.gameserver.network.serverpackets.ActionFailed;
import org.l2jpx.gameserver.network.serverpackets.SystemMessage;
import org.l2jpx.gameserver.util.Util;

/**
 * @author l3x
 */
public class RequestBuySeed extends ClientPacket
{
	private static final int BATCH_LENGTH = 12; // length of the one item
	private int _manorId;
	private List<ItemHolder> _items = null;
	
	@Override
	protected void readImpl()
	{
		_manorId = readInt();
		final int count = readInt();
		if ((count <= 0) || (count > Config.MAX_ITEM_IN_PACKET) || ((count * BATCH_LENGTH) != remaining()))
		{
			return;
		}
		
		_items = new ArrayList<>(count);
		for (int i = 0; i < count; i++)
		{
			final int itemId = readInt();
			final long cnt = readLong();
			if ((cnt < 1) || (itemId < 1))
			{
				_items = null;
				return;
			}
			_items.add(new ItemHolder(itemId, cnt));
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
		else if (!getClient().getFloodProtectors().canPerformTransaction())
		{
			player.sendMessage("You are buying seeds too fast!");
			return;
		}
		else if (_items == null)
		{
			player.sendPacket(ActionFailed.STATIC_PACKET);
			return;
		}
		
		final CastleManorManager manor = CastleManorManager.getInstance();
		if (manor.isUnderMaintenance())
		{
			player.sendPacket(ActionFailed.STATIC_PACKET);
			return;
		}
		
		final Castle castle = CastleManager.getInstance().getCastleById(_manorId);
		if (castle == null)
		{
			player.sendPacket(ActionFailed.STATIC_PACKET);
			return;
		}
		
		final Npc manager = player.getLastFolkNPC();
		if (!(manager instanceof Merchant) || !manager.canInteract(player) || (manager.getParameters().getInt("manor_id", -1) != _manorId))
		{
			player.sendPacket(ActionFailed.STATIC_PACKET);
			return;
		}
		
		long totalPrice = 0;
		int slots = 0;
		int totalWeight = 0;
		
		final Map<Integer, SeedProduction> productInfo = new HashMap<>();
		for (ItemHolder ih : _items)
		{
			final SeedProduction sp = manor.getSeedProduct(_manorId, ih.getId(), false);
			if ((sp == null) || (sp.getPrice() <= 0) || (sp.getAmount() < ih.getCount()) || ((MAX_ADENA / ih.getCount()) < sp.getPrice()))
			{
				player.sendPacket(ActionFailed.STATIC_PACKET);
				return;
			}
			
			// Calculate price
			totalPrice += (sp.getPrice() * ih.getCount());
			if (totalPrice > MAX_ADENA)
			{
				Util.handleIllegalPlayerAction(player, "Warning!! Character " + player.getName() + " of account " + player.getAccountName() + " tried to purchase over " + MAX_ADENA + " adena worth of goods.", Config.DEFAULT_PUNISH);
				player.sendPacket(ActionFailed.STATIC_PACKET);
				return;
			}
			
			// Calculate weight
			final ItemTemplate template = ItemData.getInstance().getTemplate(ih.getId());
			totalWeight += ih.getCount() * template.getWeight();
			
			// Calculate slots
			if (!template.isStackable())
			{
				slots += ih.getCount();
			}
			else if (player.getInventory().getItemByItemId(ih.getId()) == null)
			{
				slots++;
			}
			productInfo.put(ih.getId(), sp);
		}
		
		if (!player.getInventory().validateWeight(totalWeight))
		{
			player.sendPacket(SystemMessageId.YOU_HAVE_EXCEEDED_THE_WEIGHT_LIMIT);
			return;
		}
		else if (!player.getInventory().validateCapacity(slots))
		{
			player.sendPacket(SystemMessageId.YOUR_INVENTORY_IS_FULL);
			return;
		}
		else if ((totalPrice < 0) || (player.getAdena() < totalPrice))
		{
			player.sendPacket(SystemMessageId.YOU_DO_NOT_HAVE_ENOUGH_ADENA);
			return;
		}
		
		// Proceed the purchase
		for (ItemHolder i : _items)
		{
			final SeedProduction sp = productInfo.get(i.getId());
			final long price = sp.getPrice() * i.getCount();
			
			// Take Adena and decrease seed amount
			if (!sp.decreaseAmount(i.getCount()) || !player.reduceAdena("Buy", price, player, false))
			{
				// failed buy, reduce total price
				totalPrice -= price;
				continue;
			}
			
			// Add item to player's inventory
			player.addItem("Buy", i.getId(), i.getCount(), manager, true);
		}
		
		// Adding to treasury for Manor Castle
		if (totalPrice > 0)
		{
			castle.addToTreasuryNoTax(totalPrice);
			
			final SystemMessage sm = new SystemMessage(SystemMessageId.S1_ADENA_DISAPPEARED);
			sm.addLong(totalPrice);
			player.sendPacket(sm);
			
			if (Config.ALT_MANOR_SAVE_ALL_ACTIONS)
			{
				manor.updateCurrentProduction(_manorId, productInfo.values());
			}
		}
	}
}
