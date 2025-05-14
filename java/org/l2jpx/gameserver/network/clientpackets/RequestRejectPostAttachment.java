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
import org.l2jpx.gameserver.enums.MailType;
import org.l2jpx.gameserver.instancemanager.MailManager;
import org.l2jpx.gameserver.model.Message;
import org.l2jpx.gameserver.model.World;
import org.l2jpx.gameserver.model.actor.Player;
import org.l2jpx.gameserver.model.zone.ZoneId;
import org.l2jpx.gameserver.network.SystemMessageId;
import org.l2jpx.gameserver.network.serverpackets.ExChangePostState;
import org.l2jpx.gameserver.network.serverpackets.SystemMessage;
import org.l2jpx.gameserver.util.Util;

/**
 * @author Migi, DS
 */
public class RequestRejectPostAttachment extends ClientPacket
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
		if (!Config.ALLOW_MAIL || !Config.ALLOW_ATTACHMENTS)
		{
			return;
		}
		
		final Player player = getPlayer();
		if (player == null)
		{
			return;
		}
		
		if (!getClient().getFloodProtectors().canPerformTransaction())
		{
			return;
		}
		
		if (!player.isInsideZone(ZoneId.PEACE))
		{
			player.sendPacket(SystemMessageId.YOU_CANNOT_RECEIVE_OR_SEND_MAIL_WITH_ATTACHED_ITEMS_IN_NON_PEACE_ZONE_REGIONS);
			return;
		}
		
		final Message msg = MailManager.getInstance().getMessage(_msgId);
		if (msg == null)
		{
			return;
		}
		
		if (msg.getReceiverId() != player.getObjectId())
		{
			Util.handleIllegalPlayerAction(player, player + " tried to reject not own attachment!", Config.DEFAULT_PUNISH);
			return;
		}
		
		if (!msg.hasAttachments() || (msg.getMailType() != MailType.REGULAR))
		{
			return;
		}
		
		MailManager.getInstance().sendMessage(new Message(msg));
		player.sendPacket(SystemMessageId.MAIL_SUCCESSFULLY_RETURNED);
		player.sendPacket(new ExChangePostState(true, _msgId, Message.REJECTED));
		
		final Player sender = World.getInstance().getPlayer(msg.getSenderId());
		if (sender != null)
		{
			final SystemMessage sm = new SystemMessage(SystemMessageId.S1_RETURNED_THE_MAIL);
			sm.addString(player.getName());
			sender.sendPacket(sm);
		}
	}
}
