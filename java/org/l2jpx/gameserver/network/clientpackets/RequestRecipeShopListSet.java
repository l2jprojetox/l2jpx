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

import static org.l2jpx.gameserver.model.itemcontainer.Inventory.MAX_ADENA;

import org.l2jpx.Config;
import org.l2jpx.gameserver.data.xml.RecipeData;
import org.l2jpx.gameserver.enums.PrivateStoreType;
import org.l2jpx.gameserver.model.ManufactureItem;
import org.l2jpx.gameserver.model.RecipeList;
import org.l2jpx.gameserver.model.actor.Player;
import org.l2jpx.gameserver.model.zone.ZoneId;
import org.l2jpx.gameserver.network.SystemMessageId;
import org.l2jpx.gameserver.network.serverpackets.ActionFailed;
import org.l2jpx.gameserver.network.serverpackets.RecipeShopMsg;
import org.l2jpx.gameserver.taskmanager.AttackStanceTaskManager;
import org.l2jpx.gameserver.util.Broadcast;
import org.l2jpx.gameserver.util.Util;

/**
 * RequestRecipeShopListSet client packet class.
 */
public class RequestRecipeShopListSet extends ClientPacket
{
	private static final int BATCH_LENGTH = 12;
	
	private ManufactureItem[] _items = null;
	
	@Override
	protected void readImpl()
	{
		final int count = readInt();
		if ((count <= 0) || (count > Config.MAX_ITEM_IN_PACKET) || ((count * BATCH_LENGTH) != remaining()))
		{
			return;
		}
		
		_items = new ManufactureItem[count];
		for (int i = 0; i < count; i++)
		{
			final int id = readInt();
			final long cost = readLong();
			if (cost < 0)
			{
				_items = null;
				return;
			}
			_items[i] = new ManufactureItem(id, cost);
		}
	}
	
	@Override
	protected void runImpl()
	{
		final Player player = getPlayer();
		if (player == null)
		{
			return;
		}
		
		if (_items == null)
		{
			player.setPrivateStoreType(PrivateStoreType.NONE);
			player.broadcastUserInfo();
			return;
		}
		
		if (AttackStanceTaskManager.getInstance().hasAttackStanceTask(player) || player.isInDuel())
		{
			player.sendPacket(SystemMessageId.WHILE_YOU_ARE_ENGAGED_IN_COMBAT_YOU_CANNOT_OPERATE_A_PRIVATE_STORE_OR_PRIVATE_WORKSHOP);
			player.sendPacket(ActionFailed.STATIC_PACKET);
			return;
		}
		
		if (player.isInsideZone(ZoneId.NO_STORE))
		{
			player.sendPacket(SystemMessageId.YOU_CANNOT_OPEN_A_PRIVATE_WORKSHOP_HERE);
			player.sendPacket(ActionFailed.STATIC_PACKET);
			return;
		}
		
		player.getManufactureItems().clear();
		for (ManufactureItem i : _items)
		{
			final RecipeList list = RecipeData.getInstance().getRecipeList(i.getRecipeId());
			if (!player.getDwarvenRecipeBook().contains(list) && !player.getCommonRecipeBook().contains(list))
			{
				Util.handleIllegalPlayerAction(player, "Warning!! " + player + " of account " + player.getAccountName() + " tried to set recipe which he does not have.", Config.DEFAULT_PUNISH);
				return;
			}
			
			if (i.getCost() > MAX_ADENA)
			{
				Util.handleIllegalPlayerAction(player, "Warning!! " + player + " of account " + player.getAccountName() + " tried to set price more than " + MAX_ADENA + " adena in Private Manufacture.", Config.DEFAULT_PUNISH);
				return;
			}
			
			player.getManufactureItems().put(i.getRecipeId(), i);
		}
		
		player.setStoreName(!player.hasManufactureShop() ? "" : player.getStoreName());
		player.setPrivateStoreType(PrivateStoreType.MANUFACTURE);
		player.sitDown();
		player.broadcastUserInfo();
		Broadcast.toSelfAndKnownPlayers(player, new RecipeShopMsg(player));
	}
}
