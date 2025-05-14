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

import org.l2jpx.Config;
import org.l2jpx.loginserver.LoginController;
import org.l2jpx.loginserver.LoginServer;
import org.l2jpx.loginserver.SessionKey;
import org.l2jpx.loginserver.enums.LoginFailReason;
import org.l2jpx.loginserver.enums.PlayFailReason;
import org.l2jpx.loginserver.network.LoginClient;
import org.l2jpx.loginserver.network.gameserverpackets.ServerStatus;
import org.l2jpx.loginserver.network.serverpackets.PlayOk;

/**
 * <pre>
 * Format is ddc
 * d: first part of session id
 * d: second part of session id
 * c: server ID
 * </pre>
 */
public class RequestServerLogin extends LoginClientPacket
{
	private int _skey1;
	private int _skey2;
	private int _serverId;
	
	@Override
	protected boolean readImpl()
	{
		if (remaining() >= 9)
		{
			_skey1 = readInt();
			_skey2 = readInt();
			_serverId = readByte();
			return true;
		}
		return false;
	}
	
	@Override
	public void run()
	{
		final LoginClient client = getClient();
		final SessionKey sk = client.getSessionKey();
		
		// If we didn't showed the license we can't check these values.
		if (!Config.SHOW_LICENCE || sk.checkLoginPair(_skey1, _skey2))
		{
			if ((LoginServer.getInstance().getStatus() == ServerStatus.STATUS_DOWN) || ((LoginServer.getInstance().getStatus() == ServerStatus.STATUS_GM_ONLY) && (client.getAccessLevel() < 1)))
			{
				client.close(LoginFailReason.REASON_ACCESS_FAILED);
			}
			else if (LoginController.getInstance().isLoginPossible(client, _serverId))
			{
				client.setJoinedGS(true);
				client.sendPacket(new PlayOk(sk));
			}
			else
			{
				client.close(PlayFailReason.REASON_SERVER_OVERLOADED);
			}
		}
		else
		{
			client.close(LoginFailReason.REASON_ACCESS_FAILED);
		}
	}
}
