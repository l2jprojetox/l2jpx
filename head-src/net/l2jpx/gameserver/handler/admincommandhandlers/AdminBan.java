package net.l2jpx.gameserver.handler.admincommandhandlers;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.StringTokenizer;

import org.apache.log4j.Logger;

import net.l2jpx.Config;
import net.l2jpx.gameserver.communitybbs.Manager.RegionBBSManager;
import net.l2jpx.gameserver.handler.IAdminCommandHandler;
import net.l2jpx.gameserver.model.L2World;
import net.l2jpx.gameserver.model.actor.instance.L2PcInstance;
import net.l2jpx.gameserver.network.SystemMessageId;
import net.l2jpx.gameserver.network.serverpackets.ServerClose;
import net.l2jpx.gameserver.network.serverpackets.SystemMessage;
import net.l2jpx.gameserver.thread.LoginServerThread;
import net.l2jpx.util.L2Log;
import net.l2jpx.util.database.L2DatabaseFactory;

/**
 * @author ReynalDev
 */
public class AdminBan implements IAdminCommandHandler
{
	private static final Logger LOGGER = Logger.getLogger(AdminBan.class);
	
	private static final String UPDATE_CHARACTER_PUNISH = "UPDATE characters SET punish_level=?, punish_timer=? WHERE car_name=?";
	private static final String UPDATE_JAIL_OFFLINE_PLAYER = "UPDATE characters SET x=?, y=?, z=?, punish_level=?, punish_timer=? WHERE char_name=?";
	private static final String UPDATE_ACCESS_LEVEL = "UPDATE characters SET accesslevel=? WHERE char_name=?";
	private static final String INSERT_IP_BANNED = "INSERT IGNORE INTO `" + Config.LOGINSERVER_DB + "`.`ip_banned` (ip_address) VALUES(?)";
	
	private static final String[] ADMIN_COMMANDS =
	{
		"admin_ban", // returns ban commands
		"admin_ban_acc",
		"admin_ban_char",
		"admin_banchat",
		"admin_ban_ip", // Consider use the method "addBanForAddress" in LoginController class
		"admin_unban", // returns unban commands
		"admin_unban_acc",
		"admin_unban_char",
		"admin_unbanchat",
		"admin_jail",
		"admin_unjail"
	};
	
	@Override
	public boolean useAdminCommand(final String command, final L2PcInstance activeChar)
	{
		final StringTokenizer st = new StringTokenizer(command);
		st.nextToken();
		String player = "";
		int duration = -1;
		L2PcInstance targetPlayer = null;
		
		if (st.hasMoreTokens())
		{
			player = st.nextToken();
			targetPlayer = L2World.getInstance().getPlayerByName(player);
			
			if (st.hasMoreTokens())
			{
				try
				{
					duration = Integer.parseInt(st.nextToken());
				}
				catch (final NumberFormatException nfe)
				{
					activeChar.sendMessage("Invalid number format used: " + nfe);
					return false;
				}
			}
		}
		else
		{
			if (activeChar.getTarget() != null && activeChar.getTarget() instanceof L2PcInstance)
			{
				targetPlayer = (L2PcInstance) activeChar.getTarget();
			}
		}
		
		if (targetPlayer != null && targetPlayer.equals(activeChar))
		{
			activeChar.sendPacket(new SystemMessage(SystemMessageId.YOU_CANNOT_USE_THIS_ON_YOURSELF));
			return false;
		}
		
		if (command.startsWith("admin_ban ") || command.equalsIgnoreCase("admin_ban"))
		{
			activeChar.sendMessage("Available ban commands: //ban_acc, //ban_char, //ban_chat, //ban_ip");
			return false;
		}
		else if (command.startsWith("admin_ban_acc"))
		{
			// May need to check usage in admin_ban_menu as well.
			
			if (targetPlayer == null && player.equals(""))
			{
				activeChar.sendMessage("Usage: //ban_acc <account_name> (if none, target char's account gets banned)");
				return false;
			}
			else if (targetPlayer == null)
			{
				LoginServerThread.getInstance().sendAccessLevel(player, -100);
				activeChar.sendMessage("Ban request sent for account " + player);
			}
			else
			{
				targetPlayer.setPunishLevel(L2PcInstance.PunishLevel.ACC, 0);
				activeChar.sendMessage("Account " + targetPlayer.getAccountName() + " banned.");
			}
		}
		else if (command.startsWith("admin_ban_char"))
		{
			if (targetPlayer == null && player.equals(""))
			{
				activeChar.sendMessage("Usage: //ban_char <char_name> (if none, target char is banned)");
				return false;
			}
			return changeCharAccessLevel(targetPlayer, player, activeChar, -100);
		}
		else if (command.startsWith("admin_banchat"))
		{
			if (targetPlayer == null && player.equals(""))
			{
				activeChar.sendMessage("Usage: //banchat <char_name> [penalty_minutes]");
				return false;
			}
			if (targetPlayer != null)
			{
				if (targetPlayer.getPunishLevel().value() > 0)
				{
					activeChar.sendMessage(targetPlayer.getName() + " is already jailed or banned.");
					return false;
				}
				String banLengthStr = "";
				
				targetPlayer.setPunishLevel(L2PcInstance.PunishLevel.CHAT, duration);
				if (duration > 0)
				{
					banLengthStr = " for " + duration + " minutes";
				}
				activeChar.sendMessage(targetPlayer.getName() + " is now chat banned" + banLengthStr + ".");
			}
			else
			{
				banChatOfflinePlayer(activeChar, player, duration, true);
			}
		}
		else if (command.startsWith("admin_ban_ip"))
		{
			if (targetPlayer == null || player.equals(""))
			{
				activeChar.sendMessage("Usage: //admin_ban_ip <char_name>");
				return false;
			}
			
			try (Connection con = L2DatabaseFactory.getInstance().getConnection();
				PreparedStatement pst = con.prepareStatement(INSERT_IP_BANNED))
			{
				pst.setString(1, targetPlayer.getIPAddress());
				pst.executeUpdate();
				
				String text = targetPlayer.getName() + " with IP " + targetPlayer.getIPAddress() + " has been banned. Just the IP ADDRESS was banned, player still can join the game.";
				activeChar.sendMessage(text);
				targetPlayer.kick();
				
				L2Log.add(text, "ip_banned");
			}
			catch (Exception e)
			{
				LOGGER.error("AdminBan: Command admin_ban_ip, could not insert IP in database", e);
				return false;
			}
		}
		else if (command.startsWith("admin_unbanchat"))
		{
			if (targetPlayer == null && player.equals(""))
			{
				activeChar.sendMessage("Usage: //unbanchat <char_name>");
				return false;
			}
			if (targetPlayer != null)
			{
				if (targetPlayer.isChatBanned())
				{
					targetPlayer.setPunishLevel(L2PcInstance.PunishLevel.NONE, 0);
					activeChar.sendMessage(targetPlayer.getName() + "'s chat ban has now been lifted.");
				}
				else
				{
					activeChar.sendMessage(targetPlayer.getName() + " is not currently chat banned.");
				}
			}
			else
			{
				banChatOfflinePlayer(activeChar, player, 0, false);
			}
		}
		else if (command.startsWith("admin_unban ") || command.equalsIgnoreCase("admin_unban"))
		{
			activeChar.sendMessage("Available unban commands: //unban_acc, //unban_char, //unban_chat");
			return false;
		}
		else if (command.startsWith("admin_unban_acc"))
		{
			// Need to check admin_unban_menu command as well in AdminMenu.java handler.
			
			if (targetPlayer != null)
			{
				activeChar.sendMessage(targetPlayer.getName() + " is currently online so must not be banned.");
				return false;
			}
			else if (!player.equals(""))
			{
				LoginServerThread.getInstance().sendAccessLevel(player, 0);
				activeChar.sendMessage("Unban request sent for account " + player);
			}
			else
			{
				activeChar.sendMessage("Usage: //unban_acc <account_name>");
				return false;
			}
		}
		else if (command.startsWith("admin_unban_char"))
		{
			if (targetPlayer == null && player.equals(""))
			{
				activeChar.sendMessage("Usage: //unban_char <char_name>");
				return false;
			}
			else if (targetPlayer != null)
			{
				activeChar.sendMessage(targetPlayer.getName() + " is currently online so must not be banned.");
				return false;
			}
			else
			{
				return changeCharAccessLevel(null, player, activeChar, 0);
			}
		}
		else if (command.startsWith("admin_jail"))
		{
			if (targetPlayer == null && player.equals(""))
			{
				activeChar.sendMessage("Usage: //jail <charname> [penalty_minutes] (if no name is given, selected target is jailed indefinitely)");
				return false;
			}
			if (targetPlayer != null)
			{
				targetPlayer.setPunishLevel(L2PcInstance.PunishLevel.JAIL, duration);
				activeChar.sendMessage("Character " + targetPlayer.getName() + " jailed for " + (duration > 0 ? duration + " minutes." : "ever!"));
				
				if (targetPlayer.getParty() != null)
				{
					targetPlayer.getParty().removePartyMember(targetPlayer);
				}
			}
			else
			{
				jailOfflinePlayer(activeChar, player, duration);
			}
		}
		else if (command.startsWith("admin_unjail"))
		{
			if (targetPlayer == null && player.equals(""))
			{
				activeChar.sendMessage("Usage: //unjail <charname> (If no name is given target is used)");
				return false;
			}
			else if (targetPlayer != null)
			{
				targetPlayer.setPunishLevel(L2PcInstance.PunishLevel.NONE, 0);
				activeChar.sendMessage("Character " + targetPlayer.getName() + " removed from jail");
			}
			else
			{
				unjailOfflinePlayer(activeChar, player);
			}
		}
		return true;
	}
	
	private void banChatOfflinePlayer(L2PcInstance activeChar, String name, int delay, boolean ban)
	{
		int level = 0;
		long value = 0;
		if (ban)
		{
			level = L2PcInstance.PunishLevel.CHAT.value();
			value = (delay > 0 ? delay * 60000L : 60000);
		}
		else
		{
			level = L2PcInstance.PunishLevel.NONE.value();
			value = 0;
		}
		
		try (Connection con = L2DatabaseFactory.getInstance().getConnection();
			PreparedStatement statement = con.prepareStatement(UPDATE_CHARACTER_PUNISH))
		{
			statement.setInt(1, level);
			statement.setLong(2, value);
			statement.setString(3, name);
			statement.executeUpdate();
			
			int count = statement.getUpdateCount();
			
			if (count == 0)
			{
				activeChar.sendMessage("Character not found!");
			}
			else if (ban)
			{
				activeChar.sendMessage("Character " + name + " chat-banned for " + (delay > 0 ? delay + " minutes." : "ever!"));
			}
			else
			{
				activeChar.sendMessage("Character " + name + "'s chat-banned lifted");
			}
		}
		catch (Exception e)
		{
			LOGGER.error("Error while saving ban data into characters table", e);
		}
	}
	
	private void jailOfflinePlayer(L2PcInstance activeChar, String name, int delay)
	{
		try (Connection con = L2DatabaseFactory.getInstance().getConnection();
			PreparedStatement statement = con.prepareStatement(UPDATE_JAIL_OFFLINE_PLAYER))
		{
			statement.setInt(1, -114356);
			statement.setInt(2, -249645);
			statement.setInt(3, -2984);
			statement.setInt(4, L2PcInstance.PunishLevel.JAIL.value());
			statement.setLong(5, (delay > 0 ? delay * 60000L : 0));
			statement.setString(6, name);
			statement.executeUpdate();
			
			int count = statement.getUpdateCount();
			
			if (count == 0)
			{
				activeChar.sendMessage("Character not found!");
			}
			else
			{
				activeChar.sendMessage("Character " + name + " jailed for " + (delay > 0 ? delay + " minutes." : "ever!"));
			}
		}
		catch (Exception e)
		{
			LOGGER.error("Error while updating data into characters table", e);
		}
	}
	
	private void unjailOfflinePlayer(L2PcInstance activeChar, String name)
	{
		try (Connection con = L2DatabaseFactory.getInstance().getConnection();
			PreparedStatement statement = con.prepareStatement(UPDATE_JAIL_OFFLINE_PLAYER))
		{
			statement.setInt(1, 17836);
			statement.setInt(2, 170178);
			statement.setInt(3, -3507);
			statement.setInt(4, 0);
			statement.setLong(5, 0);
			statement.setString(6, name);
			statement.executeUpdate();
			
			int count = statement.getUpdateCount();
			
			if (count == 0)
			{
				activeChar.sendMessage("Character not found!");
			}
			else
			{
				activeChar.sendMessage("Character " + name + " removed from jail");
			}
		}
		catch (Exception e)
		{
			LOGGER.error("Problem while saving data (unjail character) into characters table", e);
		}
	}
	
	private boolean changeCharAccessLevel(L2PcInstance targetPlayer, String player, L2PcInstance activeChar, int lvl)
	{
		boolean output = false;
		
		if (targetPlayer != null)
		{
			targetPlayer.setAccessLevel(lvl);
			targetPlayer.sendMessage("Your character has been banned. Contact the administrator for more informations.");
			
			try
			{
				// Save player status
				targetPlayer.store();
				
				// Player Disconnect like L2OFF, no client crash.
				if (targetPlayer.getClient() != null)
				{
					targetPlayer.getClient().sendPacket(ServerClose.STATIC_PACKET);
					targetPlayer.getClient().setActiveChar(null);
					targetPlayer.setClient(null);
				}
			}
			catch (Exception e)
			{
				LOGGER.error("Something went wrong when disconnect character banned");
			}
			
			targetPlayer.deleteMe();
			
			RegionBBSManager.getInstance().changeCommunityBoard();
			activeChar.sendMessage("The character " + targetPlayer.getName() + " has now been banned.");
			
			output = true;
		}
		else
		{
			try (Connection con = L2DatabaseFactory.getInstance().getConnection();
				PreparedStatement statement = con.prepareStatement(UPDATE_ACCESS_LEVEL))
			{
				statement.setInt(1, lvl);
				statement.setString(2, player);
				statement.executeUpdate();
				
				int count = statement.getUpdateCount();
				
				if (count == 0)
				{
					activeChar.sendMessage("Character not found or access level unaltered.");
				}
				else
				{
					activeChar.sendMessage(player + " now has an access level of " + lvl);
					output = true;
				}
			}
			catch (Exception e)
			{
				LOGGER.error("Error while changing character access level", e);
			}
		}
		return output;
	}
	
	@Override
	public String[] getAdminCommandList()
	{
		return ADMIN_COMMANDS;
	}
}