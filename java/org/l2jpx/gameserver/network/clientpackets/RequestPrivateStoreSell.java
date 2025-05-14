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

import org.l2jpx.Config;
import org.l2jpx.gameserver.data.sql.OfflineTraderTable;
import org.l2jpx.gameserver.enums.PrivateStoreType;
import org.l2jpx.gameserver.model.ItemRequest;
import org.l2jpx.gameserver.model.TradeList;
import org.l2jpx.gameserver.model.World;
import org.l2jpx.gameserver.model.actor.Player;
import org.l2jpx.gameserver.network.PacketLogger;
import org.l2jpx.gameserver.network.serverpackets.ActionFailed;

public class RequestPrivateStoreSell extends ClientPacket
{
	private int _storePlayerId;
	private ItemRequest[] _items = null;
	
	@Override
	protected void readImpl()
	{
		_storePlayerId = readInt();
		final int itemsCount = readInt();
		if ((itemsCount <= 0) || (itemsCount > Config.MAX_ITEM_IN_PACKET))
		{
			return;
		}
		
		_items = new ItemRequest[itemsCount];
		for (int i = 0; i < itemsCount; i++)
		{
			final int slot = readInt();
			final int itemId = readInt();
			readShort(); // TODO analyse this
			readShort(); // TODO analyse this
			final long count = readLong();
			final long price = readLong();
			readInt(); // visual id
			readInt(); // option 1
			readInt(); // option 2
			final int soulCrystals = readByte();
			for (int s = 0; s < soulCrystals; s++)
			{
				readInt(); // soul crystal option
			}
			final int soulCrystals2 = readByte();
			for (int s = 0; s < soulCrystals2; s++)
			{
				readInt(); // sa effect
			}
			if (/* (slot < 1) || */ (itemId < 1) || (count < 1) || (price < 0))
			{
				_items = null;
				return;
			}
			_items[i] = new ItemRequest(slot, itemId, count, price);
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
		
		if (_items == null)
		{
			player.sendPacket(ActionFailed.STATIC_PACKET);
			return;
		}
		
		if (player.isRegisteredOnEvent())
		{
			player.sendMessage("You cannot open a private store while participating in an event.");
			return;
		}
		
		if (!getClient().getFloodProtectors().canPerformTransaction())
		{
			player.sendMessage("You are selling items too fast.");
			return;
		}
		
		final Player storePlayer = World.getInstance().getPlayer(_storePlayerId);
		if ((storePlayer == null) || !player.isInsideRadius3D(storePlayer, INTERACTION_DISTANCE))
		{
			return;
		}
		
		if (player.getInstanceWorld() != storePlayer.getInstanceWorld())
		{
			return;
		}
		
		if ((storePlayer.getPrivateStoreType() != PrivateStoreType.BUY) || player.isCursedWeaponEquipped())
		{
			return;
		}
		
		final TradeList storeList = storePlayer.getBuyList();
		if (storeList == null)
		{
			return;
		}
		
		if (!player.getAccessLevel().allowTransaction())
		{
			player.sendMessage("Transactions are disabled for your Access Level.");
			player.sendPacket(ActionFailed.STATIC_PACKET);
			return;
		}
		
		if (!storeList.privateStoreSell(player, _items))
		{
			player.sendPacket(ActionFailed.STATIC_PACKET);
			PacketLogger.warning("PrivateStore sell has failed due to invalid list or request. Player: " + player.getName() + ", Private store of: " + storePlayer.getName());
			return;
		}
		
		// Update offline trade record, if realtime saving is enabled
		if (Config.OFFLINE_TRADE_ENABLE && Config.STORE_OFFLINE_TRADE_IN_REALTIME && ((storePlayer.getClient() == null) || storePlayer.getClient().isDetached()))
		{
			OfflineTraderTable.getInstance().onTransaction(storePlayer, storeList.getItemCount() == 0, false);
		}
		
		if (storeList.getItemCount() == 0)
		{
			storePlayer.setPrivateStoreType(PrivateStoreType.NONE);
			storePlayer.broadcastUserInfo();
		}
	}
}
