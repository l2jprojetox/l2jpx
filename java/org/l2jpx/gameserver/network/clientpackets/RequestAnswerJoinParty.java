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
import org.l2jpx.gameserver.enums.PartyMessageType;
import org.l2jpx.gameserver.model.Party;
import org.l2jpx.gameserver.model.actor.Player;
import org.l2jpx.gameserver.model.actor.request.PartyRequest;
import org.l2jpx.gameserver.model.matching.MatchingRoom;
import org.l2jpx.gameserver.network.SystemMessageId;
import org.l2jpx.gameserver.network.serverpackets.JoinParty;
import org.l2jpx.gameserver.network.serverpackets.SystemMessage;

public class RequestAnswerJoinParty extends ClientPacket
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
		
		final PartyRequest request = player.getRequest(PartyRequest.class);
		if ((request == null) || request.isProcessing() || !player.removeRequest(request.getClass()))
		{
			return;
		}
		request.setProcessing(true);
		
		final Player requestor = request.getPlayer();
		if (requestor == null)
		{
			return;
		}
		
		final Party party = request.getParty();
		final Party requestorParty = requestor.getParty();
		if ((requestorParty != null) && (requestorParty != party))
		{
			return;
		}
		
		requestor.sendPacket(new JoinParty(_response));
		if (_response == 1)
		{
			if (party.getMemberCount() >= Config.ALT_PARTY_MAX_MEMBERS)
			{
				final SystemMessage sm = new SystemMessage(SystemMessageId.THE_PARTY_IS_FULL);
				player.sendPacket(sm);
				requestor.sendPacket(sm);
				return;
			}
			
			// Assign the party to the leader upon accept of his partner
			if (requestorParty == null)
			{
				requestor.setParty(party);
			}
			
			player.joinParty(party);
			
			final MatchingRoom requestorRoom = requestor.getMatchingRoom();
			if (requestorRoom != null)
			{
				requestorRoom.addMember(player);
			}
		}
		else if (_response == -1)
		{
			final SystemMessage sm = new SystemMessage(SystemMessageId.C1_IS_SET_TO_REFUSE_PARTY_REQUESTS_AND_CANNOT_RECEIVE_A_PARTY_REQUEST);
			sm.addPcName(player);
			requestor.sendPacket(sm);
			
			if (party.getMemberCount() == 1)
			{
				party.removePartyMember(requestor, PartyMessageType.NONE);
			}
		}
		else if (party.getMemberCount() == 1)
		{
			party.removePartyMember(requestor, PartyMessageType.NONE);
		}
		
		party.setPendingInvitation(false);
		request.setProcessing(false);
	}
}
