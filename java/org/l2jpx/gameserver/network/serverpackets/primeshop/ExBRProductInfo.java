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
package org.l2jpx.gameserver.network.serverpackets.primeshop;

import org.l2jpx.commons.network.WritableBuffer;
import org.l2jpx.gameserver.model.actor.Player;
import org.l2jpx.gameserver.model.primeshop.PrimeShopGroup;
import org.l2jpx.gameserver.model.primeshop.PrimeShopItem;
import org.l2jpx.gameserver.network.GameClient;
import org.l2jpx.gameserver.network.ServerPackets;
import org.l2jpx.gameserver.network.serverpackets.ServerPacket;

/**
 * @author Gnacik
 */
public class ExBRProductInfo extends ServerPacket
{
	private final PrimeShopGroup _item;
	private final int _charPoints;
	private final long _charAdena;
	private final long _charCoins;
	
	public ExBRProductInfo(PrimeShopGroup item, Player player)
	{
		_item = item;
		_charPoints = player.getPrimePoints();
		_charAdena = player.getAdena();
		_charCoins = player.getInventory().getInventoryItemCount(23805, -1);
	}
	
	@Override
	public void writeImpl(GameClient client, WritableBuffer buffer)
	{
		ServerPackets.EX_BR_PRODUCT_INFO.writeId(this, buffer);
		buffer.writeInt(_item.getBrId());
		buffer.writeInt(_item.getPrice());
		buffer.writeInt(_item.getItems().size());
		for (PrimeShopItem item : _item.getItems())
		{
			buffer.writeInt(item.getId());
			buffer.writeInt((int) item.getCount());
			buffer.writeInt(item.getWeight());
			buffer.writeInt(item.isTradable());
		}
		buffer.writeLong(_charAdena);
		buffer.writeLong(_charPoints);
		buffer.writeLong(_charCoins); // Hero coins
	}
}
