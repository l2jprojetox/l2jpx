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

import java.security.GeneralSecurityException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.crypto.Cipher;

import org.l2jpx.commons.crypt.NewCrypt;
import org.l2jpx.commons.network.base.BaseReadablePacket;
import org.l2jpx.loginserver.GameServerThread;
import org.l2jpx.loginserver.network.GameServerPacketHandler.GameServerState;

/**
 * @author -Wooden-
 */
public class BlowFishKey extends BaseReadablePacket
{
	protected static final Logger LOGGER = Logger.getLogger(BlowFishKey.class.getName());
	
	public BlowFishKey(byte[] decrypt, GameServerThread server)
	{
		super(decrypt);
		readByte(); // Packet id, it is already processed.
		
		final int size = readInt();
		final byte[] tempKey = readBytes(size);
		try
		{
			byte[] tempDecryptKey;
			final Cipher rsaCipher = Cipher.getInstance("RSA/ECB/nopadding");
			rsaCipher.init(Cipher.DECRYPT_MODE, server.getPrivateKey());
			tempDecryptKey = rsaCipher.doFinal(tempKey);
			// There are nulls before the key we must remove them.
			int i = 0;
			final int len = tempDecryptKey.length;
			for (; i < len; i++)
			{
				if (tempDecryptKey[i] != 0)
				{
					break;
				}
			}
			final byte[] key = new byte[len - i];
			System.arraycopy(tempDecryptKey, i, key, 0, len - i);
			server.setBlowFish(new NewCrypt(key));
			server.setLoginConnectionState(GameServerState.BF_CONNECTED);
		}
		catch (GeneralSecurityException e)
		{
			LOGGER.log(Level.SEVERE, "Error While decrypting blowfish key (RSA): " + e.getMessage(), e);
		}
	}
}
