package com.px.gameserver;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.InetAddress;
import java.util.logging.LogManager;

import com.px.commons.concurrent.ThreadPool;
import com.px.commons.lang.StringUtil;
import com.px.commons.logging.CLogger;
import com.px.commons.mmocore.SelectorConfig;
import com.px.commons.mmocore.SelectorThread;
import com.px.commons.util.SysUtil;

import com.px.Config;
import com.px.L2DatabaseFactory;
import com.px.gameserver.communitybbs.Manager.ForumsBBSManager;
import com.px.gameserver.data.ItemTable;
import com.px.gameserver.data.SkillTable;
import com.px.gameserver.data.cache.CrestCache;
import com.px.gameserver.data.cache.HtmCache;
import com.px.gameserver.data.manager.BoatManager;
import com.px.gameserver.data.manager.BufferManager;
import com.px.gameserver.data.manager.BuyListManager;
import com.px.gameserver.data.manager.CastleManager;
import com.px.gameserver.data.manager.CastleManorManager;
import com.px.gameserver.data.manager.ClanHallManager;
import com.px.gameserver.data.manager.CoupleManager;
import com.px.gameserver.data.manager.CursedWeaponManager;
import com.px.gameserver.data.manager.DayNightManager;
import com.px.gameserver.data.manager.DerbyTrackManager;
import com.px.gameserver.data.manager.DimensionalRiftManager;
import com.px.gameserver.data.manager.FestivalOfDarknessManager;
import com.px.gameserver.data.manager.FishingChampionshipManager;
import com.px.gameserver.data.manager.FourSepulchersManager;
import com.px.gameserver.data.manager.GrandBossManager;
import com.px.gameserver.data.manager.HeroManager;
import com.px.gameserver.data.manager.LotteryManager;
import com.px.gameserver.data.manager.MovieMakerManager;
import com.px.gameserver.data.manager.PetitionManager;
import com.px.gameserver.data.manager.RaidBossManager;
import com.px.gameserver.data.manager.RaidPointManager;
import com.px.gameserver.data.manager.SevenSignsManager;
import com.px.gameserver.data.manager.ZoneManager;
import com.px.gameserver.data.sql.AutoSpawnTable;
import com.px.gameserver.data.sql.BookmarkTable;
import com.px.gameserver.data.sql.ClanTable;
import com.px.gameserver.data.sql.PlayerInfoTable;
import com.px.gameserver.data.sql.ServerMemoTable;
import com.px.gameserver.data.sql.SpawnTable;
import com.px.gameserver.data.xml.AdminData;
import com.px.gameserver.data.xml.AnnouncementData;
import com.px.gameserver.data.xml.ArmorSetData;
import com.px.gameserver.data.xml.AugmentationData;
import com.px.gameserver.data.xml.DoorData;
import com.px.gameserver.data.xml.FishData;
import com.px.gameserver.data.xml.HennaData;
import com.px.gameserver.data.xml.HerbDropData;
import com.px.gameserver.data.xml.MapRegionData;
import com.px.gameserver.data.xml.MultisellData;
import com.px.gameserver.data.xml.NewbieBuffData;
import com.px.gameserver.data.xml.NpcData;
import com.px.gameserver.data.xml.PlayerData;
import com.px.gameserver.data.xml.RecipeData;
import com.px.gameserver.data.xml.ScriptData;
import com.px.gameserver.data.xml.SkillTreeData;
import com.px.gameserver.data.xml.SoulCrystalData;
import com.px.gameserver.data.xml.SpellbookData;
import com.px.gameserver.data.xml.StaticObjectData;
import com.px.gameserver.data.xml.SummonItemData;
import com.px.gameserver.data.xml.TeleportLocationData;
import com.px.gameserver.data.xml.WalkerRouteData;
import com.px.gameserver.geoengine.GeoEngine;
import com.px.gameserver.handler.AdminCommandHandler;
import com.px.gameserver.handler.ChatHandler;
import com.px.gameserver.handler.ItemHandler;
import com.px.gameserver.handler.SkillHandler;
import com.px.gameserver.handler.UserCommandHandler;
import com.px.gameserver.idfactory.IdFactory;
import com.px.gameserver.model.World;
import com.px.gameserver.model.boat.BoatGiranTalking;
import com.px.gameserver.model.boat.BoatGludinRune;
import com.px.gameserver.model.boat.BoatInnadrilTour;
import com.px.gameserver.model.boat.BoatRunePrimeval;
import com.px.gameserver.model.boat.BoatTalkingGludin;
import com.px.gameserver.model.olympiad.Olympiad;
import com.px.gameserver.model.olympiad.OlympiadGameManager;
import com.px.gameserver.model.partymatching.PartyMatchRoomList;
import com.px.gameserver.model.partymatching.PartyMatchWaitingList;
import com.px.gameserver.network.GameClient;
import com.px.gameserver.network.L2GamePacketHandler;
import com.px.gameserver.taskmanager.AttackStanceTaskManager;
import com.px.gameserver.taskmanager.DecayTaskManager;
import com.px.gameserver.taskmanager.GameTimeTaskManager;
import com.px.gameserver.taskmanager.ItemsOnGroundTaskManager;
import com.px.gameserver.taskmanager.MovementTaskManager;
import com.px.gameserver.taskmanager.PvpFlagTaskManager;
import com.px.gameserver.taskmanager.RandomAnimationTaskManager;
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
		
		StringUtil.printSection("aCis");
		
		// Initialize config
		Config.loadGameServer();
		
		// Factories
		L2DatabaseFactory.getInstance();
		ThreadPool.init();
		
		StringUtil.printSection("IdFactory");
		IdFactory.getInstance();
		
		StringUtil.printSection("World");
		World.getInstance();
		MapRegionData.getInstance();
		AnnouncementData.getInstance();
		ServerMemoTable.getInstance();
		
		StringUtil.printSection("Skills");
		SkillTable.getInstance();
		SkillTreeData.getInstance();
		
		StringUtil.printSection("Items");
		ItemTable.getInstance();
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
		MovieMakerManager.getInstance();
		PetitionManager.getInstance();
		
		StringUtil.printSection("Characters");
		PlayerData.getInstance();
		PlayerInfoTable.getInstance();
		NewbieBuffData.getInstance();
		TeleportLocationData.getInstance();
		HtmCache.getInstance();
		PartyMatchWaitingList.getInstance();
		PartyMatchRoomList.getInstance();
		RaidPointManager.getInstance();
		
		StringUtil.printSection("Community server");
		if (Config.ENABLE_COMMUNITY_BOARD) // Forums has to be loaded before clan data
			ForumsBBSManager.getInstance().initRoot();
		else
			LOGGER.info("Community server is disabled.");
		
		StringUtil.printSection("Clans");
		CrestCache.getInstance();
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
		MovementTaskManager.getInstance();
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
		
		if (Config.ALT_FISH_CHAMPIONSHIP_ENABLED)
			FishingChampionshipManager.getInstance();
		
		StringUtil.printSection("Handlers");
		LOGGER.info("Loaded {} admin command handlers.", AdminCommandHandler.getInstance().size());
		LOGGER.info("Loaded {} chat handlers.", ChatHandler.getInstance().size());
		LOGGER.info("Loaded {} item handlers.", ItemHandler.getInstance().size());
		LOGGER.info("Loaded {} skill handlers.", SkillHandler.getInstance().size());
		LOGGER.info("Loaded {} user command handlers.", UserCommandHandler.getInstance().size());
		
		StringUtil.printSection("System");
		Runtime.getRuntime().addShutdownHook(Shutdown.getInstance());
		ForumsBBSManager.getInstance();
		
		if (Config.DEADLOCK_DETECTOR)
		{
			LOGGER.info("Deadlock detector is enabled. Timer: {}s.", Config.DEADLOCK_CHECK_INTERVAL);
			
			final DeadLockDetector deadDetectThread = new DeadLockDetector();
			deadDetectThread.setDaemon(true);
			deadDetectThread.start();
		}
		else
			LOGGER.info("Deadlock detector is disabled.");
		
		System.gc();
		
		LOGGER.info("Gameserver has started, used memory: {} / {} Mo.", SysUtil.getUsedMemory(), SysUtil.getMaxMemory());
		LOGGER.info("Maximum allowed players: {}.", Config.MAXIMUM_ONLINE_USERS);
		
		StringUtil.printSection("Login");
		LoginServerThread.getInstance().start();
		
		final SelectorConfig sc = new SelectorConfig();
		sc.MAX_READ_PER_PASS = Config.MMO_MAX_READ_PER_PASS;
		sc.MAX_SEND_PER_PASS = Config.MMO_MAX_SEND_PER_PASS;
		sc.SLEEP_TIME = Config.MMO_SELECTOR_SLEEP_TIME;
		sc.HELPER_BUFFER_COUNT = Config.MMO_HELPER_BUFFER_COUNT;
		
		final L2GamePacketHandler handler = new L2GamePacketHandler();
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
			_selectorThread.openServerSocket(bindAddress, Config.PORT_GAME);
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