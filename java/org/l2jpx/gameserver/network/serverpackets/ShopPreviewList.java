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

import java.util.Collection;

import org.l2jpx.Config;
import org.l2jpx.commons.network.WritableBuffer;
import org.l2jpx.gameserver.model.buylist.Product;
import org.l2jpx.gameserver.model.buylist.ProductList;
import org.l2jpx.gameserver.model.item.ItemTemplate;
import org.l2jpx.gameserver.network.GameClient;
import org.l2jpx.gameserver.network.ServerPackets;

public class ShopPreviewList extends ServerPacket
{
	private final int _listId;
	private final Collection<Product> _list;
	private final long _money;
	private int _expertise;
	
	public ShopPreviewList(ProductList list, long currentMoney, int expertiseIndex)
	{
		_listId = list.getListId();
		_list = list.getProducts();
		_money = currentMoney;
		_expertise = expertiseIndex;
	}
	
	public ShopPreviewList(Collection<Product> lst, int listId, long currentMoney)
	{
		_listId = listId;
		_list = lst;
		_money = currentMoney;
	}
	
	@Override
	public void writeImpl(GameClient client, WritableBuffer buffer)
	{
		ServerPackets.SHOP_PREVIEW_LIST.writeId(this, buffer);
		buffer.writeInt(5056);
		buffer.writeLong(_money); // current money
		buffer.writeInt(_listId);
		int newlength = 0;
		for (Product product : _list)
		{
			if ((product.getItem().getCrystalType().getLevel() <= _expertise) && product.getItem().isEquipable())
			{
				newlength++;
			}
		}
		buffer.writeShort(newlength);
		for (Product product : _list)
		{
			if ((product.getItem().getCrystalType().getLevel() <= _expertise) && product.getItem().isEquipable())
			{
				buffer.writeInt(product.getItemId());
				buffer.writeShort(product.getItem().getType2()); // item type2
				if (product.getItem().getType1() != ItemTemplate.TYPE1_ITEM_QUESTITEM_ADENA)
				{
					buffer.writeLong(product.getItem().getBodyPart()); // rev 415 slot 0006-lr.ear 0008-neck 0030-lr.finger 0040-head 0080-?? 0100-l.hand 0200-gloves 0400-chest 0800-pants 1000-feet 2000-?? 4000-r.hand 8000-r.hand
				}
				else
				{
					buffer.writeLong(0); // rev 415 slot 0006-lr.ear 0008-neck 0030-lr.finger 0040-head 0080-?? 0100-l.hand 0200-gloves 0400-chest 0800-pants 1000-feet 2000-?? 4000-r.hand 8000-r.hand
				}
				buffer.writeLong(Config.WEAR_PRICE);
			}
		}
	}
}
