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
package org.l2jpx.loginserver.network.serverpackets;

import org.l2jpx.commons.network.WritableBuffer;
import org.l2jpx.loginserver.network.LoginClient;

/**
 * <pre>
 * Format: dd b dddd s
 * d: session id
 * d: protocol revision
 * b: 0x90 bytes : 0x80 bytes for the scrambled RSA public key
 *                 0x10 bytes at 0x00
 * d: unknow
 * d: unknow
 * d: unknow
 * d: unknow
 * s: blowfish key
 * </pre>
 */
public class Init extends LoginServerPacket
{
	private final int _sessionId;
	
	private final byte[] _publicKey;
	private final byte[] _blowfishKey;
	
	public Init(LoginClient client)
	{
		this(client.getScrambledModulus(), client.getBlowfishKey(), client.getSessionId());
	}
	
	public Init(byte[] publickey, byte[] blowfishkey, int sessionId)
	{
		_sessionId = sessionId;
		_publicKey = publickey;
		_blowfishKey = blowfishkey;
	}
	
	@Override
	protected void writeImpl(LoginClient client, WritableBuffer buffer)
	{
		buffer.writeByte(0x00); // Init packet id.
		
		buffer.writeInt(_sessionId); // Session id.
		buffer.writeInt(0x0000c621); // Protocol revision.
		
		buffer.writeBytes(_publicKey); // RSA Public Key.
		
		// GG related.
		buffer.writeInt(0x29DD954E);
		buffer.writeInt(0x77C39CFC);
		buffer.writeInt(0x97ADB620);
		buffer.writeInt(0x07BDE0F7);
		
		buffer.writeBytes(_blowfishKey); // BlowFish key.
		buffer.writeByte(0); // Null termination.
	}
}
