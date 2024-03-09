package net.l2jpx.gameserver.handler;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import net.l2jpx.Config;
import net.l2jpx.gameserver.datatables.xml.AdminCommandAccessRights;
import net.l2jpx.gameserver.handler.admincommandhandlers.AdminAdmin;
import net.l2jpx.gameserver.handler.admincommandhandlers.AdminAnnouncements;
import net.l2jpx.gameserver.handler.admincommandhandlers.AdminBBS;
import net.l2jpx.gameserver.handler.admincommandhandlers.AdminBan;
import net.l2jpx.gameserver.handler.admincommandhandlers.AdminBoat;
import net.l2jpx.gameserver.handler.admincommandhandlers.AdminBuffs;
import net.l2jpx.gameserver.handler.admincommandhandlers.AdminCache;
import net.l2jpx.gameserver.handler.admincommandhandlers.AdminChangeAccessLevel;
import net.l2jpx.gameserver.handler.admincommandhandlers.AdminCharSupervision;
import net.l2jpx.gameserver.handler.admincommandhandlers.AdminChristmas;
import net.l2jpx.gameserver.handler.admincommandhandlers.AdminClanHall;
import net.l2jpx.gameserver.handler.admincommandhandlers.AdminCreateItem;
import net.l2jpx.gameserver.handler.admincommandhandlers.AdminCursedWeapons;
import net.l2jpx.gameserver.handler.admincommandhandlers.AdminDelete;
import net.l2jpx.gameserver.handler.admincommandhandlers.AdminDoorControl;
import net.l2jpx.gameserver.handler.admincommandhandlers.AdminEditChar;
import net.l2jpx.gameserver.handler.admincommandhandlers.AdminEditNpc;
import net.l2jpx.gameserver.handler.admincommandhandlers.AdminEffects;
import net.l2jpx.gameserver.handler.admincommandhandlers.AdminEnchant;
import net.l2jpx.gameserver.handler.admincommandhandlers.AdminExpSp;
import net.l2jpx.gameserver.handler.admincommandhandlers.AdminFightCalculator;
import net.l2jpx.gameserver.handler.admincommandhandlers.AdminFortSiege;
import net.l2jpx.gameserver.handler.admincommandhandlers.AdminGeodata;
import net.l2jpx.gameserver.handler.admincommandhandlers.AdminGm;
import net.l2jpx.gameserver.handler.admincommandhandlers.AdminGmChat;
import net.l2jpx.gameserver.handler.admincommandhandlers.AdminHeal;
import net.l2jpx.gameserver.handler.admincommandhandlers.AdminHelpPage;
import net.l2jpx.gameserver.handler.admincommandhandlers.AdminInvul;
import net.l2jpx.gameserver.handler.admincommandhandlers.AdminKick;
import net.l2jpx.gameserver.handler.admincommandhandlers.AdminKill;
import net.l2jpx.gameserver.handler.admincommandhandlers.AdminLevel;
import net.l2jpx.gameserver.handler.admincommandhandlers.AdminLogin;
import net.l2jpx.gameserver.handler.admincommandhandlers.AdminMammon;
import net.l2jpx.gameserver.handler.admincommandhandlers.AdminManor;
import net.l2jpx.gameserver.handler.admincommandhandlers.AdminMassControl;
import net.l2jpx.gameserver.handler.admincommandhandlers.AdminMassRecall;
import net.l2jpx.gameserver.handler.admincommandhandlers.AdminMenu;
import net.l2jpx.gameserver.handler.admincommandhandlers.AdminMobGroup;
import net.l2jpx.gameserver.handler.admincommandhandlers.AdminMonsterRace;
import net.l2jpx.gameserver.handler.admincommandhandlers.AdminNoble;
import net.l2jpx.gameserver.handler.admincommandhandlers.AdminPForge;
import net.l2jpx.gameserver.handler.admincommandhandlers.AdminPetition;
import net.l2jpx.gameserver.handler.admincommandhandlers.AdminPledge;
import net.l2jpx.gameserver.handler.admincommandhandlers.AdminPolymorph;
import net.l2jpx.gameserver.handler.admincommandhandlers.AdminReload;
import net.l2jpx.gameserver.handler.admincommandhandlers.AdminRepairChar;
import net.l2jpx.gameserver.handler.admincommandhandlers.AdminRes;
import net.l2jpx.gameserver.handler.admincommandhandlers.AdminRideWyvern;
import net.l2jpx.gameserver.handler.admincommandhandlers.AdminScript;
import net.l2jpx.gameserver.handler.admincommandhandlers.AdminServerUpdates;
import net.l2jpx.gameserver.handler.admincommandhandlers.AdminShop;
import net.l2jpx.gameserver.handler.admincommandhandlers.AdminShutdown;
import net.l2jpx.gameserver.handler.admincommandhandlers.AdminSiege;
import net.l2jpx.gameserver.handler.admincommandhandlers.AdminSkill;
import net.l2jpx.gameserver.handler.admincommandhandlers.AdminSpawn;
import net.l2jpx.gameserver.handler.admincommandhandlers.AdminTarget;
import net.l2jpx.gameserver.handler.admincommandhandlers.AdminTeleport;
import net.l2jpx.gameserver.handler.admincommandhandlers.AdminTest;
import net.l2jpx.gameserver.handler.admincommandhandlers.AdminTownWar;
import net.l2jpx.gameserver.handler.admincommandhandlers.AdminWho;
import net.l2jpx.gameserver.handler.admincommandhandlers.AdminZone;

/**
 * @author ReynalDev
 */
public class AdminCommandHandler
{
	protected static final Logger LOGGER = Logger.getLogger(AdminCommandHandler.class);
	private static AdminCommandHandler instance;
	private Map<String, IAdminCommandHandler> adminCommands = new HashMap<>();
	
	public static AdminCommandHandler getInstance()
	{
		if (instance == null)
		{
			instance = new AdminCommandHandler();
		}
		return instance;
	}
	
	private AdminCommandHandler()
	{
		registerAdminCommandHandler(new AdminAdmin());
		registerAdminCommandHandler(new AdminAnnouncements());
		registerAdminCommandHandler(new AdminBBS());
		registerAdminCommandHandler(new AdminBan());
		registerAdminCommandHandler(new AdminBoat());
		registerAdminCommandHandler(new AdminBuffs());
		registerAdminCommandHandler(new AdminCache());
		registerAdminCommandHandler(new AdminChangeAccessLevel());
		registerAdminCommandHandler(new AdminCharSupervision());
		registerAdminCommandHandler(new AdminChristmas());
		registerAdminCommandHandler(new AdminClanHall());
		registerAdminCommandHandler(new AdminCreateItem());
		registerAdminCommandHandler(new AdminCursedWeapons());
		registerAdminCommandHandler(new AdminDelete());
		registerAdminCommandHandler(new AdminDoorControl());
		registerAdminCommandHandler(new AdminEditChar());
		registerAdminCommandHandler(new AdminEditNpc());
		registerAdminCommandHandler(new AdminEffects());
		registerAdminCommandHandler(new AdminEnchant());
		registerAdminCommandHandler(new AdminExpSp());
		registerAdminCommandHandler(new AdminFightCalculator());
		registerAdminCommandHandler(new AdminFortSiege());
		registerAdminCommandHandler(new AdminGeodata());
		registerAdminCommandHandler(new AdminGm());
		registerAdminCommandHandler(new AdminGmChat());
		registerAdminCommandHandler(new AdminHeal());
		registerAdminCommandHandler(new AdminHelpPage());
		registerAdminCommandHandler(new AdminInvul());
		registerAdminCommandHandler(new AdminKick());
		registerAdminCommandHandler(new AdminKill());
		registerAdminCommandHandler(new AdminLevel());
		registerAdminCommandHandler(new AdminLogin());
		registerAdminCommandHandler(new AdminMammon());
		registerAdminCommandHandler(new AdminManor());
		registerAdminCommandHandler(new AdminMassControl());
		registerAdminCommandHandler(new AdminMassRecall());
		registerAdminCommandHandler(new AdminMenu());
		registerAdminCommandHandler(new AdminMobGroup());
		registerAdminCommandHandler(new AdminMonsterRace());
		registerAdminCommandHandler(new AdminNoble());
		registerAdminCommandHandler(new AdminPForge());
		registerAdminCommandHandler(new AdminPetition());
		registerAdminCommandHandler(new AdminPledge());
		registerAdminCommandHandler(new AdminPolymorph());
		registerAdminCommandHandler(new AdminReload());
		registerAdminCommandHandler(new AdminRepairChar());
		registerAdminCommandHandler(new AdminRes());
		registerAdminCommandHandler(new AdminRideWyvern());
		registerAdminCommandHandler(new AdminScript());
		registerAdminCommandHandler(new AdminServerUpdates());
		registerAdminCommandHandler(new AdminShop());
		registerAdminCommandHandler(new AdminShutdown());
		registerAdminCommandHandler(new AdminSiege());
		registerAdminCommandHandler(new AdminSkill());
		registerAdminCommandHandler(new AdminSpawn());
		registerAdminCommandHandler(new AdminTarget());
		registerAdminCommandHandler(new AdminTeleport());
		registerAdminCommandHandler(new AdminTest());
		registerAdminCommandHandler(new AdminTownWar());
		registerAdminCommandHandler(new AdminWho());
		registerAdminCommandHandler(new AdminZone());
		// ATTENTION: adding new command handlers, you have to add the new command into gameserver\config\access_level\adminCommands.xml
		
		LOGGER.info("AdminCommandHandler: Loaded " + adminCommands.size() + " handlers.");
		
		if (Config.DEBUG)
		{
			String[] commands = new String[adminCommands.keySet().size()];
			
			commands = adminCommands.keySet().toArray(commands);
			
			Arrays.sort(commands);
			
			for (final String command : commands)
			{
				if (AdminCommandAccessRights.getInstance().accessRightForCommand(command) < 0)
				{
					LOGGER.info("ATTENTION: admin command " + command + " has not an access right");
				}
			}
			
		}
		
	}
	
	public void registerAdminCommandHandler(IAdminCommandHandler handler)
	{
		String[] commands = handler.getAdminCommandList();
		
		for (String command : commands)
		{
			if (Config.DEBUG)
			{
				LOGGER.info("Adding handler for command " + command);
			}
			
			if (adminCommands.containsKey(command))
			{
				LOGGER.warn("Duplicated command \"" + command + "\" defined in " + handler.getClass().getName() + ".");
			}
			else
			{
				adminCommands.put(command, handler);
			}
		}
	}
	
	public IAdminCommandHandler getAdminCommandHandler(String adminCommand)
	{
		String command = adminCommand;
		
		if (adminCommand.indexOf(" ") != -1)
		{
			command = adminCommand.substring(0, adminCommand.indexOf(" "));
		}
		
		if (Config.DEBUG)
		{
			LOGGER.info("getting handler for command: " + command + " -> " + (adminCommands.get(command) != null));
		}
		
		return adminCommands.get(command);
	}
}