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
package org.l2jpx.gameserver.network.clientpackets;


import org.l2jpx.gameserver.LoginServerThread;
import org.l2jpx.gameserver.LoginServerThread.SessionKey;
import org.l2jpx.gameserver.network.GameClient;

/**
 * @version $Revision: 1.9.2.3.2.4 $ $Date: 2005/03/27 15:29:30 $
 */
public class AuthLogin extends ClientPacket
{
	// loginName + keys must match what the loginserver used.
	private String _loginName;
	private int _playKey1;
	private int _playKey2;
	private int _loginKey1;
	private int _loginKey2;
	
	@Override
	protected void readImpl()
	{
		_loginName = readString().toLowerCase();
		_playKey2 = readInt();
		_playKey1 = readInt();
		_loginKey1 = readInt();
		_loginKey2 = readInt();
	}
	
	@Override
	protected void runImpl()
	{
		final GameClient client = getClient();
		if (_loginName.isEmpty() || !client.isProtocolOk())
		{
			client.closeNow();
			return;
		}
		
		// Avoid potential exploits.
		if (client.getAccountName() == null)
		{
			// Preventing duplicate login in case client login server socket was disconnected or this packet was not sent yet.
			if (LoginServerThread.getInstance().addGameServerLogin(_loginName, client))
			{
				client.setAccountName(_loginName);
				final SessionKey key = new SessionKey(_loginKey1, _loginKey2, _playKey1, _playKey2);
				// client.setSessionId(key);
				LoginServerThread.getInstance().addWaitingClientAndSendRequest(_loginName, client, key);
			}
			else
			{
				client.close(null);
			}
		}
	}
}
