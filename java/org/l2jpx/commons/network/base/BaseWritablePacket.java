package org.l2jpx.commons.network.base;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;

/**
 * Abstract class for writable packets backed by a byte array, with a maximum raw data size of 65533 bytes.<br>
 * Provides methods to write various types of data to the packet.
 * @author Pantelis Andrianakis
 * @since October 29th 2020
 */
public abstract class BaseWritablePacket
{
	private byte[] _data;
	private byte[] _sendableBytes;
	private int _position = 2; // Allocate space for size (max length 65535 - size header).
	
	/**
	 * Construct a WritablePacket with an initial data size of 32 bytes.
	 */
	protected BaseWritablePacket()
	{
		this(32);
	}
	
	/**
	 * Construct a WritablePacket with a given initial data size.
	 * @param initialSize
	 */
	protected BaseWritablePacket(int initialSize)
	{
		_data = new byte[initialSize];
	}
	
	public void write(byte value)
	{
		// Check current size.
		if (_position < 65535)
		{
			// Check capacity.
			if (_position == _data.length)
			{
				_data = Arrays.copyOf(_data, _data.length * 2); // Double the capacity.
			}
			
			// Set value.
			_data[_position++] = value;
			return;
		}
		
		throw new IndexOutOfBoundsException("Packet data exceeded the raw data size limit of 65533!");
	}
	
	/**
	 * Write <b>boolean</b> to the packet data.<br>
	 * 8bit integer (00) or (01)
	 * @param value
	 */
	public void writeBoolean(boolean value)
	{
		writeByte(value ? 1 : 0);
	}
	
	/**
	 * Write <b>String</b> to the packet data.
	 * @param text
	 */
	public void writeString(String text)
	{
		if (text != null)
		{
			writeBytes(text.getBytes(StandardCharsets.UTF_16LE));
		}
		writeShort(0);
	}
	
	/**
	 * Write <b>String</b> with fixed size specified as (short size, char[size]) to the packet data.
	 * @param text
	 */
	public void writeSizedString(String text)
	{
		if (text != null)
		{
			writeShort(text.length());
			writeBytes(text.getBytes(StandardCharsets.UTF_16LE));
		}
		else
		{
			writeShort(0);
		}
	}
	
	/**
	 * Write <b>byte[]</b> to the packet data.<br>
	 * 8bit integer array (00...)
	 * @param array
	 */
	public void writeBytes(byte[] array)
	{
		for (int i = 0; i < array.length; i++)
		{
			write(array[i]);
		}
	}
	
	/**
	 * Write <b>byte</b> to the packet data.<br>
	 * 8bit integer (00)
	 * @param value
	 */
	public void writeByte(int value)
	{
		write((byte) (value & 0xff));
	}
	
	/**
	 * Write <b>boolean</b> to the packet data.<br>
	 * 8bit integer (00) or (01)
	 * @param value
	 */
	public void writeByte(boolean value)
	{
		writeByte(value ? 1 : 0);
	}
	
	/**
	 * Write <b>short</b> to the packet data.<br>
	 * 16bit integer (00 00)
	 * @param value
	 */
	public void writeShort(int value)
	{
		write((byte) (value & 0xff));
		write((byte) ((value >> 8) & 0xff));
	}
	
	/**
	 * Write <b>boolean</b> to the packet data.<br>
	 * 16bit integer (00 00)
	 * @param value
	 */
	public void writeShort(boolean value)
	{
		writeShort(value ? 1 : 0);
	}
	
	/**
	 * Write <b>int</b> to the packet data.<br>
	 * 32bit integer (00 00 00 00)
	 * @param value
	 */
	public void writeInt(int value)
	{
		write((byte) (value & 0xff));
		write((byte) ((value >> 8) & 0xff));
		write((byte) ((value >> 16) & 0xff));
		write((byte) ((value >> 24) & 0xff));
	}
	
	/**
	 * Write <b>boolean</b> to the packet data.<br>
	 * 32bit integer (00 00 00 00)
	 * @param value
	 */
	public void writeInt(boolean value)
	{
		writeInt(value ? 1 : 0);
	}
	
	/**
	 * Write <b>long</b> to the packet data.<br>
	 * 64bit integer (00 00 00 00 00 00 00 00)
	 * @param value
	 */
	public void writeLong(long value)
	{
		write((byte) (value & 0xff));
		write((byte) ((value >> 8) & 0xff));
		write((byte) ((value >> 16) & 0xff));
		write((byte) ((value >> 24) & 0xff));
		write((byte) ((value >> 32) & 0xff));
		write((byte) ((value >> 40) & 0xff));
		write((byte) ((value >> 48) & 0xff));
		write((byte) ((value >> 56) & 0xff));
	}
	
	/**
	 * Write <b>boolean</b> to the packet data.<br>
	 * 64bit integer (00 00 00 00 00 00 00 00)
	 * @param value
	 */
	public void writeLong(boolean value)
	{
		writeLong(value ? 1 : 0);
	}
	
	/**
	 * Write <b>float</b> to the packet data.<br>
	 * 32bit single precision float (00 00 00 00)
	 * @param value
	 */
	public void writeFloat(float value)
	{
		writeInt(Float.floatToRawIntBits(value));
	}
	
	/**
	 * Write <b>double</b> to the packet data.<br>
	 * 64bit double precision float (00 00 00 00 00 00 00 00)
	 * @param value
	 */
	public void writeDouble(double value)
	{
		writeLong(Double.doubleToRawLongBits(value));
	}
	
	/**
	 * Can be overridden to write data after packet has initialized.<br>
	 * Called when getSendableBytes generates data, ensures that the data are processed only once.
	 */
	public void write()
	{
		// Overridden by server implementation.
	}
	
	/**
	 * Returns the byte array containing the packet's data, including the size header.<br>
	 * This method should be called after all data has been written to the packet.
	 * @return Byte array of the sendable packet data.
	 */
	public synchronized byte[] getSendableBytes()
	{
		// Generate sendable byte array.
		if (_sendableBytes == null /* Not processed */)
		{
			// Write packet implementation (only once).
			if (_position == 2)
			{
				write();
			}
			
			// Check if data was written.
			if (_position > 2)
			{
				// Trim array of data.
				_sendableBytes = Arrays.copyOf(_data, _position);
				
				// Add size info at start (unsigned short - max size 65535).
				_sendableBytes[0] = (byte) (_position & 0xff);
				_sendableBytes[1] = (byte) ((_position >> 8) & 0xffff);
			}
		}
		
		// Return the data.
		return _sendableBytes;
	}
	
	/**
	 * Gets the length of the data written to the packet, including the size header.<br>
	 * Note that the data must be written first before calling this method.
	 * @return The length of the data.
	 */
	public int getLength()
	{
		return _position;
	}
}
