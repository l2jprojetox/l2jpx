package com.px.gameserver.scripting.script.maker;

import com.px.gameserver.model.actor.Npc;
import com.px.gameserver.model.spawn.MultiSpawn;
import com.px.gameserver.model.spawn.NpcMaker;

public class InstantSpawnMaker extends DefaultMaker
{
	public InstantSpawnMaker(String name)
	{
		super(name);
	}
	
	@Override
	public void onNpcDeleted(Npc npc, MultiSpawn ms, NpcMaker maker)
	{
		if (maker.getNpcsAlive() == 0)
			maker.getMaker().onMakerScriptEvent("1001", maker, 0, 0);
	}
}