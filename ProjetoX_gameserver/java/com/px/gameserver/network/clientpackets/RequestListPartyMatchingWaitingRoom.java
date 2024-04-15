package com.px.gameserver.network.clientpackets;

import com.px.gameserver.model.actor.Player;
import com.px.gameserver.network.serverpackets.ExListPartyMatchingWaitingRoom;

public class RequestListPartyMatchingWaitingRoom extends L2GameClientPacket
{
	private static int _page;
	private static int _minlvl;
	private static int _maxlvl;
	private static int _mode; // 1 - waitlist 0 - room waitlist
	
	@Override
	protected void readImpl()
	{
		_page = readD();
		_minlvl = readD();
		_maxlvl = readD();
		_mode = readD();
	}
	
	@Override
	protected void runImpl()
	{
		final Player activeChar = getClient().getPlayer();
		if (activeChar == null)
			return;
		
		activeChar.sendPacket(new ExListPartyMatchingWaitingRoom(activeChar, _page, _minlvl, _maxlvl, _mode));
	}
}