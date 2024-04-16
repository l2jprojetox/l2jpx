package com.px.gameserver.network.clientpackets;

import com.px.gameserver.data.sql.ClanTable;
import com.px.gameserver.model.actor.Player;
import com.px.gameserver.model.pledge.Clan;
import com.px.gameserver.network.serverpackets.PledgeInfo;
import com.px.gameserver.network.serverpackets.PledgeStatusChanged;

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