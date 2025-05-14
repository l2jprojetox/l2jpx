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
package quests.Q00618_IntoTheFlame;

import org.l2jpx.gameserver.enums.QuestSound;
import org.l2jpx.gameserver.model.actor.Npc;
import org.l2jpx.gameserver.model.actor.Player;
import org.l2jpx.gameserver.model.quest.Quest;
import org.l2jpx.gameserver.model.quest.QuestState;
import org.l2jpx.gameserver.model.quest.State;

public class Q00618_IntoTheFlame extends Quest
{
	// NPCs
	private static final int KLEIN = 31540;
	private static final int HILDA = 31271;
	// Items
	private static final int VACUALITE_ORE = 7265;
	private static final int VACUALITE = 7266;
	// Reward
	private static final int FLOATING_STONE = 7267;
	
	public Q00618_IntoTheFlame()
	{
		super(618);
		registerQuestItems(VACUALITE_ORE, VACUALITE);
		addStartNpc(KLEIN);
		addTalkId(KLEIN, HILDA);
		// Kookaburras, Bandersnatches, Grendels
		addKillId(21274, 21275, 21276, 21282, 21283, 21284, 21290, 21291, 21292);
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
		
		switch (event)
		{
			case "31540-03.htm":
			{
				st.startQuest();
				break;
			}
			case "31540-05.htm":
			{
				takeItems(player, VACUALITE, 1);
				giveItems(player, FLOATING_STONE, 1);
				st.exitQuest(true, true);
				break;
			}
			case "31271-02.htm":
			{
				st.setCond(2, true);
				break;
			}
			case "31271-05.htm":
			{
				st.setCond(4, true);
				takeItems(player, VACUALITE_ORE, -1);
				giveItems(player, VACUALITE, 1);
				break;
			}
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
				htmltext = (player.getLevel() < 60) ? "31540-01.htm" : "31540-02.htm";
				break;
			}
			case State.STARTED:
			{
				final int cond = st.getCond();
				switch (npc.getId())
				{
					case KLEIN:
					{
						htmltext = (cond == 4) ? "31540-04.htm" : "31540-03.htm";
						break;
					}
					case HILDA:
					{
						if (cond == 1)
						{
							htmltext = "31271-01.htm";
						}
						else if (cond == 2)
						{
							htmltext = "31271-03.htm";
						}
						else if (cond == 3)
						{
							htmltext = "31271-04.htm";
						}
						else if (cond == 4)
						{
							htmltext = "31271-06.htm";
						}
						break;
					}
				}
				break;
			}
		}
		
		return htmltext;
	}
	
	@Override
	public String onKill(Npc npc, Player player, boolean isPet)
	{
		final QuestState qs = getRandomPartyMemberState(player, 2, 3, npc);
		if (qs == null)
		{
			return null;
		}
		final Player partyMember = qs.getPlayer();
		
		final QuestState st = getQuestState(partyMember, false);
		if (st == null)
		{
			return null;
		}
		
		if (getRandomBoolean())
		{
			giveItems(partyMember, VACUALITE_ORE, 1);
			if (getQuestItemsCount(partyMember, VACUALITE_ORE) < 50)
			{
				playSound(partyMember, QuestSound.ITEMSOUND_QUEST_ITEMGET);
			}
			else
			{
				st.setCond(3, true);
			}
		}
		
		return null;
	}
}