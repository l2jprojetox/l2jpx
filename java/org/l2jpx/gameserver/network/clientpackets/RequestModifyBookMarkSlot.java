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

/**
 * @author ShanSoft
 * @structure chddSdS
 */
public class RequestModifyBookMarkSlot extends ClientPacket
{
	private int _id;
	private int _icon;
	private String _name;
	private String _tag;
	
	@Override
	protected void readImpl()
	{
		_id = readInt();
		_name = readString();
		_icon = readInt();
		_tag = readString();
	}
	
	@Override
	protected void runImpl()
	{
		final Player player = getPlayer();
		if (player == null)
		{
			return;
		}
		
		player.teleportBookmarkModify(_id, _icon, _tag, _name);
	}
}
