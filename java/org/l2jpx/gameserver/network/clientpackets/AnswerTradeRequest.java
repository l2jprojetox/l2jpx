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

import org.l2jpx.gameserver.model.World;
import org.l2jpx.gameserver.model.actor.Player;
import org.l2jpx.gameserver.network.SystemMessageId;
import org.l2jpx.gameserver.network.serverpackets.ActionFailed;
import org.l2jpx.gameserver.network.serverpackets.SystemMessage;
import org.l2jpx.gameserver.network.serverpackets.TradeDone;

/**
 * @version $Revision: 1.5.4.2 $ $Date: 2005/03/27 15:29:30 $
 */
public class AnswerTradeRequest extends ClientPacket
{
	private int _response;
	
	@Override
	protected void readImpl()
	{
		_response = readInt();
	}
	
	@Override
	protected void runImpl()
	{
		final Player player = getPlayer();
		if (player == null)
		{
			return;
		}
		
		if (!player.getAccessLevel().allowTransaction())
		{
			player.sendPacket(SystemMessageId.YOU_ARE_NOT_AUTHORIZED_TO_DO_THAT);
			player.sendPacket(ActionFailed.STATIC_PACKET);
			return;
		}
		
		final Player partner = player.getActiveRequester();
		if (partner == null)
		{
			// Trade partner not found, cancel trade
			player.sendPacket(new TradeDone(0));
			player.sendPacket(SystemMessageId.THAT_PLAYER_IS_NOT_ONLINE);
			player.setActiveRequester(null);
			return;
		}
		else if (World.getInstance().getPlayer(partner.getObjectId()) == null)
		{
			// Trade partner not found, cancel trade
			player.sendPacket(new TradeDone(0));
			player.sendPacket(SystemMessageId.THAT_PLAYER_IS_NOT_ONLINE);
			player.setActiveRequester(null);
			return;
		}
		
		if ((_response == 1) && !partner.isRequestExpired())
		{
			player.startTrade(partner);
		}
		else
		{
			final SystemMessage msg = new SystemMessage(SystemMessageId.C1_HAS_DENIED_YOUR_REQUEST_TO_TRADE);
			msg.addString(player.getName());
			partner.sendPacket(msg);
		}
		
		// Clears requesting status
		player.setActiveRequester(null);
		partner.onTransactionResponse();
	}
}
