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
package quests.Q00638_SeekersOfTheHolyGrail;

import org.l2jpx.gameserver.enums.QuestSound;
import org.l2jpx.gameserver.model.actor.Npc;
import org.l2jpx.gameserver.model.actor.Player;
import org.l2jpx.gameserver.model.quest.Quest;
import org.l2jpx.gameserver.model.quest.QuestState;
import org.l2jpx.gameserver.model.quest.State;

public class Q00638_SeekersOfTheHolyGrail extends Quest
{
	// NPC
	private static final int INNOCENTIN = 31328;
	// Item
	private static final int PAGAN_TOTEM = 8068;
	
	public Q00638_SeekersOfTheHolyGrail()
	{
		super(638);
		registerQuestItems(PAGAN_TOTEM);
		addStartNpc(INNOCENTIN);
		addTalkId(INNOCENTIN);
		for (int i = 22138; i < 22175; i++)
		{
			addKillId(i);
		}
	}
	
	@Override
	public String onEvent(String event, Npc npc, Player player)
	{
		String htmltext = event;
		final QuestState st = getQuestState(player, false);
		if (st == null)
		{
			return htmltext;
		}
		
		if (event.equals("31328-02.htm"))
		{
			st.startQuest();
		}
		else if (event.equals("31328-06.htm"))
		{
			st.exitQuest(true, true);
		}
		
		return htmltext;
	}
	
	@Override
	public String onTalk(Npc npc, Player player)
	{
		String htmltext = getNoQuestMsg(player);
		final QuestState st = getQuestState(player, true);
		
		switch (st.getState())
		{
			case State.CREATED:
			{
				htmltext = (player.getLevel() < 73) ? "31328-00.htm" : "31328-01.htm";
				break;
			}
			case State.STARTED:
			{
				if (getQuestItemsCount(player, PAGAN_TOTEM) >= 2000)
				{
					htmltext = "31328-03.htm";
					playSound(player, QuestSound.ITEMSOUND_QUEST_MIDDLE);
					takeItems(player, PAGAN_TOTEM, 2000);
					
					final int chance = getRandom(3);
					if (chance == 0)
					{
						rewardItems(player, 959, 1);
					}
					else if (chance == 1)
					{
						rewardItems(player, 960, 1);
					}
					else
					{
						giveAdena(player, 3576000, true);
					}
				}
				else
				{
					htmltext = "31328-04.htm";
				}
				break;
			}
		}
		
		return htmltext;
	}
	
	@Override
	public String onKill(Npc npc, Player killer, boolean isSummon)
	{
		final QuestState qs = getRandomPartyMemberState(killer, -1, 3, npc);
		if ((qs != null))
		{
			giveItemRandomly(qs.getPlayer(), npc, PAGAN_TOTEM, 1, 0, 1, true);
		}
		
		return super.onKill(npc, killer, isSummon);
	}
}
