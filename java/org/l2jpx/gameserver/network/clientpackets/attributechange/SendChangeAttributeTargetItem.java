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
package org.l2jpx.gameserver.network.clientpackets.attributechange;

import org.l2jpx.gameserver.model.actor.Player;
import org.l2jpx.gameserver.model.item.instance.Item;
import org.l2jpx.gameserver.network.clientpackets.ClientPacket;
import org.l2jpx.gameserver.network.serverpackets.ActionFailed;
import org.l2jpx.gameserver.network.serverpackets.attributechange.ExChangeAttributeInfo;

/**
 * @author Mobius
 */
public class SendChangeAttributeTargetItem extends ClientPacket
{
	private int _crystalItemId;
	private int _itemObjId;
	
	@Override
	protected void readImpl()
	{
		_crystalItemId = readInt();
		_itemObjId = readInt();
	}
	
	@Override
	protected void runImpl()
	{
		final Player player = getPlayer();
		if (player == null)
		{
			return;
		}
		
		final Item item = player.getInventory().getItemByObjectId(_itemObjId);
		if ((item == null) || !item.isWeapon())
		{
			player.sendPacket(ActionFailed.STATIC_PACKET);
			return;
		}
		
		player.sendPacket(new ExChangeAttributeInfo(_crystalItemId, item));
	}
}
