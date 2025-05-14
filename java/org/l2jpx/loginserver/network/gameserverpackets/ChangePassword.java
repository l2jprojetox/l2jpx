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

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Base64;
import java.util.Collection;
import java.util.logging.Logger;

import org.l2jpx.commons.database.DatabaseFactory;
import org.l2jpx.commons.network.base.BaseReadablePacket;
import org.l2jpx.loginserver.GameServerTable;
import org.l2jpx.loginserver.GameServerTable.GameServerInfo;
import org.l2jpx.loginserver.GameServerThread;

/**
 * @author Nik
 */
public class ChangePassword extends BaseReadablePacket
{
	protected static final Logger LOGGER = Logger.getLogger(ChangePassword.class.getName());
	
	public ChangePassword(byte[] decrypt)
	{
		super(decrypt);
		readByte(); // Packet id, it is already processed.
		
		final String accountName = readString();
		final String characterName = readString();
		final String curpass = readString();
		final String newpass = readString();
		
		GameServerThread gst = null;
		final Collection<GameServerInfo> serverList = GameServerTable.getInstance().getRegisteredGameServers().values();
		for (GameServerInfo gsi : serverList)
		{
			if ((gsi.getGameServerThread() != null) && gsi.getGameServerThread().hasAccountOnGameServer(accountName))
			{
				gst = gsi.getGameServerThread();
			}
		}
		
		if (gst == null)
		{
			return;
		}
		
		if ((curpass == null) || (newpass == null))
		{
			gst.changePasswordResponse(characterName, "Invalid password data! Try again.");
		}
		else
		{
			try
			{
				final MessageDigest md = MessageDigest.getInstance("SHA");
				final byte[] raw = md.digest(curpass.getBytes(StandardCharsets.UTF_8));
				final String curpassEnc = Base64.getEncoder().encodeToString(raw);
				String pass = null;
				int passUpdated = 0;
				
				try (Connection con = DatabaseFactory.getConnection();
					PreparedStatement ps = con.prepareStatement("SELECT password FROM accounts WHERE login=?"))
				{
					ps.setString(1, accountName);
					try (ResultSet rs = ps.executeQuery())
					{
						if (rs.next())
						{
							pass = rs.getString("password");
						}
					}
				}
				
				if (curpassEnc.equals(pass))
				{
					final byte[] password = md.digest(newpass.getBytes(StandardCharsets.UTF_8));
					try (Connection con = DatabaseFactory.getConnection();
						PreparedStatement ps = con.prepareStatement("UPDATE accounts SET password=? WHERE login=?"))
					{
						ps.setString(1, Base64.getEncoder().encodeToString(password));
						ps.setString(2, accountName);
						passUpdated = ps.executeUpdate();
					}
					
					LOGGER.info("The password for account " + accountName + " has been changed from " + curpassEnc + " to " + Base64.getEncoder().encodeToString(password));
					if (passUpdated > 0)
					{
						gst.changePasswordResponse(characterName, "You have successfully changed your password!");
					}
					else
					{
						gst.changePasswordResponse(characterName, "The password change was unsuccessful!");
					}
				}
				else
				{
					gst.changePasswordResponse(characterName, "The typed current password doesn't match with your current one.");
				}
			}
			catch (Exception e)
			{
				LOGGER.warning("Error while changing password for account " + accountName + " requested by player " + characterName + "! " + e);
			}
		}
	}
}