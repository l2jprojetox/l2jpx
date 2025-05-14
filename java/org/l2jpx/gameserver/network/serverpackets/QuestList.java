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

import java.util.LinkedList;
import java.util.List;

import org.l2jpx.commons.network.WritableBuffer;
import org.l2jpx.gameserver.model.actor.Player;
import org.l2jpx.gameserver.model.quest.QuestState;
import org.l2jpx.gameserver.network.GameClient;
import org.l2jpx.gameserver.network.ServerPackets;

public class QuestList extends ServerPacket
{
	private final List<QuestState> _activeQuests;
	private final byte[] _oneTimeQuestMask;
	
	public QuestList(Player player)
	{
		_activeQuests = new LinkedList<>();
		_oneTimeQuestMask = new byte[128];
		for (QuestState qs : player.getAllQuestStates())
		{
			final int questId = qs.getQuest().getId();
			if (questId > 0)
			{
				if (qs.isStarted())
				{
					_activeQuests.add(qs);
				}
				else if (qs.isCompleted() && !(((questId > 255) && (questId < 10256)) || (questId > 11023)))
				{
					_oneTimeQuestMask[(questId % 10000) / 8] |= 1 << (questId % 8);
				}
			}
		}
	}
	
	@Override
	public void writeImpl(GameClient client, WritableBuffer buffer)
	{
		ServerPackets.QUEST_LIST.writeId(this, buffer);
		buffer.writeShort(_activeQuests.size());
		for (QuestState qs : _activeQuests)
		{
			buffer.writeInt(qs.getQuest().getId());
			buffer.writeInt(qs.getCondBitSet());
		}
		buffer.writeBytes(_oneTimeQuestMask);
	}
}
