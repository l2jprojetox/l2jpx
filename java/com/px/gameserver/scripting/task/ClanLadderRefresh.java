package com.px.gameserver.scripting.task;

import com.px.gameserver.data.sql.ClanTable;
import com.px.gameserver.scripting.ScheduledQuest;

public final class ClanLadderRefresh extends ScheduledQuest
{
	public ClanLadderRefresh()
	{
		super(-1, "task");
	}
	
	@Override
	public final void onStart()
	{
		ClanTable.getInstance().refreshClansLadder(true);
	}
	
	@Override
	public final void onEnd()
	{
	}
}