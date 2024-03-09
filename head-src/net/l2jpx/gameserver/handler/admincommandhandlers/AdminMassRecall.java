package net.l2jpx.gameserver.handler.admincommandhandlers;

import net.l2jpx.Config;
import net.l2jpx.gameserver.ai.CtrlIntention;
import net.l2jpx.gameserver.datatables.sql.ClanTable;
import net.l2jpx.gameserver.handler.IAdminCommandHandler;
import net.l2jpx.gameserver.model.L2Clan;
import net.l2jpx.gameserver.model.L2Party;
import net.l2jpx.gameserver.model.L2World;
import net.l2jpx.gameserver.model.actor.instance.L2PcInstance;

/**
 * @author Yamaneko
 * @author ReynalDev
 */
public class AdminMassRecall implements IAdminCommandHandler
{
	private static String[] adminCommands =
	{
		"admin_recallclan",
		"admin_recallparty",
		"admin_recallally"
	};
	
	@Override
	public boolean useAdminCommand(final String command, final L2PcInstance activeChar)
	{
		if (command.startsWith("admin_recallclan"))
		{
			try
			{
				String val = command.substring(17).trim();
				
				L2Clan clan = ClanTable.getInstance().getClanByName(val);
				
				if (clan == null)
				{
					activeChar.sendMessage("This clan doesn't exists.");
					return true;
				}
				
				clan.getOnlineMembers().forEach(member -> teleport(member, activeChar.getX(), activeChar.getY(), activeChar.getZ(), "Admin is teleporting you"));
			}
			catch (Exception e)
			{
				activeChar.sendMessage("Error in recallclan command.");
			}
		}
		else if (command.startsWith("admin_recallally"))
		{
			try
			{
				String val = command.substring(17).trim();
				L2Clan clan = ClanTable.getInstance().getClanByName(val);
				
				if (clan == null)
				{
					activeChar.sendMessage("This clan doesn't exists.");
					return true;
				}
				
				final int ally = clan.getAllyId();
				
				if (ally == 0)
				{
					
					clan.getOnlineMembers().forEach(member -> teleport(member, activeChar.getX(), activeChar.getY(), activeChar.getZ(), "Admin is teleporting you"));
				}
				else
				{
					for (L2Clan aclan : ClanTable.getInstance().getClans())
					{
						if (aclan.getAllyId() == ally)
						{
							aclan.getOnlineMembers().forEach(member -> teleport(member, activeChar.getX(), activeChar.getY(), activeChar.getZ(), "Admin is teleporting you"));
						}
					}
				}
			}
			catch (final Exception e)
			{
				if (Config.ENABLE_ALL_EXCEPTIONS)
				{
					e.printStackTrace();
				}
				
				activeChar.sendMessage("Error in recallally command.");
			}
		}
		else if (command.startsWith("admin_recallparty"))
		{
			try
			{
				String val = command.substring(18).trim();
				L2PcInstance player = L2World.getInstance().getPlayerByName(val);
				
				if (player == null)
				{
					activeChar.sendMessage("Target error.");
					return true;
				}
				
				if (!player.isInParty())
				{
					activeChar.sendMessage("Player is not in party.");
					return true;
				}
				
				L2Party p = player.getParty();
				
				for (final L2PcInstance ppl : p.getPartyMembers())
				{
					teleport(ppl, activeChar.getX(), activeChar.getY(), activeChar.getZ(), "Admin is teleporting you");
				}
				
				p = null;
				val = null;
				player = null;
				
			}
			catch (final Exception e)
			{
				if (Config.ENABLE_ALL_EXCEPTIONS)
				{
					e.printStackTrace();
				}
				
				activeChar.sendMessage("Error in recallparty command.");
			}
		}
		return true;
	}
	
	private void teleport(final L2PcInstance player, final int X, final int Y, final int Z, final String Message)
	{
		player.sendMessage(Message);
		player.getAI().setIntention(CtrlIntention.AI_INTENTION_IDLE);
		player.teleToLocation(X, Y, Z, true);
	}
	
	@Override
	public String[] getAdminCommandList()
	{
		return adminCommands;
	}
}
