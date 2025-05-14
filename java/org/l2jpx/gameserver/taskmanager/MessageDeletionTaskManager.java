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
package org.l2jpx.gameserver.taskmanager;

import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import org.l2jpx.commons.threads.ThreadPool;
import org.l2jpx.gameserver.instancemanager.MailManager;
import org.l2jpx.gameserver.model.Message;
import org.l2jpx.gameserver.model.World;
import org.l2jpx.gameserver.model.actor.Player;
import org.l2jpx.gameserver.network.SystemMessageId;
import org.l2jpx.gameserver.network.serverpackets.SystemMessage;

/**
 * @author Mobius
 */
public class MessageDeletionTaskManager implements Runnable
{
	private static final Map<Integer, Long> PENDING_MESSAGES = new ConcurrentHashMap<>();
	private static boolean _working = false;
	
	protected MessageDeletionTaskManager()
	{
		ThreadPool.scheduleAtFixedRate(this, 10000, 10000);
	}
	
	@Override
	public void run()
	{
		if (_working)
		{
			return;
		}
		_working = true;
		
		if (!PENDING_MESSAGES.isEmpty())
		{
			final long currentTime = System.currentTimeMillis();
			final Iterator<Entry<Integer, Long>> iterator = PENDING_MESSAGES.entrySet().iterator();
			Entry<Integer, Long> entry;
			Integer messageId;
			Message message;
			
			while (iterator.hasNext())
			{
				entry = iterator.next();
				if (currentTime > entry.getValue())
				{
					messageId = entry.getKey();
					message = MailManager.getInstance().getMessage(messageId);
					if (message == null)
					{
						iterator.remove();
						continue;
					}
					
					if (message.hasAttachments())
					{
						final Player sender = World.getInstance().getPlayer(message.getSenderId());
						if (sender != null)
						{
							message.getAttachments().returnToWh(sender.getWarehouse());
							sender.sendPacket(SystemMessageId.THE_MAIL_WAS_RETURNED_DUE_TO_THE_EXCEEDED_WAITING_TIME);
						}
						else
						{
							message.getAttachments().returnToWh(null);
						}
						message.getAttachments().deleteMe();
						message.removeAttachments();
						
						final Player receiver = World.getInstance().getPlayer(message.getReceiverId());
						if (receiver != null)
						{
							receiver.sendPacket(new SystemMessage(SystemMessageId.THE_MAIL_WAS_RETURNED_DUE_TO_THE_EXCEEDED_WAITING_TIME));
						}
					}
					
					MailManager.getInstance().deleteMessageInDb(messageId);
					iterator.remove();
				}
			}
		}
		
		_working = false;
	}
	
	public void add(int msgId, long deletionTime)
	{
		PENDING_MESSAGES.put(msgId, deletionTime);
	}
	
	public static MessageDeletionTaskManager getInstance()
	{
		return SingletonHolder.INSTANCE;
	}
	
	private static class SingletonHolder
	{
		protected static final MessageDeletionTaskManager INSTANCE = new MessageDeletionTaskManager();
	}
}
