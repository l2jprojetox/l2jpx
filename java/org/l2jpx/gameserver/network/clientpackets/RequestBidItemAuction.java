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

import org.l2jpx.gameserver.instancemanager.ItemAuctionManager;
import org.l2jpx.gameserver.model.actor.Player;
import org.l2jpx.gameserver.model.itemauction.ItemAuction;
import org.l2jpx.gameserver.model.itemauction.ItemAuctionInstance;
import org.l2jpx.gameserver.model.itemcontainer.Inventory;

/**
 * @author Forsaiken
 */
public class RequestBidItemAuction extends ClientPacket
{
	private int _instanceId;
	private long _bid;
	
	@Override
	protected void readImpl()
	{
		_instanceId = readInt();
		_bid = readLong();
	}
	
	@Override
	protected void runImpl()
	{
		final Player player = getPlayer();
		if (player == null)
		{
			return;
		}
		
		// Can't perform transaction here.
		if (!getClient().getFloodProtectors().canPerformTransaction())
		{
			player.sendMessage("You are bidding too fast.");
			return;
		}
		
		if ((_bid < 0) || (_bid > Inventory.MAX_ADENA))
		{
			return;
		}
		
		final ItemAuctionInstance instance = ItemAuctionManager.getInstance().getManagerInstance(_instanceId);
		if (instance != null)
		{
			final ItemAuction auction = instance.getCurrentAuction();
			if (auction != null)
			{
				auction.registerBid(player, _bid);
			}
		}
	}
}