package com.px.gameserver.scripting.script.feature;

import com.px.gameserver.data.manager.SpawnManager;
import com.px.gameserver.model.actor.Npc;
import com.px.gameserver.model.actor.Player;
import com.px.gameserver.scripting.Quest;

public class MissQueen extends Quest
{
	// Rewards
	private static final int TRAINEES_COUPON = 7832;
	private static final int TRAVELERS_COUPON = 7833;
	
	public MissQueen()
	{
		super(-1, "feature");
		
		addTalkId(31760);
		
		SpawnManager.getInstance().spawnEventNpcs("start_weapon", true);
	}
	
	@Override
	public String onAdvEvent(String event, Npc npc, Player player)
	{
		String htmltext = event;
		
		if (event.equalsIgnoreCase("newbie_coupon"))
		{
			if (player.getClassId().getLevel() == 0 && player.getStatus().getLevel() >= 6 && player.getStatus().getLevel() <= 25 && player.getPkKills() <= 0)
			{
				if (player.getMemos().containsKey("MissQueen_Trainees"))
					htmltext = "31760-01.htm";
				else
				{
					htmltext = "31760-02.htm";
					player.getMemos().set("MissQueen_Trainees", true);
					giveItems(player, TRAINEES_COUPON, 1);
				}
			}
			else
				htmltext = "31760-03.htm";
		}
		else if (event.equalsIgnoreCase("traveller_coupon"))
		{
			if (player.getClassId().getLevel() == 1 && player.getStatus().getLevel() >= 6 && player.getStatus().getLevel() <= 25 && player.getPkKills() <= 0)
			{
				if (player.getMemos().containsKey("MissQueen_Traveler"))
					htmltext = "31760-04.htm";
				else
				{
					htmltext = "31760-05.htm";
					player.getMemos().set("MissQueen_Traveler", true);
					giveItems(player, TRAVELERS_COUPON, 1);
				}
			}
			else
				htmltext = "31760-06.htm";
		}
		
		return htmltext;
	}
}