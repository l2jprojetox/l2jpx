package com.px.gameserver;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.InetAddress;
import java.util.logging.LogManager;

import com.px.commons.lang.StringUtil;
import com.px.commons.logging.CLogger;
import com.px.commons.mmocore.SelectorConfig;
import com.px.commons.mmocore.SelectorThread;
import com.px.commons.pool.ConnectionPool;
import com.px.commons.pool.ThreadPool;
import com.px.commons.util.SysUtil;

import com.px.Config;
import com.px.gameserver.communitybbs.CommunityBoard;
import com.px.gameserver.data.SkillTable;
import com.px.gameserver.data.cache.CrestCache;
import com.px.gameserver.data.cache.HtmCache;
import com.px.gameserver.data.manager.BufferManager;
import com.px.gameserver.data.manager.BuyListManager;
import com.px.gameserver.data.manager.CastleManager;
import com.px.gameserver.data.manager.CastleManorManager;
import com.px.gameserver.data.manager.ClanHallManager;
import com.px.gameserver.data.manager.CoupleManager;
import com.px.gameserver.data.manager.CursedWeaponManager;
import com.px.gameserver.data.manager.DerbyTrackManager;
import com.px.gameserver.data.manager.FestivalOfDarknessManager;
import com.px.gameserver.data.manager.FishingChampionshipManager;
import com.px.gameserver.data.manager.GrandBossManager;
import com.px.gameserver.data.manager.HeroManager;
import com.px.gameserver.data.manager.LotteryManager;
import com.px.gameserver.data.manager.PartyMatchRoomManager;
import com.px.gameserver.data.manager.PetitionManager;
import com.px.gameserver.data.manager.RaidPointManager;
import com.px.gameserver.data.manager.SevenSignsManager;
import com.px.gameserver.data.manager.SpawnManager;
import com.px.gameserver.data.manager.ZoneManager;
import com.px.gameserver.data.sql.BookmarkTable;
import com.px.gameserver.data.sql.ClanTable;
import com.px.gameserver.data.sql.PlayerInfoTable;
import com.px.gameserver.data.sql.ServerMemoTable;
import com.px.gameserver.data.xml.AdminData;
import com.px.gameserver.data.xml.AnnouncementData;
import com.px.gameserver.data.xml.ArmorSetData;
import com.px.gameserver.data.xml.AugmentationData;
import com.px.gameserver.data.xml.BoatData;
import com.px.gameserver.data.xml.DoorData;
import com.px.gameserver.data.xml.FishData;
import com.px.gameserver.data.xml.HealSpsData;
import com.px.gameserver.data.xml.HennaData;
import com.px.gameserver.data.xml.InstantTeleportData;
import com.px.gameserver.data.xml.ItemData;
import com.px.gameserver.data.xml.ManorAreaData;
import com.px.gameserver.data.xml.MultisellData;
import com.px.gameserver.data.xml.NewbieBuffData;
import com.px.gameserver.data.xml.NpcData;
import com.px.gameserver.data.xml.ObserverGroupData;
import com.px.gameserver.data.xml.PlayerData;
import com.px.gameserver.data.xml.PlayerLevelData;
import com.px.gameserver.data.xml.RecipeData;
import com.px.gameserver.data.xml.RestartPointData;
import com.px.gameserver.data.xml.ScriptData;
import com.px.gameserver.data.xml.SkillTreeData;
import com.px.gameserver.data.xml.SoulCrystalData;
import com.px.gameserver.data.xml.SpellbookData;
import com.px.gameserver.data.xml.StaticObjectData;
import com.px.gameserver.data.xml.SummonItemData;
import com.px.gameserver.data.xml.TeleportData;
import com.px.gameserver.data.xml.WalkerRouteData;
import com.px.gameserver.geoengine.GeoEngine;
import com.px.gameserver.handler.AdminCommandHandler;
import com.px.gameserver.handler.ChatHandler;
import com.px.gameserver.handler.ItemHandler;
import com.px.gameserver.handler.SkillHandler;
import com.px.gameserver.handler.TargetHandler;
import com.px.gameserver.handler.UserCommandHandler;
import com.px.gameserver.idfactory.IdFactory;
import com.px.gameserver.model.World;
import com.px.gameserver.model.memo.GlobalMemo;
import com.px.gameserver.model.olympiad.Olympiad;
import com.px.gameserver.model.olympiad.OlympiadGameManager;
import com.px.gameserver.network.GameClient;
import com.px.gameserver.network.GamePacketHandler;
import com.px.gameserver.taskmanager.AiTaskManager;
import com.px.gameserver.taskmanager.AttackStanceTaskManager;
import com.px.gameserver.taskmanager.BoatTaskManager;
import com.px.gameserver.taskmanager.DecayTaskManager;
import com.px.gameserver.taskmanager.GameTimeTaskManager;
import com.px.gameserver.taskmanager.InventoryUpdateTaskManager;
import com.px.gameserver.taskmanager.ItemInstanceTaskManager;
import com.px.gameserver.taskmanager.ItemsOnGroundTaskManager;
import com.px.gameserver.taskmanager.PvpFlagTaskManager;
import com.px.gameserver.taskmanager.ShadowItemTaskManager;
import com.px.gameserver.taskmanager.WaterTaskManager;
import com.px.util.DeadLockDetector;
import com.px.util.IPv4Filter;

public class GameServer
{
	private static final CLogger LOGGER = new CLogger(GameServer.class.getName());
	
	private final SelectorThread<GameClient> _selectorThread;
	
	private static GameServer _gameServer;
	
	public static void main(String[] args) throws Exception
	{
		_gameServer = new GameServer();
	}
	
	public GameServer() throws Exception
	{
		// Create log folder
		new File("./log").mkdir();
		new File("./log/chat").mkdir();
		new File("./log/console").mkdir();
		new File("./log/error").mkdir();
		new File("./log/gmaudit").mkdir();
		new File("./log/item").mkdir();
		new File("./data/crests").mkdirs();
		
		// Create input stream for log file -- or store file data into memory
		try (InputStream is = new FileInputStream(new File("config/logging.properties")))
		{
			LogManager.getLogManager().readConfiguration(is);
		}
		
		StringUtil.printSection("Config");
		Config.loadGameServer();
		
		StringUtil.printSection("Poolers");
		ConnectionPool.init();
		ThreadPool.init();
		
		StringUtil.printSection("IdFactory");
		IdFactory.getInstance();
		
		StringUtil.printSection("Cache");
		HtmCache.getInstance();
		CrestCache.getInstance();
		
		StringUtil.printSection("World");
		World.getInstance();
		AnnouncementData.getInstance();
		ServerMemoTable.getInstance();
		GlobalMemo.getInstance();
		
		StringUtil.printSection("Skills");
		SkillTable.getInstance();
		SkillTreeData.getInstance();
		
		StringUtil.printSection("Items");
		ItemData.getInstance();
		SummonItemData.getInstance();
		HennaData.getInstance();
		BuyListManager.getInstance();
		MultisellData.getInstance();
		RecipeData.getInstance();
		ArmorSetData.getInstance();
		FishData.getInstance();
		SpellbookData.getInstance();
		SoulCrystalData.getInstance();
		AugmentationData.getInstance();
		CursedWeaponManager.getInstance();
		
		StringUtil.printSection("Admins");
		AdminData.getInstance();
		BookmarkTable.getInstance();
		PetitionManager.getInstance();
		
		StringUtil.printSection("Characters");
		PlayerData.getInstance();
		PlayerInfoTable.getInstance();
		PlayerLevelData.getInstance();
		PartyMatchRoomManager.getInstance();
		RaidPointManager.getInstance();
		HealSpsData.getInstance();
		RestartPointData.getInstance();
		
		StringUtil.printSection("Community server");
		CommunityBoard.getInstance();
		
		StringUtil.printSection("Clans");
		ClanTable.getInstance();
		
		StringUtil.printSection("Geodata & Pathfinding");
		GeoEngine.getInstance();
		
		StringUtil.printSection("Zones");
		ZoneManager.getInstance();
		
		StringUtil.printSection("Castles & Clan Halls");
		CastleManager.getInstance();
		ClanHallManager.getInstance();
		
		StringUtil.printSection("Task Managers");
		AiTaskManager.getInstance();
		AttackStanceTaskManager.getInstance();
		BoatTaskManager.getInstance();
		DecayTaskManager.getInstance();
		GameTimeTaskManager.getInstance();
		ItemsOnGroundTaskManager.getInstance();
		PvpFlagTaskManager.getInstance();
		ShadowItemTaskManager.getInstance();
		WaterTaskManager.getInstance();
		InventoryUpdateTaskManager.getInstance();
		ItemInstanceTaskManager.getInstance();
		
		StringUtil.printSection("Seven Signs");
		SevenSignsManager.getInstance();
		FestivalOfDarknessManager.getInstance();
		
		StringUtil.printSection("Manor Manager");
		ManorAreaData.getInstance();
		CastleManorManager.getInstance();
		
		StringUtil.printSection("NPCs");
		BufferManager.getInstance();
		NpcData.getInstance();
		WalkerRouteData.getInstance();
		DoorData.getInstance().spawn();
		StaticObjectData.getInstance();
		SpawnManager.getInstance();
		GrandBossManager.getInstance();
		NewbieBuffData.getInstance();
		InstantTeleportData.getInstance();
		TeleportData.getInstance();
		ObserverGroupData.getInstance();
		
		CastleManager.getInstance().loadArtifacts();
		
		StringUtil.printSection("Olympiads & Heroes");
		OlympiadGameManager.getInstance();
		Olympiad.getInstance();
		HeroManager.getInstance();
		
		StringUtil.printSection("Quests & Scripts");
		ScriptData.getInstance();
		
		if (Config.ALLOW_BOAT)
			BoatData.getInstance().load();
		
		StringUtil.printSection("Events");
		DerbyTrackManager.getInstance();
		LotteryManager.getInstance();
		CoupleManager.getInstance();
		
		if (Config.ALLOW_FISH_CHAMPIONSHIP)
			FishingChampionshipManager.getInstance();
		
		StringUtil.printSection("Spawns");
		SpawnManager.getInstance().spawn();
		
		StringUtil.printSection("Handlers");
		LOGGER.info("Loaded {} admin command handlers.", AdminCommandHandler.getInstance().size());
		LOGGER.info("Loaded {} chat handlers.", ChatHandler.getInstance().size());
		LOGGER.info("Loaded {} item handlers.", ItemHandler.getInstance().size());
		LOGGER.info("Loaded {} skill handlers.", SkillHandler.getInstance().size());
		LOGGER.info("Loaded {} target handlers.", TargetHandler.getInstance().size());
		LOGGER.info("Loaded {} user command handlers.", UserCommandHandler.getInstance().size());
		
		StringUtil.printSection("System");
		Runtime.getRuntime().addShutdownHook(Shutdown.getInstance());
		
		if (Config.DEADLOCK_DETECTOR)
		{
			LOGGER.info("Deadlock detector is enabled. Timer: {}s.", Config.DEADLOCK_CHECK_INTERVAL);
			
			final DeadLockDetector deadDetectThread = new DeadLockDetector();
			deadDetectThread.setDaemon(true);
			deadDetectThread.start();
		}
		else
			LOGGER.info("Deadlock detector is disabled.");
		
		LOGGER.info("Gameserver has started, used memory: {} / {} Mo.", SysUtil.getUsedMemory(), SysUtil.getMaxMemory());
		LOGGER.info("Maximum allowed players: {}.", Config.MAXIMUM_ONLINE_USERS);
		
		StringUtil.printSection("Login");
		LoginServerThread.getInstance().start();
		
		final SelectorConfig sc = new SelectorConfig();
		sc.MAX_READ_PER_PASS = Config.MMO_MAX_READ_PER_PASS;
		sc.MAX_SEND_PER_PASS = Config.MMO_MAX_SEND_PER_PASS;
		sc.SLEEP_TIME = Config.MMO_SELECTOR_SLEEP_TIME;
		sc.HELPER_BUFFER_COUNT = Config.MMO_HELPER_BUFFER_COUNT;
		
		final GamePacketHandler handler = new GamePacketHandler();
		_selectorThread = new SelectorThread<>(sc, handler, handler, handler, new IPv4Filter());
		
		InetAddress bindAddress = null;
		if (!Config.GAMESERVER_HOSTNAME.equals("*"))
		{
			try
			{
				bindAddress = InetAddress.getByName(Config.GAMESERVER_HOSTNAME);
			}
			catch (Exception e)
			{
				LOGGER.error("The GameServer bind address is invalid, using all available IPs.", e);
			}
		}
		
		try
		{
			_selectorThread.openServerSocket(bindAddress, Config.GAMESERVER_PORT);
		}
		catch (Exception e)
		{
			LOGGER.error("Failed to open server socket.", e);
			System.exit(1);
		}
		_selectorThread.start();
	}
	
	public static GameServer getInstance()
	{
		return _gameServer;
	}
	
	public SelectorThread<GameClient> getSelectorThread()
	{
		return _selectorThread;
	}
}