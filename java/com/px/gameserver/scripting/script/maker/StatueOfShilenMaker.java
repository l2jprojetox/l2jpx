package com.px.gameserver.scripting.script.maker;

import com.px.gameserver.model.spawn.MultiSpawn;
import com.px.gameserver.model.spawn.NpcMaker;

public class StatueOfShilenMaker extends DefaultMaker
{
	public StatueOfShilenMaker(String name)
	{
		super(name);
	}
	
	@Override
	public void onMakerScriptEvent(String name, NpcMaker maker, int int1, int int2)
	{
		switch (name)
		{
			case "11041":
			case "11043":
			case "11045":
			case "11047":
			case "11050":
				final MultiSpawn def0 = maker.getSpawns().get(0);
				if (def0 != null)
					def0.sendScriptEvent(Integer.parseInt(name), 1, 0);
				break;
		}
		super.onMakerScriptEvent(name, maker, int1, int2);
	}
}