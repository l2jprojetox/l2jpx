package net.l2jpx.gameserver.handler.admincommandhandlers;

import java.util.StringTokenizer;

import net.l2jpx.gameserver.communitybbs.Manager.RegionBBSManager;
import net.l2jpx.gameserver.handler.IAdminCommandHandler;
import net.l2jpx.gameserver.model.L2World;
import net.l2jpx.gameserver.model.actor.instance.L2PcInstance;
import net.l2jpx.gameserver.network.serverpackets.LeaveWorld;

public class AdminKick implements IAdminCommandHandler
{
	private static final String[] ADMIN_COMMANDS =
	{
		"admin_kick",
		"admin_kick_non_gm"
	};
	
	@Override
	public boolean useAdminCommand(String command, L2PcInstance activeChar)
	{
		if (command.startsWith("admin_kick"))
		{
			StringTokenizer st = new StringTokenizer(command);
			
			if (activeChar.getTarget() != null)
			{
				activeChar.sendMessage("Type //kick name");
			}
			
			if (st.countTokens() > 1)
			{
				st.nextToken();
				
				String player = st.nextToken();
				L2PcInstance plyr = L2World.getInstance().getPlayerByName(player);
				
				if (plyr != null)
				{
					plyr.logout(true);
					activeChar.sendMessage("You kicked " + plyr.getName() + " from the game.");
					RegionBBSManager.getInstance().changeCommunityBoard();
				}
			}
		}
		else if (command.startsWith("admin_kick_non_gm"))
		{
			int counter = 0;
			
			for (L2PcInstance player : L2World.getInstance().getAllPlayers())
			{
				if (!player.isGM())
				{
					counter++;
					player.sendPacket(new LeaveWorld());
					player.logout(true);
					RegionBBSManager.getInstance().changeCommunityBoard();
				}
			}
			activeChar.sendMessage("Kicked " + counter + " players");
		}
		return true;
	}
	
	@Override
	public String[] getAdminCommandList()
	{
		return ADMIN_COMMANDS;
	}
	
}
