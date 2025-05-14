/*
 * This file is part of the L2J Mobius project.
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.l2jpx.gameserver.network.clientpackets;

import org.l2jpx.commons.threads.ThreadPool;
import org.l2jpx.gameserver.data.xml.ClanHallData;
import org.l2jpx.gameserver.enums.TeleportWhereType;
import org.l2jpx.gameserver.instancemanager.CastleManager;
import org.l2jpx.gameserver.instancemanager.FortManager;
import org.l2jpx.gameserver.instancemanager.MapRegionManager;
import org.l2jpx.gameserver.model.Location;
import org.l2jpx.gameserver.model.SiegeClan;
import org.l2jpx.gameserver.model.actor.Player;
import org.l2jpx.gameserver.model.clan.Clan;
import org.l2jpx.gameserver.model.events.EventType;
import org.l2jpx.gameserver.model.events.listeners.AbstractEventListener;
import org.l2jpx.gameserver.model.instancezone.Instance;
import org.l2jpx.gameserver.model.quest.Event;
import org.l2jpx.gameserver.model.residences.ClanHall;
import org.l2jpx.gameserver.model.residences.ResidenceFunctionType;
import org.l2jpx.gameserver.model.siege.Castle;
import org.l2jpx.gameserver.model.siege.Castle.CastleFunction;
import org.l2jpx.gameserver.model.siege.Fort;
import org.l2jpx.gameserver.model.siege.Fort.FortFunction;
import org.l2jpx.gameserver.network.PacketLogger;

/**
 * @version $Revision: 1.7.2.3.2.6 $ $Date: 2005/03/27 15:29:30 $
 */
public class RequestRestartPoint extends ClientPacket
{
	protected int _requestedPointType;
	protected boolean _continuation;
	
	@Override
	protected void readImpl()
	{
		_requestedPointType = readInt();
	}
	
	class DeathTask implements Runnable
	{
		final Player _player;
		
		DeathTask(Player player)
		{
			_player = player;
		}
		
		@Override
		public void run()
		{
			portPlayer(_player);
		}
	}
	
	@Override
	protected void runImpl()
	{
		final Player player = getPlayer();
		if (player == null)
		{
			return;
		}
		
		if (!player.canRevive())
		{
			return;
		}
		
		if (player.isFakeDeath())
		{
			player.stopFakeDeath(true);
			return;
		}
		else if (!player.isDead())
		{
			return;
		}
		
		// Custom event resurrection management.
		if (player.isOnEvent())
		{
			for (AbstractEventListener listener : player.getListeners(EventType.ON_CREATURE_DEATH))
			{
				if (listener.getOwner() instanceof Event)
				{
					((Event) listener.getOwner()).notifyEvent("ResurrectPlayer", null, player);
					return;
				}
			}
		}
		
		final Castle castle = CastleManager.getInstance().getCastle(player.getX(), player.getY(), player.getZ());
		if ((castle != null) && castle.getSiege().isInProgress() && (player.getClan() != null) && castle.getSiege().checkIsAttacker(player.getClan()))
		{
			// Schedule respawn delay for attacker
			ThreadPool.schedule(new DeathTask(player), castle.getSiege().getAttackerRespawnDelay());
			if (castle.getSiege().getAttackerRespawnDelay() > 0)
			{
				player.sendMessage("You will be re-spawned in " + (castle.getSiege().getAttackerRespawnDelay() / 1000) + " seconds");
			}
			return;
		}
		
		portPlayer(player);
	}
	
	protected void portPlayer(Player player)
	{
		Location loc = null;
		Instance instance = null;
		
		// force jail
		if (player.isJailed())
		{
			_requestedPointType = 27;
		}
		
		switch (_requestedPointType)
		{
			case 1: // to clanhall
			{
				if ((player.getClan() == null) || (player.getClan().getHideoutId() == 0))
				{
					PacketLogger.warning("Player [" + player.getName() + "] called RestartPointPacket - To Clanhall and he doesn't have Clanhall!");
					return;
				}
				loc = MapRegionManager.getInstance().getTeleToLocation(player, TeleportWhereType.CLANHALL);
				final ClanHall residense = ClanHallData.getInstance().getClanHallByClan(player.getClan());
				if ((residense != null) && (residense.hasFunction(ResidenceFunctionType.EXP_RESTORE)))
				{
					player.restoreExp(residense.getFunction(ResidenceFunctionType.EXP_RESTORE).getValue());
				}
				break;
			}
			case 2: // to castle
			{
				final Clan clan = player.getClan();
				Castle castle = CastleManager.getInstance().getCastle(player);
				if ((castle != null) && castle.getSiege().isInProgress())
				{
					// Siege in progress
					if (castle.getSiege().checkIsDefender(clan))
					{
						loc = MapRegionManager.getInstance().getTeleToLocation(player, TeleportWhereType.CASTLE);
					}
					else if (castle.getSiege().checkIsAttacker(clan))
					{
						loc = MapRegionManager.getInstance().getTeleToLocation(player, TeleportWhereType.TOWN);
					}
					else
					{
						PacketLogger.warning("Player [" + player.getName() + "] called RestartPointPacket - To Castle and he doesn't have Castle!");
						return;
					}
				}
				else
				{
					if ((clan == null) || (clan.getCastleId() == 0))
					{
						return;
					}
					loc = MapRegionManager.getInstance().getTeleToLocation(player, TeleportWhereType.CASTLE);
				}
				
				if (clan != null)
				{
					castle = CastleManager.getInstance().getCastleByOwner(clan);
					if (castle != null)
					{
						final CastleFunction castleFunction = castle.getCastleFunction(Castle.FUNC_RESTORE_EXP);
						if (castleFunction != null)
						{
							player.restoreExp(castleFunction.getLvl());
						}
					}
				}
				break;
			}
			case 3: // to fortress
			{
				final Clan clan = player.getClan();
				if ((clan == null) || (clan.getFortId() == 0))
				{
					PacketLogger.warning("Player [" + player.getName() + "] called RestartPointPacket - To Fortress and he doesn't have Fortress!");
					return;
				}
				loc = MapRegionManager.getInstance().getTeleToLocation(player, TeleportWhereType.FORTRESS);
				
				final Fort fort = FortManager.getInstance().getFortByOwner(clan);
				if (fort != null)
				{
					final FortFunction fortFunction = fort.getFortFunction(Fort.FUNC_RESTORE_EXP);
					if (fortFunction != null)
					{
						player.restoreExp(fortFunction.getLevel());
					}
				}
				break;
			}
			case 4: // to siege HQ
			{
				SiegeClan siegeClan = null;
				final Castle castle = CastleManager.getInstance().getCastle(player);
				final Fort fort = FortManager.getInstance().getFort(player);
				if ((castle != null) && castle.getSiege().isInProgress())
				{
					siegeClan = castle.getSiege().getAttackerClan(player.getClan());
				}
				else if ((fort != null) && fort.getSiege().isInProgress())
				{
					siegeClan = fort.getSiege().getAttackerClan(player.getClan());
				}
				
				if (((siegeClan == null) || siegeClan.getFlag().isEmpty()))
				{
					PacketLogger.warning("Player [" + player.getName() + "] called RestartPointPacket - To Siege HQ and he doesn't have Siege HQ!");
					return;
				}
				loc = MapRegionManager.getInstance().getTeleToLocation(player, TeleportWhereType.SIEGEFLAG);
				break;
			}
			case 5: // Fixed or Player is a festival participant
			{
				if (!player.isGM() && !player.getInventory().haveItemForSelfResurrection())
				{
					PacketLogger.warning("Player [" + player.getName() + "] called RestartPointPacket - Fixed and he isn't festival participant!");
					return;
				}
				if (player.isGM() || player.destroyItemByItemId("Feather", 10649, 1, player, false) || player.destroyItemByItemId("Feather", 13300, 1, player, false) || player.destroyItemByItemId("Feather", 13128, 1, player, false))
				{
					player.doRevive(100.00);
				}
				else
				{
					instance = player.getInstanceWorld();
					loc = new Location(player);
				}
				break;
			}
			case 6: // TODO: Agathion resurrection
			{
				break;
			}
			case 7: // TODO: Adventurer's Song
			{
				break;
			}
			case 27: // to jail
			{
				if (!player.isJailed())
				{
					return;
				}
				loc = new Location(-114356, -249645, -2984);
				break;
			}
			default:
			{
				loc = MapRegionManager.getInstance().getTeleToLocation(player, TeleportWhereType.TOWN);
				break;
			}
		}
		
		// Teleport and revive
		if (loc != null)
		{
			player.setIn7sDungeon(false);
			player.setIsPendingRevive(true);
			player.teleToLocation(loc, true, instance);
		}
	}
}
