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

import org.l2jpx.gameserver.model.actor.Player;
import org.l2jpx.gameserver.network.SystemMessageId;
import org.l2jpx.gameserver.network.serverpackets.TargetUnselected;

/**
 * @author Mobius
 */
public class RequestTargetCanceld extends ClientPacket
{
	private boolean _targetLost;
	
	@Override
	protected void readImpl()
	{
		_targetLost = readShort() != 0;
	}
	
	@Override
	protected void runImpl()
	{
		final Player player = getPlayer();
		if (player == null)
		{
			return;
		}
		
		if (player.isLockedTarget())
		{
			player.sendPacket(SystemMessageId.FAILED_TO_REMOVE_ENMITY);
			return;
		}
		
		if (player.getQueuedSkill() != null)
		{
			player.setQueuedSkill(null, null, false, false);
		}
		
		if (player.isCastingNow())
		{
			player.abortAllSkillCasters();
		}
		
		if (_targetLost)
		{
			player.setTarget(null);
		}
		
		if (player.isInAirShip())
		{
			player.broadcastPacket(new TargetUnselected(player));
		}
	}
}
