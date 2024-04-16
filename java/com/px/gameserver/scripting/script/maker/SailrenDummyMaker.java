package com.px.gameserver.scripting.script.maker;

import com.px.gameserver.model.spawn.MultiSpawn;
import com.px.gameserver.model.spawn.NpcMaker;

public class SailrenDummyMaker extends DefaultMaker
{
	public SailrenDummyMaker(String name)
	{
		super(name);
	}
	
	@Override
	public void onMakerScriptEvent(String name, NpcMaker maker, int int1, int int2)
	{
		switch (name)
		{
			case "11046":
			case "11047":
			case "11048":
				final MultiSpawn def0 = maker.getSpawns().get(0);
				if (def0 != null)
					def0.sendScriptEvent(Integer.parseInt(name), 1, 0);
				break;
		}
		super.onMakerScriptEvent(name, maker, int1, int2);
	}
}