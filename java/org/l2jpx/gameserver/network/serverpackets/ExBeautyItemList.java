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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.l2jpx.commons.network.WritableBuffer;
import org.l2jpx.gameserver.data.xml.BeautyShopData;
import org.l2jpx.gameserver.model.actor.Player;
import org.l2jpx.gameserver.model.beautyshop.BeautyData;
import org.l2jpx.gameserver.model.beautyshop.BeautyItem;
import org.l2jpx.gameserver.network.GameClient;
import org.l2jpx.gameserver.network.ServerPackets;

/**
 * @author Sdw
 */
public class ExBeautyItemList extends ServerPacket
{
	private static final int HAIR_TYPE = 0;
	private static final int FACE_TYPE = 1;
	private static final int COLOR_TYPE = 2;
	
	private int _colorCount;
	private final BeautyData _beautyData;
	private final Map<Integer, List<BeautyItem>> _colorData = new HashMap<>();
	
	public ExBeautyItemList(Player player)
	{
		_beautyData = BeautyShopData.getInstance().getBeautyData(player.getRace(), player.getAppearance().getSexType());
		for (BeautyItem hair : _beautyData.getHairList().values())
		{
			final List<BeautyItem> colors = new ArrayList<>();
			for (BeautyItem color : hair.getColors().values())
			{
				colors.add(color);
				_colorCount++;
			}
			_colorData.put(hair.getId(), colors);
		}
	}
	
	@Override
	public void writeImpl(GameClient client, WritableBuffer buffer)
	{
		ServerPackets.EX_BEAUTY_ITEM_LIST.writeId(this, buffer);
		buffer.writeInt(HAIR_TYPE);
		buffer.writeInt(_beautyData.getHairList().size());
		for (BeautyItem hair : _beautyData.getHairList().values())
		{
			buffer.writeInt(0); // ?
			buffer.writeInt(hair.getId());
			buffer.writeInt(hair.getAdena());
			buffer.writeInt(hair.getResetAdena());
			buffer.writeInt(hair.getBeautyShopTicket());
			buffer.writeInt(1); // Limit
		}
		buffer.writeInt(FACE_TYPE);
		buffer.writeInt(_beautyData.getFaceList().size());
		for (BeautyItem face : _beautyData.getFaceList().values())
		{
			buffer.writeInt(0); // ?
			buffer.writeInt(face.getId());
			buffer.writeInt(face.getAdena());
			buffer.writeInt(face.getResetAdena());
			buffer.writeInt(face.getBeautyShopTicket());
			buffer.writeInt(1); // Limit
		}
		buffer.writeInt(COLOR_TYPE);
		buffer.writeInt(_colorCount);
		for (Entry<Integer, List<BeautyItem>> entry : _colorData.entrySet())
		{
			for (BeautyItem color : entry.getValue())
			{
				buffer.writeInt(entry.getKey());
				buffer.writeInt(color.getId());
				buffer.writeInt(color.getAdena());
				buffer.writeInt(color.getResetAdena());
				buffer.writeInt(color.getBeautyShopTicket());
				buffer.writeInt(1);
			}
		}
	}
}
