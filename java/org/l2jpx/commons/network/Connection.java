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
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.util.concurrent.TimeUnit;

import org.l2jpx.commons.network.internal.fairness.FairnessController;

/**
 * Represents a network connection associated with a client.<br>
 * This class encapsulates operations such as reading and writing data to and from the network channel, managing buffers, and handling network events.
 * @param <T> The type of the client associated with this connection.
 * @author JoeAlisson
 */
public class Connection<T extends Client<Connection<T>>>
{
	// private static final Logger LOGGER = Logger.getLogger(Connection.class.getName());
	
	private final AsynchronousSocketChannel _channel;
	private final ReadHandler<T> _readHandler;
	private final WriteHandler<T> _writeHandler;
	private final ConnectionConfig _config;
	private T _client;
	
	private ByteBuffer _readingBuffer;
	private ByteBuffer[] _writingBuffers;
	
	public Connection(AsynchronousSocketChannel channel, ReadHandler<T> readHandler, WriteHandler<T> writeHandler, ConnectionConfig config)
	{
		_channel = channel;
		_readHandler = readHandler;
		_writeHandler = writeHandler;
		_config = config;
	}
	
	public void setClient(T client)
	{
		_client = client;
	}
	
	public void read()
	{
		if (_channel.isOpen())
		{
			_channel.read(_readingBuffer, _client, _readHandler);
		}
	}
	
	public void readHeader()
	{
		if (_channel.isOpen())
		{
			releaseReadingBuffer();
			_readingBuffer = _config.resourcePool.getHeaderBuffer();
			read();
		}
	}
	
	public void read(int size)
	{
		if (_channel.isOpen())
		{
			_readingBuffer = _config.resourcePool.recycleAndGetNew(_readingBuffer, size);
			read();
		}
	}
	
	public boolean write(ByteBuffer[] buffers)
	{
		if (!_channel.isOpen())
		{
			return false;
		}
		
		_writingBuffers = buffers;
		write();
		return true;
	}
	
	public void write()
	{
		if (_channel.isOpen() && (_writingBuffers != null))
		{
			_channel.write(_writingBuffers, 0, _writingBuffers.length, -1, TimeUnit.MILLISECONDS, _client, _writeHandler);
		}
		else if (_client != null)
		{
			_client.finishWriting();
		}
	}
	
	public ByteBuffer getReadingBuffer()
	{
		return _readingBuffer;
	}
	
	private void releaseReadingBuffer()
	{
		if (_readingBuffer != null)
		{
			_config.resourcePool.recycleBuffer(_readingBuffer);
			_readingBuffer = null;
		}
	}
	
	public boolean releaseWritingBuffer()
	{
		boolean released = false;
		if (_writingBuffers != null)
		{
			for (ByteBuffer buffer : _writingBuffers)
			{
				_config.resourcePool.recycleBuffer(buffer);
				released = true;
			}
			_writingBuffers = null;
		}
		return released;
	}
	
	public void close()
	{
		releaseReadingBuffer();
		releaseWritingBuffer();
		try
		{
			if (_channel.isOpen())
			{
				_channel.close();
			}
		}
		catch (IOException e)
		{
			// LOGGER.log(Level.WARNING, e.getMessage(), e);
		}
		finally
		{
			_client = null;
		}
	}
	
	public String getRemoteAddress()
	{
		try
		{
			final InetSocketAddress address = (InetSocketAddress) _channel.getRemoteAddress();
			return address.getAddress().getHostAddress();
		}
		catch (IOException e)
		{
			return "";
		}
	}
	
	public boolean isOpen()
	{
		return _channel.isOpen();
	}
	
	public ResourcePool getResourcePool()
	{
		return _config.resourcePool;
	}
	
	public boolean dropPackets()
	{
		return _config.dropPackets;
	}
	
	public int dropPacketThreshold()
	{
		return _config.dropPacketThreshold;
	}
	
	public boolean isAutoReadingEnabled()
	{
		return _config.autoReading;
	}
	
	public FairnessController getFairnessController()
	{
		return _config.fairnessController;
	}
}
