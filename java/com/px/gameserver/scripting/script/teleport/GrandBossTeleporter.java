package com.px.gameserver.scripting.script.teleport;

import com.px.commons.random.Rnd;

import com.px.gameserver.data.manager.ZoneManager;
import com.px.gameserver.data.xml.DoorData;
import com.px.gameserver.model.actor.Npc;
import com.px.gameserver.model.actor.Player;
import com.px.gameserver.model.location.Location;
import com.px.gameserver.scripting.Quest;

/**
 * This script leads behavior of multiple bosses teleporters.
 * <ul>
 * <li>13001, Heart of Warding : Teleport into Lair of Antharas</li>
 * <li>31842, Baium/Core Teleporter</li>
 * <li>29055, Teleportation Cubic : TODO: Find</li>
 * <li>29061, Teleportation Cubic: Teleport out of The Last Imperial Tomb</li>
 * <li>31859, Teleportation Cubic : Teleport out of Lair of Antharas</li>
 * <li>31384, Gatekeeper of Fire Dragon : Opening some doors</li>
 * <li>31385, Heart of Volcano : Teleport into Lair of Valakas</li>
 * <li>31540, Watcher of Valakas Klein : Teleport into Hall of Flames</li>
 * <li>31686, Gatekeeper of Fire Dragon : Opens doors to Heart of Volcano</li>
 * <li>31687, Gatekeeper of Fire Dragon : Opens doors to Heart of Volcano</li>
 * <li>31759, Teleportation Cubic : Teleport out of Lair of Valakas</li>
 * <li>32107, Teleportation Cubic : Teleport out of Sailren Nest</li>
 * </ul>
 */
public class GrandBossTeleporter extends Quest
{
	private final int BAIUM_ZONE_ID = 110002;
	
	private static final Location[] BAIUM_OUT =
	{
		new Location(108784, 16000, -4928),
		new Location(113824, 10448, -5164),
		new Location(115488, 22096, -5168)
	};
	
	private static final Location[] SAILREN_OUT =
	{
		new Location(10610, -24035, -3676),
		new Location(10703, -24041, -3673),
		new Location(10769, -24107, -3672)
	};
	
	public GrandBossTeleporter()
	{
		super(-1, "teleport");
		
		addFirstTalkId(29055, 29061);
		addTalkId(29055, 29061, 31842, 31859, 31384, 31686, 31687, 31759, 32107);
		addCreated(29055, 29061, 31759, 31842, 31859, 32107);
	}
	
	@Override
	public void onCreated(Npc npc)
	{
		npc.scheduleDespawn(900000);
	}
	
	@Override
	public String onAdvEvent(String event, Npc npc, Player player)
	{
		String htmltext = "";
		
		if (event.equalsIgnoreCase("benom_exit"))
			player.teleportTo(23532, -8455, -1352, 0);
		else if (event.equalsIgnoreCase("frintezza_exit"))
			player.teleportTo(150037, -57720, -2976, 500);
		
		return htmltext;
	}
	
	@Override
	public String onFirstTalk(Npc npc, Player player)
	{
		return npc.getNpcId() + "-01.htm";
	}
	
	@Override
	public String onTalk(Npc npc, Player player)
	{
		String htmltext = "";
		
		switch (npc.getNpcId())
		{
			case 29055:
				player.teleportTo(23532, -8455, -1352, 0);
				break;
			case 31842:
				if (ZoneManager.getInstance().getZoneById(BAIUM_ZONE_ID).isInsideZone(npc))
				{
					player.teleportTo(Rnd.get(BAIUM_OUT), 100);
				}
				else
				{
					if (Rnd.nextBoolean())
						player.teleportTo(17252, 114121, -3439, 0);
					else
						player.teleportTo(17253, 114232, -3439, 0);
				}
				break;
			case 31859:
				player.teleportTo(79800 + Rnd.get(600), 151200 + Rnd.get(1100), -3534, 0);
				break;
			
			case 31384:
				DoorData.getInstance().getDoor(24210004).openMe();
				break;
			
			case 31686:
				DoorData.getInstance().getDoor(24210006).openMe();
				break;
			
			case 31687:
				DoorData.getInstance().getDoor(24210005).openMe();
				break;
			
			case 31759:
				player.teleportTo(150037, -57720, -2976, 250);
				break;
			
			case 32107:
				player.teleportTo(Rnd.get(SAILREN_OUT), 100);
				break;
		}
		
		return htmltext;
	}
}