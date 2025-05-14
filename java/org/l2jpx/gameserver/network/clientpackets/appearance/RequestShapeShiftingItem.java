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
import org.l2jpx.gameserver.enums.InventorySlot;
import org.l2jpx.gameserver.enums.ItemLocation;
import org.l2jpx.gameserver.model.actor.Player;
import org.l2jpx.gameserver.model.actor.request.ShapeShiftingItemRequest;
import org.l2jpx.gameserver.model.holders.AppearanceHolder;
import org.l2jpx.gameserver.model.item.ItemTemplate;
import org.l2jpx.gameserver.model.item.appearance.AppearanceStone;
import org.l2jpx.gameserver.model.item.appearance.AppearanceType;
import org.l2jpx.gameserver.model.item.instance.Item;
import org.l2jpx.gameserver.model.itemcontainer.PlayerInventory;
import org.l2jpx.gameserver.model.variables.ItemVariables;
import org.l2jpx.gameserver.network.SystemMessageId;
import org.l2jpx.gameserver.network.clientpackets.ClientPacket;
import org.l2jpx.gameserver.network.serverpackets.ExAdenaInvenCount;
import org.l2jpx.gameserver.network.serverpackets.ExUserInfoEquipSlot;
import org.l2jpx.gameserver.network.serverpackets.InventoryUpdate;
import org.l2jpx.gameserver.network.serverpackets.appearance.ExShapeShiftingResult;

/**
 * @author UnAfraid
 */
public class RequestShapeShiftingItem extends ClientPacket
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
			player.sendPacket(ExShapeShiftingResult.CLOSE);
			player.sendPacket(SystemMessageId.YOU_CANNOT_USE_THIS_SYSTEM_DURING_TRADING_PRIVATE_STORE_AND_WORKSHOP_SETUP);
			return;
		}
		
		final PlayerInventory inventory = player.getInventory();
		final Item targetItem = inventory.getItemByObjectId(_targetItemObjId);
		Item stone = request.getAppearanceStone();
		if ((targetItem == null) || (stone == null))
		{
			player.sendPacket(ExShapeShiftingResult.CLOSE);
			player.removeRequest(ShapeShiftingItemRequest.class);
			return;
		}
		
		if ((stone.getOwnerId() != player.getObjectId()) || (targetItem.getOwnerId() != player.getObjectId()))
		{
			player.sendPacket(ExShapeShiftingResult.CLOSE);
			player.removeRequest(ShapeShiftingItemRequest.class);
			return;
		}
		
		if (!targetItem.getTemplate().isAppearanceable())
		{
			player.sendPacket(SystemMessageId.THIS_ITEM_CANNOT_BE_MODIFIED_OR_RESTORED);
			player.sendPacket(ExShapeShiftingResult.CLOSE);
			player.removeRequest(ShapeShiftingItemRequest.class);
			return;
		}
		
		if ((targetItem.getItemLocation() != ItemLocation.INVENTORY) && (targetItem.getItemLocation() != ItemLocation.PAPERDOLL))
		{
			player.sendPacket(ExShapeShiftingResult.CLOSE);
			player.removeRequest(ShapeShiftingItemRequest.class);
			return;
		}
		
		if ((stone = inventory.getItemByObjectId(stone.getObjectId())) == null)
		{
			player.sendPacket(ExShapeShiftingResult.CLOSE);
			player.removeRequest(ShapeShiftingItemRequest.class);
			return;
		}
		
		final AppearanceStone appearanceStone = AppearanceItemData.getInstance().getStone(stone.getId());
		if (appearanceStone == null)
		{
			player.sendPacket(ExShapeShiftingResult.CLOSE);
			player.removeRequest(ShapeShiftingItemRequest.class);
			return;
		}
		
		if (!appearanceStone.checkConditions(player, targetItem))
		{
			player.sendPacket(ExShapeShiftingResult.CLOSE);
			player.removeRequest(ShapeShiftingItemRequest.class);
			return;
		}
		
		final Item extractItem = request.getAppearanceExtractItem();
		int extracItemId = 0;
		if ((appearanceStone.getType() != AppearanceType.RESTORE) && (appearanceStone.getType() != AppearanceType.FIXED))
		{
			if (extractItem == null)
			{
				player.sendPacket(ExShapeShiftingResult.CLOSE);
				player.removeRequest(ShapeShiftingItemRequest.class);
				return;
			}
			
			if (extractItem.getOwnerId() != player.getObjectId())
			{
				player.sendPacket(ExShapeShiftingResult.CLOSE);
				player.removeRequest(ShapeShiftingItemRequest.class);
				return;
			}
			
			if (!extractItem.getTemplate().isAppearanceable())
			{
				player.sendPacket(ExShapeShiftingResult.CLOSE);
				player.removeRequest(ShapeShiftingItemRequest.class);
				return;
			}
			
			if ((extractItem.getItemLocation() != ItemLocation.INVENTORY) && (extractItem.getItemLocation() != ItemLocation.PAPERDOLL))
			{
				player.sendPacket(ExShapeShiftingResult.CLOSE);
				player.removeRequest(ShapeShiftingItemRequest.class);
				return;
			}
			
			if (extractItem.getTemplate().getCrystalType().isGreater(targetItem.getTemplate().getCrystalType()))
			{
				player.sendPacket(ExShapeShiftingResult.CLOSE);
				player.removeRequest(ShapeShiftingItemRequest.class);
				return;
			}
			
			if (extractItem.getVisualId() > 0)
			{
				player.sendPacket(ExShapeShiftingResult.CLOSE);
				player.removeRequest(ShapeShiftingItemRequest.class);
				return;
			}
			
			if ((extractItem.getItemType() != targetItem.getItemType()) || (extractItem.getId() == targetItem.getId()) || (extractItem.getObjectId() == targetItem.getObjectId()))
			{
				player.sendPacket(ExShapeShiftingResult.CLOSE);
				player.removeRequest(ShapeShiftingItemRequest.class);
				return;
			}
			
			if ((extractItem.getTemplate().getBodyPart() != targetItem.getTemplate().getBodyPart()) && ((extractItem.getTemplate().getBodyPart() != ItemTemplate.SLOT_FULL_ARMOR) || (targetItem.getTemplate().getBodyPart() != ItemTemplate.SLOT_CHEST)))
			{
				player.sendPacket(ExShapeShiftingResult.CLOSE);
				player.removeRequest(ShapeShiftingItemRequest.class);
				return;
			}
			
			extracItemId = extractItem.getId();
		}
		
		final long cost = appearanceStone.getCost();
		if (cost > player.getAdena())
		{
			player.sendPacket(SystemMessageId.YOU_CANNOT_MODIFY_AS_YOU_DO_NOT_HAVE_ENOUGH_ADENA);
			player.sendPacket(ExShapeShiftingResult.CLOSE);
			player.removeRequest(ShapeShiftingItemRequest.class);
			return;
		}
		
		if (stone.getCount() < 1L)
		{
			player.sendPacket(ExShapeShiftingResult.CLOSE);
			player.removeRequest(ShapeShiftingItemRequest.class);
			return;
		}
		if ((appearanceStone.getType() == AppearanceType.NORMAL) && (inventory.destroyItem(getClass().getSimpleName(), extractItem, 1, player, this) == null))
		{
			player.sendPacket(ExShapeShiftingResult.FAILED);
			player.removeRequest(ShapeShiftingItemRequest.class);
			return;
		}
		
		inventory.destroyItem(getClass().getSimpleName(), stone, 1, player, this);
		player.reduceAdena(getClass().getSimpleName(), cost, extractItem, false);
		
		switch (appearanceStone.getType())
		{
			case RESTORE:
			{
				targetItem.setVisualId(0);
				targetItem.getVariables().set(ItemVariables.VISUAL_APPEARANCE_STONE_ID, 0);
				break;
			}
			case NORMAL:
			{
				targetItem.setVisualId(extractItem.getId());
				break;
			}
			case BLESSED:
			{
				targetItem.setVisualId(extractItem.getId());
				break;
			}
			case FIXED:
			{
				targetItem.removeVisualSetSkills();
				
				if (appearanceStone.getVisualIds().isEmpty())
				{
					extracItemId = appearanceStone.getVisualId();
					targetItem.setVisualId(appearanceStone.getVisualId());
					targetItem.getVariables().set(ItemVariables.VISUAL_APPEARANCE_STONE_ID, appearanceStone.getId());
				}
				else
				{
					final AppearanceHolder holder = appearanceStone.findVisualChange(targetItem);
					if (holder != null)
					{
						extracItemId = holder.getVisualId();
						targetItem.setVisualId(holder.getVisualId());
						targetItem.getVariables().set(ItemVariables.VISUAL_APPEARANCE_STONE_ID, appearanceStone.getId());
					}
				}
				
				targetItem.applyVisualSetSkills();
				break;
			}
		}
		
		if ((appearanceStone.getType() != AppearanceType.RESTORE) && (appearanceStone.getLifeTime() > 0))
		{
			targetItem.getVariables().set(ItemVariables.VISUAL_APPEARANCE_LIFE_TIME, System.currentTimeMillis() + appearanceStone.getLifeTime());
			targetItem.scheduleVisualLifeTime();
		}
		
		targetItem.getVariables().storeMe();
		
		final InventoryUpdate iu = new InventoryUpdate();
		iu.addModifiedItem(targetItem);
		if (extractItem != null)
		{
			iu.addModifiedItem(extractItem);
		}
		if (inventory.getItemByObjectId(stone.getObjectId()) == null)
		{
			iu.addRemovedItem(stone);
		}
		else
		{
			iu.addModifiedItem(stone);
		}
		player.sendInventoryUpdate(iu);
		
		player.removeRequest(ShapeShiftingItemRequest.class);
		player.sendPacket(new ExShapeShiftingResult(ExShapeShiftingResult.RESULT_SUCCESS, targetItem.getId(), extracItemId));
		if (targetItem.isEquipped())
		{
			player.broadcastUserInfo();
			final ExUserInfoEquipSlot slots = new ExUserInfoEquipSlot(player, false);
			for (InventorySlot slot : InventorySlot.values())
			{
				if (slot.getSlot() == targetItem.getLocationSlot())
				{
					slots.addComponentType(slot);
				}
			}
			player.sendPacket(slots);
		}
		player.sendPacket(new ExAdenaInvenCount(player));
	}
}
