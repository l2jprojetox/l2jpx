package net.l2jpx.gameserver.handler.admincommandhandlers;

import org.apache.log4j.Logger;

import net.l2jpx.gameserver.datatables.csv.DoorTable;
import net.l2jpx.gameserver.handler.IAdminCommandHandler;
import net.l2jpx.gameserver.managers.CastleManager;
import net.l2jpx.gameserver.model.L2Object;
import net.l2jpx.gameserver.model.actor.instance.L2DoorInstance;
import net.l2jpx.gameserver.model.actor.instance.L2PcInstance;
import net.l2jpx.gameserver.model.entity.siege.Castle;

/**
 * @author ProGramMoS
 * @author ReynalDev
 */
public class AdminDoorControl implements IAdminCommandHandler
{
	private static final Logger LOGGER = Logger.getLogger(AdminDoorControl.class);
	private static DoorTable doorTable;
	private static final String[] ADMIN_COMMANDS =
	{
		"admin_open",
		"admin_close",
		"admin_openall",
		"admin_closeall"
	};
	
	@Override
	public boolean useAdminCommand(final String command, final L2PcInstance activeChar)
	{
		doorTable = DoorTable.getInstance();
		
		L2Object target2 = null;
		
		if (command.startsWith("admin_close ")) // id
		{
			try
			{
				int doorId = Integer.parseInt(command.substring(12));
				
				if (doorTable.getDoor(doorId) != null)
				{
					doorTable.getDoor(doorId).closeMe();
				}
				else
				{
					for (Castle castle : CastleManager.getInstance().getCastles())
					{
						if (castle.getDoor(doorId) != null)
						{
							castle.getDoor(doorId).closeMe();
						}
					}
				}
			}
			catch (Exception e)
			{
				LOGGER.error("Something went wrong for the admin command admin_close", e);
				return false;
			}
		}
		else if (command.equals("admin_close")) // target
		{
			target2 = activeChar.getTarget();
			
			if (target2 instanceof L2DoorInstance)
			{
				((L2DoorInstance) target2).closeMe();
			}
			else
			{
				activeChar.sendMessage("Incorrect target.");
			}
			
			target2 = null;
		}
		else if (command.startsWith("admin_open ")) // id
		{
			try
			{
				final int doorId = Integer.parseInt(command.substring(11));
				
				if (doorTable.getDoor(doorId) != null)
				{
					doorTable.getDoor(doorId).openMe();
				}
				else
				{
					for (final Castle castle : CastleManager.getInstance().getCastles())
					{
						if (castle.getDoor(doorId) != null)
						{
							castle.getDoor(doorId).openMe();
						}
					}
				}
			}
			catch (Exception e)
			{
				activeChar.sendMessage("Wrong ID door.");
				LOGGER.error("AdminDoorControl error in command admin_open", e);
				return false;
			}
		}
		else if (command.equals("admin_open")) // target
		{
			target2 = activeChar.getTarget();
			
			if (target2 instanceof L2DoorInstance)
			{
				((L2DoorInstance) target2).openMe();
			}
			else
			{
				activeChar.sendMessage("Incorrect target.");
			}
			
			target2 = null;
		}
		
		// need optimize cycle
		// set limits on the ID doors that do not cycle to close doors
		else if (command.equals("admin_closeall"))
		{
			try
			{
				for (L2DoorInstance door : doorTable.getDoors())
				{
					door.closeMe();
				}
				
				for (Castle castle : CastleManager.getInstance().getCastles())
				{
					for (L2DoorInstance door : castle.getDoors())
					{
						door.closeMe();
					}
				}
			}
			catch (Exception e)
			{
				LOGGER.error("Something went wrong for the admin command admin_closeall", e);
				return false;
			}
		}
		else if (command.equals("admin_openall"))
		{
			// need optimize cycle
			// set limits on the PH door to do a cycle of opening doors.
			try
			{
				for (L2DoorInstance door : doorTable.getDoors())
				{
					door.openMe();
				}
				
				for (Castle castle : CastleManager.getInstance().getCastles())
				{
					for (L2DoorInstance door : castle.getDoors())
					{
						door.openMe();
					}
				}
			}
			catch (Exception e)
			{
				LOGGER.error("Something went wrong for the admin command admin_openall", e);
				return false;
			}
		}
		
		return true;
	}
	
	@Override
	public String[] getAdminCommandList()
	{
		return ADMIN_COMMANDS;
	}
}
