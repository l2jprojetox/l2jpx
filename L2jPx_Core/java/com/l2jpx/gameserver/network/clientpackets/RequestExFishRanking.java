package com.l2jpx.gameserver.network.clientpackets;

import com.l2jpx.Config;
import com.l2jpx.gameserver.data.manager.FishingChampionshipManager;
import com.l2jpx.gameserver.model.actor.Player;

public final class RequestExFishRanking extends L2GameClientPacket
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
		
		if (Config.ALLOW_FISH_CHAMPIONSHIP)
			FishingChampionshipManager.getInstance().showMidResult(player);
	}
}