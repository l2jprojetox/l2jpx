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

import java.util.Collection;

import org.l2jpx.commons.network.WritableBuffer;
import org.l2jpx.gameserver.model.actor.Player;
import org.l2jpx.gameserver.model.quest.Quest;
import org.l2jpx.gameserver.model.quest.QuestState;
import org.l2jpx.gameserver.network.GameClient;
import org.l2jpx.gameserver.network.ServerPackets;

/**
 * @author Tempy
 */
public class GmViewQuestInfo extends ServerPacket
{
	private final Player _player;
	private final Collection<Quest> _questList;
	
	public GmViewQuestInfo(Player player)
	{
		_player = player;
		_questList = player.getAllActiveQuests();
	}
	
	@Override
	public void writeImpl(GameClient client, WritableBuffer buffer)
	{
		ServerPackets.GM_VIEW_QUEST_INFO.writeId(this, buffer);
		buffer.writeString(_player.getName());
		buffer.writeShort(_questList.size()); // quest count
		for (Quest quest : _questList)
		{
			final QuestState qs = _player.getQuestState(quest.getName());
			buffer.writeInt(quest.getId());
			buffer.writeInt(qs == null ? 0 : qs.getCond());
		}
		buffer.writeShort(0); // some size
		// for size; ddQQ
	}
}
