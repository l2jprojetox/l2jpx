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
package org.l2jpx.loginserver.network.gameserverpackets;

import org.l2jpx.commons.network.base.BaseReadablePacket;
import org.l2jpx.loginserver.GameServerThread;
import org.l2jpx.loginserver.LoginController;
import org.l2jpx.loginserver.SessionKey;
import org.l2jpx.loginserver.network.loginserverpackets.PlayerAuthResponse;

/**
 * @author -Wooden-
 */
public class PlayerAuthRequest extends BaseReadablePacket
{
	public PlayerAuthRequest(byte[] decrypt, GameServerThread server)
	{
		super(decrypt);
		readByte(); // Packet id, it is already processed.
		
		final String account = readString();
		final int playKey1 = readInt();
		final int playKey2 = readInt();
		final int loginKey1 = readInt();
		final int loginKey2 = readInt();
		
		final SessionKey sessionKey = new SessionKey(loginKey1, loginKey2, playKey1, playKey2);
		final SessionKey key = LoginController.getInstance().getKeyForAccount(account);
		if ((key != null) && key.equals(sessionKey))
		{
			LoginController.getInstance().removeAuthedLoginClient(account);
			server.sendPacket(new PlayerAuthResponse(account, true));
		}
		else
		{
			server.sendPacket(new PlayerAuthResponse(account, false));
		}
	}
}
