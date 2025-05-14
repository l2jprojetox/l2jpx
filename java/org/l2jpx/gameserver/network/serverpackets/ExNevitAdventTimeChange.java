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
import org.l2jpx.gameserver.network.GameClient;
import org.l2jpx.gameserver.network.ServerPackets;

/**
 * @author mochitto
 */
public class ExNevitAdventTimeChange extends ServerPacket
{
	private final boolean _paused;
	private final int _time;
	
	public ExNevitAdventTimeChange(int time)
	{
		_time = time > 240000 ? 240000 : time;
		_paused = _time < 1;
	}
	
	@Override
	public void writeImpl(GameClient client, WritableBuffer buffer)
	{
		ServerPackets.EX_GET_CRYSTALIZING_ESTIMATION.writeId(this, buffer);
		// state 0 - pause 1 - started
		buffer.writeByte(!_paused);
		// left time in ms max is 16000 its 4m and state is automatically changed to quit
		buffer.writeInt(_time);
	}
}
