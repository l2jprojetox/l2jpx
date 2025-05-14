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
package org.l2jpx.gameserver.network.clientpackets.commission;

import org.l2jpx.gameserver.instancemanager.ItemCommissionManager;
import org.l2jpx.gameserver.model.actor.Player;
import org.l2jpx.gameserver.network.PacketLogger;
import org.l2jpx.gameserver.network.clientpackets.ClientPacket;
import org.l2jpx.gameserver.network.serverpackets.commission.ExCloseCommission;

/**
 * @author NosBit, Ren
 */
public class RequestCommissionRegister extends ClientPacket
{
	private int _itemObjectId;
	private long _pricePerUnit;
	private long _itemCount;
	private int _durationType; // -1 = None, 0 = 1 Day, 1 = 3 Days, 2 = 5 Days, 3 = 7 Days, 4 = 15 Days, 5 = 30 Days;
	private int _feeDiscountType; // 0 = none, 1 = 30% discount, 2 = 100% discount;
	
	@Override
	protected void readImpl()
	{
		_itemObjectId = readInt();
		readString(); // Item Name they use it for search we will use server side available names.
		_pricePerUnit = readLong();
		_itemCount = readLong();
		_durationType = readInt();
		_feeDiscountType = readShort();
		// readShort(); // Unknown IDS;
		// readInt(); // Unknown
		// readInt(); // Unknown
	}
	
	@Override
	protected void runImpl()
	{
		final Player player = getPlayer();
		if (player == null)
		{
			return;
		}
		
		if ((_feeDiscountType < 0) || (_feeDiscountType > 2))
		{
			PacketLogger.warning(player + " sent incorrect commission discount type: " + _feeDiscountType + ".");
			return;
		}
		
		if ((_feeDiscountType == 1) && (player.getInventory().getItemByItemId(22351) == null))
		{
			PacketLogger.warning(player + ": Auction House Fee 30% Voucher not found in inventory.");
			return;
		}
		else if ((_feeDiscountType == 2) && (player.getInventory().getItemByItemId(22352) == null))
		{
			PacketLogger.warning(player + ": Auction House Fee 100% Voucher not found in inventory.");
			return;
		}
		
		if ((_durationType < 0) || (_durationType > 5))
		{
			PacketLogger.warning(player + " sent incorrect commission duration type: " + _durationType + ".");
			return;
		}
		
		if ((_durationType == 4) && (player.getInventory().getItemByItemId(22353) == null))
		{
			
			PacketLogger.warning(player + ": Auction House (15-day) Extension not found in inventory.");
			return;
			
		}
		else if ((_durationType == 5) && (player.getInventory().getItemByItemId(22354) == null))
		{
			PacketLogger.warning(player + ": Auction House (30-day) Extension not found in inventory.");
			return;
		}
		
		if (!ItemCommissionManager.isPlayerAllowedToInteract(player))
		{
			player.sendPacket(ExCloseCommission.STATIC_PACKET);
			return;
		}
		
		ItemCommissionManager.getInstance().registerItem(player, _itemObjectId, _itemCount, _pricePerUnit, _durationType, (byte) Math.min((_feeDiscountType * 30) * _feeDiscountType, 100));
	}
}
