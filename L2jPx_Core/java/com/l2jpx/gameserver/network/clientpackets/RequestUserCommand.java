package com.l2jpx.gameserver.network.clientpackets;

import com.l2jpx.gameserver.handler.IUserCommandHandler;
import com.l2jpx.gameserver.handler.UserCommandHandler;
import com.l2jpx.gameserver.model.actor.Player;

public class RequestUserCommand extends L2GameClientPacket
{
	private int _commandId;
	
	@Override
	protected void readImpl()
	{
		_commandId = readD();
	}
	
	@Override
	protected void runImpl()
	{
		final Player player = getClient().getPlayer();
		if (player == null)
			return;
		
		final IUserCommandHandler handler = UserCommandHandler.getInstance().getHandler(_commandId);
		if (handler != null)
			handler.useUserCommand(_commandId, player);
	}
}