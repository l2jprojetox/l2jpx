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
package org.l2jpx.gameserver.model.olympiad;

import java.util.List;
import java.util.Objects;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import org.l2jpx.commons.util.Rnd;
import org.l2jpx.gameserver.instancemanager.InstanceManager;
import org.l2jpx.gameserver.model.Location;
import org.l2jpx.gameserver.model.Spawn;
import org.l2jpx.gameserver.model.actor.Npc;
import org.l2jpx.gameserver.model.actor.Player;
import org.l2jpx.gameserver.model.actor.instance.Door;
import org.l2jpx.gameserver.model.instancezone.Instance;
import org.l2jpx.gameserver.model.zone.ZoneId;
import org.l2jpx.gameserver.model.zone.type.OlympiadStadiumZone;
import org.l2jpx.gameserver.network.SystemMessageId;
import org.l2jpx.gameserver.network.serverpackets.ExOlympiadMatchEnd;
import org.l2jpx.gameserver.network.serverpackets.ExOlympiadUserInfo;
import org.l2jpx.gameserver.network.serverpackets.ServerPacket;
import org.l2jpx.gameserver.network.serverpackets.SystemMessage;

/**
 * @author JIV
 */
public class OlympiadStadium
{
	private static final Logger LOGGER = Logger.getLogger(OlympiadStadium.class.getName());
	
	private final OlympiadStadiumZone _zone;
	private final int _stadiumId;
	private final Instance _instance;
	private final List<Spawn> _buffers;
	private OlympiadGameTask _task = null;
	
	protected OlympiadStadium(OlympiadStadiumZone olyzone, int stadiumId)
	{
		_zone = olyzone;
		_stadiumId = stadiumId;
		_instance = InstanceManager.getInstance().createInstance(olyzone.getInstanceTemplateId(), null);
		_buffers = _instance.getNpcs().stream().map(Npc::getSpawn).collect(Collectors.toList());
		_buffers.stream().map(Spawn::getLastSpawn).forEach(Npc::deleteMe);
	}
	
	public OlympiadStadiumZone getZone()
	{
		return _zone;
	}
	
	public void registerTask(OlympiadGameTask task)
	{
		_task = task;
	}
	
	public OlympiadGameTask getTask()
	{
		return _task;
	}
	
	public Instance getInstance()
	{
		return _instance;
	}
	
	public void openDoors()
	{
		_instance.getDoors().forEach(Door::openMe);
	}
	
	public void closeDoors()
	{
		_instance.getDoors().forEach(Door::closeMe);
	}
	
	public void spawnBuffers()
	{
		_buffers.forEach(spawn -> spawn.doSpawn(false));
	}
	
	public void deleteBuffers()
	{
		_buffers.stream().map(Spawn::getLastSpawn).filter(Objects::nonNull).forEach(Npc::deleteMe);
	}
	
	public void broadcastStatusUpdate(Player player)
	{
		final ExOlympiadUserInfo packet = new ExOlympiadUserInfo(player);
		for (Player target : _instance.getPlayers())
		{
			if (target.inObserverMode() || (target.getOlympiadSide() != player.getOlympiadSide()))
			{
				target.sendPacket(packet);
			}
		}
	}
	
	public void broadcastPacket(ServerPacket packet)
	{
		_instance.broadcastPacket(packet);
	}
	
	public void broadcastPacketToObservers(ServerPacket packet)
	{
		for (Player target : _instance.getPlayers())
		{
			if (target.inObserverMode())
			{
				target.sendPacket(packet);
			}
		}
	}
	
	public void updateZoneStatusForCharactersInside()
	{
		if (_task == null)
		{
			return;
		}
		
		final boolean battleStarted = _task.isBattleStarted();
		final SystemMessage sm;
		if (battleStarted)
		{
			sm = new SystemMessage(SystemMessageId.YOU_HAVE_ENTERED_A_COMBAT_ZONE);
		}
		else
		{
			sm = new SystemMessage(SystemMessageId.YOU_HAVE_LEFT_A_COMBAT_ZONE);
		}
		
		for (Player player : _instance.getPlayers())
		{
			if (player.inObserverMode())
			{
				return;
			}
			
			if (battleStarted)
			{
				player.setInsideZone(ZoneId.PVP, true);
				player.sendPacket(sm);
			}
			else
			{
				player.setInsideZone(ZoneId.PVP, false);
				player.sendPacket(sm);
				player.sendPacket(ExOlympiadMatchEnd.STATIC_PACKET);
			}
		}
	}
	
	public void updateZoneInfoForObservers()
	{
		if (_task == null)
		{
			return;
		}
		
		for (Player player : _instance.getPlayers())
		{
			if (!player.inObserverMode())
			{
				return;
			}
			
			final List<Location> spectatorSpawns = getZone().getSpectatorSpawns();
			if (spectatorSpawns.isEmpty())
			{
				LOGGER.warning(getClass().getSimpleName() + ": Zone: " + getZone() + " doesn't have specatator spawns defined!");
				return;
			}
			
			final Location loc = spectatorSpawns.get(Rnd.get(spectatorSpawns.size()));
			player.enterOlympiadObserverMode(loc, _stadiumId);
			
			_task.getGame().sendOlympiadInfo(player);
		}
	}
}