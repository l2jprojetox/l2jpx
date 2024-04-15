package com.px.gameserver.network.clientpackets;

import com.px.gameserver.model.actor.Player;
import com.px.gameserver.network.serverpackets.HennaRemoveList;

/**
 * RequestHennaRemoveList
 * @author Tempy
 */
public final class RequestHennaRemoveList extends L2GameClientPacket
{
	@SuppressWarnings("unused")
	private int _unknown;
	
	@Override
	protected void readImpl()
	{
		_unknown = readD(); // ??
	}
	
	@Override
	protected void runImpl()
	{
		final Player activeChar = getClient().getPlayer();
		if (activeChar == null)
			return;
		
		activeChar.sendPacket(new HennaRemoveList(activeChar));
	}
}