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
package org.l2jpx.gameserver.network.serverpackets.appearance;

import org.l2jpx.commons.network.WritableBuffer;
import org.l2jpx.gameserver.network.GameClient;
import org.l2jpx.gameserver.network.ServerPackets;
import org.l2jpx.gameserver.network.serverpackets.ServerPacket;

/**
 * @author UnAfraid
 */
public class ExPutShapeShiftingExtractionItemResult extends ServerPacket
{
	public static final ExPutShapeShiftingExtractionItemResult FAILED = new ExPutShapeShiftingExtractionItemResult(0);
	public static final ExPutShapeShiftingExtractionItemResult SUCCESS = new ExPutShapeShiftingExtractionItemResult(1);
	
	private final int _result;
	
	public ExPutShapeShiftingExtractionItemResult(int result)
	{
		_result = result;
	}
	
	@Override
	public void writeImpl(GameClient client, WritableBuffer buffer)
	{
		ServerPackets.EX_PUT_SHAPE_SHIFTING_EXTRACTION_ITEM_RESULT.writeId(this, buffer);
		buffer.writeInt(_result);
	}
}