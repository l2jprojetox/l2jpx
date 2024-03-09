package net.l2jpx.gameserver.handler.admincommandhandlers;

import net.l2jpx.gameserver.handler.IAdminCommandHandler;
import net.l2jpx.gameserver.model.actor.instance.L2PcInstance;
import net.l2jpx.gameserver.model.entity.event.ChristmasPresents;

/**
 * @version $Revision: 1.2.4.4 $ $Date: 2007/07/31 10:06:02 $
 */
public class AdminChristmas implements IAdminCommandHandler
{
	// private final static Logger LOGGER = LogFactory.getLog(AdminChristmas.class);
	
	private static final String[] ADMIN_COMMANDS =
	{
		"admin_christmas_start",
		"admin_christmas_end"
	};
	
	@Override
	public boolean useAdminCommand(String command, L2PcInstance activeChar)
	{
		if (command == null)
		{
			return false;
		}
		
		if (activeChar == null)
		{
			return false;
		}
		
		if (command.equals("admin_christmas_start"))
		{
			ChristmasPresents.getInstance().init(activeChar);
		}
		else if (command.equals("admin_christmas_end"))
		{
			ChristmasPresents.getInstance().end(activeChar);
		}
		
		return true;
	}
	
	@Override
	public String[] getAdminCommandList()
	{
		return ADMIN_COMMANDS;
	}
}
