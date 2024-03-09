package net.l2jpx.gameserver.handler;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import net.l2jpx.Config;
import net.l2jpx.gameserver.GameServer;
import net.l2jpx.gameserver.handler.usercommandhandlers.ChannelDelete;
import net.l2jpx.gameserver.handler.usercommandhandlers.ChannelLeave;
import net.l2jpx.gameserver.handler.usercommandhandlers.ChannelListUpdate;
import net.l2jpx.gameserver.handler.usercommandhandlers.ClanPenalty;
import net.l2jpx.gameserver.handler.usercommandhandlers.ClanWarsList;
import net.l2jpx.gameserver.handler.usercommandhandlers.DisMount;
import net.l2jpx.gameserver.handler.usercommandhandlers.Escape;
import net.l2jpx.gameserver.handler.usercommandhandlers.Loc;
import net.l2jpx.gameserver.handler.usercommandhandlers.Mount;
import net.l2jpx.gameserver.handler.usercommandhandlers.OlympiadStat;
import net.l2jpx.gameserver.handler.usercommandhandlers.PartyInfo;
import net.l2jpx.gameserver.handler.usercommandhandlers.SiegeStatus;
import net.l2jpx.gameserver.handler.usercommandhandlers.Time;

public class UserCommandHandler
{
	private static final Logger LOGGER = Logger.getLogger(GameServer.class);
	private static UserCommandHandler instance;
	
	private Map<Integer, IUserCommandHandler> dataTable = new HashMap<>();
	
	public static UserCommandHandler getInstance()
	{
		if (instance == null)
		{
			instance = new UserCommandHandler();
		}
		
		return instance;
	}
	
	private UserCommandHandler()
	{
		registerUserCommandHandler(new Time());
		registerUserCommandHandler(new OlympiadStat());
		registerUserCommandHandler(new ChannelLeave());
		registerUserCommandHandler(new ChannelDelete());
		registerUserCommandHandler(new ChannelListUpdate());
		registerUserCommandHandler(new ClanPenalty());
		registerUserCommandHandler(new ClanWarsList());
		registerUserCommandHandler(new DisMount());
		registerUserCommandHandler(new Escape());
		registerUserCommandHandler(new Loc());
		registerUserCommandHandler(new Mount());
		registerUserCommandHandler(new PartyInfo());
		registerUserCommandHandler(new SiegeStatus());
		LOGGER.info("UserCommandHandler: Loaded " + dataTable.size() + " handlers.");
	}
	
	public void registerUserCommandHandler(final IUserCommandHandler handler)
	{
		int[] ids = handler.getUserCommandList();
		
		for (final int id : ids)
		{
			if (Config.DEBUG)
			{
				LOGGER.debug("Adding handler for user command " + id);
			}
			dataTable.put(id, handler);
		}
	}
	
	public IUserCommandHandler getUserCommandHandler(final int userCommand)
	{
		if (Config.DEBUG)
		{
			LOGGER.debug("getting handler for user command: " + userCommand);
		}
		
		return dataTable.get(userCommand);
	}
	
	public int size()
	{
		return dataTable.size();
	}
}