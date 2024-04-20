package com.l2jpx.gameserver.network.clientpackets;

import com.l2jpx.commons.math.MathUtil;

import com.l2jpx.gameserver.data.xml.RecipeData;
import com.l2jpx.gameserver.enums.FloodProtector;
import com.l2jpx.gameserver.enums.actors.OperateType;
import com.l2jpx.gameserver.model.World;
import com.l2jpx.gameserver.model.actor.Player;
import com.l2jpx.gameserver.model.craft.RecipeItemMaker;
import com.l2jpx.gameserver.model.item.Recipe;
import com.l2jpx.gameserver.network.SystemMessageId;

public final class RequestRecipeShopMakeItem extends L2GameClientPacket
{
	private int _objectId;
	private int _recipeId;
	@SuppressWarnings("unused")
	private int _unknow;
	
	@Override
	protected void readImpl()
	{
		_objectId = readD();
		_recipeId = readD();
		_unknow = readD();
	}
	
	@Override
	protected void runImpl()
	{
		if (!getClient().performAction(FloodProtector.MANUFACTURE))
			return;
		
		final Player player = getClient().getPlayer();
		if (player == null)
			return;
		
		final Player manufacturer = World.getInstance().getPlayer(_objectId);
		if (manufacturer == null)
			return;
		
		if (player.isOperating())
			return;
		
		if (manufacturer.getOperateType() != OperateType.MANUFACTURE)
			return;
		
		if (player.isCrafting() || manufacturer.isCrafting())
			return;
		
		if (manufacturer.isInDuel() || player.isInDuel() || manufacturer.isInCombat() || player.isInCombat())
		{
			player.sendPacket(SystemMessageId.CANT_OPERATE_PRIVATE_STORE_DURING_COMBAT);
			return;
		}
		
		if (!MathUtil.checkIfInRange(150, player, manufacturer, true))
			return;
		
		final Recipe recipe = RecipeData.getInstance().getRecipeList(_recipeId);
		if (recipe == null)
			return;
		
		if (!manufacturer.getRecipeBook().hasRecipeOnSpecificBook(_recipeId, recipe.isDwarven()))
			return;
		
		final RecipeItemMaker maker = new RecipeItemMaker(manufacturer, recipe, player);
		if (maker._isValid)
			maker.run();
	}
}