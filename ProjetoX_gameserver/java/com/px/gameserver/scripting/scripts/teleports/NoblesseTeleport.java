package com.px.gameserver.scripting.scripts.teleports;

import com.px.gameserver.model.actor.Npc;
import com.px.gameserver.model.actor.Player;
import com.px.gameserver.scripting.Quest;

public class NoblesseTeleport extends Quest
{
	public NoblesseTeleport()
	{
		super(-1, "teleports");
		
		addStartNpc(30006, 30059, 30080, 30134, 30146, 30177, 30233, 30256, 30320, 30540, 30576, 30836, 30848, 30878, 30899, 31275, 31320, 31964);
		addTalkId(30006, 30059, 30080, 30134, 30146, 30177, 30233, 30256, 30320, 30540, 30576, 30836, 30848, 30878, 30899, 31275, 31320, 31964);
	}
	
	@Override
	public String onTalk(Npc npc, Player player)
	{
		if (player.isNoble())
			return "noble.htm";
		
		return "nobleteleporter-no.htm";
	}
}