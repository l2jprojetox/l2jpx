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
package org.l2jpx.loginserver.network.clientpackets;

import org.l2jpx.loginserver.enums.LoginFailReason;
import org.l2jpx.loginserver.network.ConnectionState;
import org.l2jpx.loginserver.network.serverpackets.GGAuth;

/**
 * Format: ddddd
 * @author -Wooden-
 */
public class AuthGameGuard extends LoginClientPacket
{
	private int _sessionId;
	
	@Override
	protected boolean readImpl()
	{
		if (remaining() >= 20)
		{
			_sessionId = readInt();
			readInt(); // data1
			readInt(); // data2
			readInt(); // data3
			readInt(); // data4
			return true;
		}
		return false;
	}
	
	@Override
	public void run()
	{
		if (_sessionId == getClient().getSessionId())
		{
			getClient().setConnectionState(ConnectionState.AUTHED_GG);
			getClient().sendPacket(new GGAuth(getClient().getSessionId()));
		}
		else
		{
			getClient().close(LoginFailReason.REASON_ACCESS_FAILED);
		}
	}
}
