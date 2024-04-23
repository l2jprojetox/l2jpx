package com.l2jpx.gameserver.scripting.task;

import com.l2jpx.gameserver.Shutdown;
import com.l2jpx.gameserver.scripting.ScheduledQuest;

public final class ServerShutdown extends ScheduledQuest
{
	private static final int PERIOD = 600; // 10 minutes
	
	public ServerShutdown()
	{
		super(-1, "task");
	}
	
	@Override
	public final void onStart()
	{
		new Shutdown(PERIOD, false).start();
	}
	
	@Override
	public final void onEnd()
	{
	}
}