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
import org.l2jpx.gameserver.model.commission.CommissionItem;
import org.l2jpx.gameserver.network.SystemMessageId;
import org.l2jpx.gameserver.network.clientpackets.ClientPacket;
import org.l2jpx.gameserver.network.serverpackets.commission.ExCloseCommission;
import org.l2jpx.gameserver.network.serverpackets.commission.ExResponseCommissionBuyInfo;

/**
 * @author NosBit
 */
public class RequestCommissionBuyInfo extends ClientPacket
{
	private long _commissionId;
	
	@Override
	protected void readImpl()
	{
		_commissionId = readLong();
		// readInt(); // CommissionItemType
	}
	
	@Override
	protected void runImpl()
	{
		final Player player = getPlayer();
		if (player == null)
		{
			return;
		}
		
		if (!ItemCommissionManager.isPlayerAllowedToInteract(player))
		{
			player.sendPacket(ExCloseCommission.STATIC_PACKET);
			return;
		}
		
		if (!player.isInventoryUnder80(false) || (player.getWeightPenalty() >= 3))
		{
			player.sendPacket(SystemMessageId.IF_THE_WEIGHT_IS_80_OR_MORE_AND_THE_INVENTORY_NUMBER_IS_90_OR_MORE_PURCHASE_CANCELLATION_IS_NOT_POSSIBLE);
			player.sendPacket(ExResponseCommissionBuyInfo.FAILED);
			return;
		}
		
		final CommissionItem commissionItem = ItemCommissionManager.getInstance().getCommissionItem(_commissionId);
		if (commissionItem != null)
		{
			player.sendPacket(new ExResponseCommissionBuyInfo(commissionItem));
		}
		else
		{
			player.sendPacket(SystemMessageId.ITEM_PURCHASE_IS_NOT_AVAILABLE_BECAUSE_THE_CORRESPONDING_ITEM_DOES_NOT_EXIST);
			player.sendPacket(ExResponseCommissionBuyInfo.FAILED);
		}
	}
}
