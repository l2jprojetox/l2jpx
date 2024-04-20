package com.l2jpx.gameserver.scripting.quest;

import com.l2jpx.gameserver.enums.QuestStatus;
import com.l2jpx.gameserver.enums.actors.ClassRace;
import com.l2jpx.gameserver.model.actor.Creature;
import com.l2jpx.gameserver.model.actor.Npc;
import com.l2jpx.gameserver.model.actor.Player;
import com.l2jpx.gameserver.scripting.Quest;
import com.l2jpx.gameserver.scripting.QuestState;

public class Q169_OffspringOfNightmares extends Quest
{
	private static final String QUEST_NAME = "Q169_OffspringOfNightmares";
	
	// Items
	private static final int CRACKED_SKULL = 1030;
	private static final int PERFECT_SKULL = 1031;
	private static final int BONE_GAITERS = 31;
	
	public Q169_OffspringOfNightmares()
	{
		super(169, "Offspring of Nightmares");
		
		setItemsIds(CRACKED_SKULL, PERFECT_SKULL);
		
		addStartNpc(30145); // Vlasty
		addTalkId(30145);
		
		addKillId(20105, 20025);
	}
	
	@Override
	public String onAdvEvent(String event, Npc npc, Player player)
	{
		String htmltext = event;
		QuestState st = player.getQuestList().getQuestState(QUEST_NAME);
		if (st == null)
			return htmltext;
		
		if (event.equalsIgnoreCase("30145-04.htm"))
		{
			st.setState(QuestStatus.STARTED);
			st.setCond(1);
			playSound(player, SOUND_ACCEPT);
		}
		else if (event.equalsIgnoreCase("30145-08.htm"))
		{
			int reward = 17000 + (player.getInventory().getItemCount(CRACKED_SKULL) * 20);
			takeItems(player, PERFECT_SKULL, -1);
			takeItems(player, CRACKED_SKULL, -1);
			giveItems(player, BONE_GAITERS, 1);
			rewardItems(player, 57, reward);
			playSound(player, SOUND_FINISH);
			st.exitQuest(false);
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
				if (player.getRace() != ClassRace.DARK_ELF)
					htmltext = "30145-00.htm";
				else if (player.getStatus().getLevel() < 15)
					htmltext = "30145-02.htm";
				else
					htmltext = "30145-03.htm";
				break;
			
			case STARTED:
				int cond = st.getCond();
				if (cond == 1)
				{
					if (player.getInventory().hasItems(CRACKED_SKULL))
						htmltext = "30145-06.htm";
					else
						htmltext = "30145-05.htm";
				}
				else if (cond == 2)
					htmltext = "30145-07.htm";
				break;
			
			case COMPLETED:
				htmltext = getAlreadyCompletedMsg();
				break;
		}
		
		return htmltext;
	}
	
	@Override
	public String onKill(Npc npc, Creature killer)
	{
		final Player player = killer.getActingPlayer();
		
		final QuestState st = checkPlayerState(player, npc, QuestStatus.STARTED);
		if (st == null)
			return null;
		
		if (st.getCond() == 1 && dropItems(player, PERFECT_SKULL, 1, 1, 200000))
			st.setCond(2);
		else
			dropItems(player, CRACKED_SKULL, 1, 0, 500000);
		
		return null;
	}
}