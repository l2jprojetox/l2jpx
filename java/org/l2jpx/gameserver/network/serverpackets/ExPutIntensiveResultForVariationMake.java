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

public class ExPutIntensiveResultForVariationMake extends ServerPacket
{
	private final int _refinerItemObjId;
	private final int _lifestoneItemId;
	private final int _gemstoneItemId;
	private final long _gemstoneCount;
	private final int _unk2;
	
	public ExPutIntensiveResultForVariationMake(int refinerItemObjId, int lifeStoneId, int gemstoneItemId, long gemstoneCount)
	{
		_refinerItemObjId = refinerItemObjId;
		_lifestoneItemId = lifeStoneId;
		_gemstoneItemId = gemstoneItemId;
		_gemstoneCount = gemstoneCount;
		_unk2 = 1;
	}
	
	@Override
	public void writeImpl(GameClient client, WritableBuffer buffer)
	{
		ServerPackets.EX_PUT_INTENSIVE_RESULT_FOR_VARIATION_MAKE.writeId(this, buffer);
		buffer.writeInt(_refinerItemObjId);
		buffer.writeInt(_lifestoneItemId);
		buffer.writeInt(_gemstoneItemId);
		buffer.writeLong(_gemstoneCount);
		buffer.writeInt(_unk2);
	}
}
