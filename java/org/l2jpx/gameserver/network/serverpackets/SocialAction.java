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
import org.l2jpx.gameserver.network.GameClient;
import org.l2jpx.gameserver.network.ServerPackets;

public class SocialAction extends ServerPacket
{
	// TODO: Enum
	public static final int LEVEL_UP = 2122;
	
	private final int _objectId;
	private final int _actionId;
	
	public SocialAction(int objectId, int actionId)
	{
		_objectId = objectId;
		_actionId = actionId;
	}
	
	@Override
	public void writeImpl(GameClient client, WritableBuffer buffer)
	{
		ServerPackets.SOCIAL_ACTION.writeId(this, buffer);
		buffer.writeInt(_objectId);
		buffer.writeInt(_actionId);
		buffer.writeInt(0); // TODO: Find me!
	}
	
	@Override
	public boolean canBeDropped(GameClient client)
	{
		return true;
	}
}
