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
import org.l2jpx.gameserver.enums.AttributeType;
import org.l2jpx.gameserver.network.GameClient;
import org.l2jpx.gameserver.network.ServerPackets;

public class ExAttributeEnchantResult extends ServerPacket
{
	private final int _result;
	private final boolean _isWeapon;
	private final int _type;
	private final int _before;
	private final int _after;
	private final int _successCount;
	private final int _failedCount;
	
	public ExAttributeEnchantResult(int result, boolean isWeapon, AttributeType type, int before, int after, int successCount, int failedCount)
	{
		_result = result;
		_isWeapon = isWeapon;
		_type = type.getClientId();
		_before = before;
		_after = after;
		_successCount = successCount;
		_failedCount = failedCount;
	}
	
	@Override
	public void writeImpl(GameClient client, WritableBuffer buffer)
	{
		ServerPackets.EX_ATTRIBUTE_ENCHANT_RESULT.writeId(this, buffer);
		buffer.writeInt(_result);
		buffer.writeByte(_isWeapon);
		buffer.writeShort(_type);
		buffer.writeShort(_before);
		buffer.writeShort(_after);
		buffer.writeShort(_successCount);
		buffer.writeShort(_failedCount);
	}
}
