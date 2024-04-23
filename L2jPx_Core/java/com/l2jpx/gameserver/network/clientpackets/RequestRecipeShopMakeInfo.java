package com.l2jpx.gameserver.network.clientpackets;

import com.l2jpx.gameserver.enums.actors.OperateType;
import com.l2jpx.gameserver.model.World;
import com.l2jpx.gameserver.model.actor.Player;
import com.l2jpx.gameserver.network.serverpackets.RecipeShopItemInfo;

public final class RequestRecipeShopMakeInfo extends L2GameClientPacket
{
	private int _objectId;
	private int _recipeId;
	
	@Override
	protected void readImpl()
	{
		_objectId = readD();
		_recipeId = readD();
	}
	
	@Override
	protected void runImpl()
	{
		final Player player = getClient().getPlayer();
		if (player == null)
			return;
		
		final Player manufacturer = World.getInstance().getPlayer(_objectId);
		if (manufacturer == null || manufacturer.getOperateType() != OperateType.MANUFACTURE)
			return;
		
		player.sendPacket(new RecipeShopItemInfo(manufacturer, _recipeId));
	}
}