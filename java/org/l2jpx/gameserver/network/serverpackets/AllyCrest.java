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

import org.l2jpx.Config;
import org.l2jpx.commons.network.WritableBuffer;
import org.l2jpx.gameserver.data.sql.CrestTable;
import org.l2jpx.gameserver.model.Crest;
import org.l2jpx.gameserver.network.GameClient;
import org.l2jpx.gameserver.network.ServerPackets;

public class AllyCrest extends ServerPacket
{
	private final int _crestId;
	private final byte[] _data;
	
	public AllyCrest(int crestId)
	{
		_crestId = crestId;
		final Crest crest = CrestTable.getInstance().getCrest(crestId);
		_data = crest != null ? crest.getData() : null;
	}
	
	public AllyCrest(int crestId, byte[] data)
	{
		_crestId = crestId;
		_data = data;
	}
	
	@Override
	public void writeImpl(GameClient client, WritableBuffer buffer)
	{
		ServerPackets.ALLIANCE_CREST.writeId(this, buffer);
		buffer.writeInt(Config.SERVER_ID);
		buffer.writeInt(_crestId);
		if (_data != null)
		{
			buffer.writeInt(_data.length);
			buffer.writeBytes(_data);
		}
		else
		{
			buffer.writeInt(0);
		}
	}
}
