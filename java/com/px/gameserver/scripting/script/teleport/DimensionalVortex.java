package com.px.gameserver.scripting.script.teleport;

import com.px.gameserver.model.actor.Npc;
import com.px.gameserver.model.actor.Player;
import com.px.gameserver.scripting.Quest;

public class DimensionalVortex extends Quest
{
	private static final int GREEN_STONE = 4401;
	private static final int BLUE_STONE = 4402;
	private static final int RED_STONE = 4403;
	
	public DimensionalVortex()
	{
		super(-1, "teleport");
		
		addTalkId(30952, 30953, 30954);
	}
	
	@Override
	public String onAdvEvent(String event, Npc npc, Player player)
	{
		String htmltext = "";
		
		if (event.equalsIgnoreCase("blue"))
		{
			if (player.getInventory().hasItems(BLUE_STONE))
			{
				takeItems(player, BLUE_STONE, 1);
				player.teleportTo(114097, 19935, 935, 0);
			}
			else
				htmltext = "no-items.htm";
		}
		else if (event.equalsIgnoreCase("green"))
		{
			if (player.getInventory().hasItems(GREEN_STONE))
			{
				takeItems(player, GREEN_STONE, 1);
				player.teleportTo(110930, 15963, -4378, 0);
			}
			else
				htmltext = "no-items.htm";
		}
		else if (event.equalsIgnoreCase("red"))
		{
			if (player.getInventory().hasItems(RED_STONE))
			{
				takeItems(player, RED_STONE, 1);
				player.teleportTo(118558, 16659, 5987, 0);
			}
			else
				htmltext = "no-items.htm";
		}
		return htmltext;
	}
}