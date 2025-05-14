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

import org.l2jpx.Config;
import org.l2jpx.gameserver.enums.AttributeType;
import org.l2jpx.gameserver.model.actor.Player;
import org.l2jpx.gameserver.model.item.enchant.attribute.AttributeHolder;
import org.l2jpx.gameserver.model.item.instance.Item;
import org.l2jpx.gameserver.model.itemcontainer.PlayerInventory;
import org.l2jpx.gameserver.network.SystemMessageId;
import org.l2jpx.gameserver.network.clientpackets.ClientPacket;
import org.l2jpx.gameserver.network.serverpackets.InventoryUpdate;
import org.l2jpx.gameserver.network.serverpackets.SystemMessage;
import org.l2jpx.gameserver.network.serverpackets.attributechange.ExChangeAttributeFail;
import org.l2jpx.gameserver.network.serverpackets.attributechange.ExChangeAttributeOk;
import org.l2jpx.gameserver.util.Util;

/**
 * @author Mobius
 */
public class RequestChangeAttributeItem extends ClientPacket
{
	private int _consumeItemId;
	private int _itemObjId;
	private int _newElementId;
	
	@Override
	protected void readImpl()
	{
		_consumeItemId = readInt();
		_itemObjId = readInt();
		_newElementId = readInt();
	}
	
	@Override
	protected void runImpl()
	{
		final Player player = getPlayer();
		if (player == null)
		{
			return;
		}
		
		final PlayerInventory inventory = player.getInventory();
		final Item item = inventory.getItemByObjectId(_itemObjId);
		
		// attempting to destroy item
		if (player.getInventory().destroyItemByItemId("ChangeAttribute", _consumeItemId, 1, player, item) == null)
		{
			player.sendPacket(SystemMessageId.INCORRECT_ITEM_COUNT_2);
			player.sendPacket(ExChangeAttributeFail.STATIC);
			Util.handleIllegalPlayerAction(player, player + " tried to change attribute without an attribute change crystal.", Config.DEFAULT_PUNISH);
			return;
		}
		
		// get values
		final int oldElementId = item.getAttackAttributeType().getClientId();
		final int elementValue = item.getAttackAttribute().getValue();
		item.clearAllAttributes();
		item.setAttribute(new AttributeHolder(AttributeType.findByClientId(_newElementId), elementValue), true);
		
		// send packets
		final SystemMessage msg = new SystemMessage(SystemMessageId.S1_S_S2_ATTRIBUTE_HAS_SUCCESSFULLY_CHANGED_TO_S3_ATTRIBUTE);
		msg.addItemName(item);
		msg.addAttribute(oldElementId);
		msg.addAttribute(_newElementId);
		player.sendPacket(msg);
		final InventoryUpdate iu = new InventoryUpdate();
		iu.addModifiedItem(item);
		for (Item i : player.getInventory().getAllItemsByItemId(_consumeItemId))
		{
			iu.addItem(i);
		}
		player.sendInventoryUpdate(iu);
		player.broadcastUserInfo();
		player.sendPacket(ExChangeAttributeOk.STATIC);
	}
}
