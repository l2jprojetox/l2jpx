package com.px.gameserver.scripting.quest;

import com.px.gameserver.enums.EventHandler;
import com.px.gameserver.enums.QuestStatus;
import com.px.gameserver.model.actor.Creature;
import com.px.gameserver.model.actor.Npc;
import com.px.gameserver.model.actor.Player;
import com.px.gameserver.network.NpcStringId;
import com.px.gameserver.scripting.Quest;
import com.px.gameserver.scripting.QuestState;
import com.px.gameserver.skills.L2Skill;

public class Q158_SeedOfEvil extends Quest
{
	private static final String QUEST_NAME = "Q158_SeedOfEvil";
	
	// Item
	private static final int CLAY_TABLET = 1025;
	
	// Reward
	private static final int ENCHANT_ARMOR_D = 956;
	
	public Q158_SeedOfEvil()
	{
		super(158, "Seed of Evil");
		
		setItemsIds(CLAY_TABLET);
		
		addQuestStart(30031); // Biotin
		addTalkId(30031);
		
		addEventIds(27016, EventHandler.ATTACKED, EventHandler.MY_DYING);
	}
	
	@Override
	public String onAdvEvent(String event, Npc npc, Player player)
	{
		String htmltext = event;
		QuestState st = player.getQuestList().getQuestState(QUEST_NAME);
		if (st == null)
			return htmltext;
		
		if (event.equalsIgnoreCase("30031-04.htm"))
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
		QuestState st = player.getQuestList().getQuestState(QUEST_NAME);
		String htmltext = getNoQuestMsg();
		if (st == null)
			return htmltext;
		
		switch (st.getState())
		{
			case CREATED:
				htmltext = (player.getStatus().getLevel() < 21) ? "30031-02.htm" : "30031-03.htm";
				break;
			
			case STARTED:
				if (!player.getInventory().hasItems(CLAY_TABLET))
					htmltext = "30031-05.htm";
				else
				{
					htmltext = "30031-06.htm";
					takeItems(player, CLAY_TABLET, 1);
					giveItems(player, ENCHANT_ARMOR_D, 1);
					playSound(player, SOUND_FINISH);
					st.exitQuest(false);
				}
				break;
			
			case COMPLETED:
				htmltext = getAlreadyCompletedMsg();
				break;
		}
		
		return htmltext;
	}
	
	@Override
	public void onAttacked(Npc npc, Creature attacker, int damage, L2Skill skill)
	{
		if (npc.isScriptValue(1))
			return;
		
		npc.broadcastNpcSay(NpcStringId.ID_15804);
		npc.setScriptValue(1);
	}
	
	@Override
	public void onMyDying(Npc npc, Creature killer)
	{
		final Player player = killer.getActingPlayer();
		
		final QuestState st = checkPlayerCondition(player, npc, 1);
		if (st == null)
			return;
		
		st.setCond(2);
		playSound(player, SOUND_MIDDLE);
		giveItems(player, CLAY_TABLET, 1);
		npc.broadcastNpcSay(NpcStringId.ID_15805);
	}
}