package com.px.gameserver.scripting.script.maker;

import com.px.gameserver.model.spawn.NpcMaker;

public class NoOnStartMaker extends DefaultMaker
{
	public NoOnStartMaker(String name)
	{
		super(name);
	}
	
	@Override
	public void onStart(NpcMaker maker)
	{
	}
}