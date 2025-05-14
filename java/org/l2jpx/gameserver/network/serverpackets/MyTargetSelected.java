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
import org.l2jpx.gameserver.model.actor.Creature;
import org.l2jpx.gameserver.model.actor.Player;
import org.l2jpx.gameserver.model.actor.instance.ControllableAirShip;
import org.l2jpx.gameserver.network.GameClient;
import org.l2jpx.gameserver.network.ServerPackets;

/**
 * MyTargetSelected server packet implementation.
 * @author UnAfraid
 */
public class MyTargetSelected extends ServerPacket
{
	private final int _objectId;
	private final int _color;
	
	/**
	 * @param player
	 * @param target
	 */
	public MyTargetSelected(Player player, Creature target)
	{
		_objectId = (target instanceof ControllableAirShip) ? ((ControllableAirShip) target).getHelmObjectId() : target.getObjectId();
		_color = target.isAutoAttackable(player) ? (player.getLevel() - target.getLevel()) : 0;
	}
	
	@Override
	public void writeImpl(GameClient client, WritableBuffer buffer)
	{
		ServerPackets.MY_TARGET_SELECTED.writeId(this, buffer);
		buffer.writeInt(1); // Grand Crusade
		buffer.writeInt(_objectId);
		buffer.writeShort(_color);
		buffer.writeInt(0);
	}
}
