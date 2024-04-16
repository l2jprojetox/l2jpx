package com.px.gameserver.scripting.script.maker;

import com.px.commons.pool.ThreadPool;
import com.px.commons.random.Rnd;

import com.px.gameserver.model.actor.Npc;
import com.px.gameserver.model.spawn.MultiSpawn;
import com.px.gameserver.model.spawn.NpcMaker;

public class RandomSpawnMaker extends DefaultMaker
{
	public RandomSpawnMaker(String name)
	{
		super(name);
	}
	
	@Override
	public void onStart(NpcMaker maker)
	{
		if (!maker.isOnStart())
			return;
		
		final MultiSpawn rndMs = Rnd.get(maker.getSpawns());
		for (int i = 0; i < rndMs.getTotal(); i++)
			rndMs.doSpawn(false);
	}
	
	@Override
	public void onNpcDeleted(Npc npc, MultiSpawn ms, NpcMaker maker)
	{
		final MultiSpawn rndMs = Rnd.get(maker.getSpawns());
		
		final long i2 = rndMs.getTotal() - rndMs.getSpawned();
		if (i2 > 0)
		{
			if (maker.getMaximumNpc() - maker.getNpcsAlive() > 0)
			{
				ThreadPool.schedule(() ->
				{
					if (rndMs.getDecayed() > 0)
						rndMs.doRespawn();
					else
						rndMs.doSpawn(false);
				}, rndMs.calculateRespawnDelay() * 1000);
			}
		}
	}
}