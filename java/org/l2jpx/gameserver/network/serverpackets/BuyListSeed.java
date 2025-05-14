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

import java.util.ArrayList;
import java.util.List;

import org.l2jpx.commons.network.WritableBuffer;
import org.l2jpx.gameserver.instancemanager.CastleManorManager;
import org.l2jpx.gameserver.model.SeedProduction;
import org.l2jpx.gameserver.network.GameClient;
import org.l2jpx.gameserver.network.ServerPackets;

/**
 * @author l3x
 */
public class BuyListSeed extends ServerPacket
{
	private final int _manorId;
	private final long _money;
	private final List<SeedProduction> _list = new ArrayList<>();
	
	public BuyListSeed(long currentMoney, int castleId)
	{
		_money = currentMoney;
		_manorId = castleId;
		for (SeedProduction s : CastleManorManager.getInstance().getSeedProduction(castleId, false))
		{
			if ((s.getAmount() > 0) && (s.getPrice() > 0))
			{
				_list.add(s);
			}
		}
	}
	
	@Override
	public void writeImpl(GameClient client, WritableBuffer buffer)
	{
		ServerPackets.BUY_LIST_SEED.writeId(this, buffer);
		buffer.writeLong(_money); // current money
		buffer.writeInt(0); // TODO: Find me!
		buffer.writeInt(_manorId); // manor id
		if (!_list.isEmpty())
		{
			buffer.writeShort(_list.size()); // list length
			for (SeedProduction s : _list)
			{
				buffer.writeByte(0); // mask item 0 to print minimum item information
				buffer.writeInt(s.getId()); // ObjectId
				buffer.writeInt(s.getId()); // ItemId
				buffer.writeByte(0xFF); // T1
				buffer.writeLong(s.getAmount()); // Quantity
				buffer.writeByte(5); // Item Type 2 : 00-weapon, 01-shield/armor, 02-ring/earring/necklace, 03-questitem, 04-adena, 05-item
				buffer.writeByte(0); // Filler (always 0)
				buffer.writeShort(0); // Equipped : 00-No, 01-yes
				buffer.writeLong(0); // Slot : 0006-lr.ear, 0008-neck, 0030-lr.finger, 0040-head, 0100-l.hand, 0200-gloves, 0400-chest, 0800-pants, 1000-feet, 4000-r.hand, 8000-r.hand
				buffer.writeShort(0); // Enchant level (pet level shown in control item)
				buffer.writeInt(-1);
				buffer.writeInt(-9999);
				buffer.writeByte(1); // GOD Item enabled = 1 disabled (red) = 0
				buffer.writeLong(s.getPrice()); // price
			}
			_list.clear();
		}
		else
		{
			buffer.writeShort(0);
		}
	}
}
