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

import java.util.List;

import org.l2jpx.commons.network.WritableBuffer;
import org.l2jpx.gameserver.enums.MailType;
import org.l2jpx.gameserver.instancemanager.MailManager;
import org.l2jpx.gameserver.model.Message;
import org.l2jpx.gameserver.network.GameClient;
import org.l2jpx.gameserver.network.ServerPackets;
import org.l2jpx.gameserver.network.SystemMessageId;

/**
 * @author Migi, DS
 */
public class ExShowReceivedPostList extends ServerPacket
{
	private static final int MESSAGE_FEE = 100;
	private static final int MESSAGE_FEE_PER_SLOT = 1000;
	
	private final List<Message> _inbox;
	
	public ExShowReceivedPostList(int objectId)
	{
		_inbox = MailManager.getInstance().getInbox(objectId);
	}
	
	@Override
	public void writeImpl(GameClient client, WritableBuffer buffer)
	{
		ServerPackets.EX_SHOW_RECEIVED_POST_LIST.writeId(this, buffer);
		buffer.writeInt((int) (System.currentTimeMillis() / 1000));
		if ((_inbox != null) && !_inbox.isEmpty())
		{
			buffer.writeInt(_inbox.size());
			for (Message msg : _inbox)
			{
				buffer.writeInt(msg.getMailType().ordinal());
				if (msg.getMailType() == MailType.COMMISSION_ITEM_SOLD)
				{
					buffer.writeInt(SystemMessageId.THE_ITEM_YOU_REGISTERED_HAS_BEEN_SOLD.getId());
				}
				else if (msg.getMailType() == MailType.COMMISSION_ITEM_RETURNED)
				{
					buffer.writeInt(SystemMessageId.THE_REGISTRATION_PERIOD_FOR_THE_ITEM_YOU_REGISTERED_HAS_EXPIRED.getId());
				}
				buffer.writeInt(msg.getId());
				buffer.writeString(msg.getSubject());
				buffer.writeString(msg.getSenderName());
				buffer.writeInt(msg.isLocked());
				buffer.writeInt(msg.getExpirationSeconds());
				buffer.writeInt(msg.isUnread());
				buffer.writeInt(!((msg.getMailType() == MailType.COMMISSION_ITEM_SOLD) || (msg.getMailType() == MailType.COMMISSION_ITEM_RETURNED)));
				buffer.writeInt(msg.hasAttachments());
				buffer.writeInt(msg.isReturned());
				buffer.writeInt(0); // SysString in some case it seems
			}
		}
		else
		{
			buffer.writeInt(0);
		}
		buffer.writeInt(MESSAGE_FEE);
		buffer.writeInt(MESSAGE_FEE_PER_SLOT);
	}
}
