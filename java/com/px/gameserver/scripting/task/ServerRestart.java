package com.px.gameserver.scripting.task;

import com.px.gameserver.Shutdown;
import com.px.gameserver.scripting.ScheduledQuest;

public final class ServerRestart extends ScheduledQuest
{
	private static final int PERIOD = 600; // 10 minutes
	
	public ServerRestart()
	{
		super(-1, "task");
	}
	
	@Override
	public final void onStart()
	{
		new Shutdown(PERIOD, true).start();
	}
	
	@Override
	public final void onEnd()
	{
	}
}