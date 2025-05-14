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
public class PlayerTracert extends BaseWritablePacket
{
	public PlayerTracert(String account, String pcIp, String hop1, String hop2, String hop3, String hop4)
	{
		writeByte(0x07);
		writeString(account);
		writeString(pcIp);
		writeString(hop1);
		writeString(hop2);
		writeString(hop3);
		writeString(hop4);
	}
}