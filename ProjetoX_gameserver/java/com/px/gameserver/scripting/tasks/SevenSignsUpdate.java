package com.px.gameserver.scripting.tasks;

import com.px.gameserver.data.manager.FestivalOfDarknessManager;
import com.px.gameserver.data.manager.SevenSignsManager;
import com.px.gameserver.scripting.ScheduledQuest;

public final class SevenSignsUpdate extends ScheduledQuest
{
	public SevenSignsUpdate()
	{
		super(-1, "tasks");
	}
	
	@Override
	public final void onStart()
	{
		if (!SevenSignsManager.getInstance().isSealValidationPeriod())
			FestivalOfDarknessManager.getInstance().saveFestivalData(false);
		
		SevenSignsManager.getInstance().saveSevenSignsData();
		SevenSignsManager.getInstance().saveSevenSignsStatus();
	}
	
	@Override
	public final void onEnd()
	{
	}
}