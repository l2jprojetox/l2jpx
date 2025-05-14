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

import java.util.List;

import org.l2jpx.gameserver.data.xml.CombinationItemsData;
import org.l2jpx.gameserver.model.actor.Player;
import org.l2jpx.gameserver.model.actor.request.CompoundRequest;
import org.l2jpx.gameserver.model.item.combination.CombinationItem;
import org.l2jpx.gameserver.model.item.instance.Item;
import org.l2jpx.gameserver.network.SystemMessageId;
import org.l2jpx.gameserver.network.clientpackets.ClientPacket;
import org.l2jpx.gameserver.network.serverpackets.compound.ExEnchantOneFail;
import org.l2jpx.gameserver.network.serverpackets.compound.ExEnchantOneOK;

/**
 * @author UnAfraid
 */
public class RequestNewEnchantPushOne extends ClientPacket
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
		
		final CompoundRequest request = new CompoundRequest(player);
		if (!player.addRequest(request))
		{
			player.sendPacket(ExEnchantOneFail.STATIC_PACKET);
			return;
		}
		
		// Make sure player owns this item.
		request.setItemOne(_objectId);
		final Item itemOne = request.getItemOne();
		if (itemOne == null)
		{
			player.sendPacket(ExEnchantOneFail.STATIC_PACKET);
			player.removeRequest(request.getClass());
			return;
		}
		
		final List<CombinationItem> combinationItems = CombinationItemsData.getInstance().getItemsByFirstSlot(itemOne.getId());
		
		// Not implemented or not able to merge!
		if (combinationItems.isEmpty())
		{
			player.sendPacket(ExEnchantOneFail.STATIC_PACKET);
			player.removeRequest(request.getClass());
			return;
		}
		
		player.sendPacket(ExEnchantOneOK.STATIC_PACKET);
	}
}
