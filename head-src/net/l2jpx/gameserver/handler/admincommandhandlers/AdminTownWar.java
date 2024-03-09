package net.l2jpx.gameserver.handler.admincommandhandlers;

import net.l2jpx.gameserver.handler.IAdminCommandHandler;
import net.l2jpx.gameserver.model.actor.instance.L2PcInstance;
import net.l2jpx.gameserver.model.entity.event.TownWar;

/**
 * @author ReynalDev
 */
public class AdminTownWar implements IAdminCommandHandler
{
	private static final String[] ADMIN_COMMANDS =
	{
		"admin_townwar_start",
		"admin_townwar_end"
	};
	
	@Override
	public boolean useAdminCommand(String command, L2PcInstance activeChar)
	{
		if (command == null || activeChar == null)
		{
			return false;
		}
		
		if (command.startsWith("admin_townwar_start"))
		{
			TownWar.getInstance().start();
		}
		else if (command.startsWith("admin_townwar_end"))
		{
			TownWar.getInstance().end();
		}
		
		return true;
	}
	
	@Override
	public String[] getAdminCommandList()
	{
		return ADMIN_COMMANDS;
	}
}