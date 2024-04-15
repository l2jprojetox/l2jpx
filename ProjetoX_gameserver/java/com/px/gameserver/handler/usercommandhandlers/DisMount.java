package com.px.gameserver.handler.usercommandhandlers;

import com.px.gameserver.handler.IUserCommandHandler;
import com.px.gameserver.model.actor.Player;

public class DisMount implements IUserCommandHandler
{
	private static final int[] COMMAND_IDS =
	{
		62
	};
	
	@Override
	public boolean useUserCommand(int id, Player activeChar)
	{
		if (activeChar.isMounted())
			activeChar.dismount();
		
		return true;
	}
	
	@Override
	public int[] getUserCommandList()
	{
		return COMMAND_IDS;
	}
}