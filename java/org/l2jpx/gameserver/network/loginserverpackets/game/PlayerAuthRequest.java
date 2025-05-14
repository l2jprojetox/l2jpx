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

import org.l2jpx.commons.network.base.BaseWritablePacket;
import org.l2jpx.gameserver.LoginServerThread.SessionKey;

/**
 * @author -Wooden-
 */
public class PlayerAuthRequest extends BaseWritablePacket
{
	public PlayerAuthRequest(String account, SessionKey key)
	{
		writeByte(0x05);
		writeString(account);
		writeInt(key.playOkID1);
		writeInt(key.playOkID2);
		writeInt(key.loginOkID1);
		writeInt(key.loginOkID2);
	}
}