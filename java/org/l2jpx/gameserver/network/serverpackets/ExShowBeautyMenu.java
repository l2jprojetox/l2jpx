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
import org.l2jpx.gameserver.network.GameClient;
import org.l2jpx.gameserver.network.ServerPackets;

/**
 * @author Sdw
 */
public class ExShowBeautyMenu extends ServerPacket
{
	// TODO: Enum
	public static final int MODIFY_APPEARANCE = 0;
	public static final int RESTORE_APPEARANCE = 1;
	
	private final Player _player;
	private final int _type;
	
	public ExShowBeautyMenu(Player player, int type)
	{
		_player = player;
		_type = type;
	}
	
	@Override
	public void writeImpl(GameClient client, WritableBuffer buffer)
	{
		ServerPackets.EX_SHOW_BEAUTY_MENU.writeId(this, buffer);
		buffer.writeInt(_type);
		buffer.writeInt(_player.getVisualHair());
		buffer.writeInt(_player.getVisualHairColor());
		buffer.writeInt(_player.getVisualFace());
	}
}