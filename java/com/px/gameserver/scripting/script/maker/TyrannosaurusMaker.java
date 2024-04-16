package com.px.gameserver.scripting.script.maker;

import com.px.commons.pool.ThreadPool;

import com.px.gameserver.data.manager.SpawnManager;
import com.px.gameserver.model.actor.Npc;
import com.px.gameserver.model.spawn.MultiSpawn;
import com.px.gameserver.model.spawn.NpcMaker;

public class TyrannosaurusMaker extends VelociraptorMaker
{
	public TyrannosaurusMaker(String name)
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
		if (maker.getNpcsAlive() == 0)
			ThreadPool.schedule(() -> onTimer("1002", maker), 180000);
	}
	
	@Override
	public void onTimer(String name, NpcMaker maker)
	{
		if (name.equalsIgnoreCase("1002"))
		{
			final NpcMaker maker0 = SpawnManager.getInstance().getNpcMaker("rune20_mb2017_04m1");
			if (maker0 != null)
				maker0.getMaker().onMakerScriptEvent("11042", maker0, 0, 0);
		}
		super.onTimer(name, maker);
	}
}