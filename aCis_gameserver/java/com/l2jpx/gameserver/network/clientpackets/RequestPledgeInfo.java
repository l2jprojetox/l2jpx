package com.l2jpx.gameserver.network.clientpackets;

import com.l2jpx.gameserver.data.sql.ClanTable;
import com.l2jpx.gameserver.model.actor.Player;
import com.l2jpx.gameserver.model.pledge.Clan;
import com.l2jpx.gameserver.network.serverpackets.PledgeInfo;
import com.l2jpx.gameserver.network.serverpackets.PledgeStatusChanged;

public final class RequestPledgeInfo extends L2GameClientPacket
{
	private int _clanId;
	
	@Override
	protected void readImpl()
	{
		_clanId = readD();
	}
	
	@Override
	protected void runImpl()
	{
		final Player player = getClient().getPlayer();
		if (player == null)
			return;
		
		final Clan clan = ClanTable.getInstance().getClan(_clanId);
		if (clan == null)
			return;
		
		player.sendPacket(new PledgeInfo(clan));
		player.sendPacket(new PledgeStatusChanged(clan));
	}
	
	@Override
	protected boolean triggersOnActionRequest()
	{
		return false;
	}
}