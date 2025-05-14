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
package quests.Q00634_InSearchOfFragmentsOfDimension;

import org.l2jpx.gameserver.enums.QuestSound;
import org.l2jpx.gameserver.model.actor.Npc;
import org.l2jpx.gameserver.model.actor.Player;
import org.l2jpx.gameserver.model.quest.Quest;
import org.l2jpx.gameserver.model.quest.QuestState;
import org.l2jpx.gameserver.model.quest.State;

public class Q00634_InSearchOfFragmentsOfDimension extends Quest
{
	// Items
	private static final int DIMENSION_FRAGMENT = 7079;
	
	public Q00634_InSearchOfFragmentsOfDimension()
	{
		super(634);
		
		// Dimensional Gate Keepers.
		for (int i = 31494; i < 31508; i++)
		{
			addStartNpc(i);
			addTalkId(i);
		}
		
		// All mobs.
		for (int i = 21208; i < 21256; i++)
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
		
		if (event.equals("02.htm"))
		{
			st.startQuest();
		}
		else if (event.equals("05.htm"))
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
				htmltext = (player.getLevel() < 20) ? "01a.htm" : "01.htm";
				break;
			}
			case State.STARTED:
			{
				htmltext = "03.htm";
				break;
			}
		}
		
		return htmltext;
	}
	
	@Override
	public String onKill(Npc npc, Player player, boolean isPet)
	{
		final QuestState st = getRandomPartyMemberState(player, -1, 3, npc);
		if ((st == null) || !st.isStarted())
		{
			return null;
		}
		final Player partyMember = st.getPlayer();
		
		if (getRandom(100) < 8)
		{
			giveItems(partyMember, DIMENSION_FRAGMENT, (int) ((npc.getLevel() * 0.15) + 2.6));
			playSound(partyMember, QuestSound.ITEMSOUND_QUEST_ITEMGET);
		}
		
		return null;
	}
}
