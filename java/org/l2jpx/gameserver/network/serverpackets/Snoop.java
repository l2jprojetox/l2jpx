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
import org.l2jpx.gameserver.enums.ChatType;
import org.l2jpx.gameserver.network.GameClient;
import org.l2jpx.gameserver.network.ServerPackets;

public class Snoop extends ServerPacket
{
	private final int _convoId;
	private final String _name;
	private final ChatType _type;
	private final String _speaker;
	private final String _msg;
	
	public Snoop(int id, String name, ChatType type, String speaker, String msg)
	{
		_convoId = id;
		_name = name;
		_type = type;
		_speaker = speaker;
		_msg = msg;
	}
	
	@Override
	public void writeImpl(GameClient client, WritableBuffer buffer)
	{
		ServerPackets.SNOOP.writeId(this, buffer);
		buffer.writeInt(_convoId);
		buffer.writeString(_name);
		buffer.writeInt(0); // ??
		buffer.writeInt(_type.getClientId());
		buffer.writeString(_speaker);
		buffer.writeString(_msg);
	}
}