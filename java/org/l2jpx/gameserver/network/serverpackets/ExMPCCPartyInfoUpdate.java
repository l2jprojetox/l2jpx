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
import org.l2jpx.gameserver.network.GameClient;
import org.l2jpx.gameserver.network.ServerPackets;

/**
 * @author chris_00
 */
public class ExMPCCPartyInfoUpdate extends ServerPacket
{
	private final int _mode;
	private final int _LeaderOID;
	private final int _memberCount;
	private final String _name;
	
	/**
	 * @param party
	 * @param mode 0 = Remove, 1 = Add
	 */
	public ExMPCCPartyInfoUpdate(Party party, int mode)
	{
		_name = party.getLeader().getName();
		_LeaderOID = party.getLeaderObjectId();
		_memberCount = party.getMemberCount();
		_mode = mode;
	}
	
	@Override
	public void writeImpl(GameClient client, WritableBuffer buffer)
	{
		ServerPackets.EX_MPCCPARTY_INFO_UPDATE.writeId(this, buffer);
		buffer.writeString(_name);
		buffer.writeInt(_LeaderOID);
		buffer.writeInt(_memberCount);
		buffer.writeInt(_mode); // mode 0 = Remove Party, 1 = AddParty, maybe more...
	}
}
