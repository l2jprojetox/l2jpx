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
public class ExResponseBeautyRegistReset extends ServerPacket
{
	public static final int FAILURE = 0;
	public static final int SUCCESS = 1;
	public static final int CHANGE = 0;
	public static final int RESTORE = 1;
	
	private final Player _player;
	private final int _type;
	private final int _result;
	
	public ExResponseBeautyRegistReset(Player player, int type, int result)
	{
		_player = player;
		_type = type;
		_result = result;
	}
	
	@Override
	public void writeImpl(GameClient client, WritableBuffer buffer)
	{
		ServerPackets.EX_RESPONSE_BEAUTY_REGIST_RESET.writeId(this, buffer);
		buffer.writeLong(_player.getAdena());
		buffer.writeLong(_player.getBeautyTickets());
		buffer.writeInt(_type);
		buffer.writeInt(_result);
		buffer.writeInt(_player.getVisualHair());
		buffer.writeInt(_player.getVisualFace());
		buffer.writeInt(_player.getVisualHairColor());
	}
}
