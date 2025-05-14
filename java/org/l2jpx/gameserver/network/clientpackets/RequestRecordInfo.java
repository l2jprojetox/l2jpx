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

import org.l2jpx.gameserver.model.World;
import org.l2jpx.gameserver.model.WorldObject;
import org.l2jpx.gameserver.model.actor.Creature;
import org.l2jpx.gameserver.model.actor.Player;

public class RequestRecordInfo extends ClientPacket
{
	@Override
	protected void readImpl()
	{
	}
	
	@Override
	protected void runImpl()
	{
		final Player player = getPlayer();
		if (player == null)
		{
			return;
		}
		
		player.updateUserInfo();
		World.getInstance().forEachVisibleObject(player, WorldObject.class, object ->
		{
			if (object.isVisibleFor(player))
			{
				object.sendInfo(player);
				
				if (object.isCreature())
				{
					// Update the state of the Creature object client side by sending Server->Client packet
					// MoveToPawn/MoveToLocation and AutoAttackStart to the Player
					final Creature creature = (Creature) object;
					if (creature.hasAI())
					{
						creature.getAI().describeStateToPlayer(player);
					}
				}
			}
		});
	}
}
