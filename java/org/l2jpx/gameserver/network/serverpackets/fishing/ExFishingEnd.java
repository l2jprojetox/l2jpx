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
package org.l2jpx.gameserver.network.serverpackets.fishing;

import org.l2jpx.commons.network.WritableBuffer;
import org.l2jpx.gameserver.enums.FishingEndReason;
import org.l2jpx.gameserver.model.actor.Player;
import org.l2jpx.gameserver.network.GameClient;
import org.l2jpx.gameserver.network.ServerPackets;
import org.l2jpx.gameserver.network.serverpackets.ServerPacket;

/**
 * @author -Wooden-
 */
public class ExFishingEnd extends ServerPacket
{
	private final Player _player;
	private final FishingEndReason _reason;
	
	public ExFishingEnd(Player player, FishingEndReason reason)
	{
		_player = player;
		_reason = reason;
	}
	
	@Override
	public void writeImpl(GameClient client, WritableBuffer buffer)
	{
		ServerPackets.EX_FISHING_END.writeId(this, buffer);
		buffer.writeInt(_player.getObjectId());
		buffer.writeByte(_reason.getReason());
	}
}
