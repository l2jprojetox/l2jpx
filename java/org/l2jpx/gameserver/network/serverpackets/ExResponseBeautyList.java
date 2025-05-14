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

import org.l2jpx.commons.network.WritableBuffer;
import org.l2jpx.gameserver.data.xml.BeautyShopData;
import org.l2jpx.gameserver.model.actor.Player;
import org.l2jpx.gameserver.model.beautyshop.BeautyItem;
import org.l2jpx.gameserver.network.GameClient;
import org.l2jpx.gameserver.network.ServerPackets;

/**
 * @author Sdw
 */
public class ExResponseBeautyList extends ServerPacket
{
	public static final int SHOW_FACESHAPE = 1;
	public static final int SHOW_HAIRSTYLE = 0;
	
	private final Player _player;
	private final int _type;
	private final Map<Integer, BeautyItem> _beautyItem;
	
	public ExResponseBeautyList(Player player, int type)
	{
		_player = player;
		_type = type;
		if (type == SHOW_HAIRSTYLE)
		{
			_beautyItem = BeautyShopData.getInstance().getBeautyData(player.getRace(), player.getAppearance().getSexType()).getHairList();
		}
		else
		{
			_beautyItem = BeautyShopData.getInstance().getBeautyData(player.getRace(), player.getAppearance().getSexType()).getFaceList();
		}
	}
	
	@Override
	public void writeImpl(GameClient client, WritableBuffer buffer)
	{
		ServerPackets.EX_RESPONSE_BEAUTY_LIST.writeId(this, buffer);
		buffer.writeLong(_player.getAdena());
		buffer.writeLong(_player.getBeautyTickets());
		buffer.writeInt(_type);
		buffer.writeInt(_beautyItem.size());
		for (BeautyItem item : _beautyItem.values())
		{
			buffer.writeInt(item.getId());
			buffer.writeInt(1); // Limit
		}
		buffer.writeInt(0);
	}
}
