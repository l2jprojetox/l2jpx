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
package org.l2jpx.loginserver;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.l2jpx.Config;

/**
 * @author -Wooden-
 */
public abstract class FloodProtectedListener extends Thread
{
	private static final Logger LOGGER = Logger.getLogger(FloodProtectedListener.class.getName());
	
	private final Map<String, ForeignConnection> _floodProtection = new ConcurrentHashMap<>();
	
	private ServerSocket _serverSocket;
	
	public FloodProtectedListener(String listenIp, int port) throws IOException
	{
		// If listening IP is "*", listen on all interfaces.
		if (listenIp.equals("*"))
		{
			_serverSocket = new ServerSocket(port);
		}
		else // Else, listen on the specified IP.
		{
			_serverSocket = new ServerSocket(port, 50, InetAddress.getByName(listenIp));
		}
	}
	
	@Override
	public void run()
	{
		Socket connection = null;
		while (!isInterrupted()) // Continue until the thread is interrupted.
		{
			try
			{
				connection = _serverSocket.accept(); // Accept incoming connections.
				if (Config.FLOOD_PROTECTION)
				{
					// Check for flood protection on the connection.
					ForeignConnection fConnection = _floodProtection.get(connection.getInetAddress().getHostAddress());
					if (fConnection != null) // If there's an existing connection from this IP.
					{
						fConnection.connectionNumber += 1;
						if (((fConnection.connectionNumber > Config.FAST_CONNECTION_LIMIT) && ((System.currentTimeMillis() - fConnection.lastConnection) < Config.NORMAL_CONNECTION_TIME)) || ((System.currentTimeMillis() - fConnection.lastConnection) < Config.FAST_CONNECTION_TIME) || (fConnection.connectionNumber > Config.MAX_CONNECTION_PER_IP))
						{
							fConnection.lastConnection = System.currentTimeMillis();
							connection.close();
							fConnection.connectionNumber -= 1;
							if (!fConnection.isFlooding)
							{
								LOGGER.warning("Potential Flood from " + connection.getInetAddress().getHostAddress());
							}
							fConnection.isFlooding = true;
							continue;
						}
						if (fConnection.isFlooding) // if connection was flooding server but now passed the check
						{
							fConnection.isFlooding = false;
							LOGGER.info(connection.getInetAddress().getHostAddress() + " is not considered as flooding anymore.");
						}
						fConnection.lastConnection = System.currentTimeMillis();
					}
					else // If it's a new connection.
					{
						// Initialize flood protection for the new connection.
						fConnection = new ForeignConnection(System.currentTimeMillis());
						_floodProtection.put(connection.getInetAddress().getHostAddress(), fConnection);
					}
				}
				
				// Add client connection for further processing (implementation in subclasses).
				addClient(connection);
			}
			catch (Exception e)
			{
				// Handle exceptions and potential thread interruption.
				if (isInterrupted())
				{
					// Close server socket and break the loop on thread interruption.
					try
					{
						_serverSocket.close();
					}
					catch (IOException io)
					{
						LOGGER.log(Level.INFO, "", io);
					}
					break;
				}
			}
		}
	}
	
	protected static class ForeignConnection
	{
		public int connectionNumber;
		public long lastConnection;
		public boolean isFlooding = false;
		
		public ForeignConnection(long time)
		{
			// Initialize a new foreign connection.
			lastConnection = time;
			connectionNumber = 1;
		}
	}
	
	public abstract void addClient(Socket s);
	
	public void removeFloodProtection(String ip)
	{
		// Only proceed if flood protection is enabled.
		if (!Config.FLOOD_PROTECTION)
		{
			return;
		}
		// Decrement connection count or remove if no more connections exist.
		final ForeignConnection fConnection = _floodProtection.get(ip);
		if (fConnection != null)
		{
			fConnection.connectionNumber -= 1;
			if (fConnection.connectionNumber == 0)
			{
				_floodProtection.remove(ip);
			}
		}
		else
		{
			LOGGER.warning("Removing a flood protection for a GameServer that was not in the connection map??? :" + ip);
		}
	}
	
	public void close()
	{
		try
		{
			_serverSocket.close();
		}
		catch (IOException e)
		{
			LOGGER.warning(getClass().getSimpleName() + ": " + e.getMessage());
		}
	}
}