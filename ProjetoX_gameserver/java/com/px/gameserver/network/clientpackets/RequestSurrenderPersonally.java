package com.px.gameserver.network.clientpackets;

import com.px.gameserver.data.sql.ClanTable;
import com.px.gameserver.model.actor.Player;
import com.px.gameserver.model.pledge.Clan;
import com.px.gameserver.network.SystemMessageId;
import com.px.gameserver.network.serverpackets.SystemMessage;

public final class RequestSurrenderPersonally extends L2GameClientPacket
{
	private String _pledgeName;
	
	@Override
	protected void readImpl()
	{
		_pledgeName = readS();
	}
	
	@Override
	protected void runImpl()
	{
		final Player activeChar = getClient().getPlayer();
		if (activeChar == null)
			return;
		
		final Clan playerClan = activeChar.getClan();
		if (playerClan == null)
			return;
		
		Clan clan = ClanTable.getInstance().getClanByName(_pledgeName);
		if (clan == null)
			return;
		
		if (!playerClan.isAtWarWith(clan.getClanId()) || activeChar.wantsPeace())
		{
			activeChar.sendPacket(SystemMessageId.FAILED_TO_PERSONALLY_SURRENDER);
			return;
		}
		
		activeChar.setWantsPeace(true);
		activeChar.deathPenalty(false, false, false);
		activeChar.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.YOU_HAVE_PERSONALLY_SURRENDERED_TO_THE_S1_CLAN).addString(_pledgeName));
		ClanTable.getInstance().checkSurrender(playerClan, clan);
	}
}