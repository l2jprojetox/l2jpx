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

import java.util.Map;
import java.util.Map.Entry;

import org.l2jpx.commons.network.WritableBuffer;
import org.l2jpx.gameserver.model.actor.Player;
import org.l2jpx.gameserver.network.GameClient;
import org.l2jpx.gameserver.network.ServerPackets;

/**
 * @author Mobius
 */
public class ExPVPMatchCCRecord extends ServerPacket
{
	public static final int INITIALIZE = 0;
	public static final int UPDATE = 1;
	public static final int FINISH = 2;
	
	private final int _state;
	private final Map<Player, Integer> _players;
	
	public ExPVPMatchCCRecord(int state, Map<Player, Integer> players)
	{
		_state = state;
		_players = players;
	}
	
	@Override
	public void writeImpl(GameClient client, WritableBuffer buffer)
	{
		ServerPackets.EX_PVP_MATCH_CCRECORD.writeId(this, buffer);
		buffer.writeInt(_state); // 0 - initialize, 1 - update, 2 - finish
		buffer.writeInt(Math.min(_players.size(), 25));
		int counter = 0;
		for (Entry<Player, Integer> entry : _players.entrySet())
		{
			counter++;
			if (counter > 25)
			{
				break;
			}
			buffer.writeString(entry.getKey().getName());
			buffer.writeInt(entry.getValue());
		}
	}
}
