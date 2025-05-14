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

import org.l2jpx.Config;
import org.l2jpx.gameserver.data.xml.ElementalAttributeData;
import org.l2jpx.gameserver.enums.AttributeType;
import org.l2jpx.gameserver.model.actor.Player;
import org.l2jpx.gameserver.model.actor.request.EnchantItemAttributeRequest;
import org.l2jpx.gameserver.model.holders.ElementalItemHolder;
import org.l2jpx.gameserver.model.item.enchant.attribute.AttributeHolder;
import org.l2jpx.gameserver.model.item.instance.Item;
import org.l2jpx.gameserver.network.SystemMessageId;
import org.l2jpx.gameserver.network.serverpackets.ExAttributeEnchantResult;
import org.l2jpx.gameserver.network.serverpackets.InventoryUpdate;
import org.l2jpx.gameserver.network.serverpackets.SystemMessage;
import org.l2jpx.gameserver.util.Util;

public class RequestExEnchantItemAttribute extends ClientPacket
{
	private int _objectId;
	private long _count;
	
	@Override
	protected void readImpl()
	{
		_objectId = readInt();
		_count = readLong();
	}
	
	@Override
	protected void runImpl()
	{
		final Player player = getPlayer();
		if (player == null)
		{
			return;
		}
		
		final EnchantItemAttributeRequest request = player.getRequest(EnchantItemAttributeRequest.class);
		if (request == null)
		{
			return;
		}
		
		request.setProcessing(true);
		
		if (_objectId == 0xFFFFFFFF)
		{
			// Player canceled enchant
			player.removeRequest(request.getClass());
			player.sendPacket(SystemMessageId.ATTRIBUTE_ITEM_USAGE_HAS_BEEN_CANCELLED);
			return;
		}
		
		if (!player.isOnline())
		{
			player.removeRequest(request.getClass());
			return;
		}
		
		if (player.isInStoreMode())
		{
			player.sendPacket(SystemMessageId.YOU_CANNOT_ADD_ELEMENTAL_POWER_WHILE_OPERATING_A_PRIVATE_STORE_OR_PRIVATE_WORKSHOP);
			player.removeRequest(request.getClass());
			return;
		}
		
		// Restrict enchant during a trade (bug if enchant fails)
		if (player.getActiveRequester() != null)
		{
			// Cancel trade
			player.cancelActiveTrade();
			player.removeRequest(request.getClass());
			player.sendPacket(SystemMessageId.YOU_CANNOT_DO_THAT_WHILE_TRADING);
			return;
		}
		
		final Item item = player.getInventory().getItemByObjectId(_objectId);
		final Item stone = request.getEnchantingStone();
		if ((item == null) || (stone == null))
		{
			player.removeRequest(request.getClass());
			player.sendPacket(SystemMessageId.ATTRIBUTE_ITEM_USAGE_HAS_BEEN_CANCELLED);
			return;
		}
		
		if (!item.isElementable())
		{
			player.sendPacket(SystemMessageId.ELEMENTAL_POWER_ENHANCER_USAGE_REQUIREMENT_IS_NOT_SUFFICIENT);
			player.removeRequest(request.getClass());
			return;
		}
		
		if (_count < 1)
		{
			player.removeRequest(request.getClass());
			return;
		}
		
		switch (item.getItemLocation())
		{
			case INVENTORY:
			case PAPERDOLL:
			{
				if (item.getOwnerId() != player.getObjectId())
				{
					player.removeRequest(request.getClass());
					return;
				}
				break;
			}
			default:
			{
				player.removeRequest(request.getClass());
				Util.handleIllegalPlayerAction(player, player + " tried to use enchant Exploit!", Config.DEFAULT_PUNISH);
				return;
			}
		}
		
		final int stoneId = stone.getId();
		final long count = Math.min(stone.getCount(), _count);
		AttributeType elementToAdd = ElementalAttributeData.getInstance().getItemElement(stoneId);
		// Armors have the opposite element
		if (item.isArmor())
		{
			elementToAdd = elementToAdd.getOpposite();
		}
		final AttributeType opositeElement = elementToAdd.getOpposite();
		final AttributeHolder oldElement = item.getAttribute(elementToAdd);
		final int elementValue = oldElement == null ? 0 : oldElement.getValue();
		final int limit = getLimit(item, stoneId);
		int powerToAdd = getPowerToAdd(stoneId, elementValue, item);
		if ((item.isWeapon() && (oldElement != null) && (oldElement.getType() != elementToAdd) && (oldElement.getType() != AttributeType.NONE)) || (item.isArmor() && (item.getAttribute(elementToAdd) == null) && (item.getAttributes() != null) && (item.getAttributes().size() >= 3)))
		{
			player.sendPacket(SystemMessageId.ANOTHER_ELEMENTAL_POWER_HAS_ALREADY_BEEN_ADDED_THIS_ELEMENTAL_POWER_CANNOT_BE_ADDED);
			player.removeRequest(request.getClass());
			return;
		}
		
		if (item.isArmor() && (item.getAttributes() != null))
		{
			// can't add opposite element
			for (AttributeHolder attribute : item.getAttributes())
			{
				if (attribute.getType() == opositeElement)
				{
					player.removeRequest(request.getClass());
					Util.handleIllegalPlayerAction(player, player + " tried to add oposite attribute to item!", Config.DEFAULT_PUNISH);
					return;
				}
			}
		}
		
		int newPower = elementValue + powerToAdd;
		if (newPower > limit)
		{
			newPower = limit;
			powerToAdd = limit - elementValue;
		}
		
		if (powerToAdd <= 0)
		{
			player.sendPacket(SystemMessageId.ATTRIBUTE_ITEM_USAGE_HAS_BEEN_CANCELLED);
			player.removeRequest(request.getClass());
			return;
		}
		
		int usedStones = 0;
		int successfulAttempts = 0;
		int failedAttempts = 0;
		for (int i = 0; i < count; i++)
		{
			usedStones++;
			final int result = addElement(player, stone, item, elementToAdd);
			if (result == 1)
			{
				successfulAttempts++;
			}
			else if (result == 0)
			{
				failedAttempts++;
			}
			else
			{
				break;
			}
		}
		
		item.updateItemElementals();
		player.destroyItem("AttrEnchant", stone, usedStones, player, true);
		final AttributeHolder newElement = item.getAttribute(elementToAdd);
		final int newValue = newElement != null ? newElement.getValue() : 0;
		final AttributeType realElement = item.isArmor() ? opositeElement : elementToAdd;
		final InventoryUpdate iu = new InventoryUpdate();
		if (successfulAttempts > 0)
		{
			SystemMessage sm;
			if (item.getEnchantLevel() == 0)
			{
				if (item.isArmor())
				{
					sm = new SystemMessage(SystemMessageId.THE_S2_S_ATTRIBUTE_WAS_SUCCESSFULLY_BESTOWED_ON_S1_AND_RESISTANCE_TO_S3_WAS_INCREASED);
				}
				else
				{
					sm = new SystemMessage(SystemMessageId.S2_ELEMENTAL_POWER_HAS_BEEN_ADDED_SUCCESSFULLY_TO_S1);
				}
				sm.addItemName(item);
				sm.addAttribute(realElement.getClientId());
				if (item.isArmor())
				{
					sm.addAttribute(realElement.getOpposite().getClientId());
				}
			}
			else
			{
				if (item.isArmor())
				{
					sm = new SystemMessage(SystemMessageId.THE_S3_S_ATTRIBUTE_WAS_SUCCESSFULLY_BESTOWED_ON_S1_S2_AND_RESISTANCE_TO_S4_WAS_INCREASED);
				}
				else
				{
					sm = new SystemMessage(SystemMessageId.S3_ELEMENTAL_POWER_HAS_BEEN_ADDED_SUCCESSFULLY_TO_S1_S2);
				}
				sm.addInt(item.getEnchantLevel());
				sm.addItemName(item);
				sm.addAttribute(realElement.getClientId());
				if (item.isArmor())
				{
					sm.addAttribute(realElement.getOpposite().getClientId());
				}
			}
			player.sendPacket(sm);
			
			// send packets
			iu.addModifiedItem(item);
		}
		else
		{
			player.sendPacket(SystemMessageId.YOU_HAVE_FAILED_TO_ADD_ELEMENTAL_POWER);
		}
		
		int result = 0;
		if (successfulAttempts == 0)
		{
			// Failed
			result = 2;
		}
		
		// Stone must be removed
		if (stone.getCount() == 0)
		{
			iu.addRemovedItem(stone);
		}
		else
		{
			iu.addModifiedItem(stone);
		}
		
		player.removeRequest(request.getClass());
		player.sendPacket(new ExAttributeEnchantResult(result, item.isWeapon(), elementToAdd, elementValue, newValue, successfulAttempts, failedAttempts));
		player.updateUserInfo();
		player.sendInventoryUpdate(iu);
	}
	
	private int addElement(Player player, Item stone, Item item, AttributeType elementToAdd)
	{
		final AttributeHolder oldElement = item.getAttribute(elementToAdd);
		final int elementValue = oldElement == null ? 0 : oldElement.getValue();
		final int limit = getLimit(item, stone.getId());
		int powerToAdd = getPowerToAdd(stone.getId(), elementValue, item);
		int newPower = elementValue + powerToAdd;
		if (newPower > limit)
		{
			newPower = limit;
			powerToAdd = limit - elementValue;
		}
		
		if (powerToAdd <= 0)
		{
			player.sendPacket(SystemMessageId.ATTRIBUTE_ITEM_USAGE_HAS_BEEN_CANCELLED);
			player.removeRequest(EnchantItemAttributeRequest.class);
			return -1;
		}
		
		final boolean success = ElementalAttributeData.getInstance().isSuccess(item, stone.getId());
		if (success)
		{
			item.setAttribute(new AttributeHolder(elementToAdd, newPower), false);
		}
		
		return success ? 1 : 0;
	}
	
	public int getLimit(Item item, int sotneId)
	{
		final ElementalItemHolder elementItem = ElementalAttributeData.getInstance().getItemElemental(sotneId);
		if (elementItem == null)
		{
			return 0;
		}
		
		if (item.isWeapon())
		{
			return ElementalAttributeData.WEAPON_VALUES[elementItem.getType().getMaxLevel()];
		}
		return ElementalAttributeData.ARMOR_VALUES[elementItem.getType().getMaxLevel()];
	}
	
	public int getPowerToAdd(int stoneId, int oldValue, Item item)
	{
		if (ElementalAttributeData.getInstance().getItemElement(stoneId) != AttributeType.NONE)
		{
			if (ElementalAttributeData.getInstance().getItemElemental(stoneId).getPower() > 0)
			{
				return ElementalAttributeData.getInstance().getItemElemental(stoneId).getPower();
			}
			if (item.isWeapon())
			{
				if (oldValue == 0)
				{
					return ElementalAttributeData.FIRST_WEAPON_BONUS;
				}
				return ElementalAttributeData.NEXT_WEAPON_BONUS;
			}
			else if (item.isArmor())
			{
				return ElementalAttributeData.ARMOR_BONUS;
			}
		}
		return 0;
	}
}
