package com.px.gameserver.network.clientpackets;

import com.px.gameserver.handler.IUserCommandHandler;
import com.px.gameserver.handler.UserCommandHandler;
import com.px.gameserver.model.actor.Player;

public class RequestUserCommand extends L2GameClientPacket
{
	private int _command;
	
	@Override
	protected void readImpl()
	{
		_command = readD();
	}
	
	@Override
	protected void runImpl()
	{
		final Player player = getClient().getPlayer();
		if (player == null)
			return;
		
		final IUserCommandHandler handler = UserCommandHandler.getInstance().getHandler(_command);
		if (handler != null)
			handler.useUserCommand(_command, getClient().getPlayer());
	}
}