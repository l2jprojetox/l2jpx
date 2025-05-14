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

import java.util.Arrays;
import java.util.logging.Logger;

import org.l2jpx.Config;
import org.l2jpx.commons.network.base.BaseReadablePacket;
import org.l2jpx.loginserver.GameServerTable;
import org.l2jpx.loginserver.GameServerTable.GameServerInfo;
import org.l2jpx.loginserver.GameServerThread;
import org.l2jpx.loginserver.network.GameServerPacketHandler.GameServerState;
import org.l2jpx.loginserver.network.loginserverpackets.AuthResponse;
import org.l2jpx.loginserver.network.loginserverpackets.LoginServerFail;

/**
 * <pre>
 * Format: cccddb
 * c desired ID
 * c accept alternative ID
 * c reserve Host
 * s ExternalHostName
 * s InetranlHostName
 * d max players
 * d hexid size
 * b hexid
 * </pre>
 * 
 * @author -Wooden-
 */
public class GameServerAuth extends BaseReadablePacket
{
	protected static final Logger LOGGER = Logger.getLogger(GameServerAuth.class.getName());
	
	GameServerThread _server;
	private final byte[] _hexId;
	private final int _desiredId;
	private final boolean _acceptAlternativeId;
	private final int _maxPlayers;
	private final int _port;
	private final String[] _hosts;
	
	public GameServerAuth(byte[] decrypt, GameServerThread server)
	{
		super(decrypt);
		readByte(); // Packet id, it is already processed.
		
		_server = server;
		_desiredId = readByte();
		_acceptAlternativeId = readByte() != 0;
		readByte(); // _hostReserved = readByte() != 0
		_port = readShort();
		_maxPlayers = readInt();
		int size = readInt();
		_hexId = readBytes(size);
		size = 2 * readInt();
		_hosts = new String[size];
		for (int i = 0; i < size; i++)
		{
			_hosts[i] = readString();
		}
		
		if (handleRegProcess())
		{
			final AuthResponse ar = new AuthResponse(server.getGameServerInfo().getId());
			server.sendPacket(ar);
			server.setLoginConnectionState(GameServerState.AUTHED);
		}
	}
	
	private boolean handleRegProcess()
	{
		final GameServerTable gameServerTable = GameServerTable.getInstance();
		final int id = _desiredId;
		final byte[] hexId = _hexId;
		
		// Is there a gameserver registered with this id?
		GameServerInfo gsi = gameServerTable.getRegisteredGameServerById(id);
		if (gsi != null)
		{
			// Does the hex id match?
			if (Arrays.equals(gsi.getHexId(), hexId))
			{
				// Check to see if this GS is already connected.
				synchronized (gsi)
				{
					if (gsi.isAuthed())
					{
						_server.forceClose(LoginServerFail.REASON_ALREADY_LOGGED8IN);
						return false;
					}
					_server.attachGameServerInfo(gsi, _port, _hosts, _maxPlayers);
				}
			}
			else
			{
				// There is already a server registered with the desired id and different hex id.
				// Try to register this one with an alternative id.
				if (Config.ACCEPT_NEW_GAMESERVER && _acceptAlternativeId)
				{
					gsi = new GameServerInfo(id, hexId, _server);
					if (gameServerTable.registerWithFirstAvailableId(gsi))
					{
						_server.attachGameServerInfo(gsi, _port, _hosts, _maxPlayers);
						gameServerTable.registerServerOnDB(gsi);
					}
					else
					{
						_server.forceClose(LoginServerFail.REASON_NO_FREE_ID);
						return false;
					}
				}
				else
				{
					// Server id is already taken, and we cannot get a new one for you.
					_server.forceClose(LoginServerFail.REASON_WRONG_HEXID);
					return false;
				}
			}
		}
		else
		{
			// Can we register on this id?
			if (Config.ACCEPT_NEW_GAMESERVER)
			{
				gsi = new GameServerInfo(id, hexId, _server);
				if (gameServerTable.register(id, gsi))
				{
					_server.attachGameServerInfo(gsi, _port, _hosts, _maxPlayers);
					gameServerTable.registerServerOnDB(gsi);
				}
				else
				{
					// Some one took this ID meanwhile.
					_server.forceClose(LoginServerFail.REASON_ID_RESERVED);
					return false;
				}
			}
			else
			{
				_server.forceClose(LoginServerFail.REASON_WRONG_HEXID);
				return false;
			}
		}
		return true;
	}
}
