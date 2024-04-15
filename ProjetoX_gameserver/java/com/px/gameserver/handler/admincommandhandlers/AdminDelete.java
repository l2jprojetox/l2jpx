package com.px.gameserver.handler.admincommandhandlers;

import com.px.gameserver.data.manager.RaidBossManager;
import com.px.gameserver.data.sql.SpawnTable;
import com.px.gameserver.handler.IAdminCommandHandler;
import com.px.gameserver.model.WorldObject;
import com.px.gameserver.model.actor.Npc;
import com.px.gameserver.model.actor.Player;
import com.px.gameserver.model.spawn.L2Spawn;
import com.px.gameserver.network.SystemMessageId;

/**
 * This class handles following admin commands: - delete = deletes target
 */
public class AdminDelete implements IAdminCommandHandler
{
	private static final String[] ADMIN_COMMANDS =
	{
		"admin_delete"
	};
	
	@Override
	public boolean useAdminCommand(String command, Player activeChar)
	{
		if (command.equals("admin_delete"))
			handleDelete(activeChar);
		
		return true;
	}
	
	@Override
	public String[] getAdminCommandList()
	{
		return ADMIN_COMMANDS;
	}
	
	private static void handleDelete(Player activeChar)
	{
		WorldObject obj = activeChar.getTarget();
		if (obj != null && obj instanceof Npc)
		{
			Npc target = (Npc) obj;
			
			L2Spawn spawn = target.getSpawn();
			if (spawn != null)
			{
				spawn.setRespawnState(false);
				
				if (RaidBossManager.getInstance().getBossSpawn(spawn.getNpcId()) != null)
					RaidBossManager.getInstance().deleteSpawn(spawn);
				else
					SpawnTable.getInstance().deleteSpawn(spawn, true);
			}
			target.deleteMe();
			
			activeChar.sendMessage("Deleted " + target.getName() + " from " + target.getObjectId() + ".");
		}
		else
			activeChar.sendPacket(SystemMessageId.INCORRECT_TARGET);
	}
}