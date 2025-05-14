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
package org.l2jpx.gameserver.network.clientpackets.shuttle;

import org.l2jpx.gameserver.model.World;
import org.l2jpx.gameserver.model.actor.Player;
import org.l2jpx.gameserver.model.actor.instance.Shuttle;
import org.l2jpx.gameserver.network.PacketLogger;
import org.l2jpx.gameserver.network.clientpackets.ClientPacket;

/**
 * @author UnAfraid
 */
public class RequestShuttleGetOn extends ClientPacket
{
	private int _x;
	private int _y;
	private int _z;
	
	@Override
	protected void readImpl()
	{
		readInt(); // charId
		_x = readInt();
		_y = readInt();
		_z = readInt();
	}
	
	@Override
	protected void runImpl()
	{
		final Player player = getPlayer();
		if (player == null)
		{
			return;
		}
		
		// TODO: better way?
		for (Shuttle shuttle : World.getInstance().getVisibleObjects(player, Shuttle.class))
		{
			if (shuttle.calculateDistance3D(player) < 1000)
			{
				shuttle.addPassenger(player);
				player.getInVehiclePosition().setXYZ(_x, _y, _z);
				break;
			}
			PacketLogger.info(getClass().getSimpleName() + ": range between char and shuttle: " + shuttle.calculateDistance3D(player));
		}
	}
}
