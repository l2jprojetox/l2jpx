package net.l2jpx.gameserver.handler.voicedcommandhandlers;

import net.l2jpx.gameserver.handler.IVoicedCommandHandler;
import net.l2jpx.gameserver.model.L2World;
import net.l2jpx.gameserver.model.actor.instance.L2PcInstance;

public class Online implements IVoicedCommandHandler
{
	private static String[] voicedCommands =
	{
		"online"
	};
	
	@Override
	public boolean useVoicedCommand(final String command, final L2PcInstance activeChar, final String target)
	{
		if (command.equalsIgnoreCase("online"))
		{
			activeChar.sendMessage("======<Players Online!>======");
			activeChar.sendMessage("There are " + L2World.getInstance().getAllPlayers().size() + " players online!.");
			activeChar.sendMessage("=======================");
		}
		return true;
	}
	
	@Override
	public String[] getVoicedCommandList()
	{
		return voicedCommands;
	}
}
