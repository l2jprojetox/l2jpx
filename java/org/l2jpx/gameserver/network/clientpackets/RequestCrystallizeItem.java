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

import java.util.List;

import org.l2jpx.Config;
import org.l2jpx.commons.util.Rnd;
import org.l2jpx.gameserver.data.xml.ItemCrystallizationData;
import org.l2jpx.gameserver.enums.Race;
import org.l2jpx.gameserver.model.actor.Player;
import org.l2jpx.gameserver.model.holders.ItemChanceHolder;
import org.l2jpx.gameserver.model.item.instance.Item;
import org.l2jpx.gameserver.model.item.type.CrystalType;
import org.l2jpx.gameserver.model.itemcontainer.PlayerInventory;
import org.l2jpx.gameserver.model.skill.CommonSkill;
import org.l2jpx.gameserver.network.PacketLogger;
import org.l2jpx.gameserver.network.SystemMessageId;
import org.l2jpx.gameserver.network.serverpackets.ActionFailed;
import org.l2jpx.gameserver.network.serverpackets.InventoryUpdate;
import org.l2jpx.gameserver.network.serverpackets.SystemMessage;
import org.l2jpx.gameserver.util.Util;

/**
 * @version $Revision: 1.2.2.3.2.5 $ $Date: 2005/03/27 15:29:30 $
 */
public class RequestCrystallizeItem extends ClientPacket
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
			// PacketLogger.finer("RequestCrystalizeItem: activeChar was null.");
			return;
		}
		
		// if (!client.getFloodProtectors().canPerformTransaction())
		// {
		// player.sendMessage("You are crystallizing too fast.");
		// return;
		// }
		
		if (_count < 1)
		{
			Util.handleIllegalPlayerAction(player, "[RequestCrystallizeItem] count <= 0! ban! oid: " + _objectId + " owner: " + player.getName(), Config.DEFAULT_PUNISH);
			return;
		}
		
		if (player.isInStoreMode() || !player.isInCrystallize())
		{
			player.sendPacket(SystemMessageId.WHILE_OPERATING_A_PRIVATE_STORE_OR_WORKSHOP_YOU_CANNOT_DISCARD_DESTROY_OR_TRADE_AN_ITEM);
			return;
		}
		
		final int skillLevel = player.getSkillLevel(CommonSkill.CRYSTALLIZE.getId());
		if (skillLevel <= 0)
		{
			player.sendPacket(SystemMessageId.YOU_MAY_NOT_CRYSTALLIZE_THIS_ITEM_YOUR_CRYSTALLIZATION_SKILL_LEVEL_IS_TOO_LOW);
			player.sendPacket(ActionFailed.STATIC_PACKET);
			if ((player.getRace() != Race.DWARF) && (player.getClassId().getId() != 117) && (player.getClassId().getId() != 55))
			{
				PacketLogger.info(player + " used crystalize with classid: " + player.getClassId().getId());
			}
			return;
		}
		
		final PlayerInventory inventory = player.getInventory();
		if (inventory != null)
		{
			final Item item = inventory.getItemByObjectId(_objectId);
			if ((item == null) || item.isHeroItem() || (!Config.ALT_ALLOW_AUGMENT_DESTROY && item.isAugmented()))
			{
				player.sendPacket(ActionFailed.STATIC_PACKET);
				return;
			}
			
			if (_count > item.getCount())
			{
				_count = player.getInventory().getItemByObjectId(_objectId).getCount();
			}
		}
		
		final Item itemToRemove = player.getInventory().getItemByObjectId(_objectId);
		if ((itemToRemove == null) || itemToRemove.isShadowItem() || itemToRemove.isTimeLimitedItem())
		{
			player.sendPacket(ActionFailed.STATIC_PACKET);
			return;
		}
		
		if (!itemToRemove.getTemplate().isCrystallizable() || (itemToRemove.getTemplate().getCrystalCount() <= 0) || (itemToRemove.getTemplate().getCrystalType() == CrystalType.NONE))
		{
			player.sendPacket(SystemMessageId.THIS_ITEM_CANNOT_BE_CRYSTALLIZED);
			return;
		}
		
		if (!player.getInventory().canManipulateWithItemId(itemToRemove.getId()))
		{
			player.sendPacket(SystemMessageId.THIS_ITEM_CANNOT_BE_CRYSTALLIZED);
			return;
		}
		
		// Check if the char can crystallize items and return if false;
		boolean canCrystallize = true;
		
		switch (itemToRemove.getTemplate().getCrystalTypePlus())
		{
			case D:
			{
				if (skillLevel < 1)
				{
					canCrystallize = false;
				}
				break;
			}
			case C:
			{
				if (skillLevel < 2)
				{
					canCrystallize = false;
				}
				break;
			}
			case B:
			{
				if (skillLevel < 3)
				{
					canCrystallize = false;
				}
				break;
			}
			case A:
			{
				if (skillLevel < 4)
				{
					canCrystallize = false;
				}
				break;
			}
			case S:
			{
				if (skillLevel < 5)
				{
					canCrystallize = false;
				}
				break;
			}
			case R:
			{
				if (skillLevel < 6)
				{
					canCrystallize = false;
				}
				break;
			}
		}
		
		if (!canCrystallize)
		{
			player.sendPacket(SystemMessageId.YOU_MAY_NOT_CRYSTALLIZE_THIS_ITEM_YOUR_CRYSTALLIZATION_SKILL_LEVEL_IS_TOO_LOW);
			player.sendPacket(ActionFailed.STATIC_PACKET);
			return;
		}
		
		final List<ItemChanceHolder> crystallizationRewards = ItemCrystallizationData.getInstance().getCrystallizationRewards(itemToRemove);
		if ((crystallizationRewards == null) || crystallizationRewards.isEmpty())
		{
			player.sendPacket(SystemMessageId.CRYSTALLIZATION_CANNOT_BE_PROCEEDED_BECAUSE_THERE_ARE_NO_ITEMS_REGISTERED);
			return;
		}
		
		// player.setInCrystallize(true);
		
		// unequip if needed
		SystemMessage sm;
		if (itemToRemove.isEquipped())
		{
			final InventoryUpdate iu = new InventoryUpdate();
			for (Item item : player.getInventory().unEquipItemInSlotAndRecord(itemToRemove.getLocationSlot()))
			{
				iu.addModifiedItem(item);
			}
			player.sendPacket(iu); // Sent inventory update for unequip instantly.
			
			if (itemToRemove.getEnchantLevel() > 0)
			{
				sm = new SystemMessage(SystemMessageId.THE_EQUIPMENT_S1_S2_HAS_BEEN_REMOVED);
				sm.addInt(itemToRemove.getEnchantLevel());
				sm.addItemName(itemToRemove);
			}
			else
			{
				sm = new SystemMessage(SystemMessageId.S1_HAS_BEEN_UNEQUIPPED);
				sm.addItemName(itemToRemove);
			}
			player.sendPacket(sm);
		}
		
		// remove from inventory
		final Item removedItem = player.getInventory().destroyItem("Crystalize", _objectId, _count, player, null);
		final InventoryUpdate iu = new InventoryUpdate();
		iu.addRemovedItem(removedItem);
		player.sendPacket(iu); // Sent inventory update for destruction instantly.
		player.updateAdenaAndWeight();
		
		for (ItemChanceHolder holder : crystallizationRewards)
		{
			final double rand = Rnd.nextDouble() * 100;
			if (rand < holder.getChance())
			{
				// add crystals
				final Item createdItem = player.getInventory().addItem("Crystalize", holder.getId(), holder.getCount(), player, player);
				sm = new SystemMessage(SystemMessageId.YOU_HAVE_EARNED_S2_S1_S);
				sm.addItemName(createdItem);
				sm.addLong(holder.getCount());
				player.sendPacket(sm);
			}
		}
		
		sm = new SystemMessage(SystemMessageId.S1_HAS_BEEN_CRYSTALLIZED);
		sm.addItemName(removedItem);
		player.sendPacket(sm);
		
		player.broadcastUserInfo();
		
		player.setInCrystallize(false);
	}
}
