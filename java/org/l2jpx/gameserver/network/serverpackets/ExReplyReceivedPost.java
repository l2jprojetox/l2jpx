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
package org.l2jpx.gameserver.network.serverpackets;

import java.util.Collection;

import org.l2jpx.commons.network.WritableBuffer;
import org.l2jpx.gameserver.enums.MailType;
import org.l2jpx.gameserver.model.Message;
import org.l2jpx.gameserver.model.item.instance.Item;
import org.l2jpx.gameserver.model.itemcontainer.ItemContainer;
import org.l2jpx.gameserver.network.GameClient;
import org.l2jpx.gameserver.network.PacketLogger;
import org.l2jpx.gameserver.network.ServerPackets;
import org.l2jpx.gameserver.network.SystemMessageId;

/**
 * @author Migi, DS
 */
public class ExReplyReceivedPost extends AbstractItemPacket
{
	private final Message _msg;
	private Collection<Item> _items = null;
	
	public ExReplyReceivedPost(Message msg)
	{
		_msg = msg;
		if (msg.hasAttachments())
		{
			final ItemContainer attachments = msg.getAttachments();
			if ((attachments != null) && (attachments.getSize() > 0))
			{
				_items = attachments.getItems();
			}
			else
			{
				PacketLogger.warning("Message " + msg.getId() + " has attachments but itemcontainer is empty.");
			}
		}
	}
	
	@Override
	public void writeImpl(GameClient client, WritableBuffer buffer)
	{
		ServerPackets.EX_REPLY_RECEIVED_POST.writeId(this, buffer);
		buffer.writeInt(_msg.getMailType().ordinal()); // GOD
		if (_msg.getMailType() == MailType.COMMISSION_ITEM_RETURNED)
		{
			buffer.writeInt(SystemMessageId.THE_REGISTRATION_PERIOD_FOR_THE_ITEM_YOU_REGISTERED_HAS_EXPIRED.getId());
			buffer.writeInt(SystemMessageId.THE_AUCTION_HOUSE_REGISTRATION_PERIOD_HAS_EXPIRED_AND_THE_CORRESPONDING_ITEM_IS_BEING_FORWARDED.getId());
		}
		else if (_msg.getMailType() == MailType.COMMISSION_ITEM_SOLD)
		{
			buffer.writeInt(_msg.getItemId());
			buffer.writeInt(_msg.getEnchantLvl());
			for (int i = 0; i < 6; i++)
			{
				buffer.writeInt(_msg.getElementals()[i]);
			}
			buffer.writeInt(SystemMessageId.THE_ITEM_YOU_REGISTERED_HAS_BEEN_SOLD.getId());
			buffer.writeInt(SystemMessageId.S1_HAS_BEEN_SOLD.getId());
		}
		buffer.writeInt(_msg.getId());
		buffer.writeInt(_msg.isLocked());
		buffer.writeInt(0); // Unknown
		buffer.writeString(_msg.getSenderName());
		buffer.writeString(_msg.getSubject());
		buffer.writeString(_msg.getContent());
		if ((_items != null) && !_items.isEmpty())
		{
			buffer.writeInt(_items.size());
			for (Item item : _items)
			{
				writeItem(item, buffer);
				buffer.writeInt(item.getObjectId());
			}
		}
		else
		{
			buffer.writeInt(0);
		}
		buffer.writeLong(_msg.getReqAdena());
		buffer.writeInt(_msg.hasAttachments());
		buffer.writeInt(_msg.isReturned());
	}
}
