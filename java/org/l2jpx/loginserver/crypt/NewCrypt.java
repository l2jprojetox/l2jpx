/*
 * This file is part of the L2J Mobius project.
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.l2jpx.loginserver.crypt;

import java.io.IOException;

import org.l2jpx.commons.network.Buffer;

/**
 * Class to use a blowfish cipher with ECB processing.<br>
 * Static methods are present to append/check the checksum of<br>
 * packets exchanged between the following partners:<br>
 * Login Server <-> Game Client<br>
 * Login Server <-> Game Server<br>
 * Also a static method is provided for the initial xor encryption between Login Server <-> Game Client.
 */
public class NewCrypt
{
	private final BlowfishEngine _crypter;
	private final BlowfishEngine _decrypter;
	
	public NewCrypt(byte[] blowfishKey)
	{
		_crypter = new BlowfishEngine();
		_crypter.init(true, blowfishKey);
		_decrypter = new BlowfishEngine();
		_decrypter.init(false, blowfishKey);
	}
	
	public NewCrypt(String key)
	{
		this(key.getBytes());
	}
	
	public static boolean verifyChecksum(Buffer data, final int offset, final int size)
	{
		// check if size is multiple of 4 and if there is more then only the checksum
		if (((size & 3) != 0) || (size <= 4))
		{
			return false;
		}
		
		long checksum = 0;
		final int count = size - 4;
		int i;
		for (i = offset; i < count; i += 4)
		{
			checksum ^= data.readInt(i);
		}
		
		return data.readInt(i) == checksum;
	}
	
	public static void appendChecksum(Buffer data, final int offset, final int size)
	{
		int checksum = 0;
		final int count = size - 4;
		int i;
		for (i = offset; i < count; i += 4)
		{
			checksum ^= data.readInt(i);
		}
		data.writeInt(i, checksum);
	}
	
	/**
	 * Packet is first XOR encoded with <code>key</code> Then, the last 4 bytes are overwritten with the the XOR "key". Thus this assume that there is enough room for the key to fit without overwriting data.
	 * @param raw The raw bytes to be encrypted
	 * @param offset The begining of the data to be encrypted
	 * @param size Length of the data to be encrypted
	 * @param key The 4 bytes (int) XOR key
	 */
	public static void encXORPass(Buffer raw, final int offset, final int size, int key)
	{
		final int stop = size - 8;
		int pos = 4 + offset;
		int edx;
		int ecx = key; // Initial xor key
		while (pos < stop)
		{
			edx = raw.readInt(pos);
			ecx += edx;
			edx ^= ecx;
			raw.writeInt(pos, edx);
			pos += 4;
		}
		raw.writeInt(pos, ecx);
	}
	
	public synchronized void decrypt(Buffer raw, final int offset, final int size) throws IOException
	{
		final int block = _decrypter.getBlockSize();
		final int count = size / block;
		for (int i = 0; i < count; i++)
		{
			_decrypter.processBlock(raw, offset + (i * block));
		}
	}
	
	public void crypt(Buffer raw, final int offset, final int size) throws IOException
	{
		int block = _crypter.getBlockSize();
		int count = size / block;
		for (int i = 0; i < count; i++)
		{
			_crypter.processBlock(raw, offset + (i * block));
		}
	}
}
