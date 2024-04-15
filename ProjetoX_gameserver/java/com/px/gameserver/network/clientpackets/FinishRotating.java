package com.px.gameserver.network.clientpackets;

import com.px.gameserver.model.actor.Player;
import com.px.gameserver.network.serverpackets.StopRotation;

public final class FinishRotating extends L2GameClientPacket
{
	private int _degree;
	@SuppressWarnings("unused")
	private int _unknown;
	
	@Override
	protected void readImpl()
	{
		_degree = readD();
		_unknown = readD();
	}
	
	@Override
	protected void runImpl()
	{
		final Player activeChar = getClient().getPlayer();
		if (activeChar == null)
			return;
		
		activeChar.broadcastPacket(new StopRotation(activeChar.getObjectId(), _degree, 0));
	}
}