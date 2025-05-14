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

import org.l2jpx.commons.network.WritableBuffer;
import org.l2jpx.gameserver.model.Party;
import org.l2jpx.gameserver.model.actor.Player;
import org.l2jpx.gameserver.network.GameClient;
import org.l2jpx.gameserver.network.ServerPackets;

/**
 * @author chris_00
 */
public class ExMPCCShowPartyMemberInfo extends ServerPacket
{
	private final Party _party;
	
	public ExMPCCShowPartyMemberInfo(Party party)
	{
		_party = party;
	}
	
	@Override
	public void writeImpl(GameClient client, WritableBuffer buffer)
	{
		ServerPackets.EX_MPCCSHOW_PARTY_MEMBER_INFO.writeId(this, buffer);
		buffer.writeInt(_party.getMemberCount());
		for (Player pc : _party.getMembers())
		{
			buffer.writeString(pc.getName());
			buffer.writeInt(pc.getObjectId());
			buffer.writeInt(pc.getClassId().getId());
		}
	}
}
