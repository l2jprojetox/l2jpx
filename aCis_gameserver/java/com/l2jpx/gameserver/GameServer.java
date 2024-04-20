package com.l2jpx.gameserver;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.InetAddress;
import java.util.logging.LogManager;

import com.l2jpx.commons.lang.StringUtil;
import com.l2jpx.commons.logging.CLogger;
import com.l2jpx.commons.mmocore.SelectorConfig;
import com.l2jpx.commons.mmocore.SelectorThread;
import com.l2jpx.commons.pool.ConnectionPool;
import com.l2jpx.commons.pool.ThreadPool;
import com.l2jpx.commons.util.SysUtil;

import com.l2jpx.Config;
import com.l2jpx.gameserver.communitybbs.CommunityBoard;
import com.l2jpx.gameserver.data.SkillTable;
import com.l2jpx.gameserver.data.cache.CrestCache;
import com.l2jpx.gameserver.data.cache.HtmCache;
import com.l2jpx.gameserver.data.manager.BoatManager;
import com.l2jpx.gameserver.data.manager.BufferManager;
import com.l2jpx.gameserver.data.manager.BuyListManager;
import com.l2jpx.gameserver.data.manager.CastleManager;
import com.l2jpx.gameserver.data.manager.CastleManorManager;
import com.l2jpx.gameserver.data.manager.ClanHallManager;
import com.l2jpx.gameserver.data.manager.CoupleManager;
import com.l2jpx.gameserver.data.manager.CursedWeaponManager;
import com.l2jpx.gameserver.data.manager.DayNightManager;
import com.l2jpx.gameserver.data.manager.DerbyTrackManager;
import com.l2jpx.gameserver.data.manager.DimensionalRiftManager;
import com.l2jpx.gameserver.data.manager.FestivalOfDarknessManager;
import com.l2jpx.gameserver.data.manager.FishingChampionshipManager;
import com.l2jpx.gameserver.data.manager.FourSepulchersManager;
import com.l2jpx.gameserver.data.manager.GrandBossManager;
import com.l2jpx.gameserver.data.manager.HeroManager;
import com.l2jpx.gameserver.data.manager.LotteryManager;
import com.l2jpx.gameserver.data.manager.PartyMatchRoomManager;
import com.l2jpx.gameserver.data.manager.PetitionManager;
import com.l2jpx.gameserver.data.manager.RaidBossManager;
import com.l2jpx.gameserver.data.manager.RaidPointManager;
import com.l2jpx.gameserver.data.manager.SevenSignsManager;
import com.l2jpx.gameserver.data.manager.ZoneManager;
import com.l2jpx.gameserver.data.sql.AutoSpawnTable;
import com.l2jpx.gameserver.data.sql.BookmarkTable;
import com.l2jpx.gameserver.data.sql.ClanTable;
import com.l2jpx.gameserver.data.sql.PlayerInfoTable;
import com.l2jpx.gameserver.data.sql.ServerMemoTable;
import com.l2jpx.gameserver.data.sql.SpawnTable;
import com.l2jpx.gameserver.data.xml.AdminData;
import com.l2jpx.gameserver.data.xml.AnnouncementData;
import com.l2jpx.gameserver.data.xml.ArmorSetData;
import com.l2jpx.gameserver.data.xml.AugmentationData;
import com.l2jpx.gameserver.data.xml.DoorData;
import com.l2jpx.gameserver.data.xml.FishData;
import com.l2jpx.gameserver.data.xml.HennaData;
import com.l2jpx.gameserver.data.xml.HerbDropData;
import com.l2jpx.gameserver.data.xml.InstantTeleportData;
import com.l2jpx.gameserver.data.xml.ItemData;
import com.l2jpx.gameserver.data.xml.MapRegionData;
import com.l2jpx.gameserver.data.xml.MultisellData;
import com.l2jpx.gameserver.data.xml.NewbieBuffData;
import com.l2jpx.gameserver.data.xml.NpcData;
import com.l2jpx.gameserver.data.xml.PlayerData;
import com.l2jpx.gameserver.data.xml.PlayerLevelData;
import com.l2jpx.gameserver.data.xml.RecipeData;
import com.l2jpx.gameserver.data.xml.ScriptData;
import com.l2jpx.gameserver.data.xml.SkillTreeData;
import com.l2jpx.gameserver.data.xml.SoulCrystalData;
import com.l2jpx.gameserver.data.xml.SpellbookData;
import com.l2jpx.gameserver.data.xml.StaticObjectData;
import com.l2jpx.gameserver.data.xml.SummonItemData;
import com.l2jpx.gameserver.data.xml.TeleportData;
import com.l2jpx.gameserver.data.xml.WalkerRouteData;
import com.l2jpx.gameserver.geoengine.GeoEngine;
import com.l2jpx.gameserver.handler.AdminCommandHandler;
import com.l2jpx.gameserver.handler.ChatHandler;
import com.l2jpx.gameserver.handler.ItemHandler;
import com.l2jpx.gameserver.handler.SkillHandler;
import com.l2jpx.gameserver.handler.TargetHandler;
import com.l2jpx.gameserver.handler.UserCommandHandler;
import com.l2jpx.gameserver.idfactory.IdFactory;
import com.l2jpx.gameserver.model.World;
import com.l2jpx.gameserver.model.boat.BoatGiranTalking;
import com.l2jpx.gameserver.model.boat.BoatGludinRune;
import com.l2jpx.gameserver.model.boat.BoatInnadrilTour;
import com.l2jpx.gameserver.model.boat.BoatRunePrimeval;
import com.l2jpx.gameserver.model.boat.BoatTalkingGludin;
import com.l2jpx.gameserver.model.olympiad.Olympiad;
import com.l2jpx.gameserver.model.olympiad.OlympiadGameManager;
import com.l2jpx.gameserver.network.GameClient;
import com.l2jpx.gameserver.network.GamePacketHandler;
import com.l2jpx.gameserver.taskmanager.AttackStanceTaskManager;
import com.l2jpx.gameserver.taskmanager.DecayTaskManager;
import com.l2jpx.gameserver.taskmanager.GameTimeTaskManager;
import com.l2jpx.gameserver.taskmanager.ItemsOnGroundTaskManager;
import com.l2jpx.gameserver.taskmanager.PvpFlagTaskManager;
import com.l2jpx.gameserver.taskmanager.RandomAnimationTaskManager;
import com.l2jpx.gameserver.taskmanager.ShadowItemTaskManager;
import com.l2jpx.gameserver.taskmanager.WaterTaskManager;
import com.l2jpx.util.DeadLockDetector;
import com.l2jpx.util.IPv4Filter;

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
		MapRegionData.getInstance();
		AnnouncementData.getInstance();
		ServerMemoTable.getInstance();
		
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
		AttackStanceTaskManager.getInstance();
		DecayTaskManager.getInstance();
		GameTimeTaskManager.getInstance();
		ItemsOnGroundTaskManager.getInstance();
		PvpFlagTaskManager.getInstance();
		RandomAnimationTaskManager.getInstance();
		ShadowItemTaskManager.getInstance();
		WaterTaskManager.getInstance();
		
		StringUtil.printSection("Auto Spawns");
		AutoSpawnTable.getInstance();
		
		StringUtil.printSection("Seven Signs");
		SevenSignsManager.getInstance().spawnSevenSignsNPC();
		FestivalOfDarknessManager.getInstance();
		
		StringUtil.printSection("Manor Manager");
		CastleManorManager.getInstance();
		
		StringUtil.printSection("NPCs");
		BufferManager.getInstance();
		HerbDropData.getInstance();
		NpcData.getInstance();
		WalkerRouteData.getInstance();
		DoorData.getInstance().spawn();
		StaticObjectData.getInstance();
		SpawnTable.getInstance();
		RaidBossManager.getInstance();
		GrandBossManager.getInstance();
		DayNightManager.getInstance().notifyChangeMode();
		DimensionalRiftManager.getInstance();
		NewbieBuffData.getInstance();
		InstantTeleportData.getInstance();
		TeleportData.getInstance();
		
		StringUtil.printSection("Olympiads & Heroes");
		OlympiadGameManager.getInstance();
		Olympiad.getInstance();
		HeroManager.getInstance();
		
		StringUtil.printSection("Four Sepulchers");
		FourSepulchersManager.getInstance();
		
		StringUtil.printSection("Quests & Scripts");
		ScriptData.getInstance();
		
		if (Config.ALLOW_BOAT)
		{
			BoatManager.getInstance();
			BoatGiranTalking.load();
			BoatGludinRune.load();
			BoatInnadrilTour.load();
			BoatRunePrimeval.load();
			BoatTalkingGludin.load();
		}
		
		StringUtil.printSection("Events");
		DerbyTrackManager.getInstance();
		LotteryManager.getInstance();
		
		if (Config.ALLOW_WEDDING)
			CoupleManager.getInstance();
		
		if (Config.ALLOW_FISH_CHAMPIONSHIP)
			FishingChampionshipManager.getInstance();
		
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