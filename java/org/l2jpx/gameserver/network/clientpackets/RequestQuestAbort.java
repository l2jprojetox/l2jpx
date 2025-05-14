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

import org.l2jpx.gameserver.enums.QuestType;
import org.l2jpx.gameserver.instancemanager.QuestManager;
import org.l2jpx.gameserver.model.actor.Player;
import org.l2jpx.gameserver.model.events.Containers;
import org.l2jpx.gameserver.model.events.EventDispatcher;
import org.l2jpx.gameserver.model.events.EventType;
import org.l2jpx.gameserver.model.events.impl.creature.player.OnPlayerQuestAbort;
import org.l2jpx.gameserver.model.quest.Quest;
import org.l2jpx.gameserver.model.quest.QuestState;
import org.l2jpx.gameserver.network.serverpackets.QuestList;

/**
 * @version $Revision: 1.3.4.2 $ $Date: 2005/03/27 15:29:30 $
 */
public class RequestQuestAbort extends ClientPacket
{
	private int _questId;
	
	@Override
	protected void readImpl()
	{
		_questId = readInt();
	}
	
	@Override
	protected void runImpl()
	{
		final Player player = getPlayer();
		if (player == null)
		{
			return;
		}
		
		final Quest qe = QuestManager.getInstance().getQuest(_questId);
		if (qe != null)
		{
			final QuestState qs = player.getQuestState(qe.getName());
			if (qs != null)
			{
				qs.setSimulated(false);
				qs.exitQuest(QuestType.REPEATABLE);
				player.sendPacket(new QuestList(player));
				
				if (EventDispatcher.getInstance().hasListener(EventType.ON_PLAYER_QUEST_ABORT, player, Containers.Players()))
				{
					EventDispatcher.getInstance().notifyEventAsync(new OnPlayerQuestAbort(player, _questId), player, Containers.Players());
				}
				
				qe.onQuestAborted(player);
			}
		}
	}
}