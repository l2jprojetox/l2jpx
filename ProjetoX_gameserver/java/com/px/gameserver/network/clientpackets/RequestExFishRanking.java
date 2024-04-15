package com.px.gameserver.network.clientpackets;

import com.px.Config;
import com.px.gameserver.data.manager.FishingChampionshipManager;
import com.px.gameserver.model.actor.Player;

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
		
		if (Config.ALT_FISH_CHAMPIONSHIP_ENABLED)
			FishingChampionshipManager.getInstance().showMidResult(player);
	}
}