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
 * Format: (ch)ddd
 */
public class ExVariationResult extends ServerPacket
{
	private final int _option1;
	private final int _option2;
	private final boolean _success;
	
	public ExVariationResult(int option1, int option2, boolean success)
	{
		_option1 = option1;
		_option2 = option2;
		_success = success;
	}
	
	@Override
	public void writeImpl(GameClient client, WritableBuffer buffer)
	{
		ServerPackets.EX_VARIATION_RESULT.writeId(this, buffer);
		buffer.writeInt(_option1);
		buffer.writeInt(_option2);
		buffer.writeInt(_success);
	}
}
