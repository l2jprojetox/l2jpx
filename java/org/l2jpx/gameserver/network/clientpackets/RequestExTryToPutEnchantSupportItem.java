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
package org.l2jpx.gameserver.network.clientpackets;

import org.l2jpx.gameserver.data.xml.EnchantItemData;
import org.l2jpx.gameserver.model.actor.Player;
import org.l2jpx.gameserver.model.actor.request.EnchantItemRequest;
import org.l2jpx.gameserver.model.item.enchant.EnchantScroll;
import org.l2jpx.gameserver.model.item.enchant.EnchantSupportItem;
import org.l2jpx.gameserver.model.item.instance.Item;
import org.l2jpx.gameserver.network.SystemMessageId;
import org.l2jpx.gameserver.network.serverpackets.ExPutEnchantSupportItemResult;

/**
 * @author KenM
 */
public class RequestExTryToPutEnchantSupportItem extends ClientPacket
{
	private int _supportObjectId;
	private int _enchantObjectId;
	
	@Override
	protected void readImpl()
	{
		_supportObjectId = readInt();
		_enchantObjectId = readInt();
	}
	
	@Override
	protected void runImpl()
	{
		final Player player = getPlayer();
		if (player == null)
		{
			return;
		}
		
		final EnchantItemRequest request = player.getRequest(EnchantItemRequest.class);
		if ((request == null) || request.isProcessing())
		{
			return;
		}
		
		request.setEnchantingItem(_enchantObjectId);
		request.setSupportItem(_supportObjectId);
		
		final Item item = request.getEnchantingItem();
		final Item scroll = request.getEnchantingScroll();
		final Item support = request.getSupportItem();
		if ((item == null) || (scroll == null) || (support == null))
		{
			// message may be custom
			player.sendPacket(SystemMessageId.INAPPROPRIATE_ENCHANT_CONDITIONS);
			request.setEnchantingItem(Player.ID_NONE);
			request.setSupportItem(Player.ID_NONE);
			return;
		}
		
		final EnchantScroll scrollTemplate = EnchantItemData.getInstance().getEnchantScroll(scroll);
		final EnchantSupportItem supportTemplate = EnchantItemData.getInstance().getSupportItem(support);
		if ((scrollTemplate == null) || (supportTemplate == null) || !scrollTemplate.isValid(item, supportTemplate))
		{
			// message may be custom
			player.sendPacket(SystemMessageId.INAPPROPRIATE_ENCHANT_CONDITIONS);
			request.setSupportItem(Player.ID_NONE);
			player.sendPacket(new ExPutEnchantSupportItemResult(0));
			return;
		}
		
		request.setSupportItem(support.getObjectId());
		request.setTimestamp(System.currentTimeMillis());
		player.sendPacket(new ExPutEnchantSupportItemResult(_supportObjectId));
	}
}
