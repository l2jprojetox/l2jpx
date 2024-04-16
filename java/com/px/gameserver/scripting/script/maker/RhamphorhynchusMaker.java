package com.px.gameserver.scripting.script.maker;

import com.px.gameserver.data.manager.SpawnManager;
import com.px.gameserver.model.actor.Npc;
import com.px.gameserver.model.spawn.MultiSpawn;
import com.px.gameserver.model.spawn.NpcMaker;

public class RhamphorhynchusMaker extends VelociraptorMaker
{
	public RhamphorhynchusMaker(String name)
	{
		super(name);
	}
	
	@Override
	public void onStart(NpcMaker maker)
	{
		maker.getMakerMemo().set("i_ai0", 0);
	}
	
	@Override
	public void onNpcDeleted(Npc npc, MultiSpawn ms, NpcMaker maker)
	{
		final NpcMaker maker0 = SpawnManager.getInstance().getNpcMaker("rune20_mb2017_03m1");
		if (maker0 != null)
		{
			if (maker.getNpcsAlive() == 0 && maker.getMakerMemo().getInteger("i_ai0", 0) == 0)
				maker0.getMaker().onMakerScriptEvent("1001", maker0, 0, 0);
		}
	}
}