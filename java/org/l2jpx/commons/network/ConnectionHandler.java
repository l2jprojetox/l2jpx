/*
 * Copyright © 2019-2021 Async-mmocore
 *
 * This file is part of the Async-mmocore project.
 *
 * Async-mmocore is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Async-mmocore is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.l2jpx.commons.network;

import java.io.IOException;
import java.net.StandardSocketOptions;
import java.nio.channels.AsynchronousChannelGroup;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.CompletionHandler;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.l2jpx.commons.network.internal.MMOThreadFactory;

/**
 * Manages network connections for clients.<br>
 * This class handles the creation and management of server socket channels, and processes incoming client connections.
 * @param <T> The type of Client associated with this connection handler.
 * @author JoeAlisson
 */
public class ConnectionHandler<T extends Client<Connection<T>>>
{
	// private static final Logger LOGGER = Logger.getLogger(ConnectionHandler.class.getName());
	
	private final AsynchronousChannelGroup _group;
	protected final AsynchronousServerSocketChannel _listener;
	protected final ConnectionConfig _config;
	protected final WriteHandler<T> _writeHandler;
	protected final ReadHandler<T> _readHandler;
	protected final ClientFactory<T> _clientFactory;
	
	public ConnectionHandler(ConnectionConfig config, ClientFactory<T> clientFactory, ReadHandler<T> readHandler) throws IOException
	{
		_config = config;
		_readHandler = readHandler;
		_clientFactory = clientFactory;
		_writeHandler = new WriteHandler<>();
		_group = createChannelGroup();
		_listener = openServerSocket(config);
	}
	
	private AsynchronousChannelGroup createChannelGroup() throws IOException
	{
		if (_config.useCachedThreadPool)
		{
			// LOGGER.info("Channel group is using CachedThreadPool");
			final ExecutorService threadPool = new ThreadPoolExecutor(_config.threadPoolSize, _config.maxCachedThreads, 60L, TimeUnit.SECONDS, new SynchronousQueue<>(), new MMOThreadFactory("Server", _config.threadPriority));
			return AsynchronousChannelGroup.withCachedThreadPool(threadPool, 0);
		}
		
		// LOGGER.info("Channel group is using FixedThreadPool");
		return AsynchronousChannelGroup.withFixedThreadPool(_config.threadPoolSize, new MMOThreadFactory("Server", _config.threadPriority));
	}
	
	private AsynchronousServerSocketChannel openServerSocket(ConnectionConfig config) throws IOException
	{
		final AsynchronousServerSocketChannel socketChannel = _group.provider().openAsynchronousServerSocketChannel(_group);
		socketChannel.setOption(StandardSocketOptions.SO_REUSEADDR, true);
		socketChannel.bind(config.address);
		return socketChannel;
	}
	
	/**
	 * Start to listen connections.
	 */
	public void start()
	{
		_listener.accept(null, new AcceptConnectionHandler());
	}
	
	/**
	 * Shutdown the connection listener, the thread pool and all associated resources.<br>
	 * This method closes all established connections.
	 */
	public void shutdown()
	{
		// LOGGER.info("Shutting ConnectionHandler down");
		// boolean terminated = false;
		try
		{
			_listener.close();
			_group.shutdown();
			// terminated = group.awaitTermination(config.shutdownWaitTime, TimeUnit.MILLISECONDS);
			_group.awaitTermination(_config.shutdownWaitTime, TimeUnit.MILLISECONDS);
			_group.shutdownNow();
		}
		catch (InterruptedException e)
		{
			// LOGGER.log(Level.WARNING, e.getMessage(), e);
			Thread.currentThread().interrupt();
		}
		catch (Exception e)
		{
			// LOGGER.log(Level.WARNING, e.getMessage(), e);
		}
		// LOGGER.info("ConnectionHandler was shutdown with success status " + terminated);
	}
	
	/**
	 * Return the current use of Resource Buffers Pools<br>
	 * API Note: This method exists mainly to support debugging, where you want to see the use of Buffers resource.
	 * @return the resource buffers pool stats
	 */
	public String resourceStats()
	{
		return _config.resourcePool.stats();
	}
	
	private class AcceptConnectionHandler implements CompletionHandler<AsynchronousSocketChannel, Void>
	{
		public AcceptConnectionHandler()
		{
		}
		
		@Override
		public void completed(AsynchronousSocketChannel clientChannel, Void attachment)
		{
			listenConnections();
			processNewConnection(clientChannel);
		}
		
		private void listenConnections()
		{
			if (_listener.isOpen())
			{
				_listener.accept(null, this);
			}
		}
		
		@Override
		public void failed(Throwable t, Void attachment)
		{
			// LOGGER.log(Level.WARNING, t.getMessage(), t);
			listenConnections();
		}
		
		private void processNewConnection(AsynchronousSocketChannel channel)
		{
			if ((channel != null) && channel.isOpen())
			{
				try
				{
					connectToChannel(channel);
				}
				catch (ClosedChannelException e)
				{
					// LOGGER.log(Level.INFO, e.getMessage(), e);
				}
				catch (Exception e)
				{
					// LOGGER.log(Level.SEVERE, e.getMessage(), e);
					closeChannel(channel);
				}
			}
		}
		
		private void closeChannel(AsynchronousSocketChannel channel)
		{
			try
			{
				channel.close();
			}
			catch (IOException e)
			{
				// LOGGER.log(Level.WARNING, e.getMessage(), e);
			}
		}
		
		private void connectToChannel(AsynchronousSocketChannel channel) throws IOException
		{
			// LOGGER.info("Connecting to " + channel);
			if (acceptConnection(channel))
			{
				final T client = createClient(channel);
				client.onConnected();
				client.read();
			}
			else
			{
				// LOGGER.info("Rejected connection");
				closeChannel(channel);
			}
		}
		
		private boolean acceptConnection(AsynchronousSocketChannel channel)
		{
			return (_config.acceptFilter == null) || _config.acceptFilter.accept(channel);
		}
		
		private T createClient(AsynchronousSocketChannel channel) throws IOException
		{
			channel.setOption(StandardSocketOptions.TCP_NODELAY, !_config.useNagle);
			final Connection<T> connection = new Connection<>(channel, _readHandler, _writeHandler, _config);
			final T client = _clientFactory.create(connection);
			connection.setClient(client);
			return client;
		}
	}
}
