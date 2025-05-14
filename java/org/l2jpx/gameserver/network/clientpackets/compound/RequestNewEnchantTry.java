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
package org.l2jpx.gameserver.network.clientpackets.compound;

import org.l2jpx.commons.util.Rnd;
import org.l2jpx.gameserver.data.xml.CombinationItemsData;
import org.l2jpx.gameserver.model.actor.Player;
import org.l2jpx.gameserver.model.actor.request.CompoundRequest;
import org.l2jpx.gameserver.model.item.combination.CombinationItem;
import org.l2jpx.gameserver.model.item.combination.CombinationItemReward;
import org.l2jpx.gameserver.model.item.combination.CombinationItemType;
import org.l2jpx.gameserver.model.item.instance.Item;
import org.l2jpx.gameserver.network.SystemMessageId;
import org.l2jpx.gameserver.network.clientpackets.ClientPacket;
import org.l2jpx.gameserver.network.serverpackets.InventoryUpdate;
import org.l2jpx.gameserver.network.serverpackets.compound.ExEnchantFail;
import org.l2jpx.gameserver.network.serverpackets.compound.ExEnchantOneFail;
import org.l2jpx.gameserver.network.serverpackets.compound.ExEnchantSucess;

/**
 * @author UnAfraid
 */
public class RequestNewEnchantTry extends ClientPacket
{
	@Override
	protected void readImpl()
	{
	}
	
	@Override
	protected void runImpl()
	{
		final Player player = getPlayer();
		if (player == null)
		{
			return;
		}
		
		if (player.isInStoreMode())
		{
			player.sendPacket(SystemMessageId.YOU_CANNOT_DO_THAT_WHILE_IN_A_PRIVATE_STORE_OR_PRIVATE_WORKSHOP);
			player.sendPacket(ExEnchantOneFail.STATIC_PACKET);
			return;
		}
		
		if (player.isProcessingTransaction() || player.isProcessingRequest())
		{
			player.sendPacket(SystemMessageId.YOU_CANNOT_USE_THIS_SYSTEM_DURING_TRADING_PRIVATE_STORE_AND_WORKSHOP_SETUP);
			player.sendPacket(ExEnchantOneFail.STATIC_PACKET);
			return;
		}
		
		final CompoundRequest request = player.getRequest(CompoundRequest.class);
		if ((request == null) || request.isProcessing())
		{
			player.sendPacket(ExEnchantFail.STATIC_PACKET);
			return;
		}
		
		request.setProcessing(true);
		
		final Item itemOne = request.getItemOne();
		final Item itemTwo = request.getItemTwo();
		if ((itemOne == null) || (itemTwo == null))
		{
			player.sendPacket(ExEnchantFail.STATIC_PACKET);
			player.removeRequest(request.getClass());
			return;
		}
		
		// Lets prevent using same item twice. Also stackable item check.
		if ((itemOne.getObjectId() == itemTwo.getObjectId()) && (!itemOne.isStackable() || (player.getInventory().getInventoryItemCount(itemOne.getTemplate().getId(), -1) < 2)))
		{
			player.sendPacket(new ExEnchantFail(itemOne.getId(), itemTwo.getId()));
			player.removeRequest(request.getClass());
			return;
		}
		
		final CombinationItem combinationItem = CombinationItemsData.getInstance().getItemsBySlots(itemOne.getId(), itemTwo.getId());
		
		// Not implemented or not able to merge!
		if (combinationItem == null)
		{
			player.sendPacket(new ExEnchantFail(itemOne.getId(), itemTwo.getId()));
			player.removeRequest(request.getClass());
			return;
		}
		
		if (player.destroyItem("Compound-Item-One", itemOne, 1, null, true) && player.destroyItem("Compound-Item-Two", itemTwo, 1, null, true))
		{
			final double random = (Rnd.nextDouble() * 100);
			final boolean success = random <= combinationItem.getChance();
			final CombinationItemReward rewardItem = combinationItem.getReward(success ? CombinationItemType.ON_SUCCESS : CombinationItemType.ON_FAILURE);
			final Item item = player.addItem("Compound-Result", rewardItem.getId(), rewardItem.getCount(), null, true);
			if (success)
			{
				player.sendPacket(new ExEnchantSucess(item.getId()));
			}
			else
			{
				player.sendPacket(new ExEnchantFail(itemOne.getId(), itemTwo.getId()));
			}
		}
		
		final InventoryUpdate iu = new InventoryUpdate();
		if (itemOne.isStackable() && (itemOne.getCount() > 0))
		{
			iu.addModifiedItem(itemOne);
		}
		else
		{
			iu.addRemovedItem(itemOne);
		}
		if (itemTwo.isStackable() && (itemTwo.getCount() > 0))
		{
			iu.addModifiedItem(itemTwo);
		}
		else
		{
			iu.addRemovedItem(itemTwo);
		}
		player.sendInventoryUpdate(iu);
		
		player.removeRequest(request.getClass());
	}
}
