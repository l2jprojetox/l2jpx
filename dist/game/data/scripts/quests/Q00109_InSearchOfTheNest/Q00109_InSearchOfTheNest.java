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
package quests.Q00109_InSearchOfTheNest;

import org.l2jpx.gameserver.model.actor.Npc;
import org.l2jpx.gameserver.model.actor.Player;
import org.l2jpx.gameserver.model.quest.Quest;
import org.l2jpx.gameserver.model.quest.QuestState;
import org.l2jpx.gameserver.model.quest.State;

public class Q00109_InSearchOfTheNest extends Quest
{
	// NPCs
	private static final int PIERCE = 31553;
	private static final int KAHMAN = 31554;
	private static final int SCOUT_CORPSE = 32015;
	// Items
	private static final int SCOUT_MEMO = 8083;
	private static final int RECRUIT_BADGE = 7246;
	private static final int SOLDIER_BADGE = 7247;
	
	public Q00109_InSearchOfTheNest()
	{
		super(109);
		registerQuestItems(SCOUT_MEMO);
		addStartNpc(PIERCE);
		addTalkId(PIERCE, SCOUT_CORPSE, KAHMAN);
	}
	
	@Override
	public String onEvent(String event, Npc npc, Player player)
	{
		final String htmltext = event;
		final QuestState st = getQuestState(player, false);
		if (st == null)
		{
			return htmltext;
		}
		
		switch (event)
		{
			case "31553-01.htm":
			{
				st.startQuest();
				break;
			}
			case "32015-02.htm":
			{
				st.setCond(2, true);
				giveItems(player, SCOUT_MEMO, 1);
				break;
			}
			case "31553-03.htm":
			{
				st.setCond(3, true);
				takeItems(player, SCOUT_MEMO, 1);
				break;
			}
			case "31554-02.htm":
			{
				giveAdena(player, 5168, true);
				st.exitQuest(false, true);
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
				// Must worn one or other Golden Ram Badge in order to be accepted.
				if ((player.getLevel() >= 66) && hasAtLeastOneQuestItem(player, RECRUIT_BADGE, SOLDIER_BADGE))
				{
					htmltext = "31553-00.htm";
				}
				else
				{
					htmltext = "31553-00a.htm";
				}
				break;
			}
			case State.STARTED:
			{
				final int cond = st.getCond();
				switch (npc.getId())
				{
					case PIERCE:
					{
						if (cond == 1)
						{
							htmltext = "31553-01a.htm";
						}
						else if (cond == 2)
						{
							htmltext = "31553-02.htm";
						}
						else if (cond == 3)
						{
							htmltext = "31553-03.htm";
						}
						break;
					}
					case SCOUT_CORPSE:
					{
						if (cond == 1)
						{
							htmltext = "32015-01.htm";
						}
						else if (cond == 2)
						{
							htmltext = "32015-02.htm";
						}
						break;
					}
					case KAHMAN:
					{
						if (cond == 3)
						{
							htmltext = "31554-01.htm";
						}
						break;
					}
				}
				break;
			}
			case State.COMPLETED:
			{
				htmltext = getAlreadyCompletedMsg(player);
				break;
			}
		}
		
		return htmltext;
	}
}