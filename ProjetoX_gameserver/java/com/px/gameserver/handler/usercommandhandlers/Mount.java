package com.px.gameserver.handler.usercommandhandlers;

import com.px.gameserver.handler.IUserCommandHandler;
import com.px.gameserver.model.actor.Player;

public class Mount implements IUserCommandHandler
{
	private static final int[] COMMAND_IDS =
	{
		61
	};
	
	@Override
	public boolean useUserCommand(int id, Player activeChar)
	{
		return activeChar.mountPlayer(activeChar.getSummon());
	}
	
	@Override
	public int[] getUserCommandList()
	{
		return COMMAND_IDS;
	}
}