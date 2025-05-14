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
package org.l2jpx.loginserver.network;

import java.util.logging.Logger;

import org.l2jpx.commons.network.PacketHandler;
import org.l2jpx.commons.network.ReadableBuffer;
import org.l2jpx.commons.network.ReadablePacket;
import org.l2jpx.commons.util.CommonUtil;
import org.l2jpx.loginserver.enums.LoginFailReason;

/**
 * @author Mobius
 */
public class LoginPacketHandler implements PacketHandler<LoginClient>
{
	private static final Logger LOGGER = Logger.getLogger(LoginPacketHandler.class.getName());
	
	@Override
	public ReadablePacket<LoginClient> handlePacket(ReadableBuffer buffer, LoginClient client)
	{
		// Read packet id.
		final int packetId;
		try
		{
			packetId = Byte.toUnsignedInt(buffer.readByte());
		}
		catch (Exception e)
		{
			LOGGER.warning("LoginPacketHandler: Problem receiving packet id from " + client);
			LOGGER.warning(CommonUtil.getStackTrace(e));
			client.close(LoginFailReason.REASON_ACCESS_FAILED);
			return null;
		}
		
		// Check if packet id is within valid range.
		if ((packetId < 0) || (packetId >= LoginClientPackets.PACKET_ARRAY.length))
		{
			return null;
		}
		
		// Find packet enum.
		final LoginClientPackets packetEnum = LoginClientPackets.PACKET_ARRAY[packetId];
		if (packetEnum == null)
		{
			return null;
		}
		
		// Check connection state.
		if (!packetEnum.getConnectionStates().contains(client.getConnectionState()))
		{
			return null;
		}
		
		// Create new LoginClientPacket.
		return packetEnum.newPacket();
	}
}
