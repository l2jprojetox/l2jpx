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

import org.l2jpx.gameserver.enums.PrivateStoreType;
import org.l2jpx.gameserver.instancemanager.RecipeManager;
import org.l2jpx.gameserver.model.World;
import org.l2jpx.gameserver.model.actor.Player;
import org.l2jpx.gameserver.util.Util;

/**
 * @author Administrator
 */
public class RequestRecipeShopMakeItem extends ClientPacket
{
	private int _id;
	private int _recipeId;
	@SuppressWarnings("unused")
	private long _unknown;
	
	@Override
	protected void readImpl()
	{
		_id = readInt();
		_recipeId = readInt();
		_unknown = readLong();
	}
	
	@Override
	protected void runImpl()
	{
		final Player player = getPlayer();
		if (player == null)
		{
			return;
		}
		
		if (!getClient().getFloodProtectors().canManufacture())
		{
			return;
		}
		
		final Player manufacturer = World.getInstance().getPlayer(_id);
		if (manufacturer == null)
		{
			return;
		}
		
		if (manufacturer.getInstanceWorld() != player.getInstanceWorld())
		{
			return;
		}
		
		if (player.isInStoreMode())
		{
			player.sendMessage("You cannot create items while trading.");
			return;
		}
		if (manufacturer.getPrivateStoreType() != PrivateStoreType.MANUFACTURE)
		{
			// player.sendMessage("You cannot create items while trading.");
			return;
		}
		
		if (player.isCrafting() || manufacturer.isCrafting())
		{
			player.sendMessage("You are currently in Craft Mode.");
			return;
		}
		if (Util.checkIfInRange(150, player, manufacturer, true))
		{
			RecipeManager.getInstance().requestManufactureItem(manufacturer, _recipeId, player);
		}
	}
}
