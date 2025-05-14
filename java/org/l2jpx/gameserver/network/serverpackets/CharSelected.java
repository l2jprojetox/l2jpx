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
import org.l2jpx.gameserver.taskmanager.GameTimeTaskManager;

public class CharSelected extends ServerPacket
{
	private final Player _player;
	private final int _sessionId;
	
	/**
	 * @param player
	 * @param sessionId
	 */
	public CharSelected(Player player, int sessionId)
	{
		_player = player;
		_sessionId = sessionId;
	}
	
	@Override
	public void writeImpl(GameClient client, WritableBuffer buffer)
	{
		ServerPackets.CHARACTER_SELECTED.writeId(this, buffer);
		buffer.writeString(_player.getName());
		buffer.writeInt(_player.getObjectId());
		buffer.writeString(_player.getTitle());
		buffer.writeInt(_sessionId);
		buffer.writeInt(_player.getClanId());
		buffer.writeInt(0); // ??
		buffer.writeInt(_player.getAppearance().isFemale());
		buffer.writeInt(_player.getRace().ordinal());
		buffer.writeInt(_player.getClassId().getId());
		buffer.writeInt(1); // active ??
		buffer.writeInt(_player.getX());
		buffer.writeInt(_player.getY());
		buffer.writeInt(_player.getZ());
		buffer.writeDouble(_player.getCurrentHp());
		buffer.writeDouble(_player.getCurrentMp());
		buffer.writeLong(_player.getSp());
		buffer.writeLong(_player.getExp());
		buffer.writeInt(_player.getLevel());
		buffer.writeInt(_player.getReputation());
		buffer.writeInt(_player.getPkKills());
		buffer.writeInt(GameTimeTaskManager.getInstance().getGameTime() % (24 * 60)); // "reset" on 24th hour
		buffer.writeInt(0);
		buffer.writeInt(_player.getClassId().getId());
		buffer.writeBytes(new byte[16]);
		buffer.writeInt(0);
		buffer.writeInt(0);
		buffer.writeInt(0);
		buffer.writeInt(0);
		buffer.writeInt(0);
		buffer.writeInt(0);
		buffer.writeInt(0);
		buffer.writeInt(0);
		buffer.writeInt(0);
		buffer.writeBytes(new byte[28]);
		buffer.writeInt(0);
	}
}
