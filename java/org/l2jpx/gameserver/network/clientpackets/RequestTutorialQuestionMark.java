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
import org.l2jpx.gameserver.model.events.EventDispatcher;
import org.l2jpx.gameserver.model.events.EventType;
import org.l2jpx.gameserver.model.events.impl.creature.player.OnPlayerPressTutorialMark;

public class RequestTutorialQuestionMark extends ClientPacket
{
	private int _number = 0;
	
	@Override
	protected void readImpl()
	{
		readByte(); // index ?
		_number = readInt();
	}
	
	@Override
	protected void runImpl()
	{
		final Player player = getPlayer();
		if (player == null)
		{
			return;
		}
		
		// Notify scripts
		if (EventDispatcher.getInstance().hasListener(EventType.ON_PLAYER_PRESS_TUTORIAL_MARK, player))
		{
			EventDispatcher.getInstance().notifyEventAsync(new OnPlayerPressTutorialMark(player, _number), player);
		}
	}
}
