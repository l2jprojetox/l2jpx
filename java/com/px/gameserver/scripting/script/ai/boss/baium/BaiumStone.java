package com.px.gameserver.scripting.script.ai.boss.baium;

import com.px.gameserver.model.World;
import com.px.gameserver.model.actor.Npc;
import com.px.gameserver.model.actor.Player;
import com.px.gameserver.model.memo.GlobalMemo;
import com.px.gameserver.scripting.Quest;

public class BaiumStone extends Quest
{
	private final int GM_ID = 2;
	private final int BAIUM_STONE = 29025;
	
	public BaiumStone()
	{
		super(-1, "ai/boss/baium");
		
		addCreated(BAIUM_STONE);
		addTalkId(BAIUM_STONE);
	}
	
	@Override
	public void onCreated(Npc npc)
	{
		npc._i_ai0 = 0;
	}
	
	@Override
	public String onTalk(Npc npc, Player player)
	{
		if (npc._i_ai0 == 0)
		{
			npc._i_ai0 = 1;
			
			final int i0 = GlobalMemo.getInstance().getInteger(String.valueOf(GM_ID));
			if (i0 != -1)
			{
				final Npc c0 = (Npc) World.getInstance().getObject(i0);
				if (c0 != null)
					c0.sendScriptEvent(10025, player.getObjectId(), 0);
			}
			npc.deleteMe();
		}
		return null;
	}
}