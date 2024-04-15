package com.px.gameserver.network.clientpackets;

import com.px.gameserver.model.actor.Player;
import com.px.gameserver.model.pledge.Clan;
import com.px.gameserver.network.serverpackets.PledgePowerGradeList;

public final class RequestPledgePowerGradeList extends L2GameClientPacket
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
		
		final Clan clan = player.getClan();
		if (clan == null)
			return;
		
		player.sendPacket(new PledgePowerGradeList(clan.getPriviledges().keySet(), clan.getMembers()));
	}
}