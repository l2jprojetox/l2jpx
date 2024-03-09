package net.l2jpx.gameserver.handler.admincommandhandlers;

import java.util.StringTokenizer;

import engine.Instance.InstanceManager;
import net.l2jpx.gameserver.handler.IAdminCommandHandler;
import net.l2jpx.gameserver.model.L2World;
import net.l2jpx.gameserver.model.actor.instance.L2PcInstance;

/**
 * @author Administrador
 */
public class AdminInstance implements IAdminCommandHandler
{
	
	@Override
	public boolean useAdminCommand(String command, L2PcInstance activeChar)
	{
		if (command.startsWith("admin_resetmyinstance"))
		{
			activeChar.setInstance(InstanceManager.getInstance().getInstance(0), false);
			activeChar.sendMessage("Your instance is now default");
		}
		else if (command.startsWith("admin_instanceid"))
		{
			StringTokenizer st = new StringTokenizer(command, " ");
			st.nextToken(); // skip command
			
			if (!st.hasMoreTokens())
			{
				activeChar.sendMessage("Write the name.");
				return false;
			}
			
			String target_name = st.nextToken();
			L2PcInstance player = L2World.getInstance().getPlayerByName(target_name);
			if (player == null)
			{
				activeChar.sendMessage("Player is offline");
				return false;
			}
			
			activeChar.sendMessage("" + target_name + " instance id: " + player.getInstance().getId());
		}
		else if (command.startsWith("admin_getinstance"))
		{
			StringTokenizer st = new StringTokenizer(command, " ");
			st.nextToken(); // skip command
			
			if (!st.hasMoreTokens())
			{
				activeChar.sendMessage("Write the name.");
				return false;
			}
			
			String target_name = st.nextToken();
			L2PcInstance player = L2World.getInstance().getPlayerByName(target_name);
			if (player == null)
			{
				activeChar.sendMessage("Player is offline");
				return false;
			}
			
			activeChar.setInstance(player.getInstance(), false);
			activeChar.sendMessage("You are with the same instance of player " + target_name);
		}
		return false;
	}
	
	@Override
	public String[] getAdminCommandList()
	{
		
		return new String[]
		{
			"admin_resetmyinstance",
			"admin_getinstance",
			"admin_instanceid"
		};
	}
	
}
