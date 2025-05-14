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

import java.sql.Connection;
import java.sql.PreparedStatement;

import org.l2jpx.Config;
import org.l2jpx.commons.database.DatabaseFactory;
import org.l2jpx.gameserver.enums.PlayerCondOverride;
import org.l2jpx.gameserver.handler.AdminCommandHandler;
import org.l2jpx.gameserver.instancemanager.CursedWeaponsManager;
import org.l2jpx.gameserver.model.World;
import org.l2jpx.gameserver.model.WorldObject;
import org.l2jpx.gameserver.model.actor.Player;
import org.l2jpx.gameserver.model.actor.Summon;
import org.l2jpx.gameserver.model.item.instance.Item;
import org.l2jpx.gameserver.network.PacketLogger;
import org.l2jpx.gameserver.network.SystemMessageId;
import org.l2jpx.gameserver.network.serverpackets.InventoryUpdate;
import org.l2jpx.gameserver.network.serverpackets.SystemMessage;
import org.l2jpx.gameserver.util.Util;

/**
 * @version $Revision: 1.7.2.4.2.6 $ $Date: 2005/03/27 15:29:30 $
 */
public class RequestDestroyItem extends ClientPacket
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
		
		if (_count < 1)
		{
			player.sendPacket(SystemMessageId.YOU_CANNOT_DESTROY_IT_BECAUSE_THE_NUMBER_IS_INCORRECT);
			if (_count < 0)
			{
				Util.handleIllegalPlayerAction(player, "[RequestDestroyItem] Character " + player.getName() + " of account " + player.getAccountName() + " tried to destroy item with oid " + _objectId + " but has count < 0!", Config.DEFAULT_PUNISH);
			}
			return;
		}
		
		if (!getClient().getFloodProtectors().canPerformTransaction())
		{
			player.sendMessage("You are destroying items too fast.");
			return;
		}
		
		long count = _count;
		if (player.isProcessingTransaction() || player.isInStoreMode())
		{
			player.sendPacket(SystemMessageId.WHILE_OPERATING_A_PRIVATE_STORE_OR_WORKSHOP_YOU_CANNOT_DISCARD_DESTROY_OR_TRADE_AN_ITEM);
			return;
		}
		
		if (player.hasItemRequest())
		{
			player.sendPacket(SystemMessageId.YOU_CANNOT_DESTROY_OR_CRYSTALLIZE_ITEMS_WHILE_ENCHANTING_ATTRIBUTES);
			return;
		}
		
		final Item itemToRemove = player.getInventory().getItemByObjectId(_objectId);
		
		// if we can't find the requested item, it is actually a cheat
		if (itemToRemove == null)
		{
			// GM can destroy other player items
			if (player.isGM())
			{
				final WorldObject obj = World.getInstance().findObject(_objectId);
				if ((obj != null) && obj.isItem())
				{
					if (_count > ((Item) obj).getCount())
					{
						count = ((Item) obj).getCount();
					}
					AdminCommandHandler.getInstance().useAdminCommand(player, "admin_delete_item " + _objectId + " " + count, true);
				}
				return;
			}
			
			player.sendPacket(SystemMessageId.THIS_ITEM_CANNOT_BE_DESTROYED);
			return;
		}
		
		// Cannot discard item that the skill is consuming
		if (player.isCastingNow(s -> s.getSkill().getItemConsumeId() == itemToRemove.getId()))
		{
			player.sendPacket(SystemMessageId.THIS_ITEM_CANNOT_BE_DESTROYED);
			return;
		}
		
		final int itemId = itemToRemove.getId();
		if (!Config.DESTROY_ALL_ITEMS && ((!player.canOverrideCond(PlayerCondOverride.DESTROY_ALL_ITEMS) && !itemToRemove.isDestroyable()) || CursedWeaponsManager.getInstance().isCursed(itemId)))
		{
			if (itemToRemove.isHeroItem())
			{
				player.sendPacket(SystemMessageId.HERO_WEAPONS_CANNOT_BE_DESTROYED);
			}
			else
			{
				player.sendPacket(SystemMessageId.THIS_ITEM_CANNOT_BE_DESTROYED);
			}
			return;
		}
		
		if (!itemToRemove.isStackable() && (count > 1))
		{
			Util.handleIllegalPlayerAction(player, "[RequestDestroyItem] Character " + player.getName() + " of account " + player.getAccountName() + " tried to destroy a non-stackable item with oid " + _objectId + " but has count > 1!", Config.DEFAULT_PUNISH);
			return;
		}
		
		if (!player.getInventory().canManipulateWithItemId(itemToRemove.getId()))
		{
			player.sendMessage("You cannot use this item.");
			return;
		}
		
		if (_count > itemToRemove.getCount())
		{
			count = itemToRemove.getCount();
		}
		
		if (itemToRemove.getTemplate().isPetItem())
		{
			// Check if the player has a summoned pet or mount active with the same object ID.
			final Summon pet = player.getPet();
			if (((pet != null) && (pet.getControlObjectId() == _objectId)) || (player.isMounted() && (player.getMountObjectID() == _objectId)))
			{
				player.sendPacket(SystemMessageId.AS_YOUR_PET_IS_CURRENTLY_OUT_ITS_SUMMONING_ITEM_CANNOT_BE_DESTROYED);
				return;
			}
			
			try (Connection con = DatabaseFactory.getConnection();
				PreparedStatement statement = con.prepareStatement("DELETE FROM pets WHERE item_obj_id=?"))
			{
				statement.setInt(1, _objectId);
				statement.execute();
			}
			catch (Exception e)
			{
				PacketLogger.warning("Could not delete pet objectid: " + e.getMessage());
			}
		}
		if (itemToRemove.isTimeLimitedItem())
		{
			itemToRemove.endOfLife();
		}
		
		if (itemToRemove.isEquipped())
		{
			if (itemToRemove.getEnchantLevel() > 0)
			{
				final SystemMessage sm = new SystemMessage(SystemMessageId.THE_EQUIPMENT_S1_S2_HAS_BEEN_REMOVED);
				sm.addInt(itemToRemove.getEnchantLevel());
				sm.addItemName(itemToRemove);
				player.sendPacket(sm);
			}
			else
			{
				final SystemMessage sm = new SystemMessage(SystemMessageId.S1_HAS_BEEN_UNEQUIPPED);
				sm.addItemName(itemToRemove);
				player.sendPacket(sm);
			}
			
			final InventoryUpdate iu = new InventoryUpdate();
			for (Item itm : player.getInventory().unEquipItemInSlotAndRecord(itemToRemove.getLocationSlot()))
			{
				iu.addModifiedItem(itm);
			}
			player.sendPacket(iu); // Sent inventory update for unequip instantly.
		}
		
		final Item removedItem = player.getInventory().destroyItem("Destroy", itemToRemove, count, player, null);
		if (removedItem == null)
		{
			return;
		}
		
		final InventoryUpdate iu = new InventoryUpdate();
		if (removedItem.getCount() == 0)
		{
			iu.addRemovedItem(removedItem);
		}
		else
		{
			iu.addModifiedItem(removedItem);
		}
		player.sendPacket(iu); // Sent inventory update for destruction instantly.
		player.updateAdenaAndWeight();
		
		final SystemMessage sm;
		if (count > 1)
		{
			sm = new SystemMessage(SystemMessageId.S2_S1_S_DISAPPEARED);
			sm.addItemName(removedItem);
			sm.addLong(count);
		}
		else
		{
			sm = new SystemMessage(SystemMessageId.S1_HAS_DISAPPEARED);
			sm.addItemName(removedItem);
		}
		player.sendPacket(sm);
	}
}
