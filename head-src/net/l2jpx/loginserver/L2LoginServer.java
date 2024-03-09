package net.l2jpx.loginserver;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.security.GeneralSecurityException;
import java.util.logging.LogManager;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import net.l2jpx.Config;
import net.l2jpx.FService;
import net.l2jpx.ServerType;
import net.l2jpx.loginserver.datatables.xml.GameServerTable;
import net.l2jpx.netcore.NetcoreConfig;
import net.l2jpx.netcore.SelectorConfig;
import net.l2jpx.netcore.SelectorThread;
import net.l2jpx.util.JVM;
import net.l2jpx.util.Util;
import net.l2jpx.util.database.L2DatabaseFactory;

/**
 * @author ReynalDev
 */
public class L2LoginServer
{
	public static final int PROTOCOL_REV = 0x0102;
	
	private static L2LoginServer instance;
	private static final Logger LOGGER = Logger.getLogger(L2LoginServer.class);
	private GameServerListener gameServerListener;
	private SelectorThread<L2LoginClient> selectorThread;
	
	public static void main(final String[] args)
	{
		PropertyConfigurator.configure(FService.LOG4J_CONF_FILE);
		instance = new L2LoginServer();
	}
	
	public static L2LoginServer getInstance()
	{
		return instance;
	}
	
	public L2LoginServer()
	{
		PropertyConfigurator.configure(FService.LOG4J_CONF_FILE);
		if(!JVM.isUsingOpenJDK11())
		{
			System.exit(1);
		}
		
		ServerType.serverMode = ServerType.MODE_LOGINSERVER;
		
		File log4jConfigFile = new File(FService.LOG4J_CONF_FILE);
		
		try(InputStream is = new FileInputStream(log4jConfigFile))
		{
			LogManager.getLogManager().readConfiguration(is);
		}
		catch (Exception e) 
		{
			LOGGER.error("Gameserver: Could not load log configuration file", e);
		}
		
		// Load LoginServer Configs
		Config.load();
		
		Util.printSection("Database");
		// Prepare Database
		try
		{
			L2DatabaseFactory.getInstance();
		}
		catch (Exception e)
		{
			LOGGER.error("Failed initializing database", e);
			System.exit(1);
		}
		
		try
		{
			LoginController.load();
		}
		catch (GeneralSecurityException e)
		{
			LOGGER.error("Failed initializing LoginController", e);
			System.exit(1);
		}
		
		try
		{
			GameServerTable.load();
		}
		catch (Exception e)
		{
			LOGGER.error("Failed to load GameServerTable", e);
			System.exit(1);
		}
		
		
		InetAddress bindAddress = null;
		if (!Config.LOGIN_BIND_ADDRESS.equals("*"))
		{
			try
			{
				bindAddress = InetAddress.getByName(Config.LOGIN_BIND_ADDRESS);
			}
			catch (final UnknownHostException e1)
			{
				LOGGER.warn("WARNING: The LoginServer bind address is invalid, using all avaliable IPs", e1);
			}
		}
		
		final SelectorConfig sc = new SelectorConfig();
		sc.setMaxReadPerPass(NetcoreConfig.getInstance().MMO_MAX_READ_PER_PASS);
		sc.setMaxSendPerPass(NetcoreConfig.getInstance().MMO_MAX_SEND_PER_PASS);
		sc.setSleepTime(NetcoreConfig.getInstance().MMO_SELECTOR_SLEEP_TIME);
		sc.setHelperBufferCount(NetcoreConfig.getInstance().MMO_HELPER_BUFFER_COUNT);
		
		Util.printSection("Loginserver network");
		
		final L2LoginPacketHandler lph = new L2LoginPacketHandler();
		final SelectorHelper sh = new SelectorHelper();
		try
		{
			selectorThread = new SelectorThread<>(sc, sh, lph, sh, sh);
		}
		catch (Exception e)
		{
			LOGGER.error("Failed to open Selector", e);
			System.exit(1);
		}
		
		try
		{
			gameServerListener = new GameServerListener();
			gameServerListener.start();
			LOGGER.info("Listening for GameServers on " + Config.GAME_SERVER_LOGIN_HOST + ":" + Config.GAME_SERVER_LOGIN_PORT);
		}
		catch (final IOException e)
		{
			LOGGER.fatal("Failed to start the Game Server Listener" + e);
			System.exit(1);
		}
		
		try
		{
			selectorThread.openServerSocket(bindAddress, Config.PORT_LOGIN);
			selectorThread.start();
			
			LOGGER.info("Loginserver using external IP address: " + Config.EXTERNAL_HOSTNAME + ":" + Config.PORT_LOGIN);
			LOGGER.info("Loginserver using internal IP address: " + Config.INTERNAL_HOSTNAME + ":" + Config.PORT_LOGIN);
			LOGGER.info("Loginserver listening on " + (bindAddress == null ? "*" : bindAddress.getHostAddress()) + ":" + Config.PORT_LOGIN);
			
		}
		catch (final IOException e)
		{
			LOGGER.error("Failed to open server socket", e);
			System.exit(1);
		}
		
		bindAddress = null;
	}
	
	public GameServerListener getGameServerListener()
	{
		return gameServerListener;
	}
	
	public void shutdown(final boolean restart)
	{
		LoginController.getInstance().shutdown();
		System.gc();
		Runtime.getRuntime().exit(restart ? 2 : 0);
	}
}
