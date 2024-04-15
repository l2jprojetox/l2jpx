package com.px.gameserver.model.zone.type;

import com.px.gameserver.data.manager.ClanHallManager;
import com.px.gameserver.data.xml.MapRegionData.TeleportType;
import com.px.gameserver.enums.ZoneId;
import com.px.gameserver.model.actor.Creature;
import com.px.gameserver.model.actor.Player;
import com.px.gameserver.model.clanhall.ClanHall;
import com.px.gameserver.model.zone.SpawnZoneType;
import com.px.gameserver.network.serverpackets.ClanHallDecoration;

/**
 * A zone extending {@link SpawnZoneType} used by {@link ClanHall}s.
 */
public class ClanHallZone extends SpawnZoneType
{
	private int _clanHallId;
	
	public ClanHallZone(int id)
	{
		super(id);
	}
	
	@Override
	public void setParameter(String name, String value)
	{
		if (name.equals("clanHallId"))
			_clanHallId = Integer.parseInt(value);
		else
			super.setParameter(name, value);
	}
	
	@Override
	protected void onEnter(Creature character)
	{
		if (character instanceof Player)
		{
			// Set as in clan hall
			character.setInsideZone(ZoneId.CLAN_HALL, true);
			
			final ClanHall ch = ClanHallManager.getInstance().getClanHall(_clanHallId);
			if (ch == null)
				return;
			
			// Send decoration packet
			character.sendPacket(new ClanHallDecoration(ch));
		}
	}
	
	@Override
	protected void onExit(Creature character)
	{
		if (character instanceof Player)
			character.setInsideZone(ZoneId.CLAN_HALL, false);
	}
	
	/**
	 * Kick {@link Player}s who don't belong to the clan set as parameter from this zone. They are ported to town.
	 * @param clanId : The clanhall owner id. Related players aren't teleported out.
	 */
	public void banishForeigners(int clanId)
	{
		for (Player player : getKnownTypeInside(Player.class))
		{
			if (player.getClanId() == clanId)
				continue;
			
			player.teleportTo(TeleportType.TOWN);
		}
	}
	
	public int getClanHallId()
	{
		return _clanHallId;
	}
}