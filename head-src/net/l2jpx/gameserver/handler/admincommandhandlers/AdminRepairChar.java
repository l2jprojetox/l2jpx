package net.l2jpx.gameserver.handler.admincommandhandlers;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.apache.log4j.Logger;

import net.l2jpx.gameserver.handler.IAdminCommandHandler;
import net.l2jpx.gameserver.model.actor.instance.L2PcInstance;
import net.l2jpx.util.database.L2DatabaseFactory;

/**
 * @author ReynalDev
 */
public class AdminRepairChar implements IAdminCommandHandler
{
	private static Logger LOGGER = Logger.getLogger(AdminRepairChar.class);
	private static final String UPDATE_CHARACTER_LOCATION = "UPDATE characters SET x=-84318, y=244579, z=-3730 WHERE char_name=?";
	private static final String SELECT_CHAR_OBJ_ID_BY_CHAR_NAME = "SELECT obj_id FROM characters where char_name=?";
	private static final String DELETE_CHARACTER_SHORTCUTS_BY_CHAR_OBJ_ID = "DELETE FROM character_shortcuts WHERE char_obj_id=?";
	private static final String UPDATE_ITEMS_LOCATION_TO_INVENTORY_BY_CHAR_OBJ_ID = "UPDATE items SET loc=\"INVENTORY\" WHERE owner_id=?";
	
	private static final String[] ADMIN_COMMANDS =
	{
		"admin_restore",
		"admin_repair"
	};
	
	@Override
	public boolean useAdminCommand(String command, L2PcInstance activeChar)
	{
		handleRepair(command);
		
		return true;
	}
	
	@Override
	public String[] getAdminCommandList()
	{
		return ADMIN_COMMANDS;
	}
	
	private void handleRepair(String command)
	{
		String[] parts = command.split(" ");
		
		if (parts.length != 2)
		{
			return;
		}
		
		try(Connection connection = L2DatabaseFactory.getInstance().getConnection())
		{
			
			try(PreparedStatement statement = connection.prepareStatement(UPDATE_CHARACTER_LOCATION))
			{
				statement.setString(1, parts[1]);
				statement.executeUpdate();
			}
			
			int objId = 0;
			
			try(PreparedStatement statement = connection.prepareStatement(SELECT_CHAR_OBJ_ID_BY_CHAR_NAME))
			{
				statement.setString(1, parts[1]);
				
				try(ResultSet rset = statement.executeQuery())
				{
					if (rset.next())
					{
						objId = rset.getInt(1);
					}
				}
			}
			
			if (objId > 0)
			{
				try(PreparedStatement statement = connection.prepareStatement(DELETE_CHARACTER_SHORTCUTS_BY_CHAR_OBJ_ID))
				{
					statement.setInt(1, objId);
					statement.executeUpdate();
				}
				
				try(PreparedStatement statement = connection.prepareStatement(UPDATE_ITEMS_LOCATION_TO_INVENTORY_BY_CHAR_OBJ_ID))
				{
					statement.setInt(1, objId);
					statement.executeUpdate();
				}
			}
		}
		catch (Exception e)
		{
			LOGGER.warn("AdminRepairChar.handleRepair: Could not repair character", e);
		}
	}
}
