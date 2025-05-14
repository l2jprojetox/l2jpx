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
package org.l2jpx.gameserver.network.serverpackets;

import org.l2jpx.commons.network.WritableBuffer;
import org.l2jpx.gameserver.network.GameClient;
import org.l2jpx.gameserver.network.ServerPackets;

/**
 * @author Migi
 */
public class ExNoticePostSent extends ServerPacket
{
	private static final ExNoticePostSent STATIC_PACKET_TRUE = new ExNoticePostSent(true);
	private static final ExNoticePostSent STATIC_PACKET_FALSE = new ExNoticePostSent(false);
	
	public static ExNoticePostSent valueOf(boolean result)
	{
		return result ? STATIC_PACKET_TRUE : STATIC_PACKET_FALSE;
	}
	
	private final boolean _showAnim;
	
	public ExNoticePostSent(boolean showAnimation)
	{
		_showAnim = showAnimation;
	}
	
	@Override
	public void writeImpl(GameClient client, WritableBuffer buffer)
	{
		ServerPackets.EX_REPLY_WRITE_POST.writeId(this, buffer);
		buffer.writeInt(_showAnim);
	}
}
