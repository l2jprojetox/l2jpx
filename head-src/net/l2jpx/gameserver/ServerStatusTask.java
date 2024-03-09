package net.l2jpx.gameserver;

import java.util.concurrent.ScheduledFuture;

import org.apache.log4j.Logger;

import net.l2jpx.gameserver.model.L2World;
import net.l2jpx.gameserver.thread.ThreadPoolManager;
import net.l2jpx.util.Memory;
import net.l2jpx.util.Util;

/**
 * @author  Nefer
 * @author ReynalDev
 */
public class ServerStatusTask
{
	protected static final Logger LOGGER = Logger.getLogger(ServerStatusTask.class);
	protected ScheduledFuture<?> scheduledTask;
	
	protected ServerStatusTask()
	{
		scheduledTask = ThreadPoolManager.getInstance().scheduleGeneralAtFixedRate(() -> {
			Util.printSection("Server Status");
			LOGGER.info("Players Online: " + L2World.getAllPlayersCount());
			LOGGER.info("Threads: " + Thread.activeCount());
			LOGGER.info("Free memory: " + Memory.getFreeMemory() + " MB of " + Memory.getTotalMemory() + " MB");
			LOGGER.info("Used memory: " + Memory.getUsedMemory() + " MB of " + Memory.getTotalMemory() + " MB");
			Util.printSection("Server Status");
		}, 3600000, 3600000);
	}
	
	public static ServerStatusTask getInstance()
	{
		return SingletonHolder.instance;
	}
	
	private static class SingletonHolder
	{
		protected static final ServerStatusTask instance = new ServerStatusTask();
	}
}