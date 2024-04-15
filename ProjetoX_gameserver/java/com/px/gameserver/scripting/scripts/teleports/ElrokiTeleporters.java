package com.px.gameserver.scripting.scripts.teleports;

import com.px.gameserver.model.actor.Npc;
import com.px.gameserver.model.actor.Player;
import com.px.gameserver.scripting.Quest;

public class ElrokiTeleporters extends Quest
{
	public ElrokiTeleporters()
	{
		super(-1, "teleports");
		
		addStartNpc(32111, 32112);
		addTalkId(32111, 32112);
	}
	
	@Override
	public String onTalk(Npc npc, Player player)
	{
		if (npc.getNpcId() == 32111)
		{
			if (player.isInCombat())
				return "32111-no.htm";
			
			player.teleportTo(4990, -1879, -3178, 0);
		}
		else
			player.teleportTo(7557, -5513, -3221, 0);
		
		return null;
	}
}