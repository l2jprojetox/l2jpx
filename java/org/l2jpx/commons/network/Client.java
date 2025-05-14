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

import java.util.Collection;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;

import org.l2jpx.commons.network.internal.InternalWritableBuffer;
import org.l2jpx.commons.network.internal.NotWrittenBufferException;

/**
 * Represents a generic client in a network context.<br>
 * This abstract class provides the foundation for managing connections, sending and receiving data packets, and handling connection states.<br>
 * It requires implementation of encryption, decryption, and connection management methods.
 * @param <T> The type of Connection associated with this client.
 * @author JoeAlisson
 */
public abstract class Client<T extends Connection<?>>
{
	// private static final Logger LOGGER = Logger.getLogger(Client.class.getName());
	
	private final T _connection;
	private final Queue<WritablePacket<? extends Client<T>>> _packetsToWrite = new ConcurrentLinkedQueue<>();
	private final AtomicBoolean _writing = new AtomicBoolean(false);
	private final AtomicBoolean _disconnecting = new AtomicBoolean(false);
	private int _estimateQueueSize = 0;
	private int _dataSentSize;
	private volatile boolean _isClosing;
	private boolean _readingPayload;
	private int _expectedReadSize;
	private final AtomicBoolean _readNext = new AtomicBoolean(false);
	
	public final AtomicBoolean isReading = new AtomicBoolean(false);
	
	/**
	 * Constructs a new Client using the specified connection.<br>
	 * Throws IllegalArgumentException if the connection is null or closed.
	 * @param connection The Connection to the client.
	 */
	protected Client(T connection)
	{
		if ((connection == null) || !connection.isOpen())
		{
			throw new IllegalArgumentException("The Connection is null or closed");
		}
		
		_connection = connection;
	}
	
	/**
	 * Sends a packet to this client. If another packet is been sent to this client, the actual packet is put on a queue to be sent after all previous packets. Otherwise the packet is sent immediately.
	 * @param packet to be sent.
	 */
	protected void writePacket(WritablePacket<? extends Client<T>> packet)
	{
		if (!isConnected() || (packet == null) || packetCanBeDropped(packet))
		{
			return;
		}
		
		_estimateQueueSize++;
		_packetsToWrite.add(packet);
		writeFairPacket();
	}
	
	@SuppressWarnings(
	{
		"unchecked",
		"rawtypes"
	})
	private boolean packetCanBeDropped(WritablePacket packet)
	{
		return _connection.dropPackets() && (_estimateQueueSize > _connection.dropPacketThreshold()) && packet.canBeDropped(this);
	}
	
	protected void writePackets(Collection<WritablePacket<? extends Client<T>>> packets)
	{
		if (!isConnected() || (packets == null) || packets.isEmpty())
		{
			return;
		}
		
		_estimateQueueSize += packets.size();
		_packetsToWrite.addAll(packets);
		writeFairPacket();
	}
	
	private void writeFairPacket()
	{
		if (_writing.compareAndSet(false, true))
		{
			_connection.getFairnessController().nextFairAction(this, Client::writeNextPacket);
		}
	}
	
	private void writeNextPacket()
	{
		WritablePacket<? extends Client<T>> packet = _packetsToWrite.poll();
		if (packet == null)
		{
			releaseWritingResource();
			// LOGGER.info("There is no packet to send.");
			if (_isClosing)
			{
				disconnect();
			}
		}
		else
		{
			_estimateQueueSize--;
			write(packet);
		}
	}
	
	@SuppressWarnings(
	{
		"unchecked",
		"rawtypes"
	})
	private void write(WritablePacket packet)
	{
		boolean written = false;
		InternalWritableBuffer buffer = null;
		try
		{
			buffer = packet.writeData(this);
			
			final int payloadSize = buffer.limit() - ConnectionConfig.HEADER_SIZE;
			if (payloadSize <= 0)
			{
				return;
			}
			
			if (encrypt(buffer, ConnectionConfig.HEADER_SIZE, payloadSize))
			{
				_dataSentSize = buffer.limit();
				
				if (_dataSentSize <= ConnectionConfig.HEADER_SIZE)
				{
					return;
				}
				
				packet.writeHeader(buffer, _dataSentSize);
				written = _connection.write(buffer.toByteBuffers());
				// LOGGER.info("Sending packet " + packet + "[" + _dataSentSize + "] to " + this);
			}
		}
		catch (NotWrittenBufferException ignored)
		{
			// LOGGER.info("packet was not written " + packet + " to " + this);
		}
		catch (Exception e)
		{
			// LOGGER.info("Error while " + this + " writing " + packet + " " + e);
		}
		finally
		{
			if (!written)
			{
				handleNotWritten(buffer);
			}
		}
	}
	
	private void handleNotWritten(InternalWritableBuffer buffer)
	{
		if (!releaseWritingResource() && (buffer != null))
		{
			buffer.releaseResources();
		}
		if (isConnected())
		{
			writeFairPacket();
		}
	}
	
	public void read()
	{
		if (isReading.get())
		{
			_readNext.set(true);
			return;
		}
		
		isReading.set(true);
		_expectedReadSize = ConnectionConfig.HEADER_SIZE;
		_readingPayload = false;
		_connection.readHeader();
	}
	
	public void readPayload(int dataSize)
	{
		_expectedReadSize = dataSize;
		_readingPayload = true;
		_connection.read(dataSize);
	}
	
	public void readNextPacket()
	{
		if (isReading.get())
		{
			_readNext.set(true);
		}
		else
		{
			read();
		}
	}
	
	/**
	 * close the underlying Connection to the client.<br>
	 * All pending packets are cancelled.
	 */
	public void close()
	{
		close(null);
	}
	
	/**
	 * Sends the packet and close the underlying Connection to the client.<br>
	 * All others pending packets are cancelled.
	 * @param packet to be sent before the connection is closed.
	 */
	public void close(WritablePacket<? extends Client<T>> packet)
	{
		if (!isConnected())
		{
			return;
		}
		
		_packetsToWrite.clear();
		if (packet != null)
		{
			_packetsToWrite.add(packet);
		}
		_isClosing = true;
		// LOGGER.info("Closing client connection " + this + " with packet " + packet);
		writeFairPacket();
	}
	
	public void resumeSend(long result)
	{
		_dataSentSize -= result;
		_connection.write();
	}
	
	public void finishWriting()
	{
		_connection.releaseWritingBuffer();
		_connection.getFairnessController().nextFairAction(this, Client::writeNextPacket);
	}
	
	private boolean releaseWritingResource()
	{
		final boolean released = _connection.releaseWritingBuffer();
		_writing.set(false);
		return released;
	}
	
	public void disconnect()
	{
		if (_disconnecting.compareAndSet(false, true))
		{
			try
			{
				// LOGGER.info("Client " + this + " disconnecting");
				onDisconnection();
			}
			finally
			{
				_packetsToWrite.clear();
				_connection.close();
			}
		}
	}
	
	public T getConnection()
	{
		return _connection;
	}
	
	public int getDataSentSize()
	{
		return _dataSentSize;
	}
	
	/**
	 * @return The client's IP address.
	 */
	public String getHostAddress()
	{
		return _connection == null ? "" : _connection.getRemoteAddress();
	}
	
	/**
	 * @return if client still connected
	 */
	public boolean isConnected()
	{
		return _connection.isOpen() && !_isClosing;
	}
	
	/**
	 * @return the estimate amount of packet queued to send
	 */
	public int getEstimateQueueSize()
	{
		return _estimateQueueSize;
	}
	
	public ResourcePool getResourcePool()
	{
		return _connection.getResourcePool();
	}
	
	public boolean isReadingPayload()
	{
		return _readingPayload;
	}
	
	public void resumeRead(int bytesRead)
	{
		_expectedReadSize -= bytesRead;
		_connection.read();
	}
	
	public int getExpectedReadSize()
	{
		return _expectedReadSize;
	}
	
	public boolean canReadNextPacket()
	{
		return _connection.isAutoReadingEnabled() || _readNext.getAndSet(false);
	}
	
	/**
	 * Encrypt the data in-place.
	 * @param data - the data to be encrypted
	 * @param offset - the initial index to be encrypted
	 * @param size - the length of data to be encrypted
	 * @return if data was encrypted
	 */
	public abstract boolean encrypt(Buffer data, int offset, int size);
	
	/**
	 * Decrypt the data in-place
	 * @param data - data to be decrypted
	 * @param offset - the initial index to be encrypted.
	 * @param size - the length of data to be encrypted.
	 * @return if the data was decrypted.
	 */
	public abstract boolean decrypt(Buffer data, int offset, int size);
	
	/**
	 * Handles the client's disconnection.<br>
	 * This method must save all data and release all resources related to the client.<br>
	 * No more packet can be sent after this method is called.
	 */
	protected abstract void onDisconnection();
	
	/**
	 * Handles the client's connection.<br>
	 * This method should not use blocking operations.<br>
	 * The Packets can be sent only after this method is called.
	 */
	public abstract void onConnected();
}
