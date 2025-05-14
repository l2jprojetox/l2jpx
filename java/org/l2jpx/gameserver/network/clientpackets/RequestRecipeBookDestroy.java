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

import org.l2jpx.gameserver.data.xml.RecipeData;
import org.l2jpx.gameserver.model.RecipeList;
import org.l2jpx.gameserver.model.actor.Player;
import org.l2jpx.gameserver.network.serverpackets.RecipeBookItemList;

public class RequestRecipeBookDestroy extends ClientPacket
{
	private int _recipeID;
	
	@Override
	protected void readImpl()
	{
		_recipeID = readInt();
	}
	
	@Override
	protected void runImpl()
	{
		final Player player = getPlayer();
		if (player == null)
		{
			return;
		}
		
		if (!getClient().getFloodProtectors().canPerformTransaction())
		{
			return;
		}
		
		final RecipeList rp = RecipeData.getInstance().getRecipeList(_recipeID);
		if (rp == null)
		{
			return;
		}
		player.unregisterRecipeList(_recipeID);
		
		final RecipeBookItemList response = new RecipeBookItemList(rp.isDwarvenRecipe(), player.getMaxMp());
		if (rp.isDwarvenRecipe())
		{
			response.addRecipes(player.getDwarvenRecipeBook());
		}
		else
		{
			response.addRecipes(player.getCommonRecipeBook());
		}
		
		player.sendPacket(response);
	}
}