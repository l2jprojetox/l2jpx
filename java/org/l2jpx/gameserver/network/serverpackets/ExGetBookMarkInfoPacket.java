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
import org.l2jpx.gameserver.model.TeleportBookmark;
import org.l2jpx.gameserver.model.actor.Player;
import org.l2jpx.gameserver.network.GameClient;
import org.l2jpx.gameserver.network.ServerPackets;

/**
 * @author ShanSoft
 */
public class ExGetBookMarkInfoPacket extends ServerPacket
{
	private final Player _player;
	
	public ExGetBookMarkInfoPacket(Player player)
	{
		_player = player;
	}
	
	@Override
	public void writeImpl(GameClient client, WritableBuffer buffer)
	{
		ServerPackets.EX_GET_BOOK_MARK_INFO.writeId(this, buffer);
		buffer.writeInt(0); // Dummy
		buffer.writeInt(_player.getBookMarkSlot());
		buffer.writeInt(_player.getTeleportBookmarks().size());
		for (TeleportBookmark tpbm : _player.getTeleportBookmarks())
		{
			buffer.writeInt(tpbm.getId());
			buffer.writeInt(tpbm.getX());
			buffer.writeInt(tpbm.getY());
			buffer.writeInt(tpbm.getZ());
			buffer.writeString(tpbm.getName());
			buffer.writeInt(tpbm.getIcon());
			buffer.writeString(tpbm.getTag());
		}
	}
}
