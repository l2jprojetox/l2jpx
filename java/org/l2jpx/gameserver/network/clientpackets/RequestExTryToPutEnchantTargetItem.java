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

import org.l2jpx.Config;
import org.l2jpx.gameserver.data.xml.EnchantItemData;
import org.l2jpx.gameserver.model.actor.Player;
import org.l2jpx.gameserver.model.actor.request.EnchantItemRequest;
import org.l2jpx.gameserver.model.item.enchant.EnchantScroll;
import org.l2jpx.gameserver.model.item.instance.Item;
import org.l2jpx.gameserver.network.PacketLogger;
import org.l2jpx.gameserver.network.SystemMessageId;
import org.l2jpx.gameserver.network.serverpackets.ExPutEnchantTargetItemResult;
import org.l2jpx.gameserver.util.Util;

/**
 * @author KenM
 */
public class RequestExTryToPutEnchantTargetItem extends ClientPacket
{
	private int _objectId;
	
	@Override
	protected void readImpl()
	{
		_objectId = readInt();
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
		
		final Item scroll = request.getEnchantingScroll();
		if (scroll == null)
		{
			return;
		}
		
		final Item item = player.getInventory().getItemByObjectId(_objectId);
		if (item == null)
		{
			Util.handleIllegalPlayerAction(player, "RequestExTryToPutEnchantTargetItem: " + player + " tried to cheat using a packet manipulation tool! Ban this player!", Config.DEFAULT_PUNISH);
			return;
		}
		
		final EnchantScroll scrollTemplate = EnchantItemData.getInstance().getEnchantScroll(scroll);
		if (!item.isEnchantable() || (scrollTemplate == null) || !scrollTemplate.isValid(item, null))
		{
			player.sendPacket(SystemMessageId.DOES_NOT_FIT_STRENGTHENING_CONDITIONS_OF_THE_SCROLL);
			player.removeRequest(request.getClass());
			player.sendPacket(new ExPutEnchantTargetItemResult(0));
			if (scrollTemplate == null)
			{
				PacketLogger.warning("RequestExTryToPutEnchantTargetItem: " + player + " has used undefined scroll with id " + scroll.getId());
			}
			return;
		}
		
		request.setEnchantingItem(_objectId);
		request.setTimestamp(System.currentTimeMillis());
		
		player.sendPacket(new ExPutEnchantTargetItemResult(_objectId));
	}
}
