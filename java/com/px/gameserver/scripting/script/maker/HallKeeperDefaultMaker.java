package com.px.gameserver.scripting.script.maker;

import com.px.gameserver.model.spawn.NpcMaker;

public class HallKeeperDefaultMaker extends DefaultMaker
{
	public HallKeeperDefaultMaker(String name)
	{
		super(name);
	}
	
	@Override
	public void onStart(NpcMaker maker)
	{
		// Disables onStart
	}
}
