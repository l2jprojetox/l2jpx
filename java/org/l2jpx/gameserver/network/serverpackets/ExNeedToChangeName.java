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
 * Dialog with input field<br>
 * type 0 = char name (Selection screen)<br>
 * type 1 = clan name
 * @author JIV
 */
public class ExNeedToChangeName extends ServerPacket
{
	private final int _type;
	private final int _subType;
	private final String _name;
	
	public ExNeedToChangeName(int type, int subType, String name)
	{
		_type = type;
		_subType = subType;
		_name = name;
	}
	
	@Override
	public void writeImpl(GameClient client, WritableBuffer buffer)
	{
		ServerPackets.EX_NEED_TO_CHANGE_NAME.writeId(this, buffer);
		buffer.writeInt(_type);
		buffer.writeInt(_subType);
		buffer.writeString(_name);
	}
}
