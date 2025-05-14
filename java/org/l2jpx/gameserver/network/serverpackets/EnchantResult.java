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
import org.l2jpx.gameserver.model.item.instance.Item;
import org.l2jpx.gameserver.network.GameClient;
import org.l2jpx.gameserver.network.ServerPackets;

public class EnchantResult extends ServerPacket
{
	public static final int SUCCESS = 0;
	public static final int FAIL = 1;
	public static final int ERROR = 2;
	public static final int BLESSED_FAIL = 3;
	public static final int NO_CRYSTAL = 4;
	public static final int SAFE_FAIL = 5;
	
	private final int _result;
	private final int _crystal;
	private final int _count;
	private final int _enchantLevel;
	private final int[] _enchantOptions;
	
	public EnchantResult(int result, int crystal, int count, int enchantLevel, int[] options)
	{
		_result = result;
		_crystal = crystal;
		_count = count;
		_enchantLevel = enchantLevel;
		_enchantOptions = options;
	}
	
	public EnchantResult(int result, int crystal, int count)
	{
		this(result, crystal, count, 0, Item.DEFAULT_ENCHANT_OPTIONS);
	}
	
	public EnchantResult(int result, Item item)
	{
		this(result, 0, 0, item.getEnchantLevel(), item.getEnchantOptions());
	}
	
	@Override
	public void writeImpl(GameClient client, WritableBuffer buffer)
	{
		ServerPackets.ENCHANT_RESULT.writeId(this, buffer);
		buffer.writeInt(_result);
		buffer.writeInt(_crystal);
		buffer.writeLong(_count);
		buffer.writeInt(_enchantLevel);
		for (int option : _enchantOptions)
		{
			buffer.writeShort(option);
		}
	}
}
