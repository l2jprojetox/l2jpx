package net.l2jpx.gameserver.handler.usercommandhandlers;

import net.l2jpx.gameserver.handler.IUserCommandHandler;
import net.l2jpx.gameserver.model.actor.instance.L2PcInstance;

public class SiegeStatus implements IUserCommandHandler
{
	private static final int[] COMMAND_IDS =
	{
		99
	};
	
	@Override
	public boolean useUserCommand(final int id, final L2PcInstance activeChar)
	{
		activeChar.sendMessage("Command /siegestatus not implemented yet.");
		return true;
	}
	
	@Override
	public int[] getUserCommandList()
	{
		return COMMAND_IDS;
	}
}