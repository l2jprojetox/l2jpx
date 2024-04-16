package com.px.gameserver.network.clientpackets;

import com.px.gameserver.model.actor.Player;
import com.px.gameserver.model.pledge.Clan;
import com.px.gameserver.model.pledge.ClanMember;
import com.px.gameserver.network.serverpackets.PledgeReceiveMemberInfo;

public final class RequestPledgeMemberInfo extends L2GameClientPacket
{
	private String _player;
	
	@Override
	protected void readImpl()
	{
		readD(); // Not used for security reason. Pledge type.
		_player = readS();
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
		
		final ClanMember member = clan.getClanMember(_player);
		if (member == null)
			return;
		
		player.sendPacket(new PledgeReceiveMemberInfo(member));
	}
}