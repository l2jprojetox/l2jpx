package com.px.gameserver.network.clientpackets;

import com.px.gameserver.model.actor.Player;
import com.px.gameserver.model.pledge.Clan;
import com.px.gameserver.model.pledge.ClanMember;
import com.px.gameserver.network.serverpackets.PledgeReceiveMemberInfo;

/**
 * Format: (ch) dS
 * @author -Wooden-
 */
public final class RequestPledgeMemberInfo extends L2GameClientPacket
{
	@SuppressWarnings("unused")
	private int _pledgeType;
	private String _player;
	
	@Override
	protected void readImpl()
	{
		_pledgeType = readD();
		_player = readS();
	}
	
	@Override
	protected void runImpl()
	{
		final Player activeChar = getClient().getPlayer();
		if (activeChar == null)
			return;
		
		// do we need powers to do that??
		final Clan clan = activeChar.getClan();
		if (clan == null)
			return;
		
		final ClanMember member = clan.getClanMember(_player);
		if (member == null)
			return;
		
		activeChar.sendPacket(new PledgeReceiveMemberInfo(member));
	}
}