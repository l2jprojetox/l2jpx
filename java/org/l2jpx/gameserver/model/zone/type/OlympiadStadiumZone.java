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
package org.l2jpx.gameserver.model.zone.type;

import java.util.ArrayList;
import java.util.List;

import org.l2jpx.commons.threads.ThreadPool;
import org.l2jpx.gameserver.enums.PlayerCondOverride;
import org.l2jpx.gameserver.enums.TeleportWhereType;
import org.l2jpx.gameserver.instancemanager.ZoneManager;
import org.l2jpx.gameserver.model.Location;
import org.l2jpx.gameserver.model.Spawn;
import org.l2jpx.gameserver.model.actor.Creature;
import org.l2jpx.gameserver.model.actor.Player;
import org.l2jpx.gameserver.model.actor.Summon;
import org.l2jpx.gameserver.model.actor.instance.Door;
import org.l2jpx.gameserver.model.olympiad.OlympiadGameTask;
import org.l2jpx.gameserver.model.zone.AbstractZoneSettings;
import org.l2jpx.gameserver.model.zone.ZoneId;
import org.l2jpx.gameserver.model.zone.ZoneRespawn;
import org.l2jpx.gameserver.network.SystemMessageId;
import org.l2jpx.gameserver.network.serverpackets.ExOlympiadMatchEnd;

/**
 * An olympiad stadium
 * @author durgus, DS
 */
public class OlympiadStadiumZone extends ZoneRespawn
{
	private final List<Door> _doors = new ArrayList<>(2);
	private final List<Spawn> _buffers = new ArrayList<>(2);
	private final List<Location> _spectatorLocations = new ArrayList<>(1);
	
	public OlympiadStadiumZone(int id)
	{
		super(id);
		AbstractZoneSettings settings = ZoneManager.getSettings(getName());
		if (settings == null)
		{
			settings = new Settings();
		}
		setSettings(settings);
	}
	
	public class Settings extends AbstractZoneSettings
	{
		private OlympiadGameTask _task = null;
		
		protected Settings()
		{
		}
		
		public OlympiadGameTask getOlympiadTask()
		{
			return _task;
		}
		
		protected void setTask(OlympiadGameTask task)
		{
			_task = task;
		}
		
		@Override
		public void clear()
		{
			_task = null;
		}
	}
	
	@Override
	public Settings getSettings()
	{
		return (Settings) super.getSettings();
	}
	
	@Override
	public void parseLoc(int x, int y, int z, String type)
	{
		if ((type != null) && type.equals("spectatorSpawn"))
		{
			_spectatorLocations.add(new Location(x, y, z));
		}
		else
		{
			super.parseLoc(x, y, z, type);
		}
	}
	
	public void registerTask(OlympiadGameTask task)
	{
		getSettings().setTask(task);
	}
	
	@Override
	protected void onEnter(Creature creature)
	{
		if ((getSettings().getOlympiadTask() != null) && getSettings().getOlympiadTask().isBattleStarted())
		{
			creature.setInsideZone(ZoneId.PVP, true);
			if (creature.isPlayer())
			{
				creature.sendPacket(SystemMessageId.YOU_HAVE_ENTERED_A_COMBAT_ZONE);
				getSettings().getOlympiadTask().getGame().sendOlympiadInfo(creature);
			}
		}
		
		if (creature.isPlayable())
		{
			final Player player = creature.getActingPlayer();
			if (player != null)
			{
				// only participants, observers and GMs allowed
				if (!player.canOverrideCond(PlayerCondOverride.ZONE_CONDITIONS) && !player.isInOlympiadMode() && !player.inObserverMode())
				{
					ThreadPool.execute(new KickPlayer(player));
				}
				else
				{
					// check for pet
					final Summon pet = player.getPet();
					if (pet != null)
					{
						pet.unSummon(player);
					}
				}
			}
		}
	}
	
	@Override
	protected void onExit(Creature creature)
	{
		if ((getSettings().getOlympiadTask() != null) && getSettings().getOlympiadTask().isBattleStarted())
		{
			creature.setInsideZone(ZoneId.PVP, false);
			if (creature.isPlayer())
			{
				creature.sendPacket(SystemMessageId.YOU_HAVE_LEFT_A_COMBAT_ZONE);
				creature.sendPacket(ExOlympiadMatchEnd.STATIC_PACKET);
			}
		}
	}
	
	private static class KickPlayer implements Runnable
	{
		private Player _player;
		
		protected KickPlayer(Player player)
		{
			_player = player;
		}
		
		@Override
		public void run()
		{
			if (_player != null)
			{
				_player.getServitors().values().forEach(s -> s.unSummon(_player));
				_player.teleToLocation(TeleportWhereType.TOWN, null);
				_player = null;
			}
		}
	}
	
	public List<Door> getDoors()
	{
		return _doors;
	}
	
	public List<Spawn> getBuffers()
	{
		return _buffers;
	}
	
	public List<Location> getSpectatorSpawns()
	{
		return _spectatorLocations;
	}
}
