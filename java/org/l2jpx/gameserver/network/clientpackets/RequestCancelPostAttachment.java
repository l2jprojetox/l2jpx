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
import org.l2jpx.gameserver.enums.ItemLocation;
import org.l2jpx.gameserver.instancemanager.MailManager;
import org.l2jpx.gameserver.model.Message;
import org.l2jpx.gameserver.model.World;
import org.l2jpx.gameserver.model.actor.Player;
import org.l2jpx.gameserver.model.item.instance.Item;
import org.l2jpx.gameserver.model.itemcontainer.ItemContainer;
import org.l2jpx.gameserver.model.zone.ZoneId;
import org.l2jpx.gameserver.network.SystemMessageId;
import org.l2jpx.gameserver.network.serverpackets.ExChangePostState;
import org.l2jpx.gameserver.network.serverpackets.InventoryUpdate;
import org.l2jpx.gameserver.network.serverpackets.SystemMessage;
import org.l2jpx.gameserver.util.Util;

/**
 * @author Migi, DS
 */
public class RequestCancelPostAttachment extends ClientPacket
{
	private int _msgId;
	
	@Override
	protected void readImpl()
	{
		_msgId = readInt();
	}
	
	@Override
	protected void runImpl()
	{
		final Player player = getPlayer();
		if ((player == null) || !Config.ALLOW_MAIL || !Config.ALLOW_ATTACHMENTS)
		{
			return;
		}
		
		if (!getClient().getFloodProtectors().canPerformTransaction())
		{
			return;
		}
		
		final Message msg = MailManager.getInstance().getMessage(_msgId);
		if (msg == null)
		{
			return;
		}
		if (msg.getSenderId() != player.getObjectId())
		{
			Util.handleIllegalPlayerAction(player, player + " tried to cancel not own post!", Config.DEFAULT_PUNISH);
			return;
		}
		
		if (!player.isInsideZone(ZoneId.PEACE))
		{
			player.sendPacket(SystemMessageId.YOU_CANNOT_CANCEL_IN_A_NON_PEACE_ZONE_LOCATION);
			return;
		}
		
		if (player.getActiveTradeList() != null)
		{
			player.sendPacket(SystemMessageId.YOU_CANNOT_CANCEL_DURING_AN_EXCHANGE);
			return;
		}
		
		if (player.hasItemRequest())
		{
			player.sendPacket(SystemMessageId.YOU_CANNOT_CANCEL_DURING_AN_ITEM_ENHANCEMENT_OR_ATTRIBUTE_ENHANCEMENT);
			return;
		}
		
		if (player.isInStoreMode())
		{
			player.sendPacket(SystemMessageId.YOU_CANNOT_CANCEL_BECAUSE_THE_PRIVATE_STORE_OR_WORKSHOP_IS_IN_PROGRESS);
			return;
		}
		
		if (!msg.hasAttachments())
		{
			player.sendPacket(SystemMessageId.YOU_CANNOT_CANCEL_SENT_MAIL_SINCE_THE_RECIPIENT_RECEIVED_IT);
			return;
		}
		
		final ItemContainer attachments = msg.getAttachments();
		if ((attachments == null) || (attachments.getSize() == 0))
		{
			player.sendPacket(SystemMessageId.YOU_CANNOT_CANCEL_SENT_MAIL_SINCE_THE_RECIPIENT_RECEIVED_IT);
			return;
		}
		
		int weight = 0;
		int slots = 0;
		for (Item item : attachments.getItems())
		{
			if (item == null)
			{
				continue;
			}
			
			if (item.getOwnerId() != player.getObjectId())
			{
				Util.handleIllegalPlayerAction(player, player + " tried to get not own item from cancelled attachment!", Config.DEFAULT_PUNISH);
				return;
			}
			
			if (item.getItemLocation() != ItemLocation.MAIL)
			{
				Util.handleIllegalPlayerAction(player, player + " tried to get items not from mail !", Config.DEFAULT_PUNISH);
				return;
			}
			
			if (item.getLocationSlot() != msg.getId())
			{
				Util.handleIllegalPlayerAction(player, player + " tried to get items from different attachment!", Config.DEFAULT_PUNISH);
				return;
			}
			
			weight += item.getCount() * item.getTemplate().getWeight();
			if (!item.isStackable())
			{
				slots += item.getCount();
			}
			else if (player.getInventory().getItemByItemId(item.getId()) == null)
			{
				slots++;
			}
		}
		
		if (!player.getInventory().validateCapacity(slots))
		{
			player.sendPacket(SystemMessageId.YOU_COULD_NOT_CANCEL_RECEIPT_BECAUSE_YOUR_INVENTORY_IS_FULL);
			return;
		}
		
		if (!player.getInventory().validateWeight(weight))
		{
			player.sendPacket(SystemMessageId.YOU_COULD_NOT_CANCEL_RECEIPT_BECAUSE_YOUR_INVENTORY_IS_FULL);
			return;
		}
		
		// Proceed to the transfer
		final InventoryUpdate playerIU = new InventoryUpdate();
		for (Item item : attachments.getItems())
		{
			if (item == null)
			{
				continue;
			}
			
			final long count = item.getCount();
			final Item newItem = attachments.transferItem(attachments.getName(), item.getObjectId(), count, player.getInventory(), player, null);
			if (newItem == null)
			{
				return;
			}
			
			if (newItem.isStackable() && (newItem.getCount() > count))
			{
				playerIU.addModifiedItem(newItem);
			}
			else
			{
				playerIU.addNewItem(newItem);
			}
			
			final SystemMessage sm = new SystemMessage(SystemMessageId.YOU_HAVE_ACQUIRED_S2_S1);
			sm.addItemName(item.getId());
			sm.addLong(count);
			player.sendPacket(sm);
		}
		
		msg.removeAttachments();
		
		// Send updated item list to the player
		player.sendInventoryUpdate(playerIU);
		
		// Send full list to avoid duplicates.
		player.sendItemList(false);
		
		final Player receiver = World.getInstance().getPlayer(msg.getReceiverId());
		if (receiver != null)
		{
			final SystemMessage sm = new SystemMessage(SystemMessageId.S1_CANCELED_THE_SENT_MAIL);
			sm.addString(player.getName());
			receiver.sendPacket(sm);
			receiver.sendPacket(new ExChangePostState(true, _msgId, Message.DELETED));
		}
		
		MailManager.getInstance().deleteMessageInDb(_msgId);
		
		player.sendPacket(new ExChangePostState(false, _msgId, Message.DELETED));
		player.sendPacket(SystemMessageId.MAIL_SUCCESSFULLY_CANCELLED);
	}
}
