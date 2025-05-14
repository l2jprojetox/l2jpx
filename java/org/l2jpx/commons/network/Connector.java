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
import java.net.InetSocketAddress;
import java.nio.channels.AsynchronousChannelGroup;
import java.nio.channels.AsynchronousSocketChannel;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.l2jpx.commons.network.internal.MMOThreadFactory;

/**
 * Facilitates establishing connections for clients.<br>
 * This class provides methods to configure and create client connections using various network settings.
 * @param <T> The type of the client associated with the connector.
 * @author JoeAlisson
 */
public class Connector<T extends Client<Connection<T>>>
{
	private static final Object GROUP_LOCK = new Object();
	
	private static AsynchronousChannelGroup GROUP;
	
	private final ConnectionConfig _config;
	private final ReadHandler<T> _readHandler;
	private final ClientFactory<T> _clientFactory;
	
	/**
	 * Creates a Connector holding the minimum requirements to create a Client.
	 * @param clientFactory - The factory responsible to create a new Client when a new connection is accepted.
	 * @param packetHandler - The handle responsible to convert the data received into a {@link ReadablePacket}
	 * @param executor - The responsible to execute the incoming packets.
	 */
	public Connector(ClientFactory<T> clientFactory, PacketHandler<T> packetHandler, PacketExecutor<T> executor)
	{
		_config = new ConnectionConfig(null);
		_readHandler = new ReadHandler<>(packetHandler, executor);
		_clientFactory = clientFactory;
	}
	
	/**
	 * Add a new {@link java.nio.ByteBuffer} grouping pool
	 * @param size the max amount of {@link java.nio.ByteBuffer} supported
	 * @param bufferSize the {@link java.nio.ByteBuffer}'s size inside the pool.
	 * @return this
	 */
	public Connector<T> addBufferPool(int size, int bufferSize)
	{
		_config.newBufferGroup(size, bufferSize);
		return this;
	}
	
	/**
	 * define the factor of pre-initialized {@link java.nio.ByteBuffer} inside a pool.
	 * @param factor the factor of initialized buffers
	 * @return this
	 */
	public Connector<T> initBufferPoolFactor(float factor)
	{
		_config.initBufferPoolFactor = factor;
		return this;
	}
	
	/**
	 * Define the size of dynamic buffer's segment. A segment is used to increase the Buffer when needed.
	 * @param size of dynamic buffer segment
	 * @return this
	 */
	public Connector<T> bufferSegmentSize(int size)
	{
		_config.resourcePool.setBufferSegmentSize(size);
		return this;
	}
	
	/**
	 * Define if drop packets is enabled.
	 * @param enabled if drop packets is enabled. The default value is false
	 * @return this
	 */
	public Connector<T> dropPackets(boolean enabled)
	{
		_config.dropPackets = enabled;
		return this;
	}
	
	/**
	 * Define the threshold to allow the client to drop disposable packets.
	 * <p>
	 * When the client has queued more than {@code threshold} disposable packets will can be disposed.
	 * @param threshold the minimum value to drop packets. The default value is 250
	 * @return this
	 */
	public Connector<T> dropPacketThreshold(int threshold)
	{
		_config.dropPacketThreshold = threshold;
		return this;
	}
	
	/**
	 * Configures the connection to use CachedThreadPool as defined in {@link java.nio.channels.AsynchronousChannelGroup#withCachedThreadPool(java.util.concurrent.ExecutorService, int)}.
	 * <p>
	 * The default behaviour is to use a fixed thread poll as defined in {@link java.nio.channels.AsynchronousChannelGroup#withFixedThreadPool(int, java.util.concurrent.ThreadFactory)}.
	 * @param cached use a cached thread pool if true, otherwise use fixed thread pool
	 * @return this
	 */
	public Connector<T> useCachedThreadPool(boolean cached)
	{
		_config.useCachedThreadPool = cached;
		return this;
	}
	
	/**
	 * Set the size of the threadPool used to manage the connections and data sending.
	 * <p>
	 * If the thread pool is cached this method defines the corePoolSize of ({@link java.util.concurrent.ThreadPoolExecutor})<br>
	 * If the thread pool is fixed this method defines the amount of threads
	 * <p>
	 * The min accepted value is 1.<br>
	 * The default value is the quantity of available processors minus 2.
	 * @param size - the size of thread pool to be Set
	 * @return this
	 */
	public Connector<T> threadPoolSize(int size)
	{
		_config.threadPoolSize = size;
		return this;
	}
	
	/**
	 * Set the size of max threads allowed in the cached thread pool.<br>
	 * This config is ignored when a fixed thread pool is used.
	 * @param size the max cached threads in the cached thread pool.
	 * @return this
	 */
	public Connector<T> maxCachedThreads(int size)
	{
		_config.maxCachedThreads = size;
		return this;
	}
	
	/**
	 * Define the MMO Threads' Priority.<br>
	 * The value should be between {@link Thread#MIN_PRIORITY} and {@link Thread#MAX_PRIORITY}.
	 * @see Thread#setPriority(int)
	 * @param priority the thread priority
	 * @return Connector<T>
	 */
	public Connector<T> threadPriority(int priority)
	{
		if ((priority > Thread.MAX_PRIORITY) || (priority < Thread.MIN_PRIORITY))
		{
			throw new IllegalArgumentException();
		}
		_config.threadPriority = priority;
		return this;
	}
	
	/**
	 * Define if the auto reading should be disabled.<br>
	 * If the auto reading is enabled the server will read the client's packet automatically.<br>
	 * Other wise the server needs to call the {@link Client#read()}] when the client packet should be read.<br>
	 * The Auto Reading is enabled by default.
	 * @param value true if auto reading should be disabled. Otherwise false.
	 * @return Connector<T>
	 */
	public Connector<T> disableAutoReading(boolean value)
	{
		_config.autoReading = !value;
		return this;
	}
	
	/**
	 * Connects to a host using the address and port.
	 * @param host the address to be connected to
	 * @param port the port of the host
	 * @return A client connected to the host and port
	 * @throws IOException if a IO error happens during the connection.
	 * @throws ExecutionException if the computation threw an exception
	 * @throws InterruptedException if the current thread was interrupted while waiting
	 */
	public T connect(String host, int port) throws IOException, ExecutionException, InterruptedException
	{
		final InetSocketAddress socketAddress;
		if ((host == null) || host.isEmpty())
		{
			socketAddress = new InetSocketAddress(port);
		}
		else
		{
			socketAddress = new InetSocketAddress(host, port);
		}
		return connect(socketAddress);
	}
	
	/**
	 * Connects to a host with socketAddress
	 * @param socketAddress the address which will be connected
	 * @return a client that represents the connection with the socketAddress
	 * @throws IOException if a IO error happens during the connection.
	 * @throws ExecutionException if the computation threw an exception
	 * @throws InterruptedException if the current thread was interrupted while waiting
	 */
	public T connect(InetSocketAddress socketAddress) throws IOException, ExecutionException, InterruptedException
	{
		createChannelGroup();
		
		final AsynchronousSocketChannel channel = GROUP.provider().openAsynchronousSocketChannel(GROUP);
		channel.connect(socketAddress).get();
		final Connection<T> connection = new Connection<>(channel, _readHandler, new WriteHandler<>(), _config.complete());
		final T client = _clientFactory.create(connection);
		connection.setClient(client);
		client.onConnected();
		client.read();
		return client;
	}
	
	private void createChannelGroup() throws IOException
	{
		if (GROUP == null)
		{
			synchronized (GROUP_LOCK)
			{
				if (GROUP == null)
				{
					if (_config.useCachedThreadPool)
					{
						final ExecutorService threadPool = new ThreadPoolExecutor(_config.threadPoolSize, _config.maxCachedThreads, 60L, TimeUnit.SECONDS, new SynchronousQueue<>(), new MMOThreadFactory("Client", _config.threadPriority));
						GROUP = AsynchronousChannelGroup.withCachedThreadPool(threadPool, _config.threadPoolSize);
					}
					else
					{
						GROUP = AsynchronousChannelGroup.withFixedThreadPool(_config.threadPoolSize, new MMOThreadFactory("Client", _config.threadPriority));
					}
				}
			}
		}
	}
}
