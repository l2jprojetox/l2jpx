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
package org.l2jpx.commons.database;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;

import org.mariadb.jdbc.MariaDbPoolDataSource;

import org.l2jpx.Config;

/**
 * @author Mobius
 * @version November 10th 2018
 */
public class DatabaseFactory
{
	private static final Logger LOGGER = Logger.getLogger(DatabaseFactory.class.getName());
	
	private static MariaDbPoolDataSource DATABASE_POOL = new MariaDbPoolDataSource(Config.DATABASE_URL + "&user=" + Config.DATABASE_LOGIN + "&password=" + Config.DATABASE_PASSWORD + "&maxPoolSize=" + Config.DATABASE_MAX_CONNECTIONS);
	
	public static void init()
	{
		if (Config.DATABASE_TEST_CONNECTIONS)
		{
			final List<Connection> connections = new LinkedList<>();
			int successfulConnections = 0;
			
			try
			{
				LOGGER.info("Database: Testing database connections...");
				
				// Try to open the maximum number of connections.
				for (int i = 0; i < Config.DATABASE_MAX_CONNECTIONS; i++)
				{
					try
					{
						connections.add(DATABASE_POOL.getConnection());
						successfulConnections++; // Increment on successful connection.
					}
					catch (SQLException e)
					{
						LOGGER.warning("Database: Failed to open connection " + (i + 1) + "!");
						break; // Exit the loop if a connection fails.
					}
				}
				
				if (successfulConnections == Config.DATABASE_MAX_CONNECTIONS)
				{
					LOGGER.info("Database: Initialized with a total of " + Config.DATABASE_MAX_CONNECTIONS + " connections.");
				}
			}
			finally
			{
				// Close all opened connections.
				for (Connection conn : connections)
				{
					if (conn != null)
					{
						try
						{
							conn.close();
						}
						catch (SQLException e)
						{
							LOGGER.severe("Database: Error closing connection. " + e.getMessage());
						}
					}
				}
				
				if (successfulConnections < Config.DATABASE_MAX_CONNECTIONS)
				{
					LOGGER.warning("Database: You should change your configurations!");
					LOGGER.warning("Database: Started with " + successfulConnections + " connections out of " + Config.DATABASE_MAX_CONNECTIONS + "!");
					
					// When more than 50, use closest number divided by 50.
					int newConnectionCount = successfulConnections;
					if (successfulConnections > 50)
					{
						newConnectionCount = (successfulConnections / 50) * 50;
					}
					
					// Need at least 2 connections for the pool.
					newConnectionCount = Math.max(newConnectionCount, 2);
					
					// Close the pool and reinitialize it with the new connection count.
					if (newConnectionCount != successfulConnections)
					{
						DATABASE_POOL.close();
						DATABASE_POOL = new MariaDbPoolDataSource(Config.DATABASE_URL + "&user=" + Config.DATABASE_LOGIN + "&password=" + Config.DATABASE_PASSWORD + "&maxPoolSize=" + newConnectionCount);
						LOGGER.info("Database: Reinitialized with new pool size " + newConnectionCount + ".");
					}
				}
			}
		}
		else // Only test if connection is valid.
		{
			try
			{
				DATABASE_POOL.getConnection().close();
				LOGGER.info("Database: Initialized.");
			}
			catch (Exception e)
			{
				LOGGER.info("Database: Problem on initialize. " + e);
			}
		}
	}
	
	public static Connection getConnection()
	{
		Connection con = null;
		while (con == null)
		{
			try
			{
				con = DATABASE_POOL.getConnection();
			}
			catch (SQLException e)
			{
				LOGGER.severe("Database: Could not get a connection. " + e);
			}
		}
		return con;
	}
	
	public static void close()
	{
		try
		{
			DATABASE_POOL.close();
		}
		catch (Exception e)
		{
			LOGGER.severe("Database: There was a problem closing the data source. " + e);
		}
	}
}
