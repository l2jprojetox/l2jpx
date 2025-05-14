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
import java.util.Arrays;

import org.l2jpx.commons.network.ResourcePool;

/**
 * A dynamic packet buffer that expands as needed.<br>
 * This class manages multiple byte buffers to accommodate data that exceeds the size of individual buffers.
 * @author JoeAlisson
 */
public class DynamicPacketBuffer extends InternalWritableBuffer
{
	private PacketNode[] _nodes = new PacketNode[8];
	private PacketNode _currentNode;
	private final ResourcePool _resourcePool;
	
	private int _nodeCount;
	private int _bufferIndex;
	private int _limit;
	
	public DynamicPacketBuffer(ByteBuffer buffer, ResourcePool resourcePool)
	{
		_resourcePool = resourcePool;
		newNode(buffer, 0);
	}
	
	private void newNode(ByteBuffer buffer, int initialIndex)
	{
		if (_nodes.length == _nodeCount)
		{
			_nodes = Arrays.copyOf(_nodes, (int) ((_nodes.length + 1) * 1.2));
		}
		
		final PacketNode node = new PacketNode(buffer, initialIndex, _nodeCount);
		_nodes[_nodeCount++] = node;
		_limit = node.endIndex;
	}
	
	@Override
	public void writeByte(byte value)
	{
		ensureSize(_bufferIndex + 1);
		setByte(_bufferIndex++, value);
	}
	
	@Override
	public void writeByte(int index, byte value)
	{
		checkBounds(index, 1);
		setByte(index, value);
	}
	
	private void checkBounds(int index, int length)
	{
		if ((index < 0) || ((index + length) > _limit))
		{
			throw new IndexOutOfBoundsException("Trying access index " + index + " until index " + (index + length) + " , max accessible index is " + _limit);
		}
	}
	
	private void setByte(int index, byte value)
	{
		final PacketNode node = indexToNode(index);
		node.buffer.put(node.idx(index), value);
	}
	
	@Override
	public void writeBytes(byte... bytes)
	{
		if ((bytes == null) || (bytes.length == 0))
		{
			return;
		}
		
		ensureSize(_bufferIndex + bytes.length);
		setBytes(_bufferIndex, bytes);
		_bufferIndex += bytes.length;
	}
	
	private void setBytes(int index, byte[] bytes)
	{
		PacketNode node = indexToNode(index);
		int length = bytes.length;
		int offset = 0;
		do
		{
			int available = Math.min(length, node.endIndex - index);
			node.buffer.position(node.idx(index));
			node.buffer.put(bytes, offset, available);
			node.buffer.position(0);
			length -= available;
			offset += available;
			index += available;
			node = _nodes[Math.min(node.offset + 1, _nodes.length - 1)];
		}
		while (length > 0);
	}
	
	@Override
	public void writeShort(short value)
	{
		ensureSize(_bufferIndex + 2);
		setShort(_bufferIndex, value);
		_bufferIndex += 2;
	}
	
	@Override
	public void writeShort(int index, short value)
	{
		checkBounds(index, 2);
		setShort(index, value);
	}
	
	private void setShort(int index, short value)
	{
		final PacketNode node = indexToNode(index);
		if ((index + 2) <= node.endIndex)
		{
			node.buffer.putShort(node.idx(index), value);
		}
		else
		{
			setByte(index, (byte) value);
			setByte(index + 1, (byte) (value >>> 8));
		}
	}
	
	@Override
	public void writeChar(char value)
	{
		writeShort((short) value);
	}
	
	@Override
	public void writeInt(int value)
	{
		ensureSize(_bufferIndex + 4);
		setInt(_bufferIndex, value);
		_bufferIndex += 4;
	}
	
	@Override
	public void writeInt(int index, int value)
	{
		checkBounds(index, 4);
		setInt(index, value);
	}
	
	private void setInt(int index, int value)
	{
		final PacketNode node = indexToNode(index);
		if ((index + 4) <= node.endIndex)
		{
			node.buffer.putInt(node.idx(index), value);
		}
		else
		{
			setShort(index, (short) value);
			setShort(index + 2, (short) (value >>> 16));
		}
	}
	
	@Override
	public void writeFloat(float value)
	{
		writeInt(Float.floatToRawIntBits(value));
	}
	
	@Override
	public void writeLong(long value)
	{
		ensureSize(_bufferIndex + 8);
		setLong(_bufferIndex, value);
		_bufferIndex += 8;
	}
	
	private void setLong(int index, long value)
	{
		final PacketNode node = indexToNode(index);
		if ((index + 8) <= node.endIndex)
		{
			node.buffer.putLong(node.idx(index), value);
		}
		else
		{
			setInt(index, (int) value);
			setInt(index + 4, (int) (value >>> 32));
		}
	}
	
	@Override
	public void writeDouble(double value)
	{
		writeLong(Double.doubleToRawLongBits(value));
	}
	
	@Override
	public int position()
	{
		return _bufferIndex;
	}
	
	@Override
	public void position(int pos)
	{
		_bufferIndex = pos;
	}
	
	@Override
	public byte readByte(int index)
	{
		checkSize(index + 1);
		return getByte(index);
	}
	
	private byte getByte(int index)
	{
		final PacketNode node = indexToNode(index);
		return node.buffer.get(node.idx(index));
	}
	
	public void readBytes(int index, byte[] data)
	{
		checkSize(index + data.length);
		PacketNode node = indexToNode(index);
		int length = data.length;
		int offset = 0;
		do
		{
			int available = Math.min(length, node.endIndex - index);
			node.buffer.position(node.idx(index));
			node.buffer.get(data, offset, available);
			length -= available;
			offset += available;
			index += available;
			node = _nodes[Math.min(node.offset + 1, _nodes.length - 1)];
		}
		while (length > 0);
	}
	
	@Override
	public short readShort(int index)
	{
		checkSize(index + 2);
		return getShort(index);
	}
	
	private short getShort(int index)
	{
		final PacketNode node = indexToNode(index);
		if ((index + 2) <= node.endIndex)
		{
			return node.buffer.getShort(node.idx(index));
		}
		
		return (short) ((getByte(index) & 0xFF) | ((getByte(index + 1) & 0xFF) << 8));
	}
	
	@Override
	public int readInt(int index)
	{
		checkSize(index + 4);
		return getInt(index);
	}
	
	private int getInt(int index)
	{
		final PacketNode node = indexToNode(index);
		if ((index + 4) <= node.endIndex)
		{
			return node.buffer.getInt(node.idx(index));
		}
		
		return (getShort(index) & 0xFFFF) | ((getShort(index + 2) & 0xFFFF) << 16);
	}
	
	public float readFloat(int index)
	{
		return Float.intBitsToFloat(readInt(index));
	}
	
	public long readLong(int index)
	{
		checkSize(index + 8);
		final PacketNode node = indexToNode(index);
		if ((index + 8) <= node.endIndex)
		{
			return node.buffer.getLong(node.idx(index));
		}
		
		return (getInt(index) & 0xFFFFFFFFL) | ((getInt(index + 4) & 0xFFFFFFFFL) << 32);
	}
	
	public double readDouble(int index)
	{
		return Double.longBitsToDouble(readLong(index));
	}
	
	@Override
	public int limit()
	{
		return _limit;
	}
	
	@Override
	public void limit(int newLimit)
	{
		if (_limit != capacity())
		{
			final PacketNode node = indexToNode(_limit);
			node.buffer.clear();
		}
		ensureSize(newLimit + 1);
		_limit = newLimit;
		limitBuffer();
	}
	
	public int capacity()
	{
		return _nodes[_nodeCount - 1].endIndex;
	}
	
	@Override
	public void mark()
	{
		_limit = _bufferIndex;
		limitBuffer();
	}
	
	private void limitBuffer()
	{
		final PacketNode node = indexToNode(_limit - 1);
		node.buffer.limit(node.idx(_limit));
	}
	
	private void ensureSize(int sizeRequired)
	{
		if (capacity() < sizeRequired)
		{
			int newSize = 64;
			while (newSize < sizeRequired)
			{
				newSize <<= 1;
			}
			increaseBuffers(newSize);
		}
	}
	
	private void increaseBuffers(int size)
	{
		final int diffSize = size - capacity();
		final ByteBuffer buffer = _resourcePool.getBuffer(diffSize);
		final PacketNode lastNode = _nodes[_nodeCount - 1];
		newNode(buffer, lastNode.endIndex);
	}
	
	private void checkSize(int size)
	{
		if ((_limit < size) || (size < 0))
		{
			throw new IndexOutOfBoundsException("Trying access index " + size + ", max size is " + _limit);
		}
	}
	
	private PacketNode indexToNode(int index)
	{
		if ((_currentNode != null) && (_currentNode.initialIndex <= index) && (_currentNode.endIndex > index))
		{
			return _currentNode;
		}
		
		int min = 0;
		int max = _nodeCount - 1;
		while (min <= max)
		{
			final int mid = (min + max) >>> 1;
			final PacketNode node = _nodes[mid];
			if (index >= node.endIndex)
			{
				min = mid + 1;
			}
			else if (index < node.initialIndex)
			{
				max = mid - 1;
			}
			else
			{
				_currentNode = node;
				return node;
			}
		}
		throw new IndexOutOfBoundsException("Could not map the index to a node: " + index);
	}
	
	@Override
	public ByteBuffer[] toByteBuffers()
	{
		final int maxNode = indexToNode(_limit - 1).offset;
		final ByteBuffer[] buffers = new ByteBuffer[maxNode + 1];
		for (int i = 0; i <= maxNode; i++)
		{
			buffers[i] = _nodes[i].buffer;
		}
		return buffers;
	}
	
	@Override
	public void releaseResources()
	{
		for (int i = 0; i < _nodeCount; i++)
		{
			_resourcePool.recycleBuffer(_nodes[i].buffer);
			_nodes[i] = null;
		}
		_nodeCount = 0;
		_bufferIndex = 0;
	}
	
	private static class PacketNode
	{
		public final ByteBuffer buffer;
		public final int initialIndex;
		public final int endIndex;
		public final int offset;
		
		public PacketNode(ByteBuffer buffer, int initialIndex, int offset)
		{
			this.buffer = buffer;
			this.initialIndex = initialIndex;
			this.endIndex = initialIndex + buffer.capacity();
			this.offset = offset;
		}
		
		public int idx(int index)
		{
			return index - initialIndex;
		}
	}
}
