package net.l2jpx.gameserver.model.zone.type;

import net.l2jpx.gameserver.datatables.csv.MapRegionTable;
import net.l2jpx.gameserver.model.L2Character;
import net.l2jpx.gameserver.model.actor.instance.L2PcInstance;
import net.l2jpx.gameserver.model.zone.L2ZoneType;
import net.l2jpx.gameserver.network.SystemMessageId;
import net.l2jpx.gameserver.network.serverpackets.SystemMessage;

/**
 * An arena
 * @author durgus
 */
public class L2ArenaZone extends L2ZoneType
{
	private final int[] spawnLoc;
	
	public L2ArenaZone(int id)
	{
		super(id);
		
		spawnLoc = new int[3];
	}
	
	@Override
	public void setParameter(String name, String value)
	{
		switch (name)
		{
			case "spawnX":
				spawnLoc[0] = Integer.parseInt(value);
				break;
			case "spawnY":
				spawnLoc[1] = Integer.parseInt(value);
				break;
			case "spawnZ":
				spawnLoc[2] = Integer.parseInt(value);
				break;
			default:
				super.setParameter(name, value);
				break;
		}
	}
	
	@Override
	protected void onEnter(L2Character character)
	{
		character.setInsideZone(L2Character.ZONE_PVP, true);
		
		if (character instanceof L2PcInstance)
		{
			((L2PcInstance) character).sendPacket(new SystemMessage(SystemMessageId.YOU_HAVE_ENTERED_A_COMBAT_ZONE));
		}
	}
	
	@Override
	protected void onExit(L2Character character)
	{
		character.setInsideZone(L2Character.ZONE_PVP, false);
		
		if (character instanceof L2PcInstance)
		{
			((L2PcInstance) character).sendPacket(new SystemMessage(SystemMessageId.YOU_HAVE_LEFT_A_COMBAT_ZONE));
		}
	}
	
	@Override
	protected void onDieInside(L2Character character)
	{
	}
	
	@Override
	protected void onReviveInside(L2Character character)
	{
	}
	
	public void oustAllPlayers()
	{
		if (characterList == null)
		{
			return;
		}
		
		if (characterList.isEmpty())
		{
			return;
		}
		
		for (L2Character character : characterList.values())
		{
			if (character == null)
			{
				continue;
			}
			
			if (character instanceof L2PcInstance)
			{
				L2PcInstance player = (L2PcInstance) character;
				
				if (player.isOnline())
				{
					player.teleToLocation(MapRegionTable.TeleportWhereType.Town);
				}
			}
		}
	}
	
	public final int[] getSpawnLoc()
	{
		return spawnLoc;
	}
}
