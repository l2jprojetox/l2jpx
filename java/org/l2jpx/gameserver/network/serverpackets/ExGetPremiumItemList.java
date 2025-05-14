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
import org.l2jpx.gameserver.model.PremiumItem;
import org.l2jpx.gameserver.model.actor.Player;
import org.l2jpx.gameserver.network.GameClient;
import org.l2jpx.gameserver.network.ServerPackets;

/**
 * @author Gnacik
 */
public class ExGetPremiumItemList extends ServerPacket
{
	private final Player _player;
	private final Map<Integer, PremiumItem> _map;
	
	public ExGetPremiumItemList(Player player)
	{
		_player = player;
		_map = _player.getPremiumItemList();
	}
	
	@Override
	public void writeImpl(GameClient client, WritableBuffer buffer)
	{
		ServerPackets.EX_GET_PREMIUM_ITEM_LIST.writeId(this, buffer);
		buffer.writeInt(_map.size());
		for (Entry<Integer, PremiumItem> entry : _map.entrySet())
		{
			final PremiumItem item = entry.getValue();
			buffer.writeLong(entry.getKey());
			buffer.writeInt(item.getItemId());
			buffer.writeLong(item.getCount());
			buffer.writeInt(0); // ?
			buffer.writeString(item.getSender());
		}
	}
}
