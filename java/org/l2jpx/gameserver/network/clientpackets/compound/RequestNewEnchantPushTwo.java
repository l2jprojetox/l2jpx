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

import org.l2jpx.gameserver.data.xml.CombinationItemsData;
import org.l2jpx.gameserver.model.actor.Player;
import org.l2jpx.gameserver.model.actor.request.CompoundRequest;
import org.l2jpx.gameserver.model.item.combination.CombinationItem;
import org.l2jpx.gameserver.model.item.instance.Item;
import org.l2jpx.gameserver.network.SystemMessageId;
import org.l2jpx.gameserver.network.clientpackets.ClientPacket;
import org.l2jpx.gameserver.network.serverpackets.compound.ExEnchantOneFail;
import org.l2jpx.gameserver.network.serverpackets.compound.ExEnchantTwoFail;
import org.l2jpx.gameserver.network.serverpackets.compound.ExEnchantTwoOK;

/**
 * @author UnAfraid
 */
public class RequestNewEnchantPushTwo extends ClientPacket
{
	private int _objectId;
	
	@Override
	protected void readImpl()
	{
		_objectId = readInt();
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
			player.sendPacket(ExEnchantTwoFail.STATIC_PACKET);
			return;
		}
		
		// Make sure player owns this item.
		request.setItemTwo(_objectId);
		final Item itemOne = request.getItemOne();
		final Item itemTwo = request.getItemTwo();
		if ((itemOne == null) || (itemTwo == null))
		{
			player.sendPacket(ExEnchantTwoFail.STATIC_PACKET);
			return;
		}
		
		// Lets prevent using same item twice. Also stackable item check.
		if ((itemOne.getObjectId() == itemTwo.getObjectId()) && (!itemOne.isStackable() || (player.getInventory().getInventoryItemCount(itemOne.getTemplate().getId(), -1) < 2)))
		{
			player.sendPacket(ExEnchantTwoFail.STATIC_PACKET);
			return;
		}
		
		final CombinationItem combinationItem = CombinationItemsData.getInstance().getItemsBySlots(itemOne.getId(), itemTwo.getId());
		
		// Not implemented or not able to merge!
		if (combinationItem == null)
		{
			player.sendPacket(ExEnchantTwoFail.STATIC_PACKET);
			return;
		}
		
		player.sendPacket(ExEnchantTwoOK.STATIC_PACKET);
	}
}
