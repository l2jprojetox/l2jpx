package com.px.gameserver.network.clientpackets;

import com.px.gameserver.data.xml.RecipeData;
import com.px.gameserver.enums.actors.StoreType;
import com.px.gameserver.model.actor.Player;
import com.px.gameserver.model.craft.RecipeItemMaker;
import com.px.gameserver.model.item.Recipe;
import com.px.gameserver.network.FloodProtectors;
import com.px.gameserver.network.FloodProtectors.Action;
import com.px.gameserver.network.SystemMessageId;

public final class RequestRecipeItemMakeSelf extends L2GameClientPacket
{
	private int _recipeId;
	
	@Override
	protected void readImpl()
	{
		_recipeId = readD();
	}
	
	@Override
	protected void runImpl()
	{
		if (!FloodProtectors.performAction(getClient(), Action.MANUFACTURE))
			return;
		
		final Player player = getClient().getPlayer();
		if (player == null)
			return;
		
		if (player.getStoreType() == StoreType.MANUFACTURE || player.isCrafting())
			return;
		
		if (player.isInDuel() || player.isInCombat())
		{
			player.sendPacket(SystemMessageId.CANT_OPERATE_PRIVATE_STORE_DURING_COMBAT);
			return;
		}
		
		final Recipe recipe = RecipeData.getInstance().getRecipeList(_recipeId);
		if (recipe == null)
			return;
		
		if (recipe.isDwarven())
		{
			if (!player.getDwarvenRecipeBook().contains(recipe))
				return;
		}
		else
		{
			if (!player.getCommonRecipeBook().contains(recipe))
				return;
		}
		
		final RecipeItemMaker maker = new RecipeItemMaker(player, recipe, player);
		if (maker._isValid)
			maker.run();
	}
}