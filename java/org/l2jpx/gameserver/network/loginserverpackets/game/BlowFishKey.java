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
package org.l2jpx.gameserver.network.loginserverpackets.game;

import java.security.interfaces.RSAPublicKey;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.crypto.Cipher;

import org.l2jpx.commons.network.base.BaseWritablePacket;

/**
 * @author -Wooden-
 */
public class BlowFishKey extends BaseWritablePacket
{
	private static final Logger LOGGER = Logger.getLogger(BlowFishKey.class.getName());
	
	public BlowFishKey(byte[] blowfishKey, RSAPublicKey publicKey)
	{
		writeByte(0x00);
		try
		{
			final Cipher rsaCipher = Cipher.getInstance("RSA/ECB/nopadding");
			rsaCipher.init(Cipher.ENCRYPT_MODE, publicKey);
			final byte[] encrypted = rsaCipher.doFinal(blowfishKey);
			writeInt(encrypted.length);
			writeBytes(encrypted);
		}
		catch (Exception e)
		{
			LOGGER.log(Level.SEVERE, "Error While encrypting blowfish key for transmision (Crypt error): " + e.getMessage(), e);
		}
	}
}
