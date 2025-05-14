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
package org.l2jpx.gameserver.network;

import org.l2jpx.commons.network.Buffer;

/**
 * @author KenM
 */
public class Encryption
{
	private final byte[] _inKey = new byte[16];
	private final byte[] _outKey = new byte[16];
	private boolean _isEnabled;
	
	public void setKey(byte[] key)
	{
		System.arraycopy(key, 0, _inKey, 0, 16);
		System.arraycopy(key, 0, _outKey, 0, 16);
	}
	
	public void encrypt(Buffer data, int offset, int size)
	{
		if (!_isEnabled)
		{
			_isEnabled = true;
			return;
		}
		
		int encrypted = 0;
		for (int i = 0; i < size; i++)
		{
			final int raw = Byte.toUnsignedInt(data.readByte(offset + i));
			encrypted = raw ^ _outKey[i & 0x0f] ^ encrypted;
			data.writeByte(offset + i, (byte) encrypted);
		}
		
		// Shift key.
		int old = _outKey[8] & 0xff;
		old |= (_outKey[9] << 8) & 0xff00;
		old |= (_outKey[10] << 16) & 0xff0000;
		old |= (_outKey[11] << 24) & 0xff000000;
		old += size;
		_outKey[8] = (byte) (old & 0xff);
		_outKey[9] = (byte) ((old >> 8) & 0xff);
		_outKey[10] = (byte) ((old >> 16) & 0xff);
		_outKey[11] = (byte) ((old >> 24) & 0xff);
	}
	
	public void decrypt(Buffer data, int offset, int size)
	{
		if (!_isEnabled)
		{
			return;
		}
		
		int xOr = 0;
		for (int i = 0; i < size; i++)
		{
			final int encrypted = Byte.toUnsignedInt(data.readByte(offset + i));
			data.writeByte(offset + i, (byte) (encrypted ^ _inKey[i & 15] ^ xOr));
			xOr = encrypted;
		}
		
		// Shift key.
		int old = _inKey[8] & 0xff;
		old |= (_inKey[9] << 8) & 0xff00;
		old |= (_inKey[10] << 16) & 0xff0000;
		old |= (_inKey[11] << 24) & 0xff000000;
		old += size;
		_inKey[8] = (byte) (old & 0xff);
		_inKey[9] = (byte) ((old >> 8) & 0xff);
		_inKey[10] = (byte) ((old >> 16) & 0xff);
		_inKey[11] = (byte) ((old >> 24) & 0xff);
	}
}
