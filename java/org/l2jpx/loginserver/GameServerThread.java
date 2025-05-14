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
package org.l2jpx.loginserver;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

import org.l2jpx.commons.crypt.NewCrypt;
import org.l2jpx.commons.network.base.BaseWritablePacket;
import org.l2jpx.commons.util.CommonUtil;
import org.l2jpx.loginserver.GameServerTable.GameServerInfo;
import org.l2jpx.loginserver.network.GameServerPacketHandler;
import org.l2jpx.loginserver.network.GameServerPacketHandler.GameServerState;
import org.l2jpx.loginserver.network.ScrambledKeyPair;
import org.l2jpx.loginserver.network.loginserverpackets.ChangePasswordResponse;
import org.l2jpx.loginserver.network.loginserverpackets.InitLS;
import org.l2jpx.loginserver.network.loginserverpackets.KickPlayer;
import org.l2jpx.loginserver.network.loginserverpackets.LoginServerFail;
import org.l2jpx.loginserver.network.loginserverpackets.RequestCharacters;

/**
 * @author -Wooden-
 * @author KenM
 */
public class GameServerThread extends Thread
{
	protected static final Logger LOGGER = Logger.getLogger(GameServerThread.class.getName());
	
	/** Authed Clients on GameServer */
	private final Set<String> _accountsOnGameServer = ConcurrentHashMap.newKeySet();
	
	private final Socket _socket;
	private InputStream _in;
	private OutputStream _out;
	private final RSAPublicKey _publicKey;
	private final RSAPrivateKey _privateKey;
	private NewCrypt _blowfish;
	private GameServerState _loginConnectionState = GameServerState.CONNECTED;
	private final String _connectionIp;
	private String _connectionIPAddress;
	private GameServerInfo _gsi;
	
	@Override
	public void run()
	{
		_connectionIPAddress = _socket.getInetAddress().getHostAddress();
		if (isBannedGameserverIP(_connectionIPAddress))
		{
			LOGGER.info("GameServerRegistration: IP Address " + _connectionIPAddress + " is on Banned IP list.");
			forceClose(LoginServerFail.REASON_IP_BANNED);
			// Ensure no further processing for this connection.
			return;
		}
		
		final InitLS startPacket = new InitLS(_publicKey.getModulus().toByteArray());
		try
		{
			sendPacket(startPacket);
			
			int lengthHi = 0;
			int lengthLo = 0;
			int length = 0;
			boolean checksumOk = false;
			for (;;)
			{
				lengthLo = _in.read();
				lengthHi = _in.read();
				length = (lengthHi * 256) + lengthLo;
				if ((lengthHi < 0) || _socket.isClosed())
				{
					LOGGER.finer("LoginServerThread: Login terminated the connection.");
					break;
				}
				
				final byte[] data = new byte[length - 2];
				int receivedBytes = 0;
				int newBytes = 0;
				int left = length - 2;
				while ((newBytes != -1) && (receivedBytes < (length - 2)))
				{
					newBytes = _in.read(data, receivedBytes, left);
					receivedBytes += newBytes;
					left -= newBytes;
				}
				
				if (receivedBytes != (length - 2))
				{
					LOGGER.warning("Incomplete Packet is sent to the server, closing connection.(LS)");
					break;
				}
				
				// Decrypt if we have a key.
				_blowfish.decrypt(data, 0, data.length);
				checksumOk = NewCrypt.verifyChecksum(data);
				if (!checksumOk)
				{
					LOGGER.warning("Incorrect packet checksum, closing connection (LS)");
					return;
				}
				
				GameServerPacketHandler.handlePacket(data, this);
			}
		}
		catch (IOException e)
		{
			final String serverName = getServerId() != -1 ? "[" + getServerId() + "] " + GameServerTable.getInstance().getServerNameById(getServerId()) : "(" + _connectionIPAddress + ")";
			final String msg = "GameServer " + serverName + ": Connection lost: " + e.getMessage();
			LOGGER.info(msg);
		}
		finally
		{
			if (isAuthed())
			{
				if (_gsi != null)
				{
					_gsi.setDown();
				}
				LOGGER.info("Server [" + getServerId() + "] " + GameServerTable.getInstance().getServerNameById(getServerId()) + " is now set as disconnected.");
			}
			LoginServer.getInstance().getGameServerListener().removeGameServer(this);
			LoginServer.getInstance().getGameServerListener().removeFloodProtection(_connectionIp);
		}
	}
	
	public boolean hasAccountOnGameServer(String account)
	{
		return _accountsOnGameServer.contains(account);
	}
	
	public int getPlayerCount()
	{
		return _accountsOnGameServer.size();
	}
	
	/**
	 * Attachs a GameServerInfo to this Thread<br>
	 * <ul>
	 * <li>Updates the GameServerInfo values based on GameServerAuth packet</li>
	 * <li><b>Sets the GameServerInfo as Authed</b></li>
	 * </ul>
	 * @param gsi The GameServerInfo to be attached.
	 * @param port
	 * @param hosts
	 * @param maxPlayers
	 */
	public void attachGameServerInfo(GameServerInfo gsi, int port, String[] hosts, int maxPlayers)
	{
		setGameServerInfo(gsi);
		gsi.setGameServerThread(this);
		gsi.setPort(port);
		setGameHosts(hosts);
		gsi.setMaxPlayers(maxPlayers);
		gsi.setAuthed(true);
	}
	
	public void forceClose(int reason)
	{
		sendPacket(new LoginServerFail(reason));
		
		try
		{
			_socket.close();
		}
		catch (IOException e)
		{
			LOGGER.finer("GameServerThread: Failed disconnecting banned server, server already disconnected.");
		}
	}
	
	/**
	 * @param ipAddress
	 * @return
	 */
	public static boolean isBannedGameserverIP(String ipAddress)
	{
		return false;
	}
	
	public GameServerThread(Socket con)
	{
		_socket = con;
		_connectionIp = con.getInetAddress().getHostAddress();
		
		try
		{
			_in = _socket.getInputStream();
			_out = new BufferedOutputStream(_socket.getOutputStream());
		}
		catch (IOException e)
		{
			LOGGER.warning(getClass().getSimpleName() + ": " + e.getMessage());
		}
		
		final ScrambledKeyPair pair = LoginController.getInstance().getScrambledRSAKeyPair();
		_privateKey = (RSAPrivateKey) pair.getPrivateKey();
		_publicKey = (RSAPublicKey) pair.getPublicKey();
		_blowfish = new NewCrypt("_;v.]05-31!|+-%xT!^[$\00");
		// Java 18
		// setName(getClass().getSimpleName() + "-" + getId() + "@" + _connectionIp);
		// Java 19
		setName(getClass().getSimpleName() + "-" + threadId() + "@" + _connectionIp);
		
		start();
	}
	
	public void sendPacket(BaseWritablePacket packet)
	{
		if ((_blowfish == null) || (_socket == null) || _socket.isClosed())
		{
			return;
		}
		
		try
		{
			packet.write(); // Write initial data.
			packet.writeInt(0); // Reserved for checksum.
			int size = packet.getLength() - 2; // Size without header.
			final int padding = size % 8; // Padding of 8 bytes.
			if (padding != 0)
			{
				for (int i = padding; i < 8; i++)
				{
					packet.writeByte(0);
				}
			}
			
			// Size header + encrypted[data + checksum (int) + padding].
			final byte[] data = packet.getSendableBytes();
			
			// Encrypt.
			size = data.length - 2; // Data size without header.
			
			synchronized (_out)
			{
				NewCrypt.appendChecksum(data, 2, size);
				_blowfish.crypt(data, 2, size);
				
				_out.write(data);
				try
				{
					_out.flush();
				}
				catch (IOException e)
				{
					// GameServer might have terminated.
				}
			}
		}
		catch (IOException e)
		{
			LOGGER.severe("GameServerThread: IOException while sending packet " + packet.getClass().getSimpleName());
			LOGGER.severe(CommonUtil.getStackTrace(e));
		}
	}
	
	public void kickPlayer(String account)
	{
		sendPacket(new KickPlayer(account));
	}
	
	public void requestCharacters(String account)
	{
		sendPacket(new RequestCharacters(account));
	}
	
	public void changePasswordResponse(String characterName, String msgToSend)
	{
		sendPacket(new ChangePasswordResponse(characterName, msgToSend));
	}
	
	/**
	 * @param hosts The gameHost to set.
	 */
	public void setGameHosts(String[] hosts)
	{
		LOGGER.info("Updated Gameserver [" + getServerId() + "] " + GameServerTable.getInstance().getServerNameById(getServerId()) + " IP's:");
		_gsi.clearServerAddresses();
		for (int i = 0; i < hosts.length; i += 2)
		{
			try
			{
				_gsi.addServerAddress(hosts[i], hosts[i + 1]);
			}
			catch (Exception e)
			{
				LOGGER.warning("Couldn't resolve hostname \"" + e + "\"");
			}
		}
		
		for (String s : _gsi.getServerAddresses())
		{
			LOGGER.info(s);
		}
	}
	
	/**
	 * @return Returns if game server is authed.
	 */
	public boolean isAuthed()
	{
		if (_gsi == null)
		{
			return false;
		}
		return _gsi.isAuthed();
	}
	
	public void setGameServerInfo(GameServerInfo gsi)
	{
		_gsi = gsi;
	}
	
	public GameServerInfo getGameServerInfo()
	{
		return _gsi;
	}
	
	/**
	 * @return Returns the connectionIpAddress.
	 */
	public String getConnectionIpAddress()
	{
		return _connectionIPAddress;
	}
	
	public int getServerId()
	{
		if (_gsi != null)
		{
			return _gsi.getId();
		}
		return -1;
	}
	
	public RSAPrivateKey getPrivateKey()
	{
		return _privateKey;
	}
	
	public void setBlowFish(NewCrypt blowfish)
	{
		_blowfish = blowfish;
	}
	
	public void addAccountOnGameServer(String account)
	{
		_accountsOnGameServer.add(account);
		LoginController.getInstance().removeAuthedLoginClient(account);
	}
	
	public void removeAccountOnGameServer(String account)
	{
		_accountsOnGameServer.remove(account);
		LoginController.getInstance().removeAuthedLoginClient(account);
	}
	
	public GameServerState getLoginConnectionState()
	{
		return _loginConnectionState;
	}
	
	public void setLoginConnectionState(GameServerState state)
	{
		_loginConnectionState = state;
	}
}
