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

import org.l2jpx.gameserver.model.actor.Player;
import org.l2jpx.gameserver.model.matching.MatchingRoom;
import org.l2jpx.gameserver.network.SystemMessageId;

/**
 * Format: (ch) d
 * @author -Wooden-, Tryskell
 */
public class AnswerJoinPartyRoom extends ClientPacket
{
	private boolean _answer;
	
	@Override
	protected void readImpl()
	{
		_answer = readInt() == 1;
	}
	
	@Override
	protected void runImpl()
	{
		final Player player = getPlayer();
		if (player == null)
		{
			return;
		}
		
		final Player partner = player.getActiveRequester();
		if (partner == null)
		{
			player.sendPacket(SystemMessageId.THAT_PLAYER_IS_NOT_ONLINE);
			player.setActiveRequester(null);
			return;
		}
		
		if (_answer && !partner.isRequestExpired())
		{
			final MatchingRoom room = partner.getMatchingRoom();
			if (room == null)
			{
				return;
			}
			
			room.addMember(player);
		}
		else
		{
			partner.sendPacket(SystemMessageId.THE_RECIPIENT_OF_YOUR_INVITATION_DID_NOT_ACCEPT_THE_PARTY_MATCHING_INVITATION);
		}
		
		// reset transaction timers
		player.setActiveRequester(null);
		partner.onTransactionResponse();
	}
}
