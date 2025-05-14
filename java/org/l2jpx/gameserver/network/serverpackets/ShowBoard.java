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

import java.util.List;

import org.l2jpx.commons.network.WritableBuffer;
import org.l2jpx.gameserver.network.GameClient;
import org.l2jpx.gameserver.network.ServerPackets;

public class ShowBoard extends ServerPacket
{
	private final String _content;
	private int _showBoard = 1; // 1 show, 0 hide
	
	public ShowBoard(String htmlCode, String id)
	{
		_content = id + "\u0008" + htmlCode;
	}
	
	/**
	 * Hides the community board
	 */
	public ShowBoard()
	{
		_showBoard = 0;
		_content = "";
	}
	
	public ShowBoard(List<String> arg)
	{
		final StringBuilder builder = new StringBuilder(256).append("1002\u0008");
		for (String str : arg)
		{
			builder.append(str).append("\u0008");
		}
		_content = builder.toString();
	}
	
	@Override
	public void writeImpl(GameClient client, WritableBuffer buffer)
	{
		ServerPackets.SHOW_BOARD.writeId(this, buffer);
		buffer.writeByte(_showBoard); // c4 1 to show community 00 to hide
		buffer.writeString("bypass _bbshome"); // top
		buffer.writeString("bypass _bbsgetfav"); // favorite
		buffer.writeString("bypass _bbsloc"); // region
		buffer.writeString("bypass _bbsclan"); // clan
		buffer.writeString("bypass _bbsmemo"); // memo
		buffer.writeString("bypass _bbsmail"); // mail
		buffer.writeString("bypass _bbsfriends"); // friends
		buffer.writeString("bypass bbs_add_fav"); // add fav.
		buffer.writeString(_content);
	}
}
