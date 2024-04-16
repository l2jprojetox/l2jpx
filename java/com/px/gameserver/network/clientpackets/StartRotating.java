package com.px.gameserver.network.clientpackets;

import com.px.gameserver.model.actor.Player;
import com.px.gameserver.network.serverpackets.StartRotation;

public final class StartRotating extends L2GameClientPacket
{
	private int _degree;
	private int _side;
	
	@Override
	protected void readImpl()
	{
		_degree = readD();
		_side = readD();
	}
	
	@Override
	protected void runImpl()
	{
		final Player player = getClient().getPlayer();
		if (player == null)
			return;
		
		player.broadcastPacket(new StartRotation(player.getObjectId(), _degree, _side, 0));
	}
}