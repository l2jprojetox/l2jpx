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
package org.l2jpx.gameserver.network.clientpackets;

import org.l2jpx.gameserver.model.actor.Player;
import org.l2jpx.gameserver.model.item.instance.Item;

/**
 * @author KenM, Gnacik
 */
public class RequestChangeNicknameColor extends ClientPacket
{
	private static final int[] COLORS =
	{
		0x9393FF, // Pink
		0x7C49FC, // Rose Pink
		0x97F8FC, // Lemon Yellow
		0xFA9AEE, // Lilac
		0xFF5D93, // Cobalt Violet
		0x00FCA0, // Mint Green
		0xA0A601, // Peacock Green
		0x7898AF, // Yellow Ochre
		0x486295, // Chocolate
		0x999999, // Silver
	};
	
	private int _colorNum;
	private int _itemId;
	private String _title;
	
	@Override
	protected void readImpl()
	{
		_colorNum = readInt();
		_title = readString();
		_itemId = readInt();
	}
	
	@Override
	protected void runImpl()
	{
		final Player player = getPlayer();
		if (player == null)
		{
			return;
		}
		
		if ((_colorNum < 0) || (_colorNum >= COLORS.length))
		{
			return;
		}
		
		final Item item = player.getInventory().getItemByItemId(_itemId);
		if ((item == null) || (item.getEtcItem() == null) || (item.getEtcItem().getHandlerName() == null) || !item.getEtcItem().getHandlerName().equalsIgnoreCase("NicknameColor"))
		{
			return;
		}
		
		if (player.destroyItem("Consume", item, 1, null, true))
		{
			player.setTitle(_title);
			player.getAppearance().setTitleColor(COLORS[_colorNum]);
			player.broadcastUserInfo();
		}
	}
}
