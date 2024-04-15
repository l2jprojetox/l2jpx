package com.px.gameserver.network.clientpackets;

import com.px.gameserver.data.cache.CrestCache;
import com.px.gameserver.data.cache.CrestCache.CrestType;
import com.px.gameserver.model.actor.Player;
import com.px.gameserver.network.serverpackets.AllyCrest;

public final class RequestAllyCrest extends L2GameClientPacket
{
	private int _crestId;
	
	@Override
	protected void readImpl()
	{
		_crestId = readD();
	}
	
	@Override
	protected void runImpl()
	{
		final Player player = getClient().getPlayer();
		if (player == null)
			return;
		
		final byte[] data = CrestCache.getInstance().getCrest(CrestType.ALLY, _crestId);
		if (data == null)
			return;
		
		player.sendPacket(new AllyCrest(_crestId, data));
	}
}