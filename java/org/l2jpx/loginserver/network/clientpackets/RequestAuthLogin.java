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

import java.security.GeneralSecurityException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.crypto.Cipher;

import org.l2jpx.Config;
import org.l2jpx.loginserver.GameServerTable.GameServerInfo;
import org.l2jpx.loginserver.LoginController;
import org.l2jpx.loginserver.enums.AccountKickedReason;
import org.l2jpx.loginserver.enums.LoginFailReason;
import org.l2jpx.loginserver.model.data.AccountInfo;
import org.l2jpx.loginserver.network.ConnectionState;
import org.l2jpx.loginserver.network.LoginClient;
import org.l2jpx.loginserver.network.serverpackets.AccountKicked;
import org.l2jpx.loginserver.network.serverpackets.LoginOk;
import org.l2jpx.loginserver.network.serverpackets.ServerList;

/**
 * Format: x 0 (a leading null) x: the rsa encrypted block with the login an password.
 */
public class RequestAuthLogin extends LoginClientPacket
{
	private static final Logger LOGGER = Logger.getLogger(RequestAuthLogin.class.getName());
	
	private final byte[] _raw1 = new byte[128];
	private final byte[] _raw2 = new byte[128];
	private boolean _newAuthMethod = false;
	
	@Override
	protected boolean readImpl()
	{
		if (remaining() >= 256)
		{
			_newAuthMethod = true;
			readBytes(_raw1);
			readBytes(_raw2);
			return true;
		}
		else if (remaining() >= 128)
		{
			readBytes(_raw1);
			return true;
		}
		return false;
	}
	
	@Override
	public void run()
	{
		if (Config.ENABLE_CMD_LINE_LOGIN && Config.ONLY_CMD_LINE_LOGIN)
		{
			return;
		}
		
		final LoginClient client = getClient();
		final byte[] decrypted = new byte[_newAuthMethod ? 256 : 128];
		try
		{
			final Cipher rsaCipher = Cipher.getInstance("RSA/ECB/nopadding");
			rsaCipher.init(Cipher.DECRYPT_MODE, client.getRSAPrivateKey());
			rsaCipher.doFinal(_raw1, 0, 128, decrypted, 0);
			if (_newAuthMethod)
			{
				rsaCipher.doFinal(_raw2, 0, 128, decrypted, 128);
			}
		}
		catch (GeneralSecurityException e)
		{
			LOGGER.log(Level.INFO, "", e);
			return;
		}
		
		final String user;
		final String password;
		try
		{
			if (_newAuthMethod)
			{
				user = new String(decrypted, 0x4E, 50).trim() + new String(decrypted, 0xCE, 14).trim();
				password = new String(decrypted, 0xDC, 16).trim();
			}
			else
			{
				user = new String(decrypted, 0x5E, 14).trim();
				password = new String(decrypted, 0x6C, 16).trim();
			}
		}
		catch (Exception e)
		{
			LOGGER.log(Level.WARNING, "", e);
			return;
		}
		
		final String clientAddr = client.getIp();
		final LoginController lc = LoginController.getInstance();
		final AccountInfo info = lc.retriveAccountInfo(clientAddr, user, password);
		if (info == null)
		{
			// Account or password was wrong.
			client.close(LoginFailReason.REASON_ACCESS_FAILED);
			return;
		}
		
		switch (lc.tryCheckinAccount(client, clientAddr, info))
		{
			case AUTH_SUCCESS:
			{
				client.setAccount(info.getLogin());
				client.setConnectionState(ConnectionState.AUTHED_LOGIN);
				client.setSessionKey(lc.assignSessionKeyToClient(info.getLogin(), client));
				lc.getCharactersOnAccount(info.getLogin());
				if (Config.SHOW_LICENCE)
				{
					client.sendPacket(new LoginOk(client.getSessionKey()));
				}
				else
				{
					client.sendPacket(new ServerList(client));
				}
				break;
			}
			case INVALID_PASSWORD:
			{
				client.close(LoginFailReason.REASON_USER_OR_PASS_WRONG);
				break;
			}
			case ACCOUNT_BANNED:
			{
				client.close(new AccountKicked(AccountKickedReason.REASON_PERMANENTLY_BANNED));
				return;
			}
			case ALREADY_ON_LS:
			{
				final LoginClient oldClient = lc.getAuthedClient(info.getLogin());
				if (oldClient != null)
				{
					// Kick the other client.
					oldClient.close(LoginFailReason.REASON_ACCOUNT_IN_USE);
					lc.removeAuthedLoginClient(info.getLogin());
				}
				
				// Also kick current client.
				client.close(LoginFailReason.REASON_ACCOUNT_IN_USE);
				break;
			}
			case ALREADY_ON_GS:
			{
				final GameServerInfo gsi = lc.getAccountOnGameServer(info.getLogin());
				if (gsi != null)
				{
					client.close(LoginFailReason.REASON_ACCOUNT_IN_USE);
					if (gsi.isAuthed())
					{
						gsi.getGameServerThread().kickPlayer(info.getLogin());
					}
				}
				break;
			}
		}
	}
}
