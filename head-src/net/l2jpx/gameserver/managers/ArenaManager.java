package net.l2jpx.gameserver.managers;

import net.l2jpx.gameserver.datatables.xml.ZoneData;
import net.l2jpx.gameserver.model.L2Character;
import net.l2jpx.gameserver.model.zone.L2ZoneType;
import net.l2jpx.gameserver.model.zone.type.L2ArenaZone;

public class ArenaManager
{
	private static ArenaManager instance;
	
	public static final ArenaManager getInstance()
	{
		if (instance == null)
		{
			instance = new ArenaManager();
		}
		return instance;
	}
	
	public L2ArenaZone getArena(L2Character character)
	{
		for (L2ZoneType zone : ZoneData.getInstance().getAllZones().values())
		{
			if (zone instanceof L2ArenaZone)
			{
				L2ArenaZone temp = (L2ArenaZone) zone;
				
				if (temp.isInsideZone(character))
				{
					return temp;
				}
			}
		}
		
		return null;
	}
	
	public L2ArenaZone getArena(int x, int y, int z)
	{
		for (L2ZoneType zone : ZoneData.getInstance().getAllZones().values())
		{
			if (zone instanceof L2ArenaZone)
			{
				L2ArenaZone temp = (L2ArenaZone) zone;
				
				if (temp.isInsideZone(x, y, z))
				{
					return temp;
				}
			}
		}
		
		return null;
	}
}
