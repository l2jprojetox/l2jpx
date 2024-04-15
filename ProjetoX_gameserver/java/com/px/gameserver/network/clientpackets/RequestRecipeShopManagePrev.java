package com.px.gameserver.network.clientpackets;

import com.px.gameserver.model.actor.Player;
import com.px.gameserver.network.serverpackets.ActionFailed;
import com.px.gameserver.network.serverpackets.RecipeShopSellList;

public final class RequestRecipeShopManagePrev extends L2GameClientPacket
{
	@Override
	protected void readImpl()
	{
	}
	
	@Override
	protected void runImpl()
	{
		final Player player = getClient().getPlayer();
		if (player == null)
			return;
		
		// Player shouldn't be able to set stores if he/she is alike dead (dead or fake death)
		if (player.isAlikeDead())
		{
			sendPacket(ActionFailed.STATIC_PACKET);
			return;
		}
		
		if (!(player.getTarget() instanceof Player))
			return;
		
		player.sendPacket(new RecipeShopSellList(player, (Player) player.getTarget()));
	}
}