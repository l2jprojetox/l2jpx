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
package org.l2jpx.gameserver.network.loginserverpackets.login;

import org.l2jpx.commons.network.base.BaseReadablePacket;
import org.l2jpx.gameserver.model.World;
import org.l2jpx.gameserver.model.actor.Player;

public class ChangePasswordResponse extends BaseReadablePacket
{
	public ChangePasswordResponse(byte[] decrypt)
	{
		super(decrypt);
		readByte(); // Packet id, it is already processed.
		
		// boolean isSuccessful = readByte() > 0;
		final String character = readString();
		final String msgToSend = readString();
		final Player player = World.getInstance().getPlayer(character);
		if (player != null)
		{
			player.sendMessage(msgToSend);
		}
	}
}