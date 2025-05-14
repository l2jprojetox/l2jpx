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
import java.nio.ByteOrder;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Represents a pool of ByteBuffer objects for reuse, to avoid frequent allocation and deallocation.<br>
 * Buffers are managed in a concurrent queue with a maximum size limit.
 * @author JoeAlisson
 */
public class BufferPool
{
	private final Queue<ByteBuffer> _buffers = new ConcurrentLinkedQueue<>();
	private final int _maxSize;
	private final int _bufferSize;
	private int _estimateSize;
	
	/**
	 * Create a Buffer Pool
	 * @param maxSize the pool max size
	 * @param bufferSize the size of the buffers kept in Buffer Pool
	 */
	public BufferPool(int maxSize, int bufferSize)
	{
		_maxSize = maxSize;
		_bufferSize = bufferSize;
	}
	
	/**
	 * Initializes the buffer pool by pre-allocating a certain number of ByteBuffers.<br>
	 * The number of buffers allocated is determined by the specified factor and the maximum size of the pool.
	 * @param factor The factor used to determine the initial number of ByteBuffers to allocate.
	 */
	public void initialize(float factor)
	{
		final int amount = (int) Math.min(_maxSize, _maxSize * factor);
		for (int i = 0; i < amount; i++)
		{
			_buffers.offer(ByteBuffer.allocateDirect(_bufferSize).order(ByteOrder.LITTLE_ENDIAN));
		}
		_estimateSize = amount;
	}
	
	/**
	 * Attempts to recycle a ByteBuffer back into the pool.<br>
	 * If the pool size has not reached its maximum, the buffer is added back to the pool; otherwise, it is discarded.
	 * @param buffer The ByteBuffer to be recycled.
	 * @return true if the buffer was successfully recycled, false otherwise.
	 */
	public boolean recycle(ByteBuffer buffer)
	{
		final boolean recycle = _estimateSize < _maxSize;
		if (recycle)
		{
			_buffers.offer(buffer.clear());
			_estimateSize++;
		}
		return recycle;
	}
	
	/**
	 * Retrieves a ByteBuffer from the pool.<br>
	 * Returns a ByteBuffer if available, or null if the pool is empty.
	 * @return A ByteBuffer from the pool, or null if none are available.
	 */
	public ByteBuffer get()
	{
		_estimateSize--;
		return _buffers.poll();
	}
	
	@Override
	public String toString()
	{
		return "Pool {maxSize=" + _maxSize + ", bufferSize=" + _bufferSize + ", estimateUse=" + _estimateSize + '}';
	}
}
