package com.l2jpx.gameserver.model.actor.instance;

import java.util.concurrent.ScheduledFuture;

import com.l2jpx.commons.pool.ThreadPool;

import com.l2jpx.gameserver.model.actor.template.NpcTemplate;

public class TownPet extends Folk
{
	private ScheduledFuture<?> _aiTask;
	
	public TownPet(int objectId, NpcTemplate template)
	{
		super(objectId, template);
		
		forceRunStance();
		
		_aiTask = ThreadPool.scheduleAtFixedRate(() -> moveFromSpawnPointUsingRandomOffset(50), 1000, 10000);
	}
	
	@Override
	public void deleteMe()
	{
		if (_aiTask != null)
		{
			_aiTask.cancel(true);
			_aiTask = null;
		}
		super.deleteMe();
	}
}