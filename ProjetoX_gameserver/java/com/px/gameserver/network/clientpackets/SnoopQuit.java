package com.px.gameserver.network.clientpackets;

import com.px.gameserver.model.World;
import com.px.gameserver.model.actor.Player;

/**
 * @author -Wooden-
 */
public final class SnoopQuit extends L2GameClientPacket
{
	private int _snoopID;
	
	@Override
	protected void readImpl()
	{
		_snoopID = readD();
	}
	
	@Override
	protected void runImpl()
	{
		final Player activeChar = getClient().getPlayer();
		if (activeChar == null)
			return;
		
		final Player target = World.getInstance().getPlayer(_snoopID);
		if (target == null)
			return;
		
		// No use
	}
}