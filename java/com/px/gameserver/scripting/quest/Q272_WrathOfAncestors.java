package com.px.gameserver.scripting.quest;

import com.px.gameserver.enums.QuestStatus;
import com.px.gameserver.enums.actors.ClassRace;
import com.px.gameserver.model.actor.Creature;
import com.px.gameserver.model.actor.Npc;
import com.px.gameserver.model.actor.Player;
import com.px.gameserver.scripting.Quest;
import com.px.gameserver.scripting.QuestState;

public class Q272_WrathOfAncestors extends Quest
{
	private static final String QUEST_NAME = "Q272_WrathOfAncestors";
	
	// Item
	private static final int GRAVE_ROBBERS_HEAD = 1474;
	
	public Q272_WrathOfAncestors()
	{
		super(272, "Wrath of Ancestors");
		
		setItemsIds(GRAVE_ROBBERS_HEAD);
		
		addQuestStart(30572); // Livina
		addTalkId(30572);
		
		addMyDying(20319, 20320); // Goblin Grave Robber, Goblin Tomb Raider Leader
	}
	
	@Override
	public String onAdvEvent(String event, Npc npc, Player player)
	{
		String htmltext = event;
		QuestState st = player.getQuestList().getQuestState(QUEST_NAME);
		if (st == null)
			return htmltext;
		
		if (event.equalsIgnoreCase("30572-03.htm"))
		{
			st.setState(QuestStatus.STARTED);
			st.setCond(1);
			playSound(player, SOUND_ACCEPT);
		}
		
		return htmltext;
	}
	
	@Override
	public String onTalk(Npc npc, Player player)
	{
		String htmltext = getNoQuestMsg();
		QuestState st = player.getQuestList().getQuestState(QUEST_NAME);
		if (st == null)
			return htmltext;
		
		switch (st.getState())
		{
			case CREATED:
				if (player.getRace() != ClassRace.ORC)
					htmltext = "30572-00.htm";
				else if (player.getStatus().getLevel() < 5)
					htmltext = "30572-01.htm";
				else
					htmltext = "30572-02.htm";
				break;
			
			case STARTED:
				if (st.getCond() == 1)
					htmltext = "30572-04.htm";
				else
				{
					htmltext = "30572-05.htm";
					takeItems(player, GRAVE_ROBBERS_HEAD, -1);
					rewardItems(player, 57, 1500);
					playSound(player, SOUND_FINISH);
					st.exitQuest(true);
				}
				break;
		}
		
		return htmltext;
	}
	
	@Override
	public void onMyDying(Npc npc, Creature killer)
	{
		final Player player = killer.getActingPlayer();
		
		final QuestState st = checkPlayerCondition(player, npc, 1);
		if (st == null)
			return;
		
		if (dropItemsAlways(player, GRAVE_ROBBERS_HEAD, 1, 50))
			st.setCond(2);
	}
}