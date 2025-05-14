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
import org.l2jpx.loginserver.SessionKey;
import org.l2jpx.loginserver.network.LoginClient;

/**
 * <pre>
 * Format: dddddddd
 * f: the session key
 * d: ?
 * d: ?
 * d: ?
 * d: ?
 * d: ?
 * d: ?
 * b: 16 bytes - unknown
 * </pre>
 */
public class LoginOk extends LoginServerPacket
{
	private final int _loginOk1;
	private final int _loginOk2;
	
	public LoginOk(SessionKey sessionKey)
	{
		_loginOk1 = sessionKey.loginOkID1;
		_loginOk2 = sessionKey.loginOkID2;
	}
	
	@Override
	protected void writeImpl(LoginClient client, WritableBuffer buffer)
	{
		buffer.writeByte(0x03);
		buffer.writeInt(_loginOk1);
		buffer.writeInt(_loginOk2);
		buffer.writeInt(0x00);
		buffer.writeInt(0x00);
		buffer.writeInt(0x000003ea);
		buffer.writeInt(0x00);
		buffer.writeInt(0x00);
		buffer.writeInt(0x00);
		buffer.writeBytes(new byte[16]);
	}
}
