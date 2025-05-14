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
import org.l2jpx.gameserver.model.actor.Player;
import org.l2jpx.gameserver.model.item.Henna;
import org.l2jpx.gameserver.network.GameClient;
import org.l2jpx.gameserver.network.ServerPackets;

/**
 * @author Zoey76
 */
public class HennaRemoveList extends ServerPacket
{
	private final Player _player;
	
	public HennaRemoveList(Player player)
	{
		_player = player;
	}
	
	@Override
	public void writeImpl(GameClient client, WritableBuffer buffer)
	{
		ServerPackets.HENNA_UNEQUIP_LIST.writeId(this, buffer);
		buffer.writeLong(_player.getAdena());
		buffer.writeInt(3); // seems to be max size
		buffer.writeInt(3 - _player.getHennaEmptySlots());
		for (Henna henna : _player.getHennaList())
		{
			if (henna != null)
			{
				buffer.writeInt(henna.getDyeId());
				buffer.writeInt(henna.getDyeItemId());
				buffer.writeLong(henna.getCancelCount());
				buffer.writeLong(henna.getCancelFee());
				buffer.writeInt(henna.isAllowedClass(_player.getClassId()));
			}
		}
	}
}
