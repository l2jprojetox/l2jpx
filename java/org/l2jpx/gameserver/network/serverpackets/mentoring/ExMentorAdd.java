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
package org.l2jpx.gameserver.network.serverpackets.mentoring;

import org.l2jpx.commons.network.WritableBuffer;
import org.l2jpx.gameserver.model.actor.Player;
import org.l2jpx.gameserver.network.GameClient;
import org.l2jpx.gameserver.network.ServerPackets;
import org.l2jpx.gameserver.network.serverpackets.ServerPacket;

/**
 * @author Gnacik, UnAfraid
 */
public class ExMentorAdd extends ServerPacket
{
	final Player _mentor;
	
	public ExMentorAdd(Player mentor)
	{
		_mentor = mentor;
	}
	
	@Override
	public void writeImpl(GameClient client, WritableBuffer buffer)
	{
		ServerPackets.EX_MENTOR_ADD.writeId(this, buffer);
		buffer.writeString(_mentor.getName());
		buffer.writeInt(_mentor.getActiveClass());
		buffer.writeInt(_mentor.getLevel());
	}
}
