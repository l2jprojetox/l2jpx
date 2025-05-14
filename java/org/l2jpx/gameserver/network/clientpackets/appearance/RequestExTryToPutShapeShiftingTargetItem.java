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
package org.l2jpx.gameserver.network.clientpackets.appearance;

import org.l2jpx.gameserver.data.xml.AppearanceItemData;
import org.l2jpx.gameserver.enums.ItemLocation;
import org.l2jpx.gameserver.model.actor.Player;
import org.l2jpx.gameserver.model.actor.request.ShapeShiftingItemRequest;
import org.l2jpx.gameserver.model.item.appearance.AppearanceStone;
import org.l2jpx.gameserver.model.item.instance.Item;
import org.l2jpx.gameserver.model.itemcontainer.PlayerInventory;
import org.l2jpx.gameserver.network.SystemMessageId;
import org.l2jpx.gameserver.network.clientpackets.ClientPacket;
import org.l2jpx.gameserver.network.serverpackets.appearance.ExPutShapeShiftingTargetItemResult;

/**
 * @author UnAfraid
 */
public class RequestExTryToPutShapeShiftingTargetItem extends ClientPacket
{
	private int _targetItemObjId;
	
	@Override
	protected void readImpl()
	{
		_targetItemObjId = readInt();
	}
	
	@Override
	protected void runImpl()
	{
		final Player player = getPlayer();
		if (player == null)
		{
			return;
		}
		
		final ShapeShiftingItemRequest request = player.getRequest(ShapeShiftingItemRequest.class);
		if (player.isInStoreMode() || player.isCrafting() || player.isProcessingRequest() || player.isProcessingTransaction() || (request == null))
		{
			player.sendPacket(ExPutShapeShiftingTargetItemResult.FAILED);
			player.sendPacket(SystemMessageId.YOU_CANNOT_USE_THIS_SYSTEM_DURING_TRADING_PRIVATE_STORE_AND_WORKSHOP_SETUP);
			return;
		}
		
		final PlayerInventory inventory = player.getInventory();
		final Item targetItem = inventory.getItemByObjectId(_targetItemObjId);
		Item stone = request.getAppearanceStone();
		if ((targetItem == null) || (stone == null))
		{
			player.sendPacket(ExPutShapeShiftingTargetItemResult.FAILED);
			player.removeRequest(ShapeShiftingItemRequest.class);
			return;
		}
		
		if ((stone.getOwnerId() != player.getObjectId()) || (targetItem.getOwnerId() != player.getObjectId()))
		{
			player.sendPacket(ExPutShapeShiftingTargetItemResult.FAILED);
			player.removeRequest(ShapeShiftingItemRequest.class);
			return;
		}
		
		if (!targetItem.getTemplate().isAppearanceable())
		{
			player.sendPacket(SystemMessageId.THIS_ITEM_CANNOT_BE_MODIFIED_OR_RESTORED);
			player.sendPacket(ExPutShapeShiftingTargetItemResult.FAILED);
			return;
		}
		
		if ((targetItem.getItemLocation() != ItemLocation.INVENTORY) && (targetItem.getItemLocation() != ItemLocation.PAPERDOLL))
		{
			player.sendPacket(ExPutShapeShiftingTargetItemResult.FAILED);
			player.removeRequest(ShapeShiftingItemRequest.class);
			return;
		}
		
		if ((stone = inventory.getItemByObjectId(stone.getObjectId())) == null)
		{
			player.sendPacket(ExPutShapeShiftingTargetItemResult.FAILED);
			player.removeRequest(ShapeShiftingItemRequest.class);
			return;
		}
		
		final AppearanceStone appearanceStone = AppearanceItemData.getInstance().getStone(stone.getId());
		if (appearanceStone == null)
		{
			player.sendPacket(ExPutShapeShiftingTargetItemResult.FAILED);
			player.removeRequest(ShapeShiftingItemRequest.class);
			return;
		}
		
		if (!appearanceStone.checkConditions(player, targetItem))
		{
			player.sendPacket(ExPutShapeShiftingTargetItemResult.FAILED);
			return;
		}
		
		player.sendPacket(new ExPutShapeShiftingTargetItemResult(ExPutShapeShiftingTargetItemResult.RESULT_SUCCESS, appearanceStone.getCost()));
	}
}
