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
package org.l2jpx.commons.network.internal;

import java.nio.ByteBuffer;

import org.l2jpx.commons.network.ResourcePool;
import org.l2jpx.commons.network.WritableBuffer;

/**
 * An abstract base class for internal writable buffers.<br>
 * This class defines the common interface for various implementations of writable buffers within the network package.
 * @author JoeAlisson
 */
public abstract class InternalWritableBuffer extends WritableBuffer
{
	/**
	 * Gets the current buffer position.
	 * @return The current position within the buffer.
	 */
	public abstract int position();
	
	/**
	 * Sets the buffer position to a new value.
	 * @param pos The new position within the buffer.
	 */
	public abstract void position(int pos);
	
	/**
	 * Marks the end of the buffer's content.
	 */
	public abstract void mark();
	
	/**
	 * Converts the writable buffer into an array of ByteBuffers.
	 * @return An array of ByteBuffers containing the contents of the writable buffer.
	 */
	public abstract ByteBuffer[] toByteBuffers();
	
	/**
	 * Releases the resources used by the buffer.
	 */
	public abstract void releaseResources();
	
	/**
	 * Create a new Dynamic Buffer that increases as needed
	 * @param buffer the initial under layer buffer
	 * @param resourcePool the resource pool used to get new buffers when needed
	 * @return a new Dynamic Buffer
	 */
	public static InternalWritableBuffer dynamicOf(ByteBuffer buffer, ResourcePool resourcePool)
	{
		return new DynamicPacketBuffer(buffer, resourcePool);
	}
	
	/**
	 * Create a new Dynamic Buffer that increases as needed based on ArrayPacketBuffer
	 * @param buffer the base buffer
	 * @param resourcePool the resource pool used to get new buffers when needed
	 * @return a new Dynamic buffer
	 */
	public static InternalWritableBuffer dynamicOf(ArrayPacketBuffer buffer, ResourcePool resourcePool)
	{
		final DynamicPacketBuffer copy = new DynamicPacketBuffer(buffer.toByteBuffer(), resourcePool);
		copy.limit(buffer.limit());
		return copy;
	}
	
	/**
	 * Create a new buffer backed by array
	 * @param resourcePool the resource pool used to get new buffers
	 * @return a Buffer backed by array
	 */
	public static InternalWritableBuffer arrayBacked(ResourcePool resourcePool)
	{
		return new ArrayPacketBuffer(resourcePool.getSegmentSize(), resourcePool);
	}
}
