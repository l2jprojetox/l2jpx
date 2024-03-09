package net.l2jpx.gameserver.model.zone.type;

import net.l2jpx.gameserver.datatables.csv.MapRegionTable;
import net.l2jpx.gameserver.managers.ClanHallManager;
import net.l2jpx.gameserver.model.L2Character;
import net.l2jpx.gameserver.model.Location;
import net.l2jpx.gameserver.model.actor.instance.L2PcInstance;
import net.l2jpx.gameserver.model.entity.ClanHall;
import net.l2jpx.gameserver.model.zone.L2ZoneType;
import net.l2jpx.gameserver.network.serverpackets.ClanHallDecoration;

/**
 * @author durgus
 */
public class L2ClanHallZone extends L2ZoneType
{
	private int clanHallId;
	private final int[] spawnLoc;
	
	public L2ClanHallZone(int id)
	{
		super(id);
		spawnLoc = new int[3];
	}
	
	@Override
	public void setParameter(String name, String value)
	{
		switch (name)
		{
			case "clanHallId":
				clanHallId = Integer.parseInt(value);
				// Register self to the correct clan hall
				ClanHallManager.getInstance().getClanHallById(clanHallId).setZone(this);
				break;
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
		if (character.isPlayer())
		{
			L2PcInstance player = (L2PcInstance) character;
			
			// Set as in clan hall
			player.setInsideZone(L2Character.ZONE_CLANHALL, true);
			
			ClanHall clanHall = ClanHallManager.getInstance().getClanHallById(clanHallId);
			
			if (clanHall == null)
			{
				return;
			}
			
			// Send decoration packet
			ClanHallDecoration deco = new ClanHallDecoration(clanHall);
			player.sendPacket(deco);
			
			// Send a message
			if (clanHall.getOwnerId() != 0 && clanHall.getOwnerId() == player.getClanId())
			{
				player.sendMessage("You have entered your clan hall");
			}
			
		}
	}
	
	@Override
	protected void onExit(L2Character character)
	{
		if (character instanceof L2PcInstance)
		{
			L2PcInstance player = (L2PcInstance) character;
			
			// Unset clanhall zone
			character.setInsideZone(L2Character.ZONE_CLANHALL, false);
			
			// Send a message
			if (player.getClanId() != 0 && ClanHallManager.getInstance().getClanHallById(clanHallId).getOwnerId() == player.getClanId())
			{
				player.sendMessage("You have left your clan hall");
			}
		}
	}
	
	@Override
	protected void onDieInside(final L2Character character)
	{
	}
	
	@Override
	protected void onReviveInside(final L2Character character)
	{
	}
	
	/**
	 * Removes all foreigners from the clan hall
	 * @param owningClanId clan ID that owns the hall clan
	 */
	public void banishForeigners(int owningClanId)
	{
		getPlayersInside().stream().filter(player -> player.getClanId() != owningClanId).forEach(player -> player.teleToLocation(MapRegionTable.TeleportWhereType.Town));
	}
	
	public Location getSpawn()
	{
		return new Location(spawnLoc[0], spawnLoc[1], spawnLoc[2]);
	}
}
