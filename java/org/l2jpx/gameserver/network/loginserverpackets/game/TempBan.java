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

/**
 * @author mrTJO
 */
public class TempBan extends BaseWritablePacket
{
	public TempBan(String accountName, String ip, long time)
	{
		writeByte(0x0A);
		writeString(accountName);
		writeString(ip);
		writeLong(System.currentTimeMillis() + (time * 60000));
		// if (reason != null)
		// {
		// writeByte(0x01);
		// writeString(reason);
		// }
		// else
		// {
		writeByte(0x00);
		// }
	}
}
