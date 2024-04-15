package com.px.gameserver.network.clientpackets;

import com.px.commons.concurrent.ThreadPool;

import com.px.Config;
import com.px.gameserver.data.manager.CastleManager;
import com.px.gameserver.data.manager.ClanHallManager;
import com.px.gameserver.data.xml.MapRegionData;
import com.px.gameserver.data.xml.MapRegionData.TeleportType;
import com.px.gameserver.enums.SiegeSide;
import com.px.gameserver.model.actor.Player;
import com.px.gameserver.model.clanhall.ClanHall;
import com.px.gameserver.model.clanhall.ClanHallFunction;
import com.px.gameserver.model.entity.Siege;
import com.px.gameserver.model.location.Location;
import com.px.gameserver.model.pledge.Clan;

public final class RequestRestartPoint extends L2GameClientPacket
{
	protected static final Location JAIL_LOCATION = new Location(-114356, -249645, -2984);
	
	protected int _requestType;
	
	@Override
	protected void readImpl()
	{
		_requestType = readD();
	}
	
	@Override
	protected void runImpl()
	{
		final Player player = getClient().getPlayer();
		if (player == null)
			return;
		
		if (player.isFakeDeath())
		{
			player.stopFakeDeath(true);
			return;
		}
		
		if (!player.isDead())
			return;
		
		// Schedule a respawn delay if player is part of a clan registered in an active siege.
		if (player.getClan() != null)
		{
			final Siege siege = CastleManager.getInstance().getActiveSiege(player);
			if (siege != null && siege.checkSide(player.getClan(), SiegeSide.ATTACKER))
			{
				ThreadPool.schedule(() -> portPlayer(player), Config.ATTACKERS_RESPAWN_DELAY);
				return;
			}
		}
		
		portPlayer(player);
	}
	
	/**
	 * Teleport the {@link Player} to the associated {@link Location}, based on _requestType.
	 * @param player : The player set as parameter.
	 */
	private void portPlayer(Player player)
	{
		final Clan clan = player.getClan();
		
		Location loc = null;
		
		// Enforce type.
		if (player.isInJail())
			_requestType = 27;
		else if (player.isFestivalParticipant())
			_requestType = 4;
		
		// To clanhall.
		if (_requestType == 1)
		{
			if (clan == null || !clan.hasClanHall())
				return;
			
			loc = MapRegionData.getInstance().getLocationToTeleport(player, TeleportType.CLAN_HALL);
			
			final ClanHall ch = ClanHallManager.getInstance().getClanHallByOwner(clan);
			if (ch != null)
			{
				final ClanHallFunction function = ch.getFunction(ClanHall.FUNC_RESTORE_EXP);
				if (function != null)
					player.restoreExp(function.getLvl());
			}
		}
		// To castle.
		else if (_requestType == 2)
		{
			final Siege siege = CastleManager.getInstance().getActiveSiege(player);
			if (siege != null)
			{
				final SiegeSide side = siege.getSide(clan);
				if (side == SiegeSide.DEFENDER || side == SiegeSide.OWNER)
					loc = MapRegionData.getInstance().getLocationToTeleport(player, TeleportType.CASTLE);
				else if (side == SiegeSide.ATTACKER)
					loc = MapRegionData.getInstance().getLocationToTeleport(player, TeleportType.TOWN);
				else
					return;
			}
			else
			{
				if (clan == null || !clan.hasCastle())
					return;
				
				loc = MapRegionData.getInstance().getLocationToTeleport(player, TeleportType.CASTLE);
			}
		}
		// To siege flag.
		else if (_requestType == 3)
			loc = MapRegionData.getInstance().getLocationToTeleport(player, TeleportType.SIEGE_FLAG);
		// Fixed.
		else if (_requestType == 4)
		{
			if (!player.isGM() && !player.isFestivalParticipant())
				return;
			
			loc = player.getPosition();
		}
		// To jail.
		else if (_requestType == 27)
		{
			if (!player.isInJail())
				return;
			
			loc = JAIL_LOCATION;
		}
		// Nothing has been found, use regular "To town" behavior.
		else
			loc = MapRegionData.getInstance().getLocationToTeleport(player, TeleportType.TOWN);
		
		player.setIsIn7sDungeon(false);
		
		if (player.isDead())
			player.doRevive();
		
		player.teleportTo(loc, 20);
	}
}