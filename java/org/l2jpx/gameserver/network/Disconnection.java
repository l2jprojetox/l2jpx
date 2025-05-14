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
package org.l2jpx.gameserver.network;

import java.util.logging.Logger;

import org.l2jpx.commons.threads.ThreadPool;
import org.l2jpx.gameserver.instancemanager.AntiFeedManager;
import org.l2jpx.gameserver.model.actor.Player;
import org.l2jpx.gameserver.network.serverpackets.ServerPacket;
import org.l2jpx.gameserver.taskmanager.AttackStanceTaskManager;

/**
 * @author NB4L1
 */
public class Disconnection
{
	private static final Logger LOGGER = Logger.getLogger(Disconnection.class.getName());
	
	public static GameClient getClient(GameClient client, Player player)
	{
		if (client != null)
		{
			return client;
		}
		
		if (player != null)
		{
			return player.getClient();
		}
		
		return null;
	}
	
	public static Player getPlayer(GameClient client, Player player)
	{
		if (player != null)
		{
			return player;
		}
		
		if (client != null)
		{
			return client.getPlayer();
		}
		
		return null;
	}
	
	private final GameClient _client;
	private final Player _player;
	
	private Disconnection(GameClient client)
	{
		this(client, null);
	}
	
	public static Disconnection of(GameClient client)
	{
		return new Disconnection(client);
	}
	
	private Disconnection(Player player)
	{
		this(null, player);
	}
	
	public static Disconnection of(Player player)
	{
		return new Disconnection(player);
	}
	
	private Disconnection(GameClient client, Player player)
	{
		_client = getClient(client, player);
		_player = getPlayer(client, player);
		
		// Stop player tasks.
		if (_player != null)
		{
			_player.stopAllTasks();
		}
		
		// Anti Feed
		AntiFeedManager.getInstance().onDisconnect(_client);
		
		if (_client != null)
		{
			_client.setPlayer(null);
		}
		
		if (_player != null)
		{
			_player.setClient(null);
		}
	}
	
	public static Disconnection of(GameClient client, Player player)
	{
		return new Disconnection(client, player);
	}
	
	public Disconnection storeMe()
	{
		try
		{
			if (_player != null)
			{
				_player.storeMe();
			}
		}
		catch (RuntimeException e)
		{
			LOGGER.warning(e.getMessage());
		}
		return this;
	}
	
	public Disconnection deleteMe()
	{
		try
		{
			if ((_player != null) && _player.isOnline())
			{
				_player.deleteMe();
			}
		}
		catch (RuntimeException e)
		{
			LOGGER.warning(e.getMessage());
		}
		return this;
	}
	
	public Disconnection close(ServerPacket packet)
	{
		if (_client != null)
		{
			_client.close(packet);
		}
		return this;
	}
	
	public void defaultSequence(ServerPacket packet)
	{
		defaultSequence();
		close(packet);
	}
	
	private void defaultSequence()
	{
		storeMe();
		deleteMe();
	}
	
	public void onDisconnection()
	{
		if (_player != null)
		{
			ThreadPool.schedule(this::defaultSequence, _player.canLogout() ? 0 : AttackStanceTaskManager.COMBAT_TIME);
		}
	}
}