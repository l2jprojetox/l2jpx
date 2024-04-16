package com.px.gameserver.scripting.script.maker;

import com.px.commons.pool.ThreadPool;

import com.px.gameserver.model.actor.Npc;
import com.px.gameserver.model.spawn.MultiSpawn;
import com.px.gameserver.model.spawn.NpcMaker;

public class FrintezzaEvilateMaker extends DefaultMaker
{
	public FrintezzaEvilateMaker(String name)
	{
		super(name);
	}
	
	@Override
	public void onMakerScriptEvent(String name, NpcMaker maker, int int1, int int2)
	{
		if (name.equalsIgnoreCase("1000"))
			maker.deleteAll();
		else if (name.equalsIgnoreCase("1001"))
		{
			if (checkHasSpawnCondition(maker))
				return;
			
			for (MultiSpawn ms : maker.getSpawns())
			{
				long toSpawnCount = ms.getTotal() - ms.getSpawned();
				
				for (Npc npc : ms.getNpcs())
				{
					if (npc.isDecayed())
					{
						npc.scheduleRespawn(int1 * 1000);
						toSpawnCount--;
					}
				}
				
				for (long i = 0; i < toSpawnCount; i++)
				{
					ThreadPool.schedule(() ->
					{
						if (ms.getDecayed() > 0)
							ms.doRespawn();
						else
							ms.doSpawn(false);
					}, int1 * 1000);
				}
			}
		}
	}
}