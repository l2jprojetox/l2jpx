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

import java.nio.charset.StandardCharsets;

/**
 * Represents a packet received from the client.<br>
 * All received data must have a header with 2 bytes and an optional payload.<br>
 * The first and second bytes are a 16-bit integer holding the size of the packet.
 * @param <T> The type of Client associated with this packet.
 * @author JoeAlisson, Mobius
 */
public abstract class ReadablePacket<T extends Client<Connection<T>>> implements Runnable
{
	private ReadableBuffer _buffer;
	private T _client;
	
	protected ReadablePacket()
	{
		// No direct instances.
	}
	
	public void init(T client, ReadableBuffer buffer)
	{
		_client = client;
		_buffer = buffer;
	}
	
	/**
	 * Reads <b>char</b> from the buffer.<br>
	 * @return The char read.
	 */
	protected char readChar()
	{
		return _buffer.readChar();
	}
	
	/**
	 * Reads a <b>byte from the buffer.
	 * @return The byte read.
	 */
	protected byte readByte()
	{
		return _buffer.readByte();
	}
	
	/**
	 * Reads a <b>char</b> value from the buffer.<br>
	 * 16-bit integer (00 00)
	 * @return The char value read.
	 */
	protected int readUnsignedByte()
	{
		return Byte.toUnsignedInt(readByte());
	}
	
	/**
	 * Reads a <b>byte</b> from the buffer.
	 * @return true if byte does not equal 0.
	 */
	protected boolean readBoolean()
	{
		return readByte() != 0;
	}
	
	/**
	 * Reads and returns an array of bytes of the specified length from the internal buffer.
	 * @param length The given length of bytes to be read.
	 * @return A byte array containing the read bytes.
	 */
	protected byte[] readBytes(int length)
	{
		final byte[] result = new byte[length];
		_buffer.readBytes(result, 0, length);
		return result;
	}
	
	/**
	 * Reads as many bytes as the length of the array.
	 * @param dst The byte array which will be filled with the data.
	 */
	protected void readBytes(byte[] dst)
	{
		_buffer.readBytes(dst, 0, dst.length);
	}
	
	/**
	 * Reads bytes into the specified byte array, starting at the given offset and reading up to the specified length.
	 * @param dst The byte array to fill with data.
	 * @param offset The starting offset in the array.
	 * @param length The number of bytes to read.
	 */
	protected void readBytes(byte[] dst, int offset, int length)
	{
		_buffer.readBytes(dst, offset, length);
	}
	
	/**
	 * Reads a <b>short</b> value from the buffer.<br>
	 * 16-bit integer (00 00)
	 * @return The short value read.
	 */
	protected short readShort()
	{
		return _buffer.readShort();
	}
	
	/**
	 * Reads an <b>int</b> value from the buffer.<br>
	 * 32-bit integer (00 00 00 00)
	 * @return The int value read.
	 */
	protected int readInt()
	{
		return _buffer.readInt();
		
	}
	
	/**
	 * Reads a <b>long</b> value from the buffer.<br>
	 * 64-bit integer (00 00 00 00 00 00 00 00)
	 * @return The long value read.
	 */
	protected long readLong()
	{
		return _buffer.readLong();
	}
	
	/**
	 * Reads a <b>float</b> value from the buffer.<br>
	 * 32-bit float (00 00 00 00)
	 * @return The float value read.
	 */
	protected float readFloat()
	{
		return _buffer.readFloat();
	}
	
	/**
	 * Reads a <b>double</b> value from the buffer.<br>
	 * 64-bit float (00 00 00 00 00 00 00 00)
	 * @return The double value read.
	 */
	protected double readDouble()
	{
		return _buffer.readDouble();
	}
	
	/**
	 * Reads a <b>String</b> from the buffer.
	 * @return String read
	 */
	protected String readString()
	{
		final StringBuilder result = new StringBuilder();
		try
		{
			int charId;
			while ((charId = readShort()) != 0)
			{
				result.append((char) charId);
			}
		}
		catch (Exception ignored)
		{
		}
		return result.toString();
	}
	
	/**
	 * Reads a predefined length <b>String</b> from the buffer.
	 * @return String read
	 */
	protected String readSizedString()
	{
		String result = "";
		try
		{
			result = new String(readBytes(readShort() * 2), StandardCharsets.UTF_16LE);
		}
		catch (Exception ignored)
		{
		}
		return result;
	}
	
	/**
	 * Returns the number of remaining bytes available for reading.
	 * @return The number of remaining bytes.
	 */
	protected int remaining()
	{
		return _buffer.remaining();
	}
	
	/**
	 * Gets the client associated with this packet.
	 * @return The client instance.
	 */
	public T getClient()
	{
		return _client;
	}
	
	/**
	 * Reads the data from the buffer and processes it.<br>
	 * This method must be implemented to define the packet's reading logic.
	 * @return true if the packet was read successfully, false otherwise.
	 */
	protected abstract boolean read();
}
