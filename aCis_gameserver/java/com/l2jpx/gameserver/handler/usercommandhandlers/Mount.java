package com.l2jpx.gameserver.handler.usercommandhandlers;

import com.l2jpx.gameserver.handler.IUserCommandHandler;
import com.l2jpx.gameserver.model.actor.Player;

public class Mount implements IUserCommandHandler
{
	private static final int[] COMMAND_IDS =
	{
		61
	};
	
	@Override
	public void useUserCommand(int id, Player player)
	{
		player.mountPlayer(player.getSummon());
	}
	
	@Override
	public int[] getUserCommandList()
	{
		return COMMAND_IDS;
	}
}