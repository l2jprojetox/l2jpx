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
package org.l2jpx.gameserver.network.serverpackets.appearance;

import org.l2jpx.commons.network.WritableBuffer;
import org.l2jpx.gameserver.model.actor.Player;
import org.l2jpx.gameserver.network.GameClient;
import org.l2jpx.gameserver.network.ServerPackets;
import org.l2jpx.gameserver.network.serverpackets.ServerPacket;

/**
 * @author Sdw
 */
public class ExCuriousHouseMemberUpdate extends ServerPacket
{
	public int _objId;
	public int _maxHp;
	public int _maxCp;
	public int _currentHp;
	public int _currentCp;
	
	public ExCuriousHouseMemberUpdate(Player player)
	{
		_objId = player.getObjectId();
		_maxHp = player.getMaxHp();
		_maxCp = player.getMaxCp();
		_currentHp = (int) player.getCurrentHp();
		_currentCp = (int) player.getCurrentCp();
	}
	
	@Override
	public void writeImpl(GameClient client, WritableBuffer buffer)
	{
		ServerPackets.EX_CURIOUS_HOUSE_MEMBER_UPDATE.writeId(this, buffer);
		buffer.writeInt(_objId);
		buffer.writeInt(_maxHp);
		buffer.writeInt(_maxCp);
		buffer.writeInt(_currentHp);
		buffer.writeInt(_currentCp);
	}
}
