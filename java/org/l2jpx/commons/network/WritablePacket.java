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

import org.l2jpx.commons.network.internal.ArrayPacketBuffer;
import org.l2jpx.commons.network.internal.InternalWritableBuffer;
import org.l2jpx.commons.network.internal.NotWrittenBufferException;

/**
 * Abstract class representing a packet that can be sent to clients.<br>
 * All data sent must include a header with 2 bytes and an optional payload.<br>
 * The first two bytes are a 16-bit integer indicating the size of the packet.
 * @param <T> The type of Client associated with this packet.
 * @author JoeAlisson
 */
public abstract class WritablePacket<T extends Client<Connection<T>>>
{
	private volatile boolean _broadcast;
	private ArrayPacketBuffer _broadcastCacheBuffer;
	
	protected WritablePacket()
	{
	}
	
	public InternalWritableBuffer writeData(T client) throws NotWrittenBufferException
	{
		if (_broadcast)
		{
			return writeDataWithCache(client);
		}
		
		return writeDataToBuffer(client);
	}
	
	private synchronized InternalWritableBuffer writeDataWithCache(T client) throws NotWrittenBufferException
	{
		if (_broadcastCacheBuffer != null)
		{
			return InternalWritableBuffer.dynamicOf(_broadcastCacheBuffer, client.getResourcePool());
		}
		
		InternalWritableBuffer buffer = writeDataToBuffer(client);
		if (buffer instanceof ArrayPacketBuffer)
		{
			_broadcastCacheBuffer = (ArrayPacketBuffer) buffer;
			buffer = InternalWritableBuffer.dynamicOf(_broadcastCacheBuffer, client.getResourcePool());
		}
		
		return buffer;
	}
	
	private InternalWritableBuffer writeDataToBuffer(T client) throws NotWrittenBufferException
	{
		final InternalWritableBuffer buffer = choosePacketBuffer(client);
		buffer.position(ConnectionConfig.HEADER_SIZE);
		if (write(client, buffer))
		{
			buffer.mark();
			return buffer;
		}
		
		buffer.releaseResources();
		throw new NotWrittenBufferException();
	}
	
	private InternalWritableBuffer choosePacketBuffer(T client)
	{
		if (_broadcast)
		{
			return InternalWritableBuffer.arrayBacked(client.getResourcePool());
		}
		
		return InternalWritableBuffer.dynamicOf(client.getResourcePool().getSegmentBuffer(), client.getResourcePool());
	}
	
	public void writeHeader(InternalWritableBuffer buffer, int header)
	{
		buffer.writeShort(0, (short) header);
	}
	
	/**
	 * Mark this packet as broadcast. A broadcast packet is sent to more than one client.<br>
	 * Caution: This method should be called before {@link Client#writePacket(WritablePacket)}.<br>
	 * A broadcast packet will create a Buffer cache where the data is written once and only the copy is sent to the client. note: Each copy will be encrypted to each client
	 * @param broadcast true if the packet is sent to more than one client
	 */
	public void sendInBroadcast(boolean broadcast)
	{
		_broadcast = broadcast;
	}
	
	/**
	 * If this method returns true, the packet will be considered disposable.
	 * @param client client to send data to
	 * @return if the packet is disposable or not.
	 */
	public boolean canBeDropped(T client)
	{
		return false;
	}
	
	/**
	 * Writes the data to the buffer for the specified client.<br>
	 * This abstract method should be implemented in subclasses to define the specific packet writing logic.
	 * @param client The client to whom the packet is being sent.
	 * @param buffer The writable buffer where the packet data is written.
	 * @return true if the packet was successfully written, false otherwise.
	 */
	protected abstract boolean write(T client, WritableBuffer buffer);
	
	@Override
	public String toString()
	{
		return getClass().getSimpleName();
	}
}
