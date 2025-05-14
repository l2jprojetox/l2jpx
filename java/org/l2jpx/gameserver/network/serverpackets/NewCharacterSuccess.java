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

import java.util.ArrayList;
import java.util.List;

import org.l2jpx.commons.network.WritableBuffer;
import org.l2jpx.gameserver.model.actor.templates.PlayerTemplate;
import org.l2jpx.gameserver.network.GameClient;
import org.l2jpx.gameserver.network.ServerPackets;

public class NewCharacterSuccess extends ServerPacket
{
	private final List<PlayerTemplate> _chars = new ArrayList<>();
	
	public void addChar(PlayerTemplate template)
	{
		_chars.add(template);
	}
	
	@Override
	public void writeImpl(GameClient client, WritableBuffer buffer)
	{
		ServerPackets.NEW_CHARACTER_SUCCESS.writeId(this, buffer);
		buffer.writeInt(_chars.size());
		for (PlayerTemplate chr : _chars)
		{
			if (chr == null)
			{
				continue;
			}
			// TODO: Unhardcode these
			buffer.writeInt(chr.getRace().ordinal());
			buffer.writeInt(chr.getClassId().getId());
			buffer.writeInt(99);
			buffer.writeInt(chr.getBaseSTR());
			buffer.writeInt(1);
			buffer.writeInt(99);
			buffer.writeInt(chr.getBaseDEX());
			buffer.writeInt(1);
			buffer.writeInt(99);
			buffer.writeInt(chr.getBaseCON());
			buffer.writeInt(1);
			buffer.writeInt(99);
			buffer.writeInt(chr.getBaseINT());
			buffer.writeInt(1);
			buffer.writeInt(99);
			buffer.writeInt(chr.getBaseWIT());
			buffer.writeInt(1);
			buffer.writeInt(99);
			buffer.writeInt(chr.getBaseMEN());
			buffer.writeInt(1);
		}
	}
}
