package com.l2jpx.gameserver.network.clientpackets;

import com.l2jpx.gameserver.data.cache.CrestCache;
import com.l2jpx.gameserver.data.cache.CrestCache.CrestType;
import com.l2jpx.gameserver.data.sql.ClanTable;
import com.l2jpx.gameserver.idfactory.IdFactory;
import com.l2jpx.gameserver.model.actor.Player;
import com.l2jpx.gameserver.model.pledge.Clan;
import com.l2jpx.gameserver.network.SystemMessageId;

public final class RequestSetAllyCrest extends L2GameClientPacket
{
	private int _length;
	private byte[] _data;
	
	@Override
	protected void readImpl()
	{
		_length = readD();
		if (_length > 192)
			return;
		
		_data = new byte[_length];
		readB(_data);
	}
	
	@Override
	protected void runImpl()
	{
		if (_length < 0 || _length > 192)
			return;
		
		final Player player = getClient().getPlayer();
		if (player == null || player.getAllyId() == 0)
			return;
		
		final Clan clan = ClanTable.getInstance().getClan(player.getAllyId());
		if (player.getClanId() != clan.getClanId() || !player.isClanLeader())
			return;
		
		if (_length == 0 || _data.length == 0)
		{
			if (clan.getAllyCrestId() != 0)
			{
				clan.changeAllyCrest(0, false);
				player.sendPacket(SystemMessageId.CLAN_CREST_HAS_BEEN_DELETED);
			}
		}
		else
		{
			final int crestId = IdFactory.getInstance().getNextId();
			if (CrestCache.getInstance().saveCrest(CrestType.ALLY, crestId, _data))
			{
				clan.changeAllyCrest(crestId, false);
				player.sendPacket(SystemMessageId.CLAN_EMBLEM_WAS_SUCCESSFULLY_REGISTERED);
			}
		}
	}
}