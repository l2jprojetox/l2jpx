/*
 * This file is part of the L2J Mobius project.
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.l2jpx.gameserver;

import java.awt.GraphicsEnvironment;
import java.awt.Toolkit;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.lang.management.ManagementFactory;
import java.net.InetSocketAddress;
import java.time.Duration;
import java.util.Calendar;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import org.l2jpx.Config;
import org.l2jpx.commons.database.DatabaseFactory;
import org.l2jpx.commons.enums.ServerMode;
import org.l2jpx.commons.network.ConnectionBuilder;
import org.l2jpx.commons.network.ConnectionHandler;
import org.l2jpx.commons.threads.ThreadPool;
import org.l2jpx.commons.util.DeadLockDetector;
import org.l2jpx.commons.util.PropertiesParser;
import org.l2jpx.gameserver.cache.HtmCache;
import org.l2jpx.gameserver.data.BotReportTable;
import org.l2jpx.gameserver.data.SchemeBufferTable;
import org.l2jpx.gameserver.data.sql.AnnouncementsTable;
import org.l2jpx.gameserver.data.sql.CharInfoTable;
import org.l2jpx.gameserver.data.sql.CharSummonTable;
import org.l2jpx.gameserver.data.sql.ClanTable;
import org.l2jpx.gameserver.data.sql.CrestTable;
import org.l2jpx.gameserver.data.sql.OfflineTraderTable;
import org.l2jpx.gameserver.data.xml.ActionData;
import org.l2jpx.gameserver.data.xml.AdminData;
import org.l2jpx.gameserver.data.xml.AppearanceItemData;
import org.l2jpx.gameserver.data.xml.ArmorSetData;
import org.l2jpx.gameserver.data.xml.AttendanceRewardData;
import org.l2jpx.gameserver.data.xml.BeautyShopData;
import org.l2jpx.gameserver.data.xml.BuyListData;
import org.l2jpx.gameserver.data.xml.CategoryData;
import org.l2jpx.gameserver.data.xml.ClanHallData;
import org.l2jpx.gameserver.data.xml.ClanRewardData;
import org.l2jpx.gameserver.data.xml.ClassListData;
import org.l2jpx.gameserver.data.xml.CombinationItemsData;
import org.l2jpx.gameserver.data.xml.CubicData;
import org.l2jpx.gameserver.data.xml.DailyMissionData;
import org.l2jpx.gameserver.data.xml.DoorData;
import org.l2jpx.gameserver.data.xml.DynamicExpRateData;
import org.l2jpx.gameserver.data.xml.ElementalAttributeData;
import org.l2jpx.gameserver.data.xml.EnchantItemData;
import org.l2jpx.gameserver.data.xml.EnchantItemGroupsData;
import org.l2jpx.gameserver.data.xml.EnchantItemHPBonusData;
import org.l2jpx.gameserver.data.xml.EnchantItemOptionsData;
import org.l2jpx.gameserver.data.xml.EnchantSkillGroupsData;
import org.l2jpx.gameserver.data.xml.ExperienceData;
import org.l2jpx.gameserver.data.xml.FakePlayerData;
import org.l2jpx.gameserver.data.xml.FenceData;
import org.l2jpx.gameserver.data.xml.FishingData;
import org.l2jpx.gameserver.data.xml.HennaData;
import org.l2jpx.gameserver.data.xml.HitConditionBonusData;
import org.l2jpx.gameserver.data.xml.InitialEquipmentData;
import org.l2jpx.gameserver.data.xml.InitialShortcutData;
import org.l2jpx.gameserver.data.xml.ItemCrystallizationData;
import org.l2jpx.gameserver.data.xml.ItemData;
import org.l2jpx.gameserver.data.xml.KarmaData;
import org.l2jpx.gameserver.data.xml.LuckyGameData;
import org.l2jpx.gameserver.data.xml.MultisellData;
import org.l2jpx.gameserver.data.xml.NpcData;
import org.l2jpx.gameserver.data.xml.NpcNameLocalisationData;
import org.l2jpx.gameserver.data.xml.OptionData;
import org.l2jpx.gameserver.data.xml.PetDataTable;
import org.l2jpx.gameserver.data.xml.PetSkillData;
import org.l2jpx.gameserver.data.xml.PlayerTemplateData;
import org.l2jpx.gameserver.data.xml.PlayerXpPercentLostData;
import org.l2jpx.gameserver.data.xml.PrimeShopData;
import org.l2jpx.gameserver.data.xml.RecipeData;
import org.l2jpx.gameserver.data.xml.ResidenceFunctionsData;
import org.l2jpx.gameserver.data.xml.SayuneData;
import org.l2jpx.gameserver.data.xml.SecondaryAuthData;
import org.l2jpx.gameserver.data.xml.SendMessageLocalisationData;
import org.l2jpx.gameserver.data.xml.ShuttleData;
import org.l2jpx.gameserver.data.xml.SiegeScheduleData;
import org.l2jpx.gameserver.data.xml.SkillData;
import org.l2jpx.gameserver.data.xml.SkillTreeData;
import org.l2jpx.gameserver.data.xml.SpawnData;
import org.l2jpx.gameserver.data.xml.StaticObjectData;
import org.l2jpx.gameserver.data.xml.TeleporterData;
import org.l2jpx.gameserver.data.xml.TransformData;
import org.l2jpx.gameserver.data.xml.VariationData;
import org.l2jpx.gameserver.geoengine.GeoEngine;
import org.l2jpx.gameserver.handler.ConditionHandler;
import org.l2jpx.gameserver.handler.DailyMissionHandler;
import org.l2jpx.gameserver.handler.EffectHandler;
import org.l2jpx.gameserver.handler.SkillConditionHandler;
import org.l2jpx.gameserver.instancemanager.AirShipManager;
import org.l2jpx.gameserver.instancemanager.AntiFeedManager;
import org.l2jpx.gameserver.instancemanager.BoatManager;
import org.l2jpx.gameserver.instancemanager.CaptchaManager;
import org.l2jpx.gameserver.instancemanager.CastleManager;
import org.l2jpx.gameserver.instancemanager.CastleManorManager;
import org.l2jpx.gameserver.instancemanager.ClanEntryManager;
import org.l2jpx.gameserver.instancemanager.ClanHallAuctionManager;
import org.l2jpx.gameserver.instancemanager.CursedWeaponsManager;
import org.l2jpx.gameserver.instancemanager.CustomMailManager;
import org.l2jpx.gameserver.instancemanager.DBSpawnManager;
import org.l2jpx.gameserver.instancemanager.DailyTaskManager;
import org.l2jpx.gameserver.instancemanager.DimensionalRiftManager;
import org.l2jpx.gameserver.instancemanager.FakePlayerChatManager;
import org.l2jpx.gameserver.instancemanager.GlobalVariablesManager;
import org.l2jpx.gameserver.instancemanager.GrandBossManager;
import org.l2jpx.gameserver.instancemanager.IdManager;
import org.l2jpx.gameserver.instancemanager.InstanceManager;
import org.l2jpx.gameserver.instancemanager.ItemAuctionManager;
import org.l2jpx.gameserver.instancemanager.ItemCommissionManager;
import org.l2jpx.gameserver.instancemanager.ItemsOnGroundManager;
import org.l2jpx.gameserver.instancemanager.MailManager;
import org.l2jpx.gameserver.instancemanager.MapRegionManager;
import org.l2jpx.gameserver.instancemanager.MatchingRoomManager;
import org.l2jpx.gameserver.instancemanager.MentorManager;
import org.l2jpx.gameserver.instancemanager.PcCafePointsManager;
import org.l2jpx.gameserver.instancemanager.PetitionManager;
import org.l2jpx.gameserver.instancemanager.PrecautionaryRestartManager;
import org.l2jpx.gameserver.instancemanager.PremiumManager;
import org.l2jpx.gameserver.instancemanager.PunishmentManager;
import org.l2jpx.gameserver.instancemanager.QuestManager;
import org.l2jpx.gameserver.instancemanager.SellBuffsManager;
import org.l2jpx.gameserver.instancemanager.ServerRestartManager;
import org.l2jpx.gameserver.instancemanager.SiegeGuardManager;
import org.l2jpx.gameserver.instancemanager.SiegeManager;
import org.l2jpx.gameserver.instancemanager.WalkingManager;
import org.l2jpx.gameserver.instancemanager.ZoneManager;
import org.l2jpx.gameserver.instancemanager.events.EventDropManager;
import org.l2jpx.gameserver.instancemanager.games.LotteryManager;
import org.l2jpx.gameserver.instancemanager.games.MonsterRaceManager;
import org.l2jpx.gameserver.model.World;
import org.l2jpx.gameserver.model.events.EventDispatcher;
import org.l2jpx.gameserver.model.events.EventType;
import org.l2jpx.gameserver.model.events.impl.OnServerStart;
import org.l2jpx.gameserver.model.olympiad.Hero;
import org.l2jpx.gameserver.model.olympiad.Olympiad;
import org.l2jpx.gameserver.network.GameClient;
import org.l2jpx.gameserver.network.GamePacketHandler;
import org.l2jpx.gameserver.network.NpcStringId;
import org.l2jpx.gameserver.network.SystemMessageId;
import org.l2jpx.gameserver.scripting.ScriptEngineManager;
import org.l2jpx.gameserver.taskmanager.GameTimeTaskManager;
import org.l2jpx.gameserver.taskmanager.ItemsAutoDestroyTaskManager;
import org.l2jpx.gameserver.taskmanager.TaskManager;
import org.l2jpx.gameserver.ui.Gui;
import org.l2jpx.gameserver.util.Broadcast;

public class GameServer
{
	private static final Logger LOGGER = Logger.getLogger(GameServer.class.getName());
	
	private final DeadLockDetector _deadDetectThread;
	private static GameServer INSTANCE;
	public static final Calendar dateTimeServerStarted = Calendar.getInstance();
	
	public long getUsedMemoryMB()
	{
		return (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / 1048576;
	}
	
	public DeadLockDetector getDeadLockDetectorThread()
	{
		return _deadDetectThread;
	}
	
	public GameServer() throws Exception
	{
		final long serverLoadStart = System.currentTimeMillis();
		
		// GUI
		final PropertiesParser interfaceConfig = new PropertiesParser(Config.INTERFACE_CONFIG_FILE);
		Config.ENABLE_GUI = interfaceConfig.getBoolean("EnableGUI", true);
		if (Config.ENABLE_GUI && !GraphicsEnvironment.isHeadless())
		{
			Config.DARK_THEME = interfaceConfig.getBoolean("DarkTheme", true);
			System.out.println("GameServer: Running in GUI mode.");
			new Gui();
		}
		
		// Create log folder
		final File logFolder = new File(".", "log");
		logFolder.mkdir();
		
		// Create input stream for log file -- or store file data into memory
		try (InputStream is = new FileInputStream(new File("./log.cfg")))
		{
			LogManager.getLogManager().readConfiguration(is);
		}
		
		// Initialize config
		Config.load(ServerMode.GAME);
		
		printSection("Database");
		DatabaseFactory.init();
		
		printSection("ThreadPool");
		ThreadPool.init();
		
		// Start game time task manager early
		GameTimeTaskManager.getInstance();
		
		printSection("IdManager");
		IdManager.getInstance();
		
		printSection("Scripting Engine");
		EventDispatcher.getInstance();
		ScriptEngineManager.getInstance();
		
		printSection("World");
		World.getInstance();
		MapRegionManager.getInstance();
		ZoneManager.getInstance();
		DoorData.getInstance();
		FenceData.getInstance();
		AnnouncementsTable.getInstance();
		GlobalVariablesManager.getInstance();
		
		printSection("Data");
		ActionData.getInstance();
		CategoryData.getInstance();
		DynamicExpRateData.getInstance();
		SecondaryAuthData.getInstance();
		CombinationItemsData.getInstance();
		SayuneData.getInstance();
		ClanRewardData.getInstance();
		DailyMissionHandler.getInstance().executeScript();
		DailyMissionData.getInstance();
		
		printSection("Skills");
		SkillConditionHandler.getInstance().executeScript();
		EffectHandler.getInstance().executeScript();
		EnchantSkillGroupsData.getInstance();
		SkillTreeData.getInstance();
		SkillData.getInstance();
		PetSkillData.getInstance();
		
		printSection("Items");
		ConditionHandler.getInstance().executeScript();
		ItemData.getInstance();
		EnchantItemGroupsData.getInstance();
		EnchantItemData.getInstance();
		EnchantItemOptionsData.getInstance();
		ElementalAttributeData.getInstance();
		ItemCrystallizationData.getInstance();
		OptionData.getInstance();
		VariationData.getInstance();
		EnchantItemHPBonusData.getInstance();
		BuyListData.getInstance();
		MultisellData.getInstance();
		RecipeData.getInstance();
		ArmorSetData.getInstance();
		FishingData.getInstance();
		HennaData.getInstance();
		PrimeShopData.getInstance();
		PcCafePointsManager.getInstance();
		AppearanceItemData.getInstance();
		ItemCommissionManager.getInstance();
		LuckyGameData.getInstance();
		AttendanceRewardData.getInstance();
		
		printSection("Characters");
		ClassListData.getInstance();
		InitialEquipmentData.getInstance();
		InitialShortcutData.getInstance();
		ExperienceData.getInstance();
		PlayerXpPercentLostData.getInstance();
		KarmaData.getInstance();
		HitConditionBonusData.getInstance();
		PlayerTemplateData.getInstance();
		CharInfoTable.getInstance();
		AdminData.getInstance();
		PetDataTable.getInstance();
		CubicData.getInstance();
		CharSummonTable.getInstance().init();
		CaptchaManager.getInstance();
		BeautyShopData.getInstance();
		MentorManager.getInstance();
		
		if (Config.PREMIUM_SYSTEM_ENABLED)
		{
			LOGGER.info("PremiumManager: Premium system is enabled.");
			PremiumManager.getInstance();
		}
		
		printSection("Clans");
		ClanTable.getInstance();
		ResidenceFunctionsData.getInstance();
		ClanHallData.getInstance();
		ClanHallAuctionManager.getInstance();
		ClanEntryManager.getInstance();
		
		printSection("Geodata");
		GeoEngine.getInstance();
		
		printSection("NPCs");
		NpcData.getInstance();
		FakePlayerData.getInstance();
		FakePlayerChatManager.getInstance();
		SpawnData.getInstance();
		WalkingManager.getInstance();
		StaticObjectData.getInstance();
		ItemAuctionManager.getInstance();
		CastleManager.getInstance().loadInstances();
		SchemeBufferTable.getInstance();
		GrandBossManager.getInstance();
		EventDropManager.getInstance();
		
		printSection("Instance");
		InstanceManager.getInstance();
		
		printSection("Olympiad");
		Olympiad.getInstance();
		Hero.getInstance();
		
		// Call to load caches
		printSection("Cache");
		HtmCache.getInstance();
		CrestTable.getInstance();
		TeleporterData.getInstance();
		MatchingRoomManager.getInstance();
		PetitionManager.getInstance();
		CursedWeaponsManager.getInstance();
		TransformData.getInstance();
		BotReportTable.getInstance();
		if (Config.SELLBUFF_ENABLED)
		{
			SellBuffsManager.getInstance();
		}
		if (Config.MULTILANG_ENABLE)
		{
			SystemMessageId.loadLocalisations();
			NpcStringId.loadLocalisations();
			SendMessageLocalisationData.getInstance();
			NpcNameLocalisationData.getInstance();
		}
		
		printSection("Scripts");
		QuestManager.getInstance();
		BoatManager.getInstance();
		AirShipManager.getInstance();
		ShuttleData.getInstance();
		
		try
		{
			LOGGER.info(getClass().getSimpleName() + ": Loading server scripts:");
			ScriptEngineManager.getInstance().executeScript(ScriptEngineManager.MASTER_HANDLER_FILE);
			ScriptEngineManager.getInstance().executeScriptList();
		}
		catch (Exception e)
		{
			LOGGER.log(Level.WARNING, getClass().getSimpleName() + ": Failed to execute script list!", e);
		}
		
		SpawnData.getInstance().init();
		DimensionalRiftManager.getInstance();
		DBSpawnManager.getInstance();
		
		printSection("Siege");
		SiegeManager.getInstance().getSieges();
		CastleManager.getInstance().activateInstances();
		// No fortresses
		// FortManager.getInstance().loadInstances();
		// FortManager.getInstance().activateInstances();
		// FortSiegeManager.getInstance();
		SiegeScheduleData.getInstance();
		CastleManorManager.getInstance();
		SiegeGuardManager.getInstance();
		QuestManager.getInstance().report();
		if (Config.SAVE_DROPPED_ITEM)
		{
			ItemsOnGroundManager.getInstance();
		}
		if ((Config.AUTODESTROY_ITEM_AFTER > 0) || (Config.HERB_AUTO_DESTROY_TIME > 0))
		{
			ItemsAutoDestroyTaskManager.getInstance();
		}
		MonsterRaceManager.getInstance();
		LotteryManager.getInstance();
		TaskManager.getInstance();
		DailyTaskManager.getInstance();
		AntiFeedManager.getInstance().registerEvent(AntiFeedManager.GAME_ID);
		if (Config.ALLOW_MAIL)
		{
			MailManager.getInstance();
		}
		if (Config.CUSTOM_MAIL_MANAGER_ENABLED)
		{
			CustomMailManager.getInstance();
		}
		if (EventDispatcher.getInstance().hasListener(EventType.ON_SERVER_START))
		{
			EventDispatcher.getInstance().notifyEventAsync(new OnServerStart());
		}
		PunishmentManager.getInstance();
		
		Runtime.getRuntime().addShutdownHook(Shutdown.getInstance());
		LOGGER.info("IdManager: Free ObjectID's remaining: " + IdManager.getInstance().size());
		
		if ((Config.OFFLINE_TRADE_ENABLE || Config.OFFLINE_CRAFT_ENABLE) && Config.RESTORE_OFFLINERS)
		{
			OfflineTraderTable.getInstance().restoreOfflineTraders();
		}
		if (Config.SERVER_RESTART_SCHEDULE_ENABLED)
		{
			ServerRestartManager.getInstance();
		}
		if (Config.PRECAUTIONARY_RESTART_ENABLED)
		{
			PrecautionaryRestartManager.getInstance();
		}
		if (Config.DEADLOCK_DETECTOR)
		{
			_deadDetectThread = new DeadLockDetector(Duration.ofSeconds(Config.DEADLOCK_CHECK_INTERVAL), () ->
			{
				if (Config.RESTART_ON_DEADLOCK)
				{
					Broadcast.toAllOnlinePlayers("Server has stability issues - restarting now.");
					Shutdown.getInstance().startShutdown(null, 60, true);
				}
			});
			_deadDetectThread.setDaemon(true);
			_deadDetectThread.start();
		}
		else
		{
			_deadDetectThread = null;
		}
		
		System.gc();
		final long totalMem = Runtime.getRuntime().maxMemory() / 1048576;
		LOGGER.info(getClass().getSimpleName() + ": Started, using " + getUsedMemoryMB() + " of " + totalMem + " MB total memory.");
		LOGGER.info(getClass().getSimpleName() + ": Maximum number of connected players is " + Config.MAXIMUM_ONLINE_USERS + ".");
		LOGGER.info(getClass().getSimpleName() + ": Server loaded in " + ((System.currentTimeMillis() - serverLoadStart) / 1000) + " seconds.");
		
		final ConnectionHandler<GameClient> connectionHandler = new ConnectionBuilder<>(new InetSocketAddress(Config.PORT_GAME), GameClient::new, new GamePacketHandler(), ThreadPool::execute).build();
		connectionHandler.start();
		
		LoginServerThread.getInstance().start();
		
		Toolkit.getDefaultToolkit().beep();
	}
	
	public long getStartedTime()
	{
		return ManagementFactory.getRuntimeMXBean().getStartTime();
	}
	
	public String getUptime()
	{
		final long uptime = ManagementFactory.getRuntimeMXBean().getUptime() / 1000;
		final long hours = uptime / 3600;
		final long mins = (uptime - (hours * 3600)) / 60;
		final long secs = ((uptime - (hours * 3600)) - (mins * 60));
		if (hours > 0)
		{
			return hours + "hrs " + mins + "mins " + secs + "secs";
		}
		return mins + "mins " + secs + "secs";
	}
	
	public static void main(String[] args) throws Exception
	{
		INSTANCE = new GameServer();
	}
	
	private void printSection(String section)
	{
		String s = "=[ " + section + " ]";
		while (s.length() < 61)
		{
			s = "-" + s;
		}
		LOGGER.info(s);
	}
	
	public static GameServer getInstance()
	{
		return INSTANCE;
	}
}
