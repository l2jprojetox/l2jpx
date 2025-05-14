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
package org.l2jpx.gameserver.network.serverpackets.pledgebonus;

import org.l2jpx.commons.network.WritableBuffer;
import org.l2jpx.gameserver.enums.ClanRewardType;
import org.l2jpx.gameserver.network.GameClient;
import org.l2jpx.gameserver.network.ServerPackets;
import org.l2jpx.gameserver.network.serverpackets.ServerPacket;

/**
 * @author UnAfraid
 */
public class ExPledgeBonusUpdate extends ServerPacket
{
	private final ClanRewardType _type;
	private final int _value;
	
	public ExPledgeBonusUpdate(ClanRewardType type, int value)
	{
		_type = type;
		_value = value;
	}
	
	@Override
	public void writeImpl(GameClient client, WritableBuffer buffer)
	{
		ServerPackets.EX_PLEDGE_BONUS_UPDATE.writeId(this, buffer);
		buffer.writeByte(_type.getClientId());
		buffer.writeInt(_value);
	}
}
