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
package quests.Q00636_TruthBeyond;

import org.l2jpx.gameserver.model.actor.Npc;
import org.l2jpx.gameserver.model.actor.Player;
import org.l2jpx.gameserver.model.quest.Quest;
import org.l2jpx.gameserver.model.quest.QuestState;
import org.l2jpx.gameserver.model.quest.State;

/**
 * @author Mobius
 * @note Based on python script
 */
public class Q00636_TruthBeyond extends Quest
{
	// NPCs
	private static final int ELIYAH = 31329;
	private static final int FLAURON = 32010;
	// Item
	private static final int MARK = 8064;
	
	public Q00636_TruthBeyond()
	{
		super(636);
		addStartNpc(ELIYAH);
		addTalkId(ELIYAH, FLAURON);
	}
	
	@Override
	public String onEvent(String event, Npc npc, Player player)
	{
		final String htmltext = event;
		final QuestState qs = player.getQuestState(getName());
		if (qs == null)
		{
			return htmltext;
		}
		
		if (event.equals("31329-04.htm"))
		{
			qs.startQuest();
		}
		else if (event.equals("32010-02.htm"))
		{
			giveItems(player, MARK, 1);
			qs.unset("cond");
			qs.exitQuest(true, true);
		}
		
		return htmltext;
	}
	
	@Override
	public String onTalk(Npc npc, Player player)
	{
		String htmltext = getNoQuestMsg(player);
		final QuestState qs = getQuestState(player, true);
		
		final int npcId = npc.getId();
		final int id = qs.getState();
		final int cond = qs.getCond();
		if ((cond == 0) && (id == State.CREATED))
		{
			if (npcId == ELIYAH)
			{
				if (player.getLevel() > 72)
				{
					htmltext = "31329-02.htm";
				}
				else
				{
					htmltext = "31329-01.htm";
					qs.exitQuest(true, true);
				}
			}
		}
		else if (id == State.STARTED)
		{
			if (npcId == ELIYAH)
			{
				htmltext = "31329-05.htm";
			}
			else if (npcId == FLAURON)
			{
				if (cond == 1)
				{
					htmltext = "32010-01.htm";
					qs.setCond(2);
				}
				else
				{
					htmltext = "32010-03.htm";
				}
			}
		}
		
		return htmltext;
	}
}
