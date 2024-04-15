package com.px.gameserver.handler.admincommandhandlers;

import com.px.gameserver.handler.IAdminCommandHandler;
import com.px.gameserver.model.actor.Player;
import com.px.gameserver.network.serverpackets.NpcHtmlMessage;

/**
 * This class handles following admin commands: - help path = shows /data/html/admin/path file to char, should not be used by GM's directly
 */
public class AdminHelpPage implements IAdminCommandHandler
{
	private static final String[] ADMIN_COMMANDS =
	{
		"admin_help"
	};
	
	@Override
	public boolean useAdminCommand(String command, Player activeChar)
	{
		if (command.startsWith("admin_help"))
		{
			try
			{
				String val = command.substring(11);
				showHelpPage(activeChar, val);
			}
			catch (StringIndexOutOfBoundsException e)
			{
			}
		}
		
		return true;
	}
	
	@Override
	public String[] getAdminCommandList()
	{
		return ADMIN_COMMANDS;
	}
	
	// FIXME: implement method to send html to player in Player directly
	// PUBLIC & STATIC so other classes from package can include it directly
	public static void showHelpPage(Player targetChar, String filename)
	{
		final NpcHtmlMessage html = new NpcHtmlMessage(0);
		html.setFile("data/html/admin/" + filename);
		targetChar.sendPacket(html);
	}
}