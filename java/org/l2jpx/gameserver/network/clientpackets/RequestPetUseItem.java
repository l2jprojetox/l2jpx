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

import org.l2jpx.gameserver.handler.IItemHandler;
import org.l2jpx.gameserver.handler.ItemHandler;
import org.l2jpx.gameserver.model.actor.Player;
import org.l2jpx.gameserver.model.actor.instance.Pet;
import org.l2jpx.gameserver.model.item.instance.Item;
import org.l2jpx.gameserver.network.PacketLogger;
import org.l2jpx.gameserver.network.SystemMessageId;
import org.l2jpx.gameserver.network.serverpackets.PetItemList;
import org.l2jpx.gameserver.network.serverpackets.SystemMessage;

public class RequestPetUseItem extends ClientPacket
{
	private int _objectId;
	
	@Override
	protected void readImpl()
	{
		_objectId = readInt();
		// TODO: implement me properly
		// readLong();
		// readInt();
	}
	
	@Override
	protected void runImpl()
	{
		final Player player = getPlayer();
		if ((player == null) || !player.hasPet())
		{
			return;
		}
		
		if (!getClient().getFloodProtectors().canUseItem())
		{
			return;
		}
		
		final Pet pet = player.getPet();
		final Item item = pet.getInventory().getItemByObjectId(_objectId);
		if (item == null)
		{
			return;
		}
		
		if (!item.getTemplate().isForNpc())
		{
			player.sendPacket(SystemMessageId.THIS_PET_CANNOT_USE_THIS_ITEM);
			return;
		}
		
		if (player.isAlikeDead() || pet.isDead())
		{
			final SystemMessage sm = new SystemMessage(SystemMessageId.S1_CANNOT_BE_USED_DUE_TO_UNSUITABLE_TERMS);
			sm.addItemName(item);
			player.sendPacket(sm);
			return;
		}
		
		// If the item has reuse time and it has not passed.
		// Message from reuse delay must come from item.
		final int reuseDelay = item.getReuseDelay();
		if (reuseDelay > 0)
		{
			final long reuse = pet.getItemRemainingReuseTime(item.getObjectId());
			if (reuse > 0)
			{
				return;
			}
		}
		
		if (!item.isEquipped() && !item.getTemplate().checkCondition(pet, pet, true))
		{
			return;
		}
		
		useItem(pet, item, player);
	}
	
	private void useItem(Pet pet, Item item, Player player)
	{
		if (item.isEquipable())
		{
			if (!item.getTemplate().isConditionAttached())
			{
				player.sendPacket(SystemMessageId.THIS_PET_CANNOT_USE_THIS_ITEM);
				return;
			}
			
			if (item.isEquipped())
			{
				pet.getInventory().unEquipItemInSlot(item.getLocationSlot());
				final SystemMessage sm = new SystemMessage(SystemMessageId.YOUR_PET_TOOK_OFF_S1);
				sm.addItemName(item);
				player.sendPacket(sm);
			}
			else
			{
				pet.getInventory().equipItem(item);
				final SystemMessage sm = new SystemMessage(SystemMessageId.YOUR_PET_PUT_ON_S1);
				sm.addItemName(item);
				player.sendPacket(sm);
			}
			
			player.sendPacket(new PetItemList(pet.getInventory().getItems()));
			pet.updateAndBroadcastStatus(1);
		}
		else
		{
			final IItemHandler handler = ItemHandler.getInstance().getHandler(item.getEtcItem());
			if (handler != null)
			{
				if (handler.useItem(pet, item, false))
				{
					final int reuseDelay = item.getReuseDelay();
					if (reuseDelay > 0)
					{
						player.addTimeStampItem(item, reuseDelay);
					}
					pet.updateAndBroadcastStatus(1);
				}
			}
			else
			{
				player.sendPacket(SystemMessageId.THIS_PET_CANNOT_USE_THIS_ITEM);
				PacketLogger.warning("No item handler registered for itemId: " + item.getId());
			}
		}
	}
}
