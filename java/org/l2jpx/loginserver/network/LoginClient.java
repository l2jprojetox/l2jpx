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
package org.l2jpx.loginserver.network;

import java.io.IOException;
import java.security.interfaces.RSAPrivateKey;
import java.util.HashMap;
import java.util.Map;

import org.l2jpx.commons.network.Buffer;
import org.l2jpx.commons.network.Client;
import org.l2jpx.commons.network.Connection;
import org.l2jpx.commons.util.Rnd;
import org.l2jpx.loginserver.LoginController;
import org.l2jpx.loginserver.SessionKey;
import org.l2jpx.loginserver.enums.AccountKickedReason;
import org.l2jpx.loginserver.enums.LoginFailReason;
import org.l2jpx.loginserver.enums.PlayFailReason;
import org.l2jpx.loginserver.network.serverpackets.AccountKicked;
import org.l2jpx.loginserver.network.serverpackets.Init;
import org.l2jpx.loginserver.network.serverpackets.LoginFail;
import org.l2jpx.loginserver.network.serverpackets.LoginServerPacket;
import org.l2jpx.loginserver.network.serverpackets.PlayFail;

/**
 * Represents a client connected into the LoginServer
 * @author KenM
 */
public class LoginClient extends Client<Connection<LoginClient>>
{
	private final LoginEncryption _encryption;
	private final ScrambledKeyPair _scrambledPair;
	private final byte[] _blowfishKey;
	private String _ip = "N/A";
	private String _account;
	private int _accessLevel;
	private int _lastServer;
	private SessionKey _sessionKey;
	private final int _sessionId;
	private boolean _joinedGS;
	private Map<Integer, Integer> _charsOnServers;
	private Map<Integer, long[]> _charsToDelete;
	private ConnectionState _connectionState = ConnectionState.CONNECTED;
	private final long _connectionStartTime;
	
	public LoginClient(Connection<LoginClient> connection)
	{
		super(connection);
		_scrambledPair = LoginController.getInstance().getScrambledRSAKeyPair();
		_blowfishKey = LoginController.getInstance().getBlowfishKey();
		_ip = connection.getRemoteAddress();
		_sessionId = Rnd.nextInt();
		_connectionStartTime = System.currentTimeMillis();
		_encryption = new LoginEncryption();
		_encryption.setKey(_blowfishKey);
		
		if (LoginController.getInstance().isBannedAddress(_ip))
		{
			close(LoginFailReason.REASON_NOT_AUTHED);
		}
	}
	
	@Override
	public boolean encrypt(Buffer data, int offset, int size)
	{
		try
		{
			return _encryption.encrypt(data, offset, size);
		}
		catch (IOException e)
		{
			return false;
		}
	}
	
	@Override
	public boolean decrypt(Buffer data, int offset, int size)
	{
		boolean decrypted;
		try
		{
			decrypted = _encryption.decrypt(data, offset, size);
		}
		catch (IOException e)
		{
			close();
			return false;
		}
		
		if (!decrypted)
		{
			close();
		}
		
		return decrypted;
	}
	
	@Override
	public void onConnected()
	{
		sendPacket(new Init(this));
	}
	
	@Override
	public void onDisconnection()
	{
		// Check if the client has joined the game server.
		if (!_joinedGS)
		{
			// The client has not joined, remove it from the login authenticated clients immediately.
			LoginController.getInstance().removeAuthedLoginClient(_account);
			
			// Give time to other threads to finish client actions.
			try
			{
				Thread.sleep(1000);
			}
			catch (InterruptedException e)
			{
			}
		}
	}
	
	public byte[] getBlowfishKey()
	{
		return _blowfishKey;
	}
	
	public byte[] getScrambledModulus()
	{
		return _scrambledPair.getScrambledModulus();
	}
	
	public RSAPrivateKey getRSAPrivateKey()
	{
		return (RSAPrivateKey) _scrambledPair.getPrivateKey();
	}
	
	public String getIp()
	{
		return _ip;
	}
	
	public String getAccount()
	{
		return _account;
	}
	
	public void setAccount(String account)
	{
		_account = account;
	}
	
	public void setAccessLevel(int accessLevel)
	{
		_accessLevel = accessLevel;
	}
	
	public int getAccessLevel()
	{
		return _accessLevel;
	}
	
	public void setLastServer(int lastServer)
	{
		_lastServer = lastServer;
	}
	
	public int getLastServer()
	{
		return _lastServer;
	}
	
	public int getSessionId()
	{
		return _sessionId;
	}
	
	public ScrambledKeyPair getScrambledKeyPair()
	{
		return _scrambledPair;
	}
	
	public boolean hasJoinedGS()
	{
		return _joinedGS;
	}
	
	public void setJoinedGS(boolean value)
	{
		_joinedGS = value;
	}
	
	public void setSessionKey(SessionKey sessionKey)
	{
		_sessionKey = sessionKey;
	}
	
	public SessionKey getSessionKey()
	{
		return _sessionKey;
	}
	
	public long getConnectionStartTime()
	{
		return _connectionStartTime;
	}
	
	public void sendPacket(LoginServerPacket packet)
	{
		writePacket(packet);
	}
	
	public void close(LoginFailReason reason)
	{
		sendPacket(new LoginFail(reason));
	}
	
	public void close(PlayFailReason reason)
	{
		close(new PlayFail(reason));
	}
	
	public void close(AccountKickedReason reason)
	{
		close(new AccountKicked(reason));
	}
	
	public void setCharsOnServ(int servId, int chars)
	{
		if (_charsOnServers == null)
		{
			_charsOnServers = new HashMap<>();
		}
		_charsOnServers.put(servId, chars);
	}
	
	public Map<Integer, Integer> getCharsOnServ()
	{
		return _charsOnServers;
	}
	
	public void serCharsWaitingDelOnServ(int servId, long[] charsToDel)
	{
		if (_charsToDelete == null)
		{
			_charsToDelete = new HashMap<>();
		}
		_charsToDelete.put(servId, charsToDel);
	}
	
	public Map<Integer, long[]> getCharsWaitingDelOnServ()
	{
		return _charsToDelete;
	}
	
	public ConnectionState getConnectionState()
	{
		return _connectionState;
	}
	
	public void setConnectionState(ConnectionState connectionState)
	{
		_connectionState = connectionState;
	}
	
	@Override
	public String toString()
	{
		final String ip = getHostAddress();
		final StringBuilder sb = new StringBuilder();
		sb.append(getClass().getSimpleName());
		sb.append(" [");
		if (_account != null)
		{
			sb.append("Account: ");
			sb.append(_account);
		}
		if (ip != null)
		{
			if (_account != null)
			{
				sb.append(" - ");
			}
			sb.append("IP: ");
			sb.append(ip.isEmpty() ? "disconnected" : ip);
		}
		sb.append("]");
		return sb.toString();
	}
}
