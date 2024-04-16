package com.px.gameserver.scripting.script.teleport;

import com.px.gameserver.data.manager.SevenSignsManager;
import com.px.gameserver.enums.CabalType;
import com.px.gameserver.enums.SealType;
import com.px.gameserver.model.actor.Creature;
import com.px.gameserver.model.actor.Npc;
import com.px.gameserver.model.actor.Player;
import com.px.gameserver.scripting.Quest;

/**
 * Spawn Gatekeepers at Lilith/Anakim deaths (after a 10sec delay).<BR>
 * Despawn them after 15 minutes.
 */
public class GatekeeperSpirit extends Quest
{
	// NPCs
	private static final int ENTER_GK = 31111;
	private static final int EXIT_GK = 31112;
	
	// Raid Bosses
	private static final int LILITH = 25283;
	private static final int ANAKIM = 25286;
	
	public GatekeeperSpirit()
	{
		super(-1, "teleport");
		
		addFirstTalkId(ENTER_GK);
		addTalkId(ENTER_GK);
		
		addMyDying(LILITH, ANAKIM);
	}
	
	@Override
	public String onFirstTalk(Npc npc, Player player)
	{
		final CabalType playerCabal = SevenSignsManager.getInstance().getPlayerCabal(player.getObjectId());
		final CabalType sealAvariceOwner = SevenSignsManager.getInstance().getSealOwner(SealType.AVARICE);
		final CabalType winningCabal = SevenSignsManager.getInstance().getWinningCabal();
		
		if (playerCabal == sealAvariceOwner && playerCabal == winningCabal)
		{
			switch (sealAvariceOwner)
			{
				case DAWN:
					return "dawn.htm";
				
				case DUSK:
					return "dusk.htm";
			}
		}
		
		npc.showChatWindow(player);
		return null;
	}
	
	@Override
	public String onTimer(String name, Npc npc, Player player)
	{
		if (name.equalsIgnoreCase("lilith_exit"))
			addSpawn(EXIT_GK, 184446, -10112, -5488, 0, false, 900000, false);
		else if (name.equalsIgnoreCase("anakim_exit"))
			addSpawn(EXIT_GK, 184466, -13106, -5488, 0, false, 900000, false);
		
		return null;
	}
	
	@Override
	public void onMyDying(Npc npc, Creature killer)
	{
		switch (npc.getNpcId())
		{
			case LILITH:
				startQuestTimer("lilith_exit", null, null, 10000);
				break;
			
			case ANAKIM:
				startQuestTimer("anakim_exit", null, null, 10000);
				break;
		}
	}
}