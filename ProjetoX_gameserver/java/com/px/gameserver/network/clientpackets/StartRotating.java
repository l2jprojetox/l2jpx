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
		final Player activeChar = getClient().getPlayer();
		if (activeChar == null)
			return;
		
		activeChar.broadcastPacket(new StartRotation(activeChar.getObjectId(), _degree, _side, 0));
	}
}