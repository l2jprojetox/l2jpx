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
package org.l2jpx.gameserver.network.serverpackets.luckygame;

import org.l2jpx.commons.network.WritableBuffer;
import org.l2jpx.gameserver.enums.LuckyGameType;
import org.l2jpx.gameserver.network.GameClient;
import org.l2jpx.gameserver.network.ServerPackets;
import org.l2jpx.gameserver.network.serverpackets.ServerPacket;

/**
 * @author Sdw
 */
public class ExStartLuckyGame extends ServerPacket
{
	private final LuckyGameType _type;
	private final int _ticketCount;
	
	public ExStartLuckyGame(LuckyGameType type, long ticketCount)
	{
		_type = type;
		_ticketCount = (int) ticketCount;
	}
	
	@Override
	public void writeImpl(GameClient client, WritableBuffer buffer)
	{
		ServerPackets.EX_START_LUCKY_GAME.writeId(this, buffer);
		buffer.writeInt(_type.ordinal());
		buffer.writeInt(_ticketCount);
	}
}
