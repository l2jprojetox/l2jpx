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

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.l2jpx.commons.network.WritableBuffer;
import org.l2jpx.gameserver.model.Location;
import org.l2jpx.gameserver.model.Party;
import org.l2jpx.gameserver.model.actor.Player;
import org.l2jpx.gameserver.network.GameClient;
import org.l2jpx.gameserver.network.ServerPackets;

/**
 * @author zabbix
 */
public class PartyMemberPosition extends ServerPacket
{
	private final Map<Integer, Location> locations = new HashMap<>();
	
	public PartyMemberPosition(Party party)
	{
		reuse(party);
	}
	
	public void reuse(Party party)
	{
		locations.clear();
		for (Player member : party.getMembers())
		{
			if (member == null)
			{
				continue;
			}
			locations.put(member.getObjectId(), member.getLocation());
		}
	}
	
	@Override
	public void writeImpl(GameClient client, WritableBuffer buffer)
	{
		ServerPackets.PARTY_MEMBER_POSITION.writeId(this, buffer);
		buffer.writeInt(locations.size());
		for (Entry<Integer, Location> entry : locations.entrySet())
		{
			final Location loc = entry.getValue();
			buffer.writeInt(entry.getKey());
			buffer.writeInt(loc.getX());
			buffer.writeInt(loc.getY());
			buffer.writeInt(loc.getZ());
		}
	}
}
