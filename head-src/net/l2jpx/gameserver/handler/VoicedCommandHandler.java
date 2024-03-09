package net.l2jpx.gameserver.handler;

import java.util.HashMap;
import java.util.Map;

import net.l2jpx.gameserver.handler.voicedcommandhandlers.*;
import org.apache.log4j.Logger;

import net.l2jpx.Config;
import net.l2jpx.gameserver.GameServer;

public class VoicedCommandHandler
{
	private static Logger LOGGER = Logger.getLogger(GameServer.class);
	private static VoicedCommandHandler instance;
	private Map<String, IVoicedCommandHandler> voicedCommands = new HashMap<>();
	
	public static VoicedCommandHandler getInstance()
	{
		if (instance == null)
		{
			instance = new VoicedCommandHandler();
		}
		
		return instance;
	}
	
	private VoicedCommandHandler()
	{
		if (Config.BANKING_SYSTEM_ENABLED)
		{
			registerVoicedCommandHandler(new BankingCmd());
		}
		
		if (Config.L2JMOD_ALLOW_WEDDING)
		{
			registerVoicedCommandHandler(new WeddingCmd());
		}
		
		registerVoicedCommandHandler(new StatsCmd());
		
		if (Config.ALLOW_AWAY_STATUS)
		{
			registerVoicedCommandHandler(new AwayCmd());
		}
		
		if (Config.ALLOW_ONLINE_VIEW)
		{
			registerVoicedCommandHandler(new Online());
		}
		
		if (Config.CHARACTER_REPAIR)
		{
			registerVoicedCommandHandler(new Repair());
		}
		
		registerVoicedCommandHandler(new ServerTimeCmd());
		registerVoicedCommandHandler(new Menu());
		registerVoicedCommandHandler(new AutoFarm());

		LOGGER.info("VoicedCommandHandler: Loaded " + voicedCommands.size() + " handlers.");
		
	}
	
	public void registerVoicedCommandHandler(IVoicedCommandHandler handler)
	{
		String[] ids = handler.getVoicedCommandList();
		
		for (String id : ids)
		{
			if (Config.DEBUG)
			{
				LOGGER.debug("Adding handler for command " + id);
			}
			
			voicedCommands.put(id, handler);
		}
		
	}
	
	public IVoicedCommandHandler getVoicedCommandHandler(String voicedCommand)
	{
		String command = voicedCommand;
		
		if (voicedCommand.indexOf(" ") != -1)
		{
			command = voicedCommand.substring(0, voicedCommand.indexOf(" "));
		}
		
		if (Config.DEBUG)
		{
			LOGGER.debug("getting handler for command: " + command + " -> " + (voicedCommands.get(command) != null));
		}
		
		return voicedCommands.get(command);
	}
	
	public int size()
	{
		return voicedCommands.size();
	}
}