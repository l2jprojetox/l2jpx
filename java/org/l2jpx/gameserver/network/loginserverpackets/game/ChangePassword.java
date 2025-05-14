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
 * @author UnAfraid
 */
public class ChangePassword extends BaseWritablePacket
{
	public ChangePassword(String accountName, String characterName, String oldPass, String newPass)
	{
		writeByte(0x0B);
		writeString(accountName);
		writeString(characterName);
		writeString(oldPass);
		writeString(newPass);
	}
}