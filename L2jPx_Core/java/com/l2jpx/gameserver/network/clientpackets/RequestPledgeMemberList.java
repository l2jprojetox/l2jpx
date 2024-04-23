package com.l2jpx.gameserver.network.clientpackets;

import com.l2jpx.gameserver.model.actor.Player;
import com.l2jpx.gameserver.model.pledge.Clan;
import com.l2jpx.gameserver.model.pledge.SubPledge;
import com.l2jpx.gameserver.network.serverpackets.PledgeShowMemberListAll;

public final class RequestPledgeMemberList extends L2GameClientPacket
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
		
		player.sendPacket(new PledgeShowMemberListAll(clan, 0));
		
		for (SubPledge sp : clan.getAllSubPledges())
			player.sendPacket(new PledgeShowMemberListAll(clan, sp.getId()));
	}
}