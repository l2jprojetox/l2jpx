package com.px.gameserver.network.clientpackets;

import com.px.gameserver.model.WorldObject;
import com.px.gameserver.model.actor.Player;

public final class RequestChangeWaitType extends L2GameClientPacket
{
	private boolean _typeStand;
	
	@Override
	protected void readImpl()
	{
		_typeStand = (readD() == 1);
	}
	
	@Override
	protected void runImpl()
	{
		final Player player = getClient().getPlayer();
		if (player == null)
			return;
		
		final WorldObject target = player.getTarget();
		
		if (_typeStand)
			player.getAI().tryToStand();
		else
			player.getAI().tryToSit(target);
	}
}