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

/**
 * Responsible for building {@link ConnectionHandler} instances to manage all incoming connections.<br>
 * This class provides a fluent interface for configuring connection parameters and constructing a connection handler.
 * @param <T> The type of the client that will be associated with the connections.
 * @author JoeAlisson
 */
public class ConnectionBuilder<T extends Client<Connection<T>>>
{
	private final ConnectionConfig _config;
	private final ReadHandler<T> _readerHandler;
	private final ClientFactory<T> _clientFactory;
	
	/**
	 * Constructor for ConnectionBuilder.<br>
	 * Initializes a new builder with the specified parameters.
	 * @param address The socket address to listen to the incoming connections.
	 * @param clientFactory The factory responsible for creating a new Client when a new connection is accepted.
	 * @param packetHandler The handler responsible for converting the data received into a {@link ReadablePacket}.
	 * @param executor Responsible for executing the incoming packets.
	 */
	public ConnectionBuilder(InetSocketAddress address, ClientFactory<T> clientFactory, PacketHandler<T> packetHandler, PacketExecutor<T> executor)
	{
		_config = new ConnectionConfig(address);
		_readerHandler = new ReadHandler<>(packetHandler, executor);
		_clientFactory = clientFactory;
	}
	
	/**
	 * Sets a filter to be used on incoming connections.<br>
	 * The filter must decide if a connection is acceptable or not.
	 * @param filter - the {@link ConnectionFilter} to be set.
	 * @return
	 */
	public ConnectionBuilder<T> filter(ConnectionFilter filter)
	{
		_config.acceptFilter = filter;
		return this;
	}
	
	/**
	 * Configures the connection to use CachedThreadPool as defined in {@link java.nio.channels.AsynchronousChannelGroup#withCachedThreadPool(java.util.concurrent.ExecutorService, int)}. The default behaviour is to use a fixed thread poll as defined in
	 * {@link java.nio.channels.AsynchronousChannelGroup#withFixedThreadPool(int, java.util.concurrent.ThreadFactory)}.
	 * @param cached use a cached thread pool if true, otherwise use fixed thread pool
	 * @return this
	 */
	public ConnectionBuilder<T> useCachedThreadPool(boolean cached)
	{
		_config.useCachedThreadPool = cached;
		return this;
	}
	
	/**
	 * Set the size of the threadPool used to manage the connections and data sending.<br>
	 * If the thread pool is cached this method defines the corePoolSize of ({@link java.util.concurrent.ThreadPoolExecutor}).<br>
	 * If the thread pool is fixed this method defines the amount of threads.<br>
	 * The min accepted value is 1.<br>
	 * The default value is the quantity of available processors minus 2.
	 * @param size - the size of thread pool to be Set
	 * @return this
	 */
	public ConnectionBuilder<T> threadPoolSize(int size)
	{
		_config.threadPoolSize = size;
		return this;
	}
	
	/**
	 * Set the size of max threads allowed in the cached thread pool.<br>
	 * The execution we be rejected when all the threads in the cached thread pool is busy after reaching the max thread allowed.<br>
	 * This config is ignored when a fixed thread pool is used.
	 * @param size the max cached threads in the cached thread pool.
	 * @return this
	 */
	public ConnectionBuilder<T> maxCachedThreads(int size)
	{
		_config.maxCachedThreads = size;
		return this;
	}
	
	/**
	 * Defines if small outgoing packets must be combined to be sent all at once. This improves the network performance, but can cause lags on clients waiting for the packet. The default value is false.
	 * @param useNagle - true if the Nagle's algorithm must be used.
	 * @return
	 */
	public ConnectionBuilder<T> useNagle(boolean useNagle)
	{
		_config.useNagle = useNagle;
		return this;
	}
	
	/**
	 * Sets the shutdown wait time in milliseconds.<br>
	 * The default value is 5 seconds.
	 * @param waitTime - the wait time to close all connections resources after a {@link ConnectionHandler#shutdown()} is called.
	 * @return this
	 */
	public ConnectionBuilder<T> shutdownWaitTime(long waitTime)
	{
		_config.shutdownWaitTime = waitTime;
		return this;
	}
	
	/**
	 * Add a new {@link java.nio.ByteBuffer} grouping pool
	 * @param size the max amount of {@link java.nio.ByteBuffer} supported
	 * @param bufferSize the {@link java.nio.ByteBuffer}'s size inside the pool.
	 * @return this
	 */
	public ConnectionBuilder<T> addBufferPool(int size, int bufferSize)
	{
		_config.newBufferGroup(size, bufferSize);
		return this;
	}
	
	/**
	 * Define the factor of pre-initialized {@link java.nio.ByteBuffer} inside a pool.
	 * @param factor the factor of initialized buffers
	 * @return this
	 */
	public ConnectionBuilder<T> initBufferPoolFactor(float factor)
	{
		_config.initBufferPoolFactor = factor;
		return this;
	}
	
	/**
	 * Define the size of dynamic buffer's segment. A segment is used to increase the Buffer when needed.
	 * @param size of dynamic buffer segment
	 * @return this
	 */
	public ConnectionBuilder<T> bufferSegmentSize(int size)
	{
		_config.resourcePool.setBufferSegmentSize(size);
		return this;
	}
	
	/**
	 * Define if drop packets is enabled.
	 * @param enabled if drop packets is enabled. The default value is false
	 * @return this
	 */
	public ConnectionBuilder<T> dropPackets(boolean enabled)
	{
		_config.dropPackets = enabled;
		return this;
	}
	
	/**
	 * Define the threshold to allow the client to drop disposable packets.<br>
	 * When the client has queued more than {@code threshold} disposable packets will be disposed.
	 * @param threshold the minimum value to drop packets. The default value is 250
	 * @return this
	 */
	public ConnectionBuilder<T> dropPacketThreshold(int threshold)
	{
		_config.dropPacketThreshold = threshold;
		return this;
	}
	
	/**
	 * Define the MMO Threads' Priority.<br>
	 * The value should be between {@link Thread#MIN_PRIORITY} and {@link Thread#MAX_PRIORITY}.
	 * @see Thread#setPriority(int)
	 * @param priority the thread priority
	 * @return ConnectionBuilder<T>
	 */
	public ConnectionBuilder<T> threadPriority(int priority)
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
	 * if the auto reading is enabled the server will read the client's packet automatically.<br>
	 * Otherwise, the server needs to call the {@link Client#read()}] when the client packet should be read.<br>
	 * The Auto Reading is enabled by default
	 * @param value true if auto reading should be disabled. Otherwise false.
	 * @return ConnectionBuilder<T>
	 */
	public ConnectionBuilder<T> disableAutoReading(boolean value)
	{
		_config.autoReading = !value;
		return this;
	}
	
	/**
	 * Define the amount of buckets used in the fairness controller.<br>
	 * The fairness controller is a mechanism used to maintain the network threads fairness among the clients.<br>
	 * The default value is 1, is recommended to increase this value depending on high concurrency networks applications.
	 * @param buckets the amount of buckets to use
	 * @return this
	 */
	public ConnectionBuilder<T> fairnessBuckets(int buckets)
	{
		_config.fairnessBuckets = buckets;
		return this;
	}
	
	/**
	 * Builds a new ConnectionHandler based on the options configured.
	 * @return a ConnectionHandler
	 * @throws IOException If the Socket Address configured can't be used.
	 */
	public ConnectionHandler<T> build() throws IOException
	{
		return new ConnectionHandler<>(_config.complete(), _clientFactory, _readerHandler);
	}
}
