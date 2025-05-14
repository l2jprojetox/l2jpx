/*
 * Copyright (C) 2004-2016 L2J Unity
 * 
 * This file is part of L2J Unity.
 * 
 * L2J Unity is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * L2J Unity is distributed in the hope that it will be useful,
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
import org.l2jpx.gameserver.network.serverpackets.ExEnchantRetryToPutItemFail;
import org.l2jpx.gameserver.network.serverpackets.ExEnchantRetryToPutItemOk;

/**
 * @author Sdw
 */
public class RequestNewEnchantRetryToPutItems extends ClientPacket
{
	private int _firstItemObjectId;
	private int _secondItemObjectId;
	
	@Override
	protected void readImpl()
	{
		_firstItemObjectId = readInt();
		_secondItemObjectId = readInt();
	}
	
	@Override
	protected void runImpl()
	{
		final Player player = getPlayer();
		if (player == null)
		{
			return;
		}
		else if (player.isInStoreMode())
		{
			player.sendPacket(SystemMessageId.YOU_CANNOT_DO_THAT_WHILE_IN_A_PRIVATE_STORE_OR_PRIVATE_WORKSHOP);
			player.sendPacket(ExEnchantRetryToPutItemFail.STATIC_PACKET);
			return;
		}
		else if (player.isProcessingTransaction() || player.isProcessingRequest())
		{
			player.sendPacket(SystemMessageId.YOU_CANNOT_USE_THIS_SYSTEM_DURING_TRADING_PRIVATE_STORE_AND_WORKSHOP_SETUP);
			player.sendPacket(ExEnchantRetryToPutItemFail.STATIC_PACKET);
			return;
		}
		
		final CompoundRequest request = new CompoundRequest(player);
		if (!player.addRequest(request))
		{
			player.sendPacket(ExEnchantRetryToPutItemFail.STATIC_PACKET);
			return;
		}
		
		// Make sure player owns first item.
		request.setItemOne(_firstItemObjectId);
		final Item itemOne = request.getItemOne();
		if (itemOne == null)
		{
			player.sendPacket(ExEnchantRetryToPutItemFail.STATIC_PACKET);
			player.removeRequest(request.getClass());
			return;
		}
		
		// Make sure player owns second item.
		request.setItemTwo(_secondItemObjectId);
		final Item itemTwo = request.getItemTwo();
		if (itemTwo == null)
		{
			player.sendPacket(ExEnchantRetryToPutItemFail.STATIC_PACKET);
			player.removeRequest(request.getClass());
			return;
		}
		
		final CombinationItem combinationItem = CombinationItemsData.getInstance().getItemsBySlots(itemOne.getId(), itemTwo.getId());
		
		// Not implemented or not able to merge!
		if (combinationItem == null)
		{
			player.sendPacket(ExEnchantRetryToPutItemFail.STATIC_PACKET);
			player.removeRequest(request.getClass());
			return;
		}
		player.sendPacket(ExEnchantRetryToPutItemOk.STATIC_PACKET);
	}
}
