package com.l2jpx.gameserver.scripting.quest;

import com.l2jpx.gameserver.enums.QuestStatus;
import com.l2jpx.gameserver.enums.ScriptEventType;
import com.l2jpx.gameserver.enums.actors.ClassRace;
import com.l2jpx.gameserver.model.actor.Creature;
import com.l2jpx.gameserver.model.actor.Npc;
import com.l2jpx.gameserver.model.actor.Player;
import com.l2jpx.gameserver.network.NpcStringId;
import com.l2jpx.gameserver.scripting.Quest;
import com.l2jpx.gameserver.scripting.QuestState;
import com.l2jpx.gameserver.skills.L2Skill;

public class Q164_BloodFiend extends Quest
{
	private static final String QUEST_NAME = "Q164_BloodFiend";
	
	// Item
	private static final int KIRUNAK_SKULL = 1044;
	
	public Q164_BloodFiend()
	{
		super(164, "Blood Fiend");
		
		setItemsIds(KIRUNAK_SKULL);
		
		addStartNpc(30149);
		addTalkId(30149);
		
		addEventIds(27021, ScriptEventType.ON_ATTACK, ScriptEventType.ON_KILL);
	}
	
	@Override
	public String onAdvEvent(String event, Npc npc, Player player)
	{
		String htmltext = event;
		QuestState st = player.getQuestList().getQuestState(QUEST_NAME);
		if (st == null)
			return htmltext;
		
		if (event.equalsIgnoreCase("30149-04.htm"))
		{
			st.setState(QuestStatus.STARTED);
			st.setCond(1);
			playSound(player, SOUND_ACCEPT);
		}
		
		return htmltext;
	}
	
	@Override
	public String onAttack(Npc npc, Creature attacker, int damage, L2Skill skill)
	{
		if (npc.isScriptValue(1))
			return null;
		
		npc.broadcastNpcSay(NpcStringId.ID_16404);
		npc.setScriptValue(1);
		return null;
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
				if (player.getRace() == ClassRace.DARK_ELF)
					htmltext = "30149-00.htm";
				else if (player.getStatus().getLevel() < 21)
					htmltext = "30149-02.htm";
				else
					htmltext = "30149-03.htm";
				break;
			
			case STARTED:
				if (player.getInventory().hasItems(KIRUNAK_SKULL))
				{
					htmltext = "30149-06.htm";
					takeItems(player, KIRUNAK_SKULL, 1);
					rewardItems(player, 57, 42130);
					playSound(player, SOUND_FINISH);
					st.exitQuest(false);
				}
				else
					htmltext = "30149-05.htm";
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
		
		final QuestState st = checkPlayerCondition(player, npc, 1);
		if (st == null)
			return null;
		
		st.setCond(2);
		playSound(player, SOUND_MIDDLE);
		giveItems(player, KIRUNAK_SKULL, 1);
		npc.broadcastNpcSay(NpcStringId.ID_16405);
		
		return null;
	}
}