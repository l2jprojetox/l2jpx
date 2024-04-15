package com.px.gameserver.network.serverpackets;

import java.util.Calendar;

import com.px.gameserver.data.sql.ClanTable;
import com.px.gameserver.model.actor.Player;
import com.px.gameserver.model.entity.Castle;
import com.px.gameserver.model.pledge.Clan;

public class SiegeInfo extends L2GameServerPacket
{
	private final Castle _castle;
	
	public SiegeInfo(Castle castle)
	{
		_castle = castle;
	}
	
	@Override
	protected final void writeImpl()
	{
		final Player player = getClient().getPlayer();
		if (player == null)
			return;
		
		writeC(0xc9);
		writeD(_castle.getCastleId());
		writeD((_castle.getOwnerId() == player.getClanId() && player.isClanLeader()) ? 0x01 : 0x00);
		writeD(_castle.getOwnerId());
		
		Clan clan = null;
		if (_castle.getOwnerId() > 0)
			clan = ClanTable.getInstance().getClan(_castle.getOwnerId());
		
		if (clan != null)
		{
			writeS(clan.getName());
			writeS(clan.getLeaderName());
			writeD(clan.getAllyId());
			writeS(clan.getAllyName());
		}
		else
		{
			writeS("NPC");
			writeS("");
			writeD(0);
			writeS("");
		}
		
		writeD((int) (Calendar.getInstance().getTimeInMillis() / 1000));
		writeD((int) (_castle.getSiege().getSiegeDate().getTimeInMillis() / 1000));
		writeD(0x00);
	}
}