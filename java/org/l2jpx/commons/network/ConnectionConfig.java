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
import java.io.InputStream;
import java.net.SocketAddress;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.l2jpx.commons.network.internal.BufferPool;
import org.l2jpx.commons.network.internal.fairness.FairnessController;

/**
 * Configures and initializes connection parameters for the network module.<br>
 * This class handles configuration settings, buffer pools, and network properties.
 * @author JoeAlisson
 */
public class ConnectionConfig
{
	public static final int HEADER_SIZE = 2;
	
	private static final int MINIMUM_POOL_GROUPS = 3;
	private static final Pattern BUFFER_POOL_PROPERTY = Pattern.compile("(BufferPool\\.\\w+?\\.)Size", Pattern.CASE_INSENSITIVE);
	
	public ResourcePool resourcePool;
	public ConnectionFilter acceptFilter;
	public SocketAddress address;
	
	public float initBufferPoolFactor;
	public long shutdownWaitTime = 5000;
	public int threadPoolSize;
	public boolean useNagle;
	public boolean dropPackets;
	public int dropPacketThreshold = 250;
	public boolean useCachedThreadPool;
	public int maxCachedThreads = Integer.MAX_VALUE;
	public int threadPriority = Thread.NORM_PRIORITY;
	public boolean autoReading = true;
	public int fairnessBuckets = 1;
	public FairnessController fairnessController;
	
	ConnectionConfig(SocketAddress socketAddress)
	{
		address = socketAddress;
		threadPoolSize = Math.max(2, Runtime.getRuntime().availableProcessors() - 2);
		resourcePool = new ResourcePool();
		resourcePool.addBufferPool(HEADER_SIZE, new BufferPool(100, HEADER_SIZE));
		loadProperties("config/Network.ini");
	}
	
	private void loadProperties(String propertyFileName)
	{
		try (InputStream inputStream = resolvePropertyFile(propertyFileName))
		{
			if (inputStream != null)
			{
				Properties properties = new Properties();
				properties.load(inputStream);
				configure(properties);
			}
			else
			{
				throw new IllegalArgumentException("Cannot find property file: " + propertyFileName);
			}
		}
		catch (IOException e)
		{
			throw new IllegalArgumentException("Failed to read property file", e);
		}
	}
	
	private InputStream resolvePropertyFile(String propertyFileName) throws IOException
	{
		final Path path = Paths.get(propertyFileName);
		if (Files.isRegularFile(path))
		{
			return Files.newInputStream(path);
		}
		
		final InputStream stream = ClassLoader.getSystemResourceAsStream(propertyFileName);
		return stream != null ? stream : getClass().getResourceAsStream(propertyFileName);
	}
	
	private void configure(Properties properties)
	{
		shutdownWaitTime = parseInt(properties, "ShutdownWaitTime", 5) * 1000L;
		useCachedThreadPool = parseBoolean(properties, "UseCachedThreadPool", useCachedThreadPool);
		threadPoolSize = Math.max(1, parseInt(properties, "ThreadPoolSize", threadPoolSize));
		maxCachedThreads = parseInt(properties, "MaxCachedThreads", maxCachedThreads);
		threadPriority = parseInt(properties, "ThreadPriority", threadPriority);
		initBufferPoolFactor = parseFloat(properties, "BufferPool.InitFactor", 0);
		dropPackets = parseBoolean(properties, "DropPackets", dropPackets);
		dropPacketThreshold = parseInt(properties, "DropPacketThreshold", 250);
		resourcePool.setBufferSegmentSize(parseInt(properties, "BufferSegmentSize", resourcePool.getSegmentSize()));
		fairnessBuckets = parseInt(properties, "FairnessBuckets", fairnessBuckets);
		autoReading = parseBoolean(properties, "AutoReading", autoReading);
		
		properties.stringPropertyNames().forEach(property ->
		{
			final Matcher matcher = BUFFER_POOL_PROPERTY.matcher(property);
			if (matcher.matches())
			{
				final int size = parseInt(properties, property, 10);
				final int bufferSize = parseInt(properties, matcher.group(1) + "BufferSize", 1024);
				newBufferGroup(size, bufferSize);
			}
		});
		
		newBufferGroup(100, resourcePool.getSegmentSize());
	}
	
	private boolean parseBoolean(Properties properties, String propertyName, boolean defaultValue)
	{
		try
		{
			return Boolean.parseBoolean(properties.getProperty(propertyName));
		}
		catch (Exception e)
		{
			return defaultValue;
		}
	}
	
	private int parseInt(Properties properties, String propertyName, int defaultValue)
	{
		try
		{
			return Integer.parseInt(properties.getProperty(propertyName));
		}
		catch (Exception e)
		{
			return defaultValue;
		}
	}
	
	private float parseFloat(Properties properties, String propertyName, float defaultValue)
	{
		try
		{
			return Float.parseFloat(properties.getProperty(propertyName));
		}
		catch (Exception e)
		{
			return defaultValue;
		}
	}
	
	public void newBufferGroup(int groupSize, int bufferSize)
	{
		resourcePool.addBufferPool(bufferSize, new BufferPool(groupSize, bufferSize));
	}
	
	public ConnectionConfig complete()
	{
		completeBuffersPool();
		resourcePool.initializeBuffers(initBufferPoolFactor);
		fairnessController = FairnessController.init(fairnessBuckets);
		return this;
	}
	
	private void completeBuffersPool()
	{
		final int missingPools = MINIMUM_POOL_GROUPS - resourcePool.bufferPoolSize();
		for (int i = 0; i < missingPools; i++)
		{
			int bufferSize = 256 << i;
			resourcePool.addBufferPool(bufferSize, new BufferPool(10, bufferSize));
		}
	}
}
