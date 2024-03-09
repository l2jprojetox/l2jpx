package net.l2jpx;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.LineNumberReader;
import java.io.OutputStream;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import engine.engine.events.daily.normal.types.NpcClassMaster.ClassMasterList;
import net.l2jpx.gameserver.model.entity.olympiad.OlympiadPeriod;
import net.l2jpx.gameserver.util.FloodProtectorConfig;
import net.l2jpx.util.StringUtil;

/**
 * @author ReynalDev
 */
public final class Config
{

	private static final Logger LOGGER = Logger.getLogger(Config.class);


	public static int ID_QUESTHELP;

	public static void  loadConfigCustom()
	{
		String filePath = "config/custom/itemhandlers.properties";

		try
		{
			L2Properties hendlersConfig = new L2Properties(filePath);

			ID_QUESTHELP = Integer.parseInt(hendlersConfig.getProperty("itemQuestBook", "1"));
		}
		catch (Exception e)
		{
			LOGGER.error("Config : Failed to load " + filePath + " file.", e);
		}
	}
	// ============================================================
	public static boolean GM_STARTUP_INVISIBLE;
	public static boolean GM_STARTUP_SILENCE;
	public static boolean GM_STARTUP_AUTO_LIST;
	public static boolean GM_HERO_AURA;
	public static boolean GM_STARTUP_INVULNERABLE;
	public static boolean GM_ANNOUNCER_NAME;
	public static Map<Integer, Integer> GM_PLAYERS = new HashMap<>();
	public static boolean GM_RESTART_FIGHTING;
	
	// ============================================================
	public static void loadAccessConfig()
	{
		String filePath = "config/access_level/access.properties";
		
		try
		{
			L2Properties AccessSettings = new L2Properties(filePath);
			
			GM_STARTUP_AUTO_LIST = Boolean.parseBoolean(AccessSettings.getProperty("GMStartupAutoList", "true"));
			GM_HERO_AURA = Boolean.parseBoolean(AccessSettings.getProperty("GMHeroAura", "false"));
			GM_STARTUP_INVULNERABLE = Boolean.parseBoolean(AccessSettings.getProperty("GMStartupInvulnerable", "true"));
			GM_ANNOUNCER_NAME = Boolean.parseBoolean(AccessSettings.getProperty("AnnounceGmName", "false"));
			GM_STARTUP_INVISIBLE = Boolean.parseBoolean(AccessSettings.getProperty("GMStartupInvisible", "true"));
			GM_STARTUP_SILENCE = Boolean.parseBoolean(AccessSettings.getProperty("GMStartupSilence", "true"));
			GM_RESTART_FIGHTING = Boolean.parseBoolean(AccessSettings.getProperty("GMRestartFighting", "False"));
		}
		catch (Exception e)
		{
			LOGGER.error("Config : Failed to load " + filePath + " file.", e);
		}
	}
	
	// ============================================================
	public static boolean CHECK_KNOWNLIST;
	
	public static String DEFAULT_GLOBAL_CHAT;
	public static String DEFAULT_TRADE_CHAT;
	
	public static boolean TRADE_CHAT_WITH_PVP;
	public static int TRADE_PVP_AMOUNT;
	public static boolean GLOBAL_CHAT_WITH_PVP;
	public static int GLOBAL_PVP_AMOUNT;
	
	// Anti Brute force attack on login
	public static int BRUT_AVG_TIME;
	public static int BRUT_LOGON_ATTEMPTS;
	public static int BRUT_BAN_IP_TIME;
	
	public static int MAX_CHAT_LENGTH;
	public static boolean TRADE_CHAT_IS_NOOBLE;
	public static boolean MULTIPLY_QUANTITY_BY_CHANCE;
	public static boolean PRECISE_DROP_CALCULATION;
	public static boolean MULTIPLE_ITEM_DROP;
	public static int DELETE_DAYS;
	public static boolean ALLOWFISHING;
	public static boolean ALLOW_MANOR;
	public static int AUTODESTROY_ITEM_AFTER;
	public static int HERB_AUTO_DESTROY_TIME;
	public static String PROTECTED_ITEMS;
	public static List<Integer> LIST_PROTECTED_ITEMS = new ArrayList<>();
	public static boolean DESTROY_DROPPED_PLAYER_ITEM;
	public static boolean DESTROY_EQUIPABLE_PLAYER_ITEM;
	public static boolean SAVE_DROPPED_ITEM;
	public static boolean EMPTY_DROPPED_ITEM_TABLE_AFTER_LOAD;
	public static int SAVE_DROPPED_ITEM_INTERVAL;
	public static boolean CLEAR_DROPPED_ITEM_TABLE;
	public static boolean ALLOW_DISCARDITEM;
	public static List<Integer> ITEM_IDS_IGNORED_IN_DROP_LIST;
	public static boolean ALLOW_FREIGHT;
	public static boolean ALLOW_WAREHOUSE;
	public static boolean WAREHOUSE_CACHE;
	public static int WAREHOUSE_CACHE_TIME;
	public static boolean ALLOW_WEAR;
	public static int WEAR_DELAY;
	public static int WEAR_PRICE;
	public static boolean ALLOW_LOTTERY;
	public static boolean ALLOW_RACE;
	public static boolean ALLOW_RENTPET;
	public static boolean ALLOW_BOAT;
	public static boolean ALLOW_CURSED_WEAPONS;
	public static boolean ALLOW_NPC_WALKERS;
	public static int MIN_NPC_ANIMATION;
	public static int MAX_NPC_ANIMATION;
	public static int MIN_MONSTER_ANIMATION;
	public static int MAX_MONSTER_ANIMATION;
	public static boolean ALLOW_USE_CURSOR_FOR_WALK;
	public static String COMMUNITY_TYPE;
	public static String BBS_DEFAULT;
	public static boolean SHOW_LEVEL_COMMUNITYBOARD;
	public static boolean SHOW_STATUS_COMMUNITYBOARD;
	public static int NAME_PAGE_SIZE_COMMUNITYBOARD;
	public static int NAME_PER_ROW_COMMUNITYBOARD;
	public static int NEW_NODE_ID;
	public static int SELECTED_NODE_ID;
	public static int LINKED_NODE_ID;
	public static String NEW_NODE_TYPE;
	public static boolean SHOW_NPC_LVL;
	public static int ZONE_TOWN;
	public static int DEFAULT_PUNISH;
	public static int DEFAULT_PUNISH_PARAM;
	public static boolean AUTODELETE_INVALID_QUEST_DATA;
	public static boolean GRIDS_ALWAYS_ON;
	public static int GRID_NEIGHBOR_TURNON_TIME;
	public static int GRID_NEIGHBOR_TURNOFF_TIME;
	
	public static boolean HIGH_RATE_SERVER_DROPS;
	
	public static boolean FORCE_COMPLETE_STATUS_UPDATE;
	
	// ============================================================
	public static void loadOptionsConfig()
	{
		String filePath = "config/head/options.properties";
		
		try
		{
			L2Properties optionsSettings = new L2Properties(filePath);
			
			AUTODESTROY_ITEM_AFTER = Integer.parseInt(optionsSettings.getProperty("AutoDestroyDroppedItemAfter", "0"));
			HERB_AUTO_DESTROY_TIME = Integer.parseInt(optionsSettings.getProperty("AutoDestroyHerbTime", "15")) * 1000;
			PROTECTED_ITEMS = optionsSettings.getProperty("ListOfProtectedItems");
			LIST_PROTECTED_ITEMS = new ArrayList<>();
			for (final String id : PROTECTED_ITEMS.split(","))
			{
				LIST_PROTECTED_ITEMS.add(Integer.parseInt(id));
			}
			DESTROY_DROPPED_PLAYER_ITEM = Boolean.valueOf(optionsSettings.getProperty("DestroyPlayerDroppedItem", "false"));
			DESTROY_EQUIPABLE_PLAYER_ITEM = Boolean.valueOf(optionsSettings.getProperty("DestroyEquipableItem", "false"));
			SAVE_DROPPED_ITEM = Boolean.valueOf(optionsSettings.getProperty("SaveDroppedItem", "false"));
			EMPTY_DROPPED_ITEM_TABLE_AFTER_LOAD = Boolean.valueOf(optionsSettings.getProperty("EmptyDroppedItemTableAfterLoad", "false"));
			SAVE_DROPPED_ITEM_INTERVAL = Integer.parseInt(optionsSettings.getProperty("SaveDroppedItemInterval", "0")) * 60000;
			CLEAR_DROPPED_ITEM_TABLE = Boolean.valueOf(optionsSettings.getProperty("ClearDroppedItemTable", "false"));
			
			MULTIPLY_QUANTITY_BY_CHANCE = Boolean.valueOf(optionsSettings.getProperty("MutiplyQuantityByChance", "True"));
			PRECISE_DROP_CALCULATION = Boolean.valueOf(optionsSettings.getProperty("PreciseDropCalculation", "True"));
			MULTIPLE_ITEM_DROP = Boolean.valueOf(optionsSettings.getProperty("MultipleItemDrop", "True"));
			
			ALLOW_WAREHOUSE = Boolean.valueOf(optionsSettings.getProperty("AllowWarehouse", "True"));
			WAREHOUSE_CACHE = Boolean.valueOf(optionsSettings.getProperty("WarehouseCache", "False"));
			WAREHOUSE_CACHE_TIME = Integer.parseInt(optionsSettings.getProperty("WarehouseCacheTime", "15"));
			ALLOW_FREIGHT = Boolean.valueOf(optionsSettings.getProperty("AllowFreight", "True"));
			ALLOW_WEAR = Boolean.valueOf(optionsSettings.getProperty("AllowWear", "False"));
			WEAR_DELAY = Integer.parseInt(optionsSettings.getProperty("WearDelay", "5"));
			WEAR_PRICE = Integer.parseInt(optionsSettings.getProperty("WearPrice", "10"));
			ALLOW_LOTTERY = Boolean.valueOf(optionsSettings.getProperty("AllowLottery", "False"));
			ALLOW_RACE = Boolean.valueOf(optionsSettings.getProperty("AllowRace", "False"));
			ALLOW_RENTPET = Boolean.valueOf(optionsSettings.getProperty("AllowRentPet", "False"));
			ALLOW_DISCARDITEM = Boolean.valueOf(optionsSettings.getProperty("AllowDiscardItem", "True"));
			
			String itemsIgnoredInDropListData = optionsSettings.getProperty("ItemsIgnoredInDropList", "");
			ITEM_IDS_IGNORED_IN_DROP_LIST = new ArrayList<>();
			
			if (!itemsIgnoredInDropListData.isEmpty())
			{
				for (String sItemId : itemsIgnoredInDropListData.split(","))
				{
					try
					{
						int itemId = Integer.parseInt(sItemId.trim());
						ITEM_IDS_IGNORED_IN_DROP_LIST.add(itemId);
					}
					catch (Exception e)
					{
						LOGGER.error("Possible invalid number format exception", e);
					}
				}
			}
			
			ALLOWFISHING = Boolean.valueOf(optionsSettings.getProperty("AllowFishing", "False"));
			ALLOW_MANOR = Boolean.parseBoolean(optionsSettings.getProperty("AllowManor", "False"));
			ALLOW_BOAT = Boolean.valueOf(optionsSettings.getProperty("AllowBoat", "False"));
			ALLOW_NPC_WALKERS = Boolean.valueOf(optionsSettings.getProperty("AllowNpcWalkers", "true"));
			ALLOW_CURSED_WEAPONS = Boolean.valueOf(optionsSettings.getProperty("AllowCursedWeapons", "False"));
			
			ALLOW_USE_CURSOR_FOR_WALK = Boolean.valueOf(optionsSettings.getProperty("AllowUseCursorForWalk", "False"));
			DEFAULT_GLOBAL_CHAT = optionsSettings.getProperty("GlobalChat", "ON");
			DEFAULT_TRADE_CHAT = optionsSettings.getProperty("TradeChat", "ON");
			MAX_CHAT_LENGTH = Integer.parseInt(optionsSettings.getProperty("MaxChatLength", "100"));
			
			TRADE_CHAT_IS_NOOBLE = Boolean.valueOf(optionsSettings.getProperty("TradeChatIsNooble", "false"));
			TRADE_CHAT_WITH_PVP = Boolean.valueOf(optionsSettings.getProperty("TradeChatWithPvP", "false"));
			TRADE_PVP_AMOUNT = Integer.parseInt(optionsSettings.getProperty("TradePvPAmount", "800"));
			GLOBAL_CHAT_WITH_PVP = Boolean.valueOf(optionsSettings.getProperty("GlobalChatWithPvP", "false"));
			GLOBAL_PVP_AMOUNT = Integer.parseInt(optionsSettings.getProperty("GlobalPvPAmount", "1500"));
			
			COMMUNITY_TYPE = optionsSettings.getProperty("CommunityType", "old").toLowerCase();
			BBS_DEFAULT = optionsSettings.getProperty("BBSDefault", "_bbshome");
			SHOW_LEVEL_COMMUNITYBOARD = Boolean.valueOf(optionsSettings.getProperty("ShowLevelOnCommunityBoard", "False"));
			SHOW_STATUS_COMMUNITYBOARD = Boolean.valueOf(optionsSettings.getProperty("ShowStatusOnCommunityBoard", "True"));
			NAME_PAGE_SIZE_COMMUNITYBOARD = Integer.parseInt(optionsSettings.getProperty("NamePageSizeOnCommunityBoard", "50"));
			NAME_PER_ROW_COMMUNITYBOARD = Integer.parseInt(optionsSettings.getProperty("NamePerRowOnCommunityBoard", "5"));
			
			ZONE_TOWN = Integer.parseInt(optionsSettings.getProperty("ZoneTown", "0"));
			
			MIN_NPC_ANIMATION = Integer.parseInt(optionsSettings.getProperty("MinNPCAnimation", "10"));
			MAX_NPC_ANIMATION = Integer.parseInt(optionsSettings.getProperty("MaxNPCAnimation", "20"));
			MIN_MONSTER_ANIMATION = Integer.parseInt(optionsSettings.getProperty("MinMonsterAnimation", "5"));
			MAX_MONSTER_ANIMATION = Integer.parseInt(optionsSettings.getProperty("MaxMonsterAnimation", "20"));
			
			SHOW_NPC_LVL = Boolean.valueOf(optionsSettings.getProperty("ShowNpcLevel", "False"));
			
			FORCE_INVENTORY_UPDATE = Boolean.valueOf(optionsSettings.getProperty("ForceInventoryUpdate", "False"));
			
			FORCE_COMPLETE_STATUS_UPDATE = Boolean.valueOf(optionsSettings.getProperty("ForceCompletePlayerStatusUpdate", "true"));
			
			AUTODELETE_INVALID_QUEST_DATA = Boolean.valueOf(optionsSettings.getProperty("AutoDeleteInvalidQuestData", "False"));
			
			DELETE_DAYS = Integer.parseInt(optionsSettings.getProperty("DeleteCharAfterDays", "7"));
			
			DEFAULT_PUNISH = Integer.parseInt(optionsSettings.getProperty("DefaultPunish", "2"));
			DEFAULT_PUNISH_PARAM = Integer.parseInt(optionsSettings.getProperty("DefaultPunishParam", "0"));
			
			GRIDS_ALWAYS_ON = Boolean.parseBoolean(optionsSettings.getProperty("GridsAlwaysOn", "False"));
			GRID_NEIGHBOR_TURNON_TIME = Integer.parseInt(optionsSettings.getProperty("GridNeighborTurnOnTime", "30"));
			GRID_NEIGHBOR_TURNOFF_TIME = Integer.parseInt(optionsSettings.getProperty("GridNeighborTurnOffTime", "300"));
			
			NEW_NODE_ID = Integer.parseInt(optionsSettings.getProperty("NewNodeId", "7952"));
			SELECTED_NODE_ID = Integer.parseInt(optionsSettings.getProperty("NewNodeId", "7952"));
			LINKED_NODE_ID = Integer.parseInt(optionsSettings.getProperty("NewNodeId", "7952"));
			NEW_NODE_TYPE = optionsSettings.getProperty("NewNodeType", "npc");
			
			CHECK_KNOWNLIST = Boolean.valueOf(optionsSettings.getProperty("CheckKnownList", "false"));
			
			HIGH_RATE_SERVER_DROPS = Boolean.valueOf(optionsSettings.getProperty("HighRateServerDrops", "false"));
		}
		catch (Exception e)
		{
			LOGGER.error("Config : Failed to load " + filePath + " file.", e);
		}
	}
	
	// ============================================================
	public static int PORT_GAME;
	public static String GAMESERVER_DB;
	public static String LOGINSERVER_DB;
	public static String GAMESERVER_HOSTNAME;
	public static String DATABASE_GAMESERVER_HOST;
	public static String DATABASE_LOGINSERVER_HOST;
	public static String DATABASE_LOGINSERVER_NAME;
	public static String DATABASE_URL;
	public static String DATABASE_USER;
	public static String DATABASE_PASSWORD;
	public static boolean BETASERVER;
	public static boolean SERVER_LIST_TESTSERVER;
	public static boolean SERVER_LIST_BRACKET;
	public static boolean SERVER_LIST_CLOCK;
	public static boolean SERVER_GMONLY;
	public static String CNAME_TEMPLATE;
	public static String PET_NAME_TEMPLATE;
	public static String CLAN_NAME_TEMPLATE;
	public static String ALLY_NAME_TEMPLATE;
	public static int MAX_CHARACTERS_NUMBER_PER_ACCOUNT;
	public static int MAX_CHARACTERS_NUMBER_PER_IP;
	public static int MAXIMUM_ONLINE_USERS;
	public static int MIN_PROTOCOL_REVISION;
	public static int MAX_PROTOCOL_REVISION;
	public static int REQUEST_ID;
	public static boolean RESERVE_HOST_ON_LOGIN = false;
	
	// ============================================================
	public static void loadServerConfig()
	{
		String filePath = "config/network/gameserver.properties";
		
		try
		{
			L2Properties serverSettings = new L2Properties(filePath);
			
			GAMESERVER_HOSTNAME = serverSettings.getProperty("GameserverHostname");
			PORT_GAME = Integer.parseInt(serverSettings.getProperty("GameserverPort", "7777"));
			
			EXTERNAL_HOSTNAME = serverSettings.getProperty("ExternalHostname", "*");
			INTERNAL_HOSTNAME = serverSettings.getProperty("InternalHostname", "*");
			
			GAME_SERVER_LOGIN_PORT = Integer.parseInt(serverSettings.getProperty("LoginPort", "9014"));
			GAME_SERVER_LOGIN_HOST = serverSettings.getProperty("LoginHost", "127.0.0.1");
			
			// ------------- GameServer
			DATABASE_GAMESERVER_HOST = serverSettings.getProperty("DatabaseGameServerHost", "127.0.0.1");
			GAMESERVER_DB = serverSettings.getProperty("GameserverDB", "frozen");
			LOGINSERVER_DB = serverSettings.getProperty("LoginserverDB", "frozen");
			DATABASE_URL = "jdbc:mariadb://" + DATABASE_GAMESERVER_HOST + "/" + GAMESERVER_DB; // MariaDB JDBC URL
			// ------------- GameServer
			
			DATABASE_USER = serverSettings.getProperty("DatabaseUser", "root");
			DATABASE_PASSWORD = serverSettings.getProperty("DatabasePassword", "");
			
			BETASERVER = Boolean.parseBoolean(serverSettings.getProperty("BetaServer", "false"));
			SERVER_LIST_TESTSERVER = Boolean.parseBoolean(serverSettings.getProperty("TestServer", "false"));
			SERVER_LIST_BRACKET = Boolean.valueOf(serverSettings.getProperty("ServerListBrackets", "false"));
			SERVER_LIST_CLOCK = Boolean.valueOf(serverSettings.getProperty("ServerListClock", "false"));
			SERVER_GMONLY = Boolean.valueOf(serverSettings.getProperty("ServerGMOnly", "false"));
			
			CNAME_TEMPLATE = serverSettings.getProperty("CnameTemplate", ".*");
			PET_NAME_TEMPLATE = serverSettings.getProperty("PetNameTemplate", ".*");
			CLAN_NAME_TEMPLATE = serverSettings.getProperty("ClanNameTemplate", ".*");
			ALLY_NAME_TEMPLATE = serverSettings.getProperty("AllyNameTemplate", ".*");
			
			MAX_CHARACTERS_NUMBER_PER_ACCOUNT = Integer.parseInt(serverSettings.getProperty("CharMaxNumber", "0"));
			MAX_CHARACTERS_NUMBER_PER_IP = Integer.parseInt(serverSettings.getProperty("CharMaxNumberPerIP", "0"));
			
			MAXIMUM_ONLINE_USERS = Integer.parseInt(serverSettings.getProperty("MaximumOnlineUsers", "100"));
			
			MIN_PROTOCOL_REVISION = Integer.parseInt(serverSettings.getProperty("MinProtocolRevision", "660"));
			MAX_PROTOCOL_REVISION = Integer.parseInt(serverSettings.getProperty("MaxProtocolRevision", "665"));
			
			if (MIN_PROTOCOL_REVISION > MAX_PROTOCOL_REVISION)
			{
				throw new Error("MinProtocolRevision is bigger than MaxProtocolRevision in server configuration file.");
			}
			
			REQUEST_ID = Integer.parseInt(serverSettings.getProperty("RequestServerID", "0"));
			
			DATAPACK_ROOT = new File(serverSettings.getProperty("DatapackRoot", ".")).getCanonicalFile();
		}
		catch (Exception e)
		{
			LOGGER.error("Config : Failed to load " + filePath + " file.", e);
		}
	}
	
	// ============================================================
	public static IdFactoryType IDFACTORY_TYPE;
	public static boolean BAD_ID_CHECKING;
	public static ObjectMapType MAP_TYPE;
	public static ObjectSetType SET_TYPE;
	
	// ============================================================
	public static void loadIdFactoryConfig()
	{
		String filePath = "config/others/idfactory.properties";
		
		try
		{
			L2Properties idSettings = new L2Properties(filePath);
			
			MAP_TYPE = ObjectMapType.valueOf(idSettings.getProperty("L2Map", "WorldObjectMap"));
			SET_TYPE = ObjectSetType.valueOf(idSettings.getProperty("L2Set", "WorldObjectSet"));
			IDFACTORY_TYPE = IdFactoryType.valueOf(idSettings.getProperty("IDFactory", "Compaction"));
			BAD_ID_CHECKING = Boolean.valueOf(idSettings.getProperty("BadIdChecking", "True"));
		}
		catch (Exception e)
		{
			LOGGER.error("Config : Failed to load " + filePath + " file.", e);
		}
	}
	
	// ============================================================
	public static int MAX_ITEM_IN_PACKET;
	public static boolean JAIL_IS_PVP;
	public static boolean JAIL_DISABLE_CHAT;
	public static int WYVERN_SPEED;
	public static int STRIDER_SPEED;
	public static boolean ALLOW_WYVERN_UPGRADER;
	public static int INVENTORY_MAXIMUM_NO_DWARF;
	public static int INVENTORY_MAXIMUM_DWARF;
	public static int INVENTORY_MAXIMUM_GM;
	public static int WAREHOUSE_SLOTS_NO_DWARF;
	public static int WAREHOUSE_SLOTS_DWARF;
	public static int WAREHOUSE_SLOTS_CLAN;
	public static int FREIGHT_SLOTS;
	public static String PET_RENT_NPC;
	public static List<Integer> LIST_PET_RENT_NPC = new ArrayList<>();
	public static boolean EFFECT_CANCELING;
	public static double HP_REGEN_MULTIPLIER;
	public static double MP_REGEN_MULTIPLIER;
	public static double CP_REGEN_MULTIPLIER;
	public static boolean ANNOUNCE_CASTLE_LORDS;
	
	/** Configuration to allow custom items to be given on character creation */
	public static int CUSTOM_STARTING_LEVEL;
	public static boolean CUSTOM_STARTER_ITEMS_ENABLED;
	public static List<int[]> STARTING_CUSTOM_ITEMS_F = new ArrayList<>();
	public static List<int[]> STARTING_CUSTOM_ITEMS_M = new ArrayList<>();
	public static boolean CUSTOM_SPAWN_CHAR;
	public static int CUSTOM_SPAWN_X;
	public static int CUSTOM_SPAWN_Y;
	public static int CUSTOM_SPAWN_Z;
	
	public static boolean DEEPBLUE_DROP_RULES;
	public static int UNSTUCK_INTERVAL;
	public static int DEATH_PENALTY_CHANCE;
	public static int PLAYER_TELEPORT_PROTECTION;
	public static int PLAYER_FAKEDEATH_UP_PROTECTION;
	public static String PARTY_XP_CUTOFF_METHOD;
	public static int PARTY_XP_CUTOFF_LEVEL;
	public static double PARTY_XP_CUTOFF_PERCENT;
	public static double RESPAWN_RESTORE_CP;
	public static double RESPAWN_RESTORE_HP;
	public static double RESPAWN_RESTORE_MP;
	public static boolean RESPAWN_RANDOM_ENABLED;
	public static int RESPAWN_RANDOM_MAX_OFFSET;
	public static int MAX_PVTSTORE_SLOTS_DWARF;
	public static int MAX_PVTSTORE_SLOTS_OTHER;
	public static boolean PETITIONING_ALLOWED;
	public static int MAX_PETITIONS_PER_PLAYER;
	public static int MAX_PETITIONS_PENDING;
	public static boolean ENABLE_MODIFY_SKILL_DURATION;
	public static Map<Integer, Integer> SKILL_DURATION_LIST;
	/** Chat Filter **/
	public static int CHAT_FILTER_PUNISHMENT_PARAM1;
	public static int CHAT_FILTER_PUNISHMENT_PARAM2;
	public static int CHAT_FILTER_PUNISHMENT_PARAM3;
	public static boolean USE_SAY_FILTER;
	public static String CHAT_FILTER_CHARS;
	public static String CHAT_FILTER_PUNISHMENT;
	public static ArrayList<String> FILTER_LIST = new ArrayList<>();
	
	public static int FS_TIME_ATTACK;
	public static int FS_TIME_COOLDOWN;
	public static int FS_TIME_ENTRY;
	public static int FS_TIME_WARMUP;
	public static int FS_PARTY_MEMBER_COUNT;
	
	public static long CLICK_TASK;
	
	// ============================================================
	public static void loadOtherConfig()
	{
		String filePath = "config/head/other.properties";
		
		try
		{
			L2Properties otherSettings = new L2Properties(filePath);
			
			DEEPBLUE_DROP_RULES = Boolean.parseBoolean(otherSettings.getProperty("UseDeepBlueDropRules", "True"));
			ALLOW_GUARDS = Boolean.valueOf(otherSettings.getProperty("AllowGuards", "False"));
			EFFECT_CANCELING = Boolean.valueOf(otherSettings.getProperty("CancelLesserEffect", "True"));
			WYVERN_SPEED = Integer.parseInt(otherSettings.getProperty("WyvernSpeed", "100"));
			STRIDER_SPEED = Integer.parseInt(otherSettings.getProperty("StriderSpeed", "80"));
			ALLOW_WYVERN_UPGRADER = Boolean.valueOf(otherSettings.getProperty("AllowWyvernUpgrader", "False"));
			
			/* Select hit task */
			CLICK_TASK = Integer.parseInt(otherSettings.getProperty("ClickTask", "50"));
			
			GM_CRITANNOUNCER_NAME = Boolean.parseBoolean(otherSettings.getProperty("GMShowCritAnnouncerName", "False"));
			
			/* Inventory slots limits */
			INVENTORY_MAXIMUM_NO_DWARF = Integer.parseInt(otherSettings.getProperty("MaximumSlotsForNoDwarf", "80"));
			INVENTORY_MAXIMUM_DWARF = Integer.parseInt(otherSettings.getProperty("MaximumSlotsForDwarf", "100"));
			INVENTORY_MAXIMUM_GM = Integer.parseInt(otherSettings.getProperty("MaximumSlotsForGMPlayer", "250"));
			MAX_ITEM_IN_PACKET = Math.max(INVENTORY_MAXIMUM_NO_DWARF, Math.max(INVENTORY_MAXIMUM_DWARF, INVENTORY_MAXIMUM_GM));
			
			/* Inventory slots limits */
			WAREHOUSE_SLOTS_NO_DWARF = Integer.parseInt(otherSettings.getProperty("MaximumWarehouseSlotsForNoDwarf", "100"));
			WAREHOUSE_SLOTS_DWARF = Integer.parseInt(otherSettings.getProperty("MaximumWarehouseSlotsForDwarf", "120"));
			WAREHOUSE_SLOTS_CLAN = Integer.parseInt(otherSettings.getProperty("MaximumWarehouseSlotsForClan", "150"));
			FREIGHT_SLOTS = Integer.parseInt(otherSettings.getProperty("MaximumFreightSlots", "20"));
			
			/* If different from 100 (ie 100%) heal rate is modified acordingly */
			HP_REGEN_MULTIPLIER = Double.parseDouble(otherSettings.getProperty("HpRegenMultiplier", "100")) / 100;
			MP_REGEN_MULTIPLIER = Double.parseDouble(otherSettings.getProperty("MpRegenMultiplier", "100")) / 100;
			CP_REGEN_MULTIPLIER = Double.parseDouble(otherSettings.getProperty("CpRegenMultiplier", "100")) / 100;
			
			ANNOUNCE_CASTLE_LORDS = Boolean.parseBoolean(otherSettings.getProperty("AnnounceCastleLords", "False"));
			
			CUSTOM_STARTING_LEVEL = Integer.parseInt(otherSettings.getProperty("CustomStartingLevel", "1"));
			CUSTOM_STARTER_ITEMS_ENABLED = Boolean.parseBoolean(otherSettings.getProperty("CustomStarterItemsEnabled", "False"));
			if (Config.CUSTOM_STARTER_ITEMS_ENABLED)
			{
				String[] propertySplit = otherSettings.getProperty("StartingCustomItemsMage", "57,0").split(";");
				STARTING_CUSTOM_ITEMS_M.clear();
				for (final String reward : propertySplit)
				{
					final String[] rewardSplit = reward.split(",");
					if (rewardSplit.length != 2)
					{
						LOGGER.warn("StartingCustomItemsMage[Config.load()]: invalid config property -> StartingCustomItemsMage \"" + reward + "\"");
					}
					else
					{
						try
						{
							STARTING_CUSTOM_ITEMS_M.add(new int[]
							{
								Integer.parseInt(rewardSplit[0]),
								Integer.parseInt(rewardSplit[1])
							});
						}
						catch (NumberFormatException nfe)
						{
							if (!reward.isEmpty())
							{
								LOGGER.warn("StartingCustomItemsMage[Config.load()]: invalid config property -> StartingCustomItemsMage \"" + reward + "\"");
							}
						}
					}
				}
				
				propertySplit = otherSettings.getProperty("StartingCustomItemsFighter", "57,0").split(";");
				STARTING_CUSTOM_ITEMS_F.clear();
				for (final String reward : propertySplit)
				{
					final String[] rewardSplit = reward.split(",");
					if (rewardSplit.length != 2)
					{
						LOGGER.warn("StartingCustomItemsFighter[Config.load()]: invalid config property -> StartingCustomItemsFighter \"" + reward + "\"");
					}
					else
					{
						try
						{
							STARTING_CUSTOM_ITEMS_F.add(new int[]
							{
								Integer.parseInt(rewardSplit[0]),
								Integer.parseInt(rewardSplit[1])
							});
						}
						catch (NumberFormatException nfe)
						{
							if (!reward.isEmpty())
							{
								LOGGER.warn("StartingCustomItemsFighter[Config.load()]: invalid config property -> StartingCustomItemsFighter \"" + reward + "\"");
							}
						}
					}
				}
			}
			
			CUSTOM_SPAWN_CHAR = Boolean.parseBoolean(otherSettings.getProperty("CustomSpawn", "false"));
			CUSTOM_SPAWN_X = Integer.parseInt(otherSettings.getProperty("SpawnX", ""));
			CUSTOM_SPAWN_Y = Integer.parseInt(otherSettings.getProperty("SpawnY", ""));
			CUSTOM_SPAWN_Z = Integer.parseInt(otherSettings.getProperty("SpawnZ", ""));
			
			UNSTUCK_INTERVAL = Integer.parseInt(otherSettings.getProperty("UnstuckInterval", "300"));
			
			PLAYER_TELEPORT_PROTECTION = Integer.parseInt(otherSettings.getProperty("PlayerTeleportProtection", "0"));
			PLAYER_FAKEDEATH_UP_PROTECTION = Integer.parseInt(otherSettings.getProperty("PlayerFakeDeathUpProtection", "0"));
			
			/* Defines some Party XP related values */
			PARTY_XP_CUTOFF_METHOD = otherSettings.getProperty("PartyXpCutoffMethod", "percentage");
			PARTY_XP_CUTOFF_PERCENT = Double.parseDouble(otherSettings.getProperty("PartyXpCutoffPercent", "3."));
			PARTY_XP_CUTOFF_LEVEL = Integer.parseInt(otherSettings.getProperty("PartyXpCutoffLevel", "30"));
			
			/* Amount of HP, MP, and CP is restored */
			RESPAWN_RESTORE_CP = Double.parseDouble(otherSettings.getProperty("RespawnRestoreCP", "0")) / 100;
			RESPAWN_RESTORE_HP = Double.parseDouble(otherSettings.getProperty("RespawnRestoreHP", "70")) / 100;
			RESPAWN_RESTORE_MP = Double.parseDouble(otherSettings.getProperty("RespawnRestoreMP", "70")) / 100;
			
			RESPAWN_RANDOM_ENABLED = Boolean.parseBoolean(otherSettings.getProperty("RespawnRandomInTown", "False"));
			RESPAWN_RANDOM_MAX_OFFSET = Integer.parseInt(otherSettings.getProperty("RespawnRandomMaxOffset", "50"));
			
			/* Maximum number of available slots for pvt stores */
			MAX_PVTSTORE_SLOTS_DWARF = Integer.parseInt(otherSettings.getProperty("MaxPvtStoreSlotsDwarf", "5"));
			MAX_PVTSTORE_SLOTS_OTHER = Integer.parseInt(otherSettings.getProperty("MaxPvtStoreSlotsOther", "4"));
			
			REMOVE_BUFFS_ON_DIE = Boolean.parseBoolean(otherSettings.getProperty("RemoveBuffsOnDie", "True"));
			
			PET_RENT_NPC = otherSettings.getProperty("ListPetRentNpc", "30827");
			LIST_PET_RENT_NPC = new ArrayList<>();
			for (final String id : PET_RENT_NPC.split(","))
			{
				LIST_PET_RENT_NPC.add(Integer.parseInt(id));
			}
			
			PETITIONING_ALLOWED = Boolean.parseBoolean(otherSettings.getProperty("PetitioningAllowed", "True"));
			MAX_PETITIONS_PER_PLAYER = Integer.parseInt(otherSettings.getProperty("MaxPetitionsPerPlayer", "5"));
			MAX_PETITIONS_PENDING = Integer.parseInt(otherSettings.getProperty("MaxPetitionsPending", "25"));
			JAIL_IS_PVP = Boolean.valueOf(otherSettings.getProperty("JailIsPvp", "True"));
			JAIL_DISABLE_CHAT = Boolean.valueOf(otherSettings.getProperty("JailDisableChat", "True"));
			DEATH_PENALTY_CHANCE = Integer.parseInt(otherSettings.getProperty("DeathPenaltyChance", "20"));
			// ////////////
			ENABLE_MODIFY_SKILL_DURATION = Boolean.parseBoolean(otherSettings.getProperty("EnableModifySkillDuration", "false"));
			if (ENABLE_MODIFY_SKILL_DURATION)
			{
				SKILL_DURATION_LIST = new HashMap<>();
				
				String[] propertySplit;
				propertySplit = otherSettings.getProperty("SkillDurationList", "").split(";");
				
				for (final String skill : propertySplit)
				{
					final String[] skillSplit = skill.split(",");
					if (skillSplit.length != 2)
					{
						LOGGER.info("[SkillDurationList]: invalid config property -> SkillDurationList \"" + skill + "\"");
					}
					else
					{
						try
						{
							SKILL_DURATION_LIST.put(Integer.parseInt(skillSplit[0]), Integer.parseInt(skillSplit[1]));
						}
						catch (final NumberFormatException nfe)
						{
							if (!skill.equals(""))
							{
								LOGGER.info("[SkillDurationList]: invalid config property -> SkillList \"" + skillSplit[0] + "\"" + skillSplit[1]);
							}
						}
					}
				}
			}
			
			USE_SAY_FILTER = Boolean.parseBoolean(otherSettings.getProperty("UseChatFilter", "false"));
			CHAT_FILTER_CHARS = otherSettings.getProperty("ChatFilterChars", "[I love L2jFrozen]");
			CHAT_FILTER_PUNISHMENT = otherSettings.getProperty("ChatFilterPunishment", "off");
			CHAT_FILTER_PUNISHMENT_PARAM1 = Integer.parseInt(otherSettings.getProperty("ChatFilterPunishmentParam1", "1"));
			CHAT_FILTER_PUNISHMENT_PARAM2 = Integer.parseInt(otherSettings.getProperty("ChatFilterPunishmentParam2", "1000"));
			
			FS_TIME_ATTACK = Integer.parseInt(otherSettings.getProperty("TimeOfAttack", "50"));
			FS_TIME_COOLDOWN = Integer.parseInt(otherSettings.getProperty("TimeOfCoolDown", "5"));
			FS_TIME_ENTRY = Integer.parseInt(otherSettings.getProperty("TimeOfEntry", "3"));
			FS_TIME_WARMUP = Integer.parseInt(otherSettings.getProperty("TimeOfWarmUp", "2"));
			FS_PARTY_MEMBER_COUNT = Integer.parseInt(otherSettings.getProperty("NumberOfNecessaryPartyMembers", "4"));
			
			if (FS_TIME_ATTACK <= 0)
			{
				FS_TIME_ATTACK = 50;
			}
			if (FS_TIME_COOLDOWN <= 0)
			{
				FS_TIME_COOLDOWN = 5;
			}
			if (FS_TIME_ENTRY <= 0)
			{
				FS_TIME_ENTRY = 3;
			}
			if (FS_TIME_WARMUP <= 0)
			{
				FS_TIME_WARMUP = 2;
			}
			if (FS_PARTY_MEMBER_COUNT <= 0)
			{
				FS_PARTY_MEMBER_COUNT = 4;
			}
		}
		catch (Exception e)
		{
			LOGGER.error("Config : Failed to load " + filePath + " file.", e);
		}
	}
	
	// ============================================================
	public static float RATE_XP;
	public static float RATE_SP;
	public static float RATE_PARTY_XP;
	public static float RATE_PARTY_SP;
	public static float RATE_QUESTS_REWARD;
	public static float RATE_DROP_ADENA;
	public static float RATE_CONSUMABLE_COST;
	public static float RATE_DROP_ITEMS;
	public static float RATE_DROP_SEAL_STONES;
	public static float RATE_DROP_SPOIL;
	public static int RATE_DROP_MANOR;
	public static float RATE_DROP_QUEST;
	public static float RATE_KARMA_EXP_LOST;
	public static float RATE_SIEGE_GUARDS_PRICE;
	public static float RATE_DROP_COMMON_HERBS;
	public static float RATE_DROP_MP_HP_HERBS;
	public static float RATE_DROP_GREATER_HERBS;
	public static float RATE_DROP_SUPERIOR_HERBS;
	public static float RATE_DROP_SPECIAL_HERBS;
	public static int PLAYER_DROP_LIMIT;
	public static int PLAYER_RATE_DROP;
	public static int PLAYER_RATE_DROP_ITEM;
	public static int PLAYER_RATE_DROP_EQUIP;
	public static int PLAYER_RATE_DROP_EQUIP_WEAPON;
	public static float PET_XP_RATE;
	public static int PET_FOOD_RATE;
	public static float SINEATER_XP_RATE;
	public static int KARMA_DROP_LIMIT;
	public static int KARMA_RATE_DROP;
	public static int KARMA_RATE_DROP_ITEM;
	public static int KARMA_RATE_DROP_EQUIP;
	public static int KARMA_RATE_DROP_EQUIP_WEAPON;
	/** RB rate **/
	public static float ADENA_BOSS;
	public static float ADENA_RAID;
	public static float ADENA_MINON;
	public static float ITEMS_BOSS;
	public static float ITEMS_RAID;
	public static float ITEMS_MINON;
	public static float SPOIL_BOSS;
	public static float SPOIL_RAID;
	public static float SPOIL_MINON;
	
	// ============================================================
	public static void loadRatesConfig()
	{
		String filePath = "config/head/rates.properties";
		
		try
		{
			L2Properties ratesSettings = new L2Properties(filePath);
			
			RATE_XP = Float.parseFloat(ratesSettings.getProperty("RateXp", "1.00"));
			RATE_SP = Float.parseFloat(ratesSettings.getProperty("RateSp", "1.00"));
			RATE_PARTY_XP = Float.parseFloat(ratesSettings.getProperty("RatePartyXp", "1.00"));
			RATE_PARTY_SP = Float.parseFloat(ratesSettings.getProperty("RatePartySp", "1.00"));
			RATE_QUESTS_REWARD = Float.parseFloat(ratesSettings.getProperty("RateQuestsReward", "1.00"));
			RATE_DROP_ADENA = Float.parseFloat(ratesSettings.getProperty("RateDropAdena", "1.00"));
			RATE_CONSUMABLE_COST = Float.parseFloat(ratesSettings.getProperty("RateConsumableCost", "1.00"));
			RATE_DROP_ITEMS = Float.parseFloat(ratesSettings.getProperty("RateDropItems", "1.00"));
			RATE_DROP_SEAL_STONES = Float.parseFloat(ratesSettings.getProperty("RateDropSealStones", "1.00"));
			RATE_DROP_SPOIL = Float.parseFloat(ratesSettings.getProperty("RateDropSpoil", "1.00"));
			RATE_DROP_MANOR = Integer.parseInt(ratesSettings.getProperty("RateDropManor", "1.00"));
			RATE_DROP_QUEST = Float.parseFloat(ratesSettings.getProperty("RateDropQuest", "1.00"));
			RATE_KARMA_EXP_LOST = Float.parseFloat(ratesSettings.getProperty("RateKarmaExpLost", "1.00"));
			RATE_SIEGE_GUARDS_PRICE = Float.parseFloat(ratesSettings.getProperty("RateSiegeGuardsPrice", "1.00"));
			RATE_DROP_COMMON_HERBS = Float.parseFloat(ratesSettings.getProperty("RateCommonHerbs", "15.00"));
			RATE_DROP_MP_HP_HERBS = Float.parseFloat(ratesSettings.getProperty("RateHpMpHerbs", "10.00"));
			RATE_DROP_GREATER_HERBS = Float.parseFloat(ratesSettings.getProperty("RateGreaterHerbs", "4.00"));
			RATE_DROP_SUPERIOR_HERBS = Float.parseFloat(ratesSettings.getProperty("RateSuperiorHerbs", "0.80")) * 10;
			RATE_DROP_SPECIAL_HERBS = Float.parseFloat(ratesSettings.getProperty("RateSpecialHerbs", "0.20")) * 10;
			
			PLAYER_DROP_LIMIT = Integer.parseInt(ratesSettings.getProperty("PlayerDropLimit", "3"));
			PLAYER_RATE_DROP = Integer.parseInt(ratesSettings.getProperty("PlayerRateDrop", "5"));
			PLAYER_RATE_DROP_ITEM = Integer.parseInt(ratesSettings.getProperty("PlayerRateDropItem", "70"));
			PLAYER_RATE_DROP_EQUIP = Integer.parseInt(ratesSettings.getProperty("PlayerRateDropEquip", "25"));
			PLAYER_RATE_DROP_EQUIP_WEAPON = Integer.parseInt(ratesSettings.getProperty("PlayerRateDropEquipWeapon", "5"));
			
			PET_XP_RATE = Float.parseFloat(ratesSettings.getProperty("PetXpRate", "1.00"));
			PET_FOOD_RATE = Integer.parseInt(ratesSettings.getProperty("PetFoodRate", "1"));
			SINEATER_XP_RATE = Float.parseFloat(ratesSettings.getProperty("SinEaterXpRate", "1.00"));
			
			KARMA_DROP_LIMIT = Integer.parseInt(ratesSettings.getProperty("KarmaDropLimit", "10"));
			KARMA_RATE_DROP = Integer.parseInt(ratesSettings.getProperty("KarmaRateDrop", "70"));
			KARMA_RATE_DROP_ITEM = Integer.parseInt(ratesSettings.getProperty("KarmaRateDropItem", "50"));
			KARMA_RATE_DROP_EQUIP = Integer.parseInt(ratesSettings.getProperty("KarmaRateDropEquip", "40"));
			KARMA_RATE_DROP_EQUIP_WEAPON = Integer.parseInt(ratesSettings.getProperty("KarmaRateDropEquipWeapon", "10"));
			
			/** RB rate **/
			ADENA_BOSS = Float.parseFloat(ratesSettings.getProperty("AdenaBoss", "1.00"));
			ADENA_RAID = Float.parseFloat(ratesSettings.getProperty("AdenaRaid", "1.00"));
			ADENA_MINON = Float.parseFloat(ratesSettings.getProperty("AdenaMinon", "1.00"));
			ITEMS_BOSS = Float.parseFloat(ratesSettings.getProperty("ItemsBoss", "1.00"));
			ITEMS_RAID = Float.parseFloat(ratesSettings.getProperty("ItemsRaid", "1.00"));
			ITEMS_MINON = Float.parseFloat(ratesSettings.getProperty("ItemsMinon", "1.00"));
			SPOIL_BOSS = Float.parseFloat(ratesSettings.getProperty("SpoilBoss", "1.00"));
			SPOIL_RAID = Float.parseFloat(ratesSettings.getProperty("SpoilRaid", "1.00"));
			SPOIL_MINON = Float.parseFloat(ratesSettings.getProperty("SpoilMinon", "1.00"));
		}
		catch (Exception e)
		{
			LOGGER.error("Config : Failed to load " + filePath + " file.", e);
		}
	}
	
	// ============================================================
	public static boolean AUTO_LOOT;
	public static boolean AUTO_LOOT_HERBS;
	public static double ALT_WEIGHT_LIMIT;
	public static boolean ALT_GAME_SKILL_LEARN;
	public static boolean AUTO_LEARN_SKILLS;
	public static boolean SP_BOOK_NEEDED;
	public static boolean ALT_GAME_CANCEL_BOW;
	public static boolean ALT_GAME_CANCEL_CAST;
	public static int ALT_PARTY_RANGE;
	public static int ALT_PARTY_RANGE2;
	public static boolean ALT_GAME_SHIELD_BLOCKS;
	public static int ALT_PERFECT_SHLD_BLOCK;
	public static boolean ALT_GAME_MOB_ATTACK_AI;
	public static boolean ALT_MOB_AGRO_IN_PEACEZONE;
	public static boolean ALT_GAME_FREIGHTS;
	public static int ALT_GAME_FREIGHT_PRICE;
	public static float ALT_GAME_SKILL_HIT_RATE;
	public static boolean ALT_GAME_DELEVEL;
	public static boolean ALT_GAME_MAGICFAILURES;
	public static int ALT_TELEPORT_FREE_UNTIL_LEVEL;
	public static boolean ALT_RECOMMEND;
	public static boolean ALT_SUBCLASS_NEED_QUEST;
	public static int ALT_CLAN_MEMBERS_FOR_WAR;
	public static int ALT_CLAN_JOIN_DAYS;
	public static int ALT_CLAN_CREATE_DAYS;
	public static int ALT_CLAN_DISSOLVE_DAYS;
	public static int ALT_ALLY_JOIN_DAYS_WHEN_LEAVED;
	public static int ALT_ALLY_JOIN_DAYS_WHEN_DISMISSED;
	public static int ALT_ACCEPT_CLAN_DAYS_WHEN_DISMISSED;
	public static int ALT_CREATE_ALLY_DAYS_WHEN_DISSOLVED;
	public static boolean LIFE_CRYSTAL_NEEDED;
	public static int CLAN_LEADER_COLOR;
	public static int CLAN_LEADER_COLOR_CLAN_LEVEL;
	public static boolean CLAN_LEADER_COLOR_ENABLED;
	public static int CLAN_LEADER_COLORED;
	public static boolean ALT_GAME_NEW_CHAR_ALWAYS_IS_NEWBIE;
	public static boolean ALT_MEMBERS_CAN_WITHDRAW_FROM_CLANWH;
	public static int ALT_MAX_NUM_OF_CLANS_IN_ALLY;
	public static boolean ES_SP_BOOK_NEEDED;
	public static boolean ALT_PRIVILEGES_SECURE_CHECK;
	public static int ALT_PRIVILEGES_DEFAULT_LEVEL;
	public static int ALT_MANOR_REFRESH_TIME;
	public static int ALT_MANOR_REFRESH_MIN;
	public static int ALT_MANOR_APPROVE_TIME;
	public static int ALT_MANOR_APPROVE_MIN;
	public static int ALT_MANOR_MAINTENANCE_PERIOD;
	public static boolean ALT_MANOR_SAVE_ALL_ACTIONS;
	public static int ALT_MANOR_SAVE_PERIOD_RATE;
	public static int ALT_LOTTERY_PRIZE;
	public static int ALT_LOTTERY_TICKET_PRICE;
	public static float ALT_LOTTERY_5_NUMBER_RATE;
	public static float ALT_LOTTERY_4_NUMBER_RATE;
	public static float ALT_LOTTERY_3_NUMBER_RATE;
	public static int ALT_LOTTERY_2_AND_1_NUMBER_PRIZE;
	public static float ALT_GAME_EXPONENT_XP;
	public static float ALT_GAME_EXPONENT_SP;
	public static boolean FORCE_INVENTORY_UPDATE;
	public static boolean ALLOW_GUARDS;
	public static boolean ALT_GAME_KARMA_PLAYER_CAN_BE_KILLED_IN_PEACEZONE;
	public static boolean ALT_GAME_KARMA_PLAYER_CAN_SHOP;
	public static boolean ALT_GAME_KARMA_PLAYER_CAN_USE_GK;
	public static boolean ALT_GAME_KARMA_PLAYER_CAN_TELEPORT;
	public static boolean ALT_GAME_KARMA_PLAYER_CAN_TRADE;
	public static boolean ALT_GAME_KARMA_PLAYER_CAN_USE_WAREHOUSE;
	public static boolean ALT_KARMA_TELEPORT_TO_FLORAN;
	public static byte BUFFS_MAX_AMOUNT;
	public static byte DEBUFFS_MAX_AMOUNT;
	public static boolean AUTO_LEARN_DIVINE_INSPIRATION;
	public static boolean DIVINE_SP_BOOK_NEEDED;
	public static boolean DONT_DESTROY_SS;
	public static int MAX_LEVEL_NEWBIE;
	public static int MAX_LEVEL_NEWBIE_STATUS;
	public static int STANDARD_RESPAWN_DELAY;
	public static int ALT_RECOMMENDATIONS_NUMBER;
	
	public static boolean EXPERTISE_PENALTY;
	public static boolean MASTERY_PENALTY;
	public static int LEVEL_TO_GET_PENALITY;
	public static boolean MASTERY_WEAPON_PENALTY;
	public static int LEVEL_TO_GET_WEAPON_PENALITY;
	
	public static List<Integer> INVUL_NPC_LIST;
	/** Config for activeChar Attack Npcs in the list */
	public static boolean DISABLE_ATTACK_NPC_TYPE;
	/**
	 * Allows or dis-allows the option for NPC types that won't allow casting
	 */
	public static String ALLOWED_NPC_TYPES;
	/** List of NPC types that won't allow casting */
	public static List<String> LIST_ALLOWED_NPC_TYPES = new ArrayList<>();
	
	public static boolean SELL_BY_ITEM;
	public static int SELL_ITEM;
	public static int ALLOWED_SUBCLASS;
	public static byte BASE_SUBCLASS_LEVEL;
	public static byte MAX_SUBCLASS_LEVEL;
	
	public static String DISABLE_BOW_CLASSES_STRING;
	public static List<Integer> DISABLE_BOW_CLASSES = new ArrayList<>();
	
	public static boolean ALT_MOBS_STATS_BONUS;
	public static boolean ALT_PETS_STATS_BONUS;
	public static boolean ALLOW_REMOTE_CLASS_MASTERS;
	public static boolean ALLOW_CLASS_MASTERS_FIRST_CLASS;
	public static boolean ALLOW_CLASS_MASTERS_SECOND_CLASS;
	public static boolean ALLOW_CLASS_MASTERS_THIRD_CLASS;
	public static ClassMasterList CLASS_MASTER_SETTINGS;
	
	// ============================================================
	public static void loadAltConfig()
	{
		String filePath = "config/head/altsettings.properties";
		
		try
		{
			L2Properties altSettings = new L2Properties(filePath);
			
			ALLOW_CLASS_MASTERS_FIRST_CLASS = Boolean.valueOf(altSettings.getProperty("AllowClassMastersFirstClass", "true"));
			ALLOW_REMOTE_CLASS_MASTERS = Boolean.valueOf(altSettings.getProperty("AllowRemoteClassMasters", "False"));
			ALT_WEIGHT_LIMIT = Double.parseDouble(altSettings.getProperty("AltWeightLimit", "1"));
			ALT_GAME_SKILL_LEARN = Boolean.parseBoolean(altSettings.getProperty("AltGameSkillLearn", "false"));
			
			AUTO_LEARN_SKILLS = Boolean.parseBoolean(altSettings.getProperty("AutoLearnSkills", "false"));
			SP_BOOK_NEEDED = Boolean.parseBoolean(altSettings.getProperty("SpBookNeeded", "true"));
			
			ALT_GAME_CANCEL_BOW = altSettings.getProperty("AltGameCancelByHit", "Cast").equalsIgnoreCase("bow") || altSettings.getProperty("AltGameCancelByHit", "Cast").equalsIgnoreCase("all");
			ALT_GAME_CANCEL_CAST = altSettings.getProperty("AltGameCancelByHit", "Cast").equalsIgnoreCase("cast") || altSettings.getProperty("AltGameCancelByHit", "Cast").equalsIgnoreCase("all");
			ALT_GAME_SHIELD_BLOCKS = Boolean.parseBoolean(altSettings.getProperty("AltShieldBlocks", "false"));
			ALT_PERFECT_SHLD_BLOCK = Integer.parseInt(altSettings.getProperty("AltPerfectShieldBlockRate", "10"));
			ALT_GAME_DELEVEL = Boolean.parseBoolean(altSettings.getProperty("Delevel", "true"));
			ALT_GAME_MAGICFAILURES = Boolean.parseBoolean(altSettings.getProperty("MagicFailures", "false"));
			ALT_GAME_MOB_ATTACK_AI = Boolean.parseBoolean(altSettings.getProperty("AltGameMobAttackAI", "false"));
			ALT_MOB_AGRO_IN_PEACEZONE = Boolean.parseBoolean(altSettings.getProperty("AltMobAgroInPeaceZone", "true"));
			ALT_GAME_EXPONENT_XP = Float.parseFloat(altSettings.getProperty("AltGameExponentXp", "0."));
			ALT_GAME_EXPONENT_SP = Float.parseFloat(altSettings.getProperty("AltGameExponentSp", "0."));
			AUTO_LEARN_DIVINE_INSPIRATION = Boolean.parseBoolean(altSettings.getProperty("AutoLearnDivineInspiration", "false"));
			DIVINE_SP_BOOK_NEEDED = Boolean.parseBoolean(altSettings.getProperty("DivineInspirationSpBookNeeded", "true"));
			
			ALT_GAME_FREIGHTS = Boolean.parseBoolean(altSettings.getProperty("AltGameFreights", "false"));
			ALT_GAME_FREIGHT_PRICE = Integer.parseInt(altSettings.getProperty("AltGameFreightPrice", "1000"));
			ALT_PARTY_RANGE = Integer.parseInt(altSettings.getProperty("AltPartyRange", "1600"));
			ALT_PARTY_RANGE2 = Integer.parseInt(altSettings.getProperty("AltPartyRange2", "1400"));
			ES_SP_BOOK_NEEDED = Boolean.parseBoolean(altSettings.getProperty("EnchantSkillSpBookNeeded", "true"));
			AUTO_LOOT = altSettings.getProperty("AutoLoot").equalsIgnoreCase("True");
			AUTO_LOOT_HERBS = altSettings.getProperty("AutoLootHerbs").equalsIgnoreCase("True");
			ALT_TELEPORT_FREE_UNTIL_LEVEL = Integer.parseInt(altSettings.getProperty("AltTeleportFreeUntilLevel", "0"));
			ALT_RECOMMEND = Boolean.parseBoolean(altSettings.getProperty("AltRecommend", "False"));
			ALT_SUBCLASS_NEED_QUEST = Boolean.parseBoolean(altSettings.getProperty("AltSubClassNeedQuest", "True"));
			ALT_GAME_NEW_CHAR_ALWAYS_IS_NEWBIE = Boolean.parseBoolean(altSettings.getProperty("AltNewCharAlwaysIsNewbie", "False"));
			ALT_MEMBERS_CAN_WITHDRAW_FROM_CLANWH = Boolean.parseBoolean(altSettings.getProperty("AltMembersCanWithdrawFromClanWH", "False"));
			ALT_MAX_NUM_OF_CLANS_IN_ALLY = Integer.parseInt(altSettings.getProperty("AltMaxNumOfClansInAlly", "3"));
			
			ALT_CLAN_MEMBERS_FOR_WAR = Integer.parseInt(altSettings.getProperty("AltClanMembersForWar", "15"));
			ALT_CLAN_JOIN_DAYS = Integer.parseInt(altSettings.getProperty("DaysBeforeJoinAClan", "5"));
			ALT_CLAN_CREATE_DAYS = Integer.parseInt(altSettings.getProperty("DaysBeforeCreateAClan", "10"));
			ALT_CLAN_DISSOLVE_DAYS = Integer.parseInt(altSettings.getProperty("DaysToPassToDissolveAClan", "7"));
			ALT_ALLY_JOIN_DAYS_WHEN_LEAVED = Integer.parseInt(altSettings.getProperty("DaysBeforeJoinAllyWhenLeaved", "1"));
			ALT_ALLY_JOIN_DAYS_WHEN_DISMISSED = Integer.parseInt(altSettings.getProperty("DaysBeforeJoinAllyWhenDismissed", "1"));
			ALT_ACCEPT_CLAN_DAYS_WHEN_DISMISSED = Integer.parseInt(altSettings.getProperty("DaysBeforeAcceptNewClanWhenDismissed", "1"));
			ALT_CREATE_ALLY_DAYS_WHEN_DISSOLVED = Integer.parseInt(altSettings.getProperty("DaysBeforeCreateNewAllyWhenDissolved", "10"));
			LIFE_CRYSTAL_NEEDED = Boolean.parseBoolean(altSettings.getProperty("LifeCrystalNeeded", "true"));
			CLAN_LEADER_COLOR_ENABLED = Boolean.parseBoolean(altSettings.getProperty("ClanLeaderNameColorEnabled", "true"));
			CLAN_LEADER_COLORED = Integer.parseInt(altSettings.getProperty("ClanLeaderColored", "1"));
			CLAN_LEADER_COLOR = Integer.decode("0x" + altSettings.getProperty("ClanLeaderColor", "00FFFF"));
			CLAN_LEADER_COLOR_CLAN_LEVEL = Integer.parseInt(altSettings.getProperty("ClanLeaderColorAtClanLevel", "1"));
			
			ALT_MANOR_REFRESH_TIME = Integer.parseInt(altSettings.getProperty("AltManorRefreshTime", "20"));
			ALT_MANOR_REFRESH_MIN = Integer.parseInt(altSettings.getProperty("AltManorRefreshMin", "00"));
			ALT_MANOR_APPROVE_TIME = Integer.parseInt(altSettings.getProperty("AltManorApproveTime", "6"));
			ALT_MANOR_APPROVE_MIN = Integer.parseInt(altSettings.getProperty("AltManorApproveMin", "00"));
			ALT_MANOR_MAINTENANCE_PERIOD = Integer.parseInt(altSettings.getProperty("AltManorMaintenancePeriod", "360000"));
			ALT_MANOR_SAVE_ALL_ACTIONS = Boolean.parseBoolean(altSettings.getProperty("AltManorSaveAllActions", "false"));
			ALT_MANOR_SAVE_PERIOD_RATE = Integer.parseInt(altSettings.getProperty("AltManorSavePeriodRate", "2"));
			
			ALT_LOTTERY_PRIZE = Integer.parseInt(altSettings.getProperty("AltLotteryPrize", "50000"));
			ALT_LOTTERY_TICKET_PRICE = Integer.parseInt(altSettings.getProperty("AltLotteryTicketPrice", "2000"));
			ALT_LOTTERY_5_NUMBER_RATE = Float.parseFloat(altSettings.getProperty("AltLottery5NumberRate", "0.6"));
			ALT_LOTTERY_4_NUMBER_RATE = Float.parseFloat(altSettings.getProperty("AltLottery4NumberRate", "0.2"));
			ALT_LOTTERY_3_NUMBER_RATE = Float.parseFloat(altSettings.getProperty("AltLottery3NumberRate", "0.2"));
			ALT_LOTTERY_2_AND_1_NUMBER_PRIZE = Integer.parseInt(altSettings.getProperty("AltLottery2and1NumberPrize", "200"));
			BUFFS_MAX_AMOUNT = Byte.parseByte(altSettings.getProperty("MaxBuffAmount", "24"));
			DEBUFFS_MAX_AMOUNT = Byte.parseByte(altSettings.getProperty("MaxDebuffAmount", "6"));
			
			// Destroy ss
			DONT_DESTROY_SS = Boolean.parseBoolean(altSettings.getProperty("DontDestroySS", "false"));
			
			// Max level newbie
			MAX_LEVEL_NEWBIE = Integer.parseInt(altSettings.getProperty("MaxLevelNewbie", "20"));
			// Level when Char lost Newbie status
			MAX_LEVEL_NEWBIE_STATUS = Integer.parseInt(altSettings.getProperty("MaxLevelNewbieStatus", "40"));
			
			STANDARD_RESPAWN_DELAY = Integer.parseInt(altSettings.getProperty("StandardRespawnDelay", "180"));
			ALT_RECOMMENDATIONS_NUMBER = Integer.parseInt(altSettings.getProperty("AltMaxRecommendationNumber", "255"));
			
			EXPERTISE_PENALTY = Boolean.parseBoolean(altSettings.getProperty("ExpertisePenality", "true"));
			MASTERY_PENALTY = Boolean.parseBoolean(altSettings.getProperty("MasteryPenality", "false"));
			LEVEL_TO_GET_PENALITY = Integer.parseInt(altSettings.getProperty("LevelToGetPenalty", "20"));
			
			MASTERY_WEAPON_PENALTY = Boolean.parseBoolean(altSettings.getProperty("MasteryWeaponPenality", "false"));
			LEVEL_TO_GET_WEAPON_PENALITY = Integer.parseInt(altSettings.getProperty("LevelToGetWeaponPenalty", "20"));
			
			INVUL_NPC_LIST = new ArrayList<>();
			final String t = altSettings.getProperty("InvulNpcList", "30001-32132,35092-35103,35142-35146,35176-35187,35218-35232,35261-35278,35308-35319,35352-35367,35382-35407,35417-35427,35433-35469,35497-35513,35544-35587,35600-35617,35623-35628,35638-35640,35644,35645,50007,70010,99999");
			String as[];
			final int k = (as = t.split(",")).length;
			for (int j = 0; j < k; j++)
			{
				final String t2 = as[j];
				if (t2.contains("-"))
				{
					final int a1 = Integer.parseInt(t2.split("-")[0]);
					final int a2 = Integer.parseInt(t2.split("-")[1]);
					for (int i = a1; i <= a2; i++)
					{
						INVUL_NPC_LIST.add(Integer.valueOf(i));
					}
				}
				else
				{
					INVUL_NPC_LIST.add(Integer.valueOf(Integer.parseInt(t2)));
				}
			}
			DISABLE_ATTACK_NPC_TYPE = Boolean.parseBoolean(altSettings.getProperty("DisableAttackToNpcs", "False"));
			ALLOWED_NPC_TYPES = altSettings.getProperty("AllowedNPCTypes");
			LIST_ALLOWED_NPC_TYPES = new ArrayList<>();
			for (final String npc_type : ALLOWED_NPC_TYPES.split(","))
			{
				LIST_ALLOWED_NPC_TYPES.add(npc_type);
			}
			
			SELL_BY_ITEM = Boolean.parseBoolean(altSettings.getProperty("SellByItem", "False"));
			SELL_ITEM = Integer.parseInt(altSettings.getProperty("SellItem", "57"));
			
			ALLOWED_SUBCLASS = Integer.parseInt(altSettings.getProperty("AllowedSubclass", "3"));
			BASE_SUBCLASS_LEVEL = Byte.parseByte(altSettings.getProperty("BaseSubclassLevel", "40"));
			MAX_SUBCLASS_LEVEL = Byte.parseByte(altSettings.getProperty("MaxSubclassLevel", "81"));
			
			ALT_MOBS_STATS_BONUS = Boolean.parseBoolean(altSettings.getProperty("AltMobsStatsBonus", "True"));
			ALT_PETS_STATS_BONUS = Boolean.parseBoolean(altSettings.getProperty("AltPetsStatsBonus", "True"));
		}
		catch (Exception e)
		{
			LOGGER.error("Config : Failed to load " + filePath + " file.", e);
		}
	}
	
	// ============================================================
	public static boolean ALT_GAME_REQUIRE_CASTLE_DAWN;
	public static boolean ALT_GAME_REQUIRE_CLAN_CASTLE;
	public static boolean ALT_REQUIRE_WIN_7S;
	public static boolean ALT_ANNOUNCE_MAMMON_SPAWN;
	public static int ALT_FESTIVAL_MIN_PLAYER;
	public static int ALT_MAXIMUM_PLAYER_CONTRIB;
	public static long ALT_FESTIVAL_MANAGER_START;
	public static long ALT_FESTIVAL_LENGTH;
	public static long ALT_FESTIVAL_CYCLE_LENGTH;
	public static long ALT_FESTIVAL_FIRST_SPAWN;
	public static long ALT_FESTIVAL_FIRST_SWARM;
	public static long ALT_FESTIVAL_SECOND_SPAWN;
	public static long ALT_FESTIVAL_SECOND_SWARM;
	public static long ALT_FESTIVAL_CHEST_SPAWN;
	
	public static int RIFT_MIN_PARTY_SIZE;
	public static int RIFT_SPAWN_DELAY;
	public static int RIFT_MAX_JUMPS;
	public static int RIFT_AUTO_JUMPS_TIME_MIN;
	public static int RIFT_AUTO_JUMPS_TIME_MAX;
	public static int RIFT_ENTER_COST_RECRUIT;
	public static int RIFT_ENTER_COST_SOLDIER;
	public static int RIFT_ENTER_COST_OFFICER;
	public static int RIFT_ENTER_COST_CAPTAIN;
	public static int RIFT_ENTER_COST_COMMANDER;
	public static int RIFT_ENTER_COST_HERO;
	public static float RIFT_BOSS_ROOM_TIME_MUTIPLY;
	
	// ============================================================
	public static void load7sConfig()
	{
		String filePath = "config/head/sevensigns.properties";
		
		try
		{
			L2Properties sevenSettings = new L2Properties(filePath);
			
			ALT_GAME_REQUIRE_CASTLE_DAWN = Boolean.parseBoolean(sevenSettings.getProperty("AltRequireCastleForDawn", "False"));
			ALT_GAME_REQUIRE_CLAN_CASTLE = Boolean.parseBoolean(sevenSettings.getProperty("AltRequireClanCastle", "False"));
			ALT_REQUIRE_WIN_7S = Boolean.parseBoolean(sevenSettings.getProperty("AltRequireWin7s", "True"));
			ALT_ANNOUNCE_MAMMON_SPAWN = Boolean.parseBoolean(sevenSettings.getProperty("AltAnnounceMammonSpawn", "True"));
			ALT_FESTIVAL_MIN_PLAYER = Integer.parseInt(sevenSettings.getProperty("AltFestivalMinPlayer", "5"));
			ALT_MAXIMUM_PLAYER_CONTRIB = Integer.parseInt(sevenSettings.getProperty("AltMaxPlayerContrib", "1000000"));
			ALT_FESTIVAL_MANAGER_START = Long.parseLong(sevenSettings.getProperty("AltFestivalManagerStart", "120000"));
			ALT_FESTIVAL_LENGTH = Long.parseLong(sevenSettings.getProperty("AltFestivalLength", "1080000"));
			ALT_FESTIVAL_CYCLE_LENGTH = Long.parseLong(sevenSettings.getProperty("AltFestivalCycleLength", "2280000"));
			ALT_FESTIVAL_FIRST_SPAWN = Long.parseLong(sevenSettings.getProperty("AltFestivalFirstSpawn", "120000"));
			ALT_FESTIVAL_FIRST_SWARM = Long.parseLong(sevenSettings.getProperty("AltFestivalFirstSwarm", "300000"));
			ALT_FESTIVAL_SECOND_SPAWN = Long.parseLong(sevenSettings.getProperty("AltFestivalSecondSpawn", "540000"));
			ALT_FESTIVAL_SECOND_SWARM = Long.parseLong(sevenSettings.getProperty("AltFestivalSecondSwarm", "720000"));
			ALT_FESTIVAL_CHEST_SPAWN = Long.parseLong(sevenSettings.getProperty("AltFestivalChestSpawn", "900000"));
			
			// Dimensional Rift Config
			RIFT_MIN_PARTY_SIZE = Integer.parseInt(sevenSettings.getProperty("RiftMinPartySize", "5"));
			RIFT_MAX_JUMPS = Integer.parseInt(sevenSettings.getProperty("MaxRiftJumps", "4"));
			RIFT_SPAWN_DELAY = Integer.parseInt(sevenSettings.getProperty("RiftSpawnDelay", "10000"));
			RIFT_AUTO_JUMPS_TIME_MIN = Integer.parseInt(sevenSettings.getProperty("AutoJumpsDelayMin", "480"));
			RIFT_AUTO_JUMPS_TIME_MAX = Integer.parseInt(sevenSettings.getProperty("AutoJumpsDelayMax", "600"));
			RIFT_ENTER_COST_RECRUIT = Integer.parseInt(sevenSettings.getProperty("RecruitCost", "18"));
			RIFT_ENTER_COST_SOLDIER = Integer.parseInt(sevenSettings.getProperty("SoldierCost", "21"));
			RIFT_ENTER_COST_OFFICER = Integer.parseInt(sevenSettings.getProperty("OfficerCost", "24"));
			RIFT_ENTER_COST_CAPTAIN = Integer.parseInt(sevenSettings.getProperty("CaptainCost", "27"));
			RIFT_ENTER_COST_COMMANDER = Integer.parseInt(sevenSettings.getProperty("CommanderCost", "30"));
			RIFT_ENTER_COST_HERO = Integer.parseInt(sevenSettings.getProperty("HeroCost", "33"));
			RIFT_BOSS_ROOM_TIME_MUTIPLY = Float.parseFloat(sevenSettings.getProperty("BossRoomTimeMultiply", "1.5"));
		}
		catch (Exception e)
		{
			LOGGER.error("Config : Failed to load " + filePath + " file.", e);
		}
	}
	
	// ============================================================
	public static long CH_TELE_FEE_RATIO;
	public static int CH_TELE1_FEE;
	public static int CH_TELE2_FEE;
	public static long CH_ITEM_FEE_RATIO;
	public static int CH_ITEM1_FEE;
	public static int CH_ITEM2_FEE;
	public static int CH_ITEM3_FEE;
	public static long CH_MPREG_FEE_RATIO;
	public static int CH_MPREG1_FEE;
	public static int CH_MPREG2_FEE;
	public static int CH_MPREG3_FEE;
	public static int CH_MPREG4_FEE;
	public static int CH_MPREG5_FEE;
	public static long CH_HPREG_FEE_RATIO;
	public static int CH_HPREG1_FEE;
	public static int CH_HPREG2_FEE;
	public static int CH_HPREG3_FEE;
	public static int CH_HPREG4_FEE;
	public static int CH_HPREG5_FEE;
	public static int CH_HPREG6_FEE;
	public static int CH_HPREG7_FEE;
	public static int CH_HPREG8_FEE;
	public static int CH_HPREG9_FEE;
	public static int CH_HPREG10_FEE;
	public static int CH_HPREG11_FEE;
	public static int CH_HPREG12_FEE;
	public static int CH_HPREG13_FEE;
	public static long CH_EXPREG_FEE_RATIO;
	public static int CH_EXPREG1_FEE;
	public static int CH_EXPREG2_FEE;
	public static int CH_EXPREG3_FEE;
	public static int CH_EXPREG4_FEE;
	public static int CH_EXPREG5_FEE;
	public static int CH_EXPREG6_FEE;
	public static int CH_EXPREG7_FEE;
	public static long CH_SUPPORT_FEE_RATIO;
	public static int CH_SUPPORT1_FEE;
	public static int CH_SUPPORT2_FEE;
	public static int CH_SUPPORT3_FEE;
	public static int CH_SUPPORT4_FEE;
	public static int CH_SUPPORT5_FEE;
	public static int CH_SUPPORT6_FEE;
	public static int CH_SUPPORT7_FEE;
	public static int CH_SUPPORT8_FEE;
	public static long CH_CURTAIN_FEE_RATIO;
	public static int CH_CURTAIN1_FEE;
	public static int CH_CURTAIN2_FEE;
	public static long CH_FRONT_FEE_RATIO;
	public static int CH_FRONT1_FEE;
	public static int CH_FRONT2_FEE;
	
	// ============================================================
	public static void loadCHConfig()
	{
		String filePath = "config/head/clanhall.properties";
		
		try
		{
			L2Properties clanhallSettings = new L2Properties(filePath);
			
			CH_TELE_FEE_RATIO = Long.valueOf(clanhallSettings.getProperty("ClanHallTeleportFunctionFeeRation", "86400000"));
			CH_TELE1_FEE = Integer.valueOf(clanhallSettings.getProperty("ClanHallTeleportFunctionFeeLvl1", "86400000"));
			CH_TELE2_FEE = Integer.valueOf(clanhallSettings.getProperty("ClanHallTeleportFunctionFeeLvl2", "86400000"));
			CH_SUPPORT_FEE_RATIO = Long.valueOf(clanhallSettings.getProperty("ClanHallSupportFunctionFeeRation", "86400000"));
			CH_SUPPORT1_FEE = Integer.valueOf(clanhallSettings.getProperty("ClanHallSupportFeeLvl1", "86400000"));
			CH_SUPPORT2_FEE = Integer.valueOf(clanhallSettings.getProperty("ClanHallSupportFeeLvl2", "86400000"));
			CH_SUPPORT3_FEE = Integer.valueOf(clanhallSettings.getProperty("ClanHallSupportFeeLvl3", "86400000"));
			CH_SUPPORT4_FEE = Integer.valueOf(clanhallSettings.getProperty("ClanHallSupportFeeLvl4", "86400000"));
			CH_SUPPORT5_FEE = Integer.valueOf(clanhallSettings.getProperty("ClanHallSupportFeeLvl5", "86400000"));
			CH_SUPPORT6_FEE = Integer.valueOf(clanhallSettings.getProperty("ClanHallSupportFeeLvl6", "86400000"));
			CH_SUPPORT7_FEE = Integer.valueOf(clanhallSettings.getProperty("ClanHallSupportFeeLvl7", "86400000"));
			CH_SUPPORT8_FEE = Integer.valueOf(clanhallSettings.getProperty("ClanHallSupportFeeLvl8", "86400000"));
			CH_MPREG_FEE_RATIO = Long.valueOf(clanhallSettings.getProperty("ClanHallMpRegenerationFunctionFeeRation", "86400000"));
			CH_MPREG1_FEE = Integer.valueOf(clanhallSettings.getProperty("ClanHallMpRegenerationFeeLvl1", "86400000"));
			CH_MPREG2_FEE = Integer.valueOf(clanhallSettings.getProperty("ClanHallMpRegenerationFeeLvl2", "86400000"));
			CH_MPREG3_FEE = Integer.valueOf(clanhallSettings.getProperty("ClanHallMpRegenerationFeeLvl3", "86400000"));
			CH_MPREG4_FEE = Integer.valueOf(clanhallSettings.getProperty("ClanHallMpRegenerationFeeLvl4", "86400000"));
			CH_MPREG5_FEE = Integer.valueOf(clanhallSettings.getProperty("ClanHallMpRegenerationFeeLvl5", "86400000"));
			CH_HPREG_FEE_RATIO = Long.valueOf(clanhallSettings.getProperty("ClanHallHpRegenerationFunctionFeeRation", "86400000"));
			CH_HPREG1_FEE = Integer.valueOf(clanhallSettings.getProperty("ClanHallHpRegenerationFeeLvl1", "86400000"));
			CH_HPREG2_FEE = Integer.valueOf(clanhallSettings.getProperty("ClanHallHpRegenerationFeeLvl2", "86400000"));
			CH_HPREG3_FEE = Integer.valueOf(clanhallSettings.getProperty("ClanHallHpRegenerationFeeLvl3", "86400000"));
			CH_HPREG4_FEE = Integer.valueOf(clanhallSettings.getProperty("ClanHallHpRegenerationFeeLvl4", "86400000"));
			CH_HPREG5_FEE = Integer.valueOf(clanhallSettings.getProperty("ClanHallHpRegenerationFeeLvl5", "86400000"));
			CH_HPREG6_FEE = Integer.valueOf(clanhallSettings.getProperty("ClanHallHpRegenerationFeeLvl6", "86400000"));
			CH_HPREG7_FEE = Integer.valueOf(clanhallSettings.getProperty("ClanHallHpRegenerationFeeLvl7", "86400000"));
			CH_HPREG8_FEE = Integer.valueOf(clanhallSettings.getProperty("ClanHallHpRegenerationFeeLvl8", "86400000"));
			CH_HPREG9_FEE = Integer.valueOf(clanhallSettings.getProperty("ClanHallHpRegenerationFeeLvl9", "86400000"));
			CH_HPREG10_FEE = Integer.valueOf(clanhallSettings.getProperty("ClanHallHpRegenerationFeeLvl10", "86400000"));
			CH_HPREG11_FEE = Integer.valueOf(clanhallSettings.getProperty("ClanHallHpRegenerationFeeLvl11", "86400000"));
			CH_HPREG12_FEE = Integer.valueOf(clanhallSettings.getProperty("ClanHallHpRegenerationFeeLvl12", "86400000"));
			CH_HPREG13_FEE = Integer.valueOf(clanhallSettings.getProperty("ClanHallHpRegenerationFeeLvl13", "86400000"));
			CH_EXPREG_FEE_RATIO = Long.valueOf(clanhallSettings.getProperty("ClanHallExpRegenerationFunctionFeeRation", "86400000"));
			CH_EXPREG1_FEE = Integer.valueOf(clanhallSettings.getProperty("ClanHallExpRegenerationFeeLvl1", "86400000"));
			CH_EXPREG2_FEE = Integer.valueOf(clanhallSettings.getProperty("ClanHallExpRegenerationFeeLvl2", "86400000"));
			CH_EXPREG3_FEE = Integer.valueOf(clanhallSettings.getProperty("ClanHallExpRegenerationFeeLvl3", "86400000"));
			CH_EXPREG4_FEE = Integer.valueOf(clanhallSettings.getProperty("ClanHallExpRegenerationFeeLvl4", "86400000"));
			CH_EXPREG5_FEE = Integer.valueOf(clanhallSettings.getProperty("ClanHallExpRegenerationFeeLvl5", "86400000"));
			CH_EXPREG6_FEE = Integer.valueOf(clanhallSettings.getProperty("ClanHallExpRegenerationFeeLvl6", "86400000"));
			CH_EXPREG7_FEE = Integer.valueOf(clanhallSettings.getProperty("ClanHallExpRegenerationFeeLvl7", "86400000"));
			CH_ITEM_FEE_RATIO = Long.valueOf(clanhallSettings.getProperty("ClanHallItemCreationFunctionFeeRation", "86400000"));
			CH_ITEM1_FEE = Integer.valueOf(clanhallSettings.getProperty("ClanHallItemCreationFunctionFeeLvl1", "86400000"));
			CH_ITEM2_FEE = Integer.valueOf(clanhallSettings.getProperty("ClanHallItemCreationFunctionFeeLvl2", "86400000"));
			CH_ITEM3_FEE = Integer.valueOf(clanhallSettings.getProperty("ClanHallItemCreationFunctionFeeLvl3", "86400000"));
			CH_CURTAIN_FEE_RATIO = Long.valueOf(clanhallSettings.getProperty("ClanHallCurtainFunctionFeeRation", "86400000"));
			CH_CURTAIN1_FEE = Integer.valueOf(clanhallSettings.getProperty("ClanHallCurtainFunctionFeeLvl1", "86400000"));
			CH_CURTAIN2_FEE = Integer.valueOf(clanhallSettings.getProperty("ClanHallCurtainFunctionFeeLvl2", "86400000"));
			CH_FRONT_FEE_RATIO = Long.valueOf(clanhallSettings.getProperty("ClanHallFrontPlatformFunctionFeeRation", "86400000"));
			CH_FRONT1_FEE = Integer.valueOf(clanhallSettings.getProperty("ClanHallFrontPlatformFunctionFeeLvl1", "86400000"));
			CH_FRONT2_FEE = Integer.valueOf(clanhallSettings.getProperty("ClanHallFrontPlatformFunctionFeeLvl2", "86400000"));
		}
		catch (Exception e)
		{
			LOGGER.error("Config : Failed to load " + filePath + " file.", e);
		}
	}
	
	// ============================================================
	public static int DEVASTATED_DAY;
	public static int DEVASTATED_HOUR;
	public static int DEVASTATED_MINUTES;
	public static int PARTISAN_DAY;
	public static int PARTISAN_HOUR;
	public static int PARTISAN_MINUTES;
	
	// ============================================================
	public static void loadElitCHConfig()
	{
		String filePath = "config/head/elitclanhall.properties";
		
		try
		{
			L2Properties elitchSettings = new L2Properties(filePath);
			
			DEVASTATED_DAY = Integer.valueOf(elitchSettings.getProperty("DevastatedDay", "1"));
			DEVASTATED_HOUR = Integer.valueOf(elitchSettings.getProperty("DevastatedHour", "18"));
			DEVASTATED_MINUTES = Integer.valueOf(elitchSettings.getProperty("DevastatedMinutes", "0"));
			PARTISAN_DAY = Integer.valueOf(elitchSettings.getProperty("PartisanDay", "5"));
			PARTISAN_HOUR = Integer.valueOf(elitchSettings.getProperty("PartisanHour", "21"));
			PARTISAN_MINUTES = Integer.valueOf(elitchSettings.getProperty("PartisanMinutes", "0"));
		}
		catch (Exception e)
		{
			LOGGER.error("Config : Failed to load " + filePath + " file.", e);
		}
	}
	
	public static int ITEM_ID_MONSTER;
	public static int SPAWN_TIME;
	public static int MONSTER_ID;
	public static int EFFECT_SKILL_ID;
	public static int EFFECT_SKILL_LEVEL;
	public static long EFFECT_DURATION;
	public static String SUMMON_MESSAGE;
	public static String DESPAWN_MESSAGE;
	
	public static void loadConfigMonsterItem()
	{
		String filePath = "config/custom/MonsterItemInvocation.properties";
		
		try
		{
			L2Properties monsterItemProperties = new L2Properties(filePath);
			ITEM_ID_MONSTER = Integer.parseInt(monsterItemProperties.getProperty("ItemId", "9700"));
			SPAWN_TIME = Integer.parseInt(monsterItemProperties.getProperty("SpawnTime", "60000")); // Milissegundos
			MONSTER_ID = Integer.parseInt(monsterItemProperties.getProperty("MobId", "1"));
			EFFECT_SKILL_ID = Integer.parseInt(monsterItemProperties.getProperty("EffectSkillId", "1234"));
			EFFECT_SKILL_LEVEL = Integer.parseInt(monsterItemProperties.getProperty("EffectSkillLevel", "1"));
			EFFECT_DURATION = Long.parseLong(monsterItemProperties.getProperty("EffectDuration", "0")); // Milissegundos
			SUMMON_MESSAGE = monsterItemProperties.getProperty("SummonMessage", "Você tem 60 segundos para matar o monstro, caso contrário, ele será deletado!");
			DESPAWN_MESSAGE = monsterItemProperties.getProperty("DespawnMessage", "O tempo acabou! O monstro foi deletado.");
		}
		catch (Exception e)
		{
			LOGGER.error("Config : Failed to load " + filePath + " file.", e);
		}
	}
	
	// ============================================================
	public static boolean L2JMOD_ALLOW_WEDDING;
	public static int L2JMOD_WEDDING_PRICE;
	public static boolean L2JMOD_WEDDING_PUNISH_INFIDELITY;
	public static boolean L2JMOD_WEDDING_TELEPORT;
	public static int L2JMOD_WEDDING_TELEPORT_PRICE;
	public static int L2JMOD_WEDDING_TELEPORT_DURATION;
	public static int L2JMOD_WEDDING_NAME_COLOR_NORMAL;
	public static int L2JMOD_WEDDING_NAME_COLOR_GEY;
	public static int L2JMOD_WEDDING_NAME_COLOR_LESBO;
	public static boolean L2JMOD_WEDDING_SAMESEX;
	public static boolean L2JMOD_WEDDING_FORMALWEAR;
	public static int L2JMOD_WEDDING_DIVORCE_COSTS;
	public static boolean WEDDING_GIVE_CUPID_BOW;
	public static boolean ANNOUNCE_WEDDING;
	
	// ============================================================
	public static void loadWeddingConfig()
	{
		String filePath = "config/fun/wedding.properties";
		
		try
		{
			L2Properties WeddingSettings = new L2Properties(filePath);
			
			L2JMOD_ALLOW_WEDDING = Boolean.valueOf(WeddingSettings.getProperty("AllowWedding", "False"));
			L2JMOD_WEDDING_PRICE = Integer.parseInt(WeddingSettings.getProperty("WeddingPrice", "250000000"));
			L2JMOD_WEDDING_PUNISH_INFIDELITY = Boolean.parseBoolean(WeddingSettings.getProperty("WeddingPunishInfidelity", "True"));
			L2JMOD_WEDDING_TELEPORT = Boolean.parseBoolean(WeddingSettings.getProperty("WeddingTeleport", "True"));
			L2JMOD_WEDDING_TELEPORT_PRICE = Integer.parseInt(WeddingSettings.getProperty("WeddingTeleportPrice", "50000"));
			L2JMOD_WEDDING_TELEPORT_DURATION = Integer.parseInt(WeddingSettings.getProperty("WeddingTeleportDuration", "60"));
			L2JMOD_WEDDING_NAME_COLOR_NORMAL = Integer.decode("0x" + WeddingSettings.getProperty("WeddingNameCollorN", "FFFFFF"));
			L2JMOD_WEDDING_NAME_COLOR_GEY = Integer.decode("0x" + WeddingSettings.getProperty("WeddingNameCollorB", "FFFFFF"));
			L2JMOD_WEDDING_NAME_COLOR_LESBO = Integer.decode("0x" + WeddingSettings.getProperty("WeddingNameCollorL", "FFFFFF"));
			L2JMOD_WEDDING_SAMESEX = Boolean.parseBoolean(WeddingSettings.getProperty("WeddingAllowSameSex", "False"));
			L2JMOD_WEDDING_FORMALWEAR = Boolean.parseBoolean(WeddingSettings.getProperty("WeddingFormalWear", "True"));
			L2JMOD_WEDDING_DIVORCE_COSTS = Integer.parseInt(WeddingSettings.getProperty("WeddingDivorceCosts", "20"));
			WEDDING_GIVE_CUPID_BOW = Boolean.parseBoolean(WeddingSettings.getProperty("WeddingGiveBow", "False"));
			ANNOUNCE_WEDDING = Boolean.parseBoolean(WeddingSettings.getProperty("AnnounceWedding", "True"));
			
		}
		catch (Exception e)
		{
			LOGGER.error("Config : Failed to load " + filePath + " file.", e);
		}
	}
	
	// ============================================================
	public static int TW_TOWN_ID;
	public static boolean TW_ALL_TOWNS;
	public static int TW_ITEM_ID;
	public static int TW_ITEM_AMOUNT;
	public static boolean TW_DISABLE_GK;
	public static boolean TW_RESS_ON_DIE;
	
	// ============================================================
	public static void loadTWConfig()
	{
		String filePath = "config/events/tw.properties";
		
		try
		{
			L2Properties TWSettings = new L2Properties(filePath);
			
			TW_TOWN_ID = Integer.parseInt(TWSettings.getProperty("TWTownId", "9"));
			
			if (TW_TOWN_ID < 1)
			{
				TW_TOWN_ID = 1;
			}
			else if (TW_TOWN_ID > 18)
			{
				TW_TOWN_ID = 18;
			}
			
			TW_ALL_TOWNS = Boolean.parseBoolean(TWSettings.getProperty("TWAllTowns", "False"));
			TW_ITEM_ID = Integer.parseInt(TWSettings.getProperty("TownWarItemId", "57"));
			TW_ITEM_AMOUNT = Integer.parseInt(TWSettings.getProperty("TownWarItemAmount", "5000"));
			TW_DISABLE_GK = Boolean.parseBoolean(TWSettings.getProperty("DisableGK", "True"));
			TW_RESS_ON_DIE = Boolean.parseBoolean(TWSettings.getProperty("SendRessOnDeath", "False"));
		}
		catch (Exception e)
		{
			LOGGER.error("Config : Failed to load " + filePath + " file.", e);
		}
	}
	
	// ============================================================
	public static boolean PCB_ENABLE;
	public static int PCB_MIN_LEVEL;
	public static int PCB_POINT_MIN;
	public static int PCB_POINT_MAX;
	public static int PCB_DOUBLE_POINT_CHANCE;
	public static int PCB_INTERVAL;
	
	// ============================================================
	public static void loadPCBPointConfig()
	{
		String filePath = "config/fun/pcBang.properties";
		
		try
		{
			L2Properties pcbpSettings = new L2Properties(filePath);
			
			PCB_ENABLE = Boolean.parseBoolean(pcbpSettings.getProperty("PcBangPointEnable", "true"));
			PCB_MIN_LEVEL = Integer.parseInt(pcbpSettings.getProperty("PcBangPointMinLevel", "20"));
			PCB_POINT_MIN = Integer.parseInt(pcbpSettings.getProperty("PcBangPointMinCount", "20"));
			PCB_POINT_MAX = Integer.parseInt(pcbpSettings.getProperty("PcBangPointMaxCount", "1000000"));
			
			if (PCB_POINT_MAX < 1)
			{
				PCB_POINT_MAX = Integer.MAX_VALUE;
			}
			
			PCB_DOUBLE_POINT_CHANCE = Integer.parseInt(pcbpSettings.getProperty("PcBangDoublePointChance", "20"));
			PCB_INTERVAL = Integer.parseInt(pcbpSettings.getProperty("PcBangPointTimeStamp", "900"));
		}
		catch (Exception e)
		{
			LOGGER.error("Config : Failed to load " + filePath + " file.", e);
		}
	}
	
	// ============================================================
	public static boolean ALT_DEV_NO_QUESTS;
	public static boolean ALT_DEV_NO_SPAWNS;
	public static boolean ALT_DEV_NO_SCRIPT;
	public static boolean ALT_DEV_NO_RB;
	public static boolean ENABLE_OLYMPIAD_DISCONNECTION_DEBUG;
	public static boolean ALT_DEV_NO_AI;
	public static boolean SKILLSDEBUG;
	public static boolean ENABLE_OLYMPIAD_DEBUG;
	public static boolean DEBUG;
	public static boolean ASSERT;
	public static boolean DEVELOPER;
	public static boolean ZONE_DEBUG;
	public static boolean ENABLE_ALL_EXCEPTIONS = true;
	public static boolean ACCEPT_ALTERNATE_ID;
	public static boolean GMAUDIT;
	public static boolean LOG_CHAT;
	public static boolean LOG_HIGH_DAMAGES;
	public static boolean GAMEGUARD_L2NET_CHECK;
	
	// Threads
	public static int THREAD_P_EFFECTS;
	public static int THREAD_P_GENERAL;
	public static int GENERAL_PACKET_THREAD_CORE_SIZE;
	public static int IO_PACKET_THREAD_CORE_SIZE;
	public static int GENERAL_THREAD_CORE_SIZE;
	public static int AI_MAX_THREAD;
	public static boolean LAZY_CACHE;
	
	// ============================================================
	public static void loadDevConfig()
	{
		String filePath = "config/functions/developer.properties";
		
		try
		{
			L2Properties devSettings = new L2Properties(filePath);
			
			ENABLE_OLYMPIAD_DEBUG = Boolean.parseBoolean(devSettings.getProperty("EnableOlympiadDebug", "false"));
			SKILLSDEBUG = Boolean.parseBoolean(devSettings.getProperty("SkillsDebug", "false"));
			DEBUG = Boolean.parseBoolean(devSettings.getProperty("Debug", "false"));
			ASSERT = Boolean.parseBoolean(devSettings.getProperty("Assert", "false"));
			DEVELOPER = Boolean.parseBoolean(devSettings.getProperty("Developer", "false"));
			ZONE_DEBUG = Boolean.parseBoolean(devSettings.getProperty("ZoneDebug", "false"));
			ENABLE_ALL_EXCEPTIONS = Boolean.parseBoolean(devSettings.getProperty("EnableAllExceptionsLog", "false"));
			ALT_DEV_NO_QUESTS = Boolean.parseBoolean(devSettings.getProperty("AltDevNoQuests", "False"));
			ALT_DEV_NO_SPAWNS = Boolean.parseBoolean(devSettings.getProperty("AltDevNoSpawns", "False"));
			ALT_DEV_NO_SCRIPT = Boolean.parseBoolean(devSettings.getProperty("AltDevNoScript", "False"));
			ALT_DEV_NO_AI = Boolean.parseBoolean(devSettings.getProperty("AltDevNoAI", "False"));
			ALT_DEV_NO_RB = Boolean.parseBoolean(devSettings.getProperty("AltDevNoRB", "False"));
			ENABLE_OLYMPIAD_DISCONNECTION_DEBUG = Boolean.parseBoolean(devSettings.getProperty("EnableOlympiadDisconnectionDebug"));
			
			ACCEPT_ALTERNATE_ID = Boolean.parseBoolean(devSettings.getProperty("AcceptAlternateID", "True"));
			
			GMAUDIT = Boolean.valueOf(devSettings.getProperty("GMAudit", "False"));
			LOG_CHAT = Boolean.valueOf(devSettings.getProperty("LogChat", "false"));
			LOG_HIGH_DAMAGES = Boolean.valueOf(devSettings.getProperty("LogHighDamages", "false"));
			
			GAMEGUARD_L2NET_CHECK = Boolean.valueOf(devSettings.getProperty("GameGuardL2NetCheck", "False"));
			
			THREAD_P_EFFECTS = Integer.parseInt(devSettings.getProperty("ThreadPoolSizeEffects", "6"));
			THREAD_P_GENERAL = Integer.parseInt(devSettings.getProperty("ThreadPoolSizeGeneral", "15"));
			GENERAL_PACKET_THREAD_CORE_SIZE = Integer.parseInt(devSettings.getProperty("GeneralPacketThreadCoreSize", "4"));
			IO_PACKET_THREAD_CORE_SIZE = Integer.parseInt(devSettings.getProperty("UrgentPacketThreadCoreSize", "2"));
			AI_MAX_THREAD = Integer.parseInt(devSettings.getProperty("AiMaxThread", "10"));
			GENERAL_THREAD_CORE_SIZE = Integer.parseInt(devSettings.getProperty("GeneralThreadCoreSize", "4"));
			
			LAZY_CACHE = Boolean.valueOf(devSettings.getProperty("LazyCache", "False"));
		}
		catch (Exception e)
		{
			LOGGER.error("Config : Failed to load " + filePath + " file.", e);
		}
	}
	
	// ============================================================
	public static boolean IS_CRAFTING_ENABLED;
	public static int DWARF_RECIPE_LIMIT;
	public static int COMMON_RECIPE_LIMIT;
	public static boolean ALT_GAME_CREATION;
	public static double ALT_GAME_CREATION_SPEED;
	public static double ALT_GAME_CREATION_XP_RATE;
	public static double ALT_GAME_CREATION_SP_RATE;
	public static boolean ALT_BLACKSMITH_USE_RECIPES;
	
	// ============================================================
	public static void loadCraftConfig()
	{
		String filePath = "config/functions/crafting.properties";
		
		try
		{
			final L2Properties craftSettings = new L2Properties(filePath);
			
			DWARF_RECIPE_LIMIT = Integer.parseInt(craftSettings.getProperty("DwarfRecipeLimit", "50"));
			COMMON_RECIPE_LIMIT = Integer.parseInt(craftSettings.getProperty("CommonRecipeLimit", "50"));
			IS_CRAFTING_ENABLED = Boolean.parseBoolean(craftSettings.getProperty("CraftingEnabled", "True"));
			ALT_GAME_CREATION = Boolean.parseBoolean(craftSettings.getProperty("AltGameCreation", "False"));
			ALT_GAME_CREATION_SPEED = Double.parseDouble(craftSettings.getProperty("AltGameCreationSpeed", "1"));
			ALT_GAME_CREATION_XP_RATE = Double.parseDouble(craftSettings.getProperty("AltGameCreationRateXp", "1"));
			ALT_GAME_CREATION_SP_RATE = Double.parseDouble(craftSettings.getProperty("AltGameCreationRateSp", "1"));
			ALT_BLACKSMITH_USE_RECIPES = Boolean.parseBoolean(craftSettings.getProperty("AltBlacksmithUseRecipes", "True"));
		}
		catch (Exception e)
		{
			LOGGER.error("Config : Failed to load " + filePath + " file.", e);
		}
	}
	
	// ============================================================
	public static boolean ALLOW_AWAY_STATUS;
	public static int AWAY_TIMER;
	public static int BACK_TIMER;
	public static int AWAY_TITLE_COLOR;
	public static boolean AWAY_PLAYER_TAKE_AGGRO;
	public static boolean AWAY_PEACE_ZONE;
	
	// ============================================================
	public static void loadAWAYConfig()
	{
		String filePath = "config/fun/away.properties";
		
		try
		{
			L2Properties AWAYSettings = new L2Properties(filePath);
			
			ALLOW_AWAY_STATUS = Boolean.parseBoolean(AWAYSettings.getProperty("AllowAwayStatus", "False"));
			AWAY_PLAYER_TAKE_AGGRO = Boolean.parseBoolean(AWAYSettings.getProperty("AwayPlayerTakeAggro", "False"));
			AWAY_TITLE_COLOR = Integer.decode("0x" + AWAYSettings.getProperty("AwayTitleColor", "0000FF"));
			AWAY_TIMER = Integer.parseInt(AWAYSettings.getProperty("AwayTimer", "30"));
			BACK_TIMER = Integer.parseInt(AWAYSettings.getProperty("BackTimer", "30"));
			AWAY_PEACE_ZONE = Boolean.parseBoolean(AWAYSettings.getProperty("AwayOnlyInPeaceZone", "False"));
		}
		catch (Exception e)
		{
			LOGGER.error("Config : Failed to load " + filePath + " file.", e);
		}
	}
	
	// ============================================================
	public static boolean BANKING_SYSTEM_ENABLED;
	public static int BANKING_SYSTEM_GOLDBARS;
	public static int BANKING_SYSTEM_ADENA;
	
	// ============================================================
	public static void loadBankingConfig()
	{
		String filePath = "config/fun/bank.properties";
		
		try
		{
			L2Properties BANKSettings = new L2Properties(filePath);
			
			BANKING_SYSTEM_ENABLED = Boolean.parseBoolean(BANKSettings.getProperty("BankingEnabled", "false"));
			BANKING_SYSTEM_GOLDBARS = Integer.parseInt(BANKSettings.getProperty("BankingGoldbarCount", "1"));
			BANKING_SYSTEM_ADENA = Integer.parseInt(BANKSettings.getProperty("BankingAdenaCount", "500000000"));
			
		}
		catch (Exception e)
		{
			LOGGER.error("Config : Failed to load " + filePath + " file.", e);
		}
	}
	
	// ============================================================
	public static boolean ONLINE_PLAYERS_ON_LOGIN;
	public static boolean SUBSTUCK_SKILLS;
	public static boolean ALT_SERVER_NAME_ENABLED;
	public static boolean ANNOUNCE_TRY_BANNED_ACCOUNT;
	public static String ALT_Server_Name;
	public static boolean ALLOW_DETAILED_STATS_VIEW;
	public static boolean ALLOW_ONLINE_VIEW;
	public static boolean WELCOME_HTM;
	public static boolean PROTECTOR_PLAYER_PK;
	public static boolean PROTECTOR_PLAYER_PVP;
	public static int PROTECTOR_RADIUS_ACTION;
	public static int PROTECTOR_SKILLID;
	public static int PROTECTOR_SKILLLEVEL;
	public static int PROTECTOR_SKILLTIME;
	public static String PROTECTOR_MESSAGE;
	public static boolean CASTLE_SHIELD;
	public static boolean CLANHALL_SHIELD;
	public static boolean APELLA_ARMORS;
	public static boolean OATH_ARMORS;
	public static boolean CASTLE_CIRCLETS;
	public static boolean NOBLE_CUSTOM_ITEMS;
	public static boolean HERO_CUSTOM_ITEMS;
	public static boolean ALLOW_HERO_SUBSKILL;
	public static int HERO_COUNT;
	public static int CRUMA_TOWER_LEVEL_RESTRICT;
	/** Allow Players Level Difference Protection ? */
	public static int ALT_PLAYER_PROTECTION_LEVEL;
	public static boolean ALLOW_LOW_LEVEL_TRADE;
	/** Chat filter */
	public static boolean USE_CHAT_FILTER;
	
	public static int MONSTER_RETURN_DELAY;
	public static int MAX_DRIFT_RANGE;
	
	public static boolean ALLOW_CHAR_KILL_PROTECT;
	public static int DIFFERENT_Z_CHANGE_OBJECT;
	public static int DIFFERENT_Z_NEW_MOVIE;
	
	public static int HERO_CUSTOM_ITEM_ID;
	public static int NOBLE_CUSTOM_ITEM_ID;
	public static int HERO_CUSTOM_DAY;
	public static boolean SPAWN_CHRISTMAS_TREE;
	public static boolean GM_CRITANNOUNCER_NAME;
	public static boolean PM_MESSAGE_ON_START;
	public static boolean SERVER_TIME_ON_START;
	public static String PM_SERVER_NAME;
	public static String PM_TEXT1;
	public static String PM_TEXT2;
	public static boolean NEW_PLAYER_EFFECT;
	public static boolean ALT_OLY_END_ANNOUNCE;
	
	// ============================================================
	public static void loadL2JFrozenConfig()
	{
		String filePath = "config/functions/l2jfrozen.properties";
		
		try
		{
			L2Properties L2JFrozenSettings = new L2Properties(filePath);
			
			ONLINE_PLAYERS_ON_LOGIN = Boolean.valueOf(L2JFrozenSettings.getProperty("OnlineOnLogin", "False"));
			ALT_OLY_END_ANNOUNCE = Boolean.parseBoolean(L2JFrozenSettings.getProperty("AltOlyEndAnnounce", "False"));
			/** Protector **/
			PROTECTOR_PLAYER_PK = Boolean.parseBoolean(L2JFrozenSettings.getProperty("ProtectorPlayerPK", "false"));
			PROTECTOR_PLAYER_PVP = Boolean.parseBoolean(L2JFrozenSettings.getProperty("ProtectorPlayerPVP", "false"));
			PROTECTOR_RADIUS_ACTION = Integer.parseInt(L2JFrozenSettings.getProperty("ProtectorRadiusAction", "500"));
			PROTECTOR_SKILLID = Integer.parseInt(L2JFrozenSettings.getProperty("ProtectorSkillId", "1069"));
			PROTECTOR_SKILLLEVEL = Integer.parseInt(L2JFrozenSettings.getProperty("ProtectorSkillLevel", "42"));
			PROTECTOR_SKILLTIME = Integer.parseInt(L2JFrozenSettings.getProperty("ProtectorSkillTime", "800"));
			PROTECTOR_MESSAGE = L2JFrozenSettings.getProperty("ProtectorMessage", "Protector, not spawnkilling here, go read the rules !!!");
			
			/** Welcome Htm **/
			WELCOME_HTM = Boolean.parseBoolean(L2JFrozenSettings.getProperty("WelcomeHtm", "False"));
			
			/** Server Name **/
			ALT_SERVER_NAME_ENABLED = Boolean.parseBoolean(L2JFrozenSettings.getProperty("ServerNameEnabled", "false"));
			ANNOUNCE_TRY_BANNED_ACCOUNT = Boolean.parseBoolean(L2JFrozenSettings.getProperty("AnnounceTryBannedAccount", "false"));
			ALT_Server_Name = String.valueOf(L2JFrozenSettings.getProperty("ServerName"));
			DIFFERENT_Z_CHANGE_OBJECT = Integer.parseInt(L2JFrozenSettings.getProperty("DifferentZchangeObject", "650"));
			DIFFERENT_Z_NEW_MOVIE = Integer.parseInt(L2JFrozenSettings.getProperty("DifferentZnewmovie", "1000"));
			
			ALLOW_DETAILED_STATS_VIEW = Boolean.valueOf(L2JFrozenSettings.getProperty("AllowDetailedStatsView", "False"));
			ALLOW_ONLINE_VIEW = Boolean.valueOf(L2JFrozenSettings.getProperty("AllowOnlineView", "False"));
			
			CASTLE_SHIELD = Boolean.parseBoolean(L2JFrozenSettings.getProperty("CastleShieldRestriction", "true"));
			CLANHALL_SHIELD = Boolean.parseBoolean(L2JFrozenSettings.getProperty("ClanHallShieldRestriction", "true"));
			APELLA_ARMORS = Boolean.parseBoolean(L2JFrozenSettings.getProperty("ApellaArmorsRestriction", "true"));
			OATH_ARMORS = Boolean.parseBoolean(L2JFrozenSettings.getProperty("OathArmorsRestriction", "true"));
			CASTLE_CIRCLETS = Boolean.parseBoolean(L2JFrozenSettings.getProperty("CastleCircletsRestriction", "true"));
			
			NOBLE_CUSTOM_ITEMS = Boolean.parseBoolean(L2JFrozenSettings.getProperty("EnableNobleCustomItem", "true"));
			NOBLE_CUSTOM_ITEM_ID = Integer.parseInt(L2JFrozenSettings.getProperty("NoobleCustomItemId", "6673"));
			HERO_CUSTOM_ITEMS = Boolean.parseBoolean(L2JFrozenSettings.getProperty("EnableHeroCustomItem", "true"));
			HERO_CUSTOM_ITEM_ID = Integer.parseInt(L2JFrozenSettings.getProperty("HeroCustomItemId", "3481"));
			HERO_CUSTOM_DAY = Integer.parseInt(L2JFrozenSettings.getProperty("HeroCustomDay", "0"));
			
			ALLOW_LOW_LEVEL_TRADE = Boolean.parseBoolean(L2JFrozenSettings.getProperty("AllowLowLevelTrade", "True"));
			ALLOW_HERO_SUBSKILL = Boolean.parseBoolean(L2JFrozenSettings.getProperty("CustomHeroSubSkill", "False"));
			HERO_COUNT = Integer.parseInt(L2JFrozenSettings.getProperty("HeroCount", "1"));
			CRUMA_TOWER_LEVEL_RESTRICT = Integer.parseInt(L2JFrozenSettings.getProperty("CrumaTowerLevelRestrict", "56"));
			ALT_PLAYER_PROTECTION_LEVEL = Integer.parseInt(L2JFrozenSettings.getProperty("AltPlayerProtectionLevel", "0"));
			MONSTER_RETURN_DELAY = Integer.parseInt(L2JFrozenSettings.getProperty("MonsterReturnDelay", "1200"));
			MAX_DRIFT_RANGE = Integer.parseInt(L2JFrozenSettings.getProperty("MaxDriftRange", "300"));
			ALLOW_CHAR_KILL_PROTECT = Boolean.parseBoolean(L2JFrozenSettings.getProperty("AllowLowLvlProtect", "False"));
			
			SPAWN_CHRISTMAS_TREE = Boolean.parseBoolean(L2JFrozenSettings.getProperty("SpawnChristmasTree", "false"));
			
			PM_MESSAGE_ON_START = Boolean.parseBoolean(L2JFrozenSettings.getProperty("PMWelcomeShow", "False"));
			SERVER_TIME_ON_START = Boolean.parseBoolean(L2JFrozenSettings.getProperty("ShowServerTimeOnStart", "False"));
			PM_SERVER_NAME = L2JFrozenSettings.getProperty("PMServerName", "L2-Frozen");
			PM_TEXT1 = L2JFrozenSettings.getProperty("PMText1", "Have Fun and Nice Stay on");
			PM_TEXT2 = L2JFrozenSettings.getProperty("PMText2", "Vote for us every 24h");
			NEW_PLAYER_EFFECT = Boolean.parseBoolean(L2JFrozenSettings.getProperty("NewPlayerEffect", "True"));
		}
		catch (Exception e)
		{
			LOGGER.error("Config : Failed to load " + filePath + " file.", e);
		}
	}
	
	// ============================================================
	public static int KARMA_MIN_KARMA;
	public static int KARMA_MAX_KARMA;
	public static int KARMA_XP_DIVIDER;
	public static int KARMA_LOST_BASE;
	public static boolean KARMA_DROP_GM;
	public static boolean KILL_PK_INCREASE_PVP;
	public static int KARMA_PK_LIMIT;
	public static String KARMA_NONDROPPABLE_PET_ITEMS;
	public static String KARMA_NONDROPPABLE_ITEMS;
	public static List<Integer> KARMA_LIST_NONDROPPABLE_PET_ITEMS = new ArrayList<>();
	public static List<Integer> KARMA_LIST_NONDROPPABLE_ITEMS = new ArrayList<>();
	public static int PVP_NORMAL_TIME;
	public static int PVP_PVP_TIME;
	public static boolean PVP_AND_PK_INFO_ENABLED;
	public static boolean FLAGED_PLAYER_CAN_USE_GK;
	public static boolean PVPEXPSP_SYSTEM;
	/** Add Exp At Pvp! */
	public static int ADD_EXP;
	/** Add Sp At Pvp! */
	public static int ADD_SP;
	public static boolean ALLOW_POTS_IN_PVP;
	public static boolean ALLOW_SOE_IN_PVP;
	
	public static boolean ANNOUNCE_PVP_KILL;
	public static boolean ANNOUNCE_PK_KILL;
	
	public static int DUEL_SPAWN_X;
	public static int DUEL_SPAWN_Y;
	public static int DUEL_SPAWN_Z;
	
	public static boolean PVP_PK_TITLE;
	public static String PVP_TITLE_PREFIX;
	public static String PK_TITLE_PREFIX;
	
	public static boolean WAR_LEGEND_AURA;
	public static int KILLS_TO_GET_WAR_LEGEND_AURA;
	
	public static boolean ANTI_FARM_ENABLED;
	public static boolean ANTI_FARM_CLAN_ALLY_ENABLED;
	public static boolean ANTI_FARM_LVL_DIFF_ENABLED;
	public static int ANTI_FARM_MAX_LVL_DIFF;
	public static boolean ANTI_FARM_PDEF_DIFF_ENABLED;
	public static int ANTI_FARM_MAX_PDEF_DIFF;
	public static boolean ANTI_FARM_PATK_DIFF_ENABLED;
	public static int ANTI_FARM_MAX_PATK_DIFF;
	public static boolean ANTI_FARM_PARTY_ENABLED;
	public static boolean ANTI_FARM_IP_ENABLED;
	public static boolean ANTI_FARM_SUMMON;
	
	public static String REVISION;
	public static String BUILD_DATE;
	
	public static String HEAD_REVISION;
	public static String HEAD_PUB_DATE;
	
	// ============================================================
	public static void loadPvpConfig()
	{
		String filePath = "config/functions/pvp.properties";
		
		try
		{
			L2Properties pvpSettings = new L2Properties(filePath);
			
			/* KARMA SYSTEM */
			KARMA_MIN_KARMA = Integer.parseInt(pvpSettings.getProperty("MinKarma", "240"));
			KARMA_MAX_KARMA = Integer.parseInt(pvpSettings.getProperty("MaxKarma", "10000"));
			KARMA_XP_DIVIDER = Integer.parseInt(pvpSettings.getProperty("XPDivider", "260"));
			KARMA_LOST_BASE = Integer.parseInt(pvpSettings.getProperty("BaseKarmaLost", "0"));
			
			KARMA_DROP_GM = Boolean.parseBoolean(pvpSettings.getProperty("CanGMDropEquipment", "false"));
			KILL_PK_INCREASE_PVP = Boolean.parseBoolean(pvpSettings.getProperty("KillPKIncreasePvP", "true"));
			
			KARMA_PK_LIMIT = Integer.parseInt(pvpSettings.getProperty("MinimumPKRequiredToDrop", "5"));
			
			KARMA_NONDROPPABLE_PET_ITEMS = pvpSettings.getProperty("ListOfPetItems", "2375,3500,3501,3502,4422,4423,4424,4425,6648,6649,6650");
			KARMA_NONDROPPABLE_ITEMS = pvpSettings.getProperty("ListOfNonDroppableItems", "57,1147,425,1146,461,10,2368,7,6,2370,2369,6842,6611,6612,6613,6614,6615,6616,6617,6618,6619,6620,6621");
			
			KARMA_LIST_NONDROPPABLE_PET_ITEMS = new ArrayList<>();
			for (final String id : KARMA_NONDROPPABLE_PET_ITEMS.split(","))
			{
				KARMA_LIST_NONDROPPABLE_PET_ITEMS.add(Integer.parseInt(id));
			}
			
			KARMA_LIST_NONDROPPABLE_ITEMS = new ArrayList<>();
			for (final String id : KARMA_NONDROPPABLE_ITEMS.split(","))
			{
				KARMA_LIST_NONDROPPABLE_ITEMS.add(Integer.parseInt(id));
			}
			
			PVP_NORMAL_TIME = Integer.parseInt(pvpSettings.getProperty("PvPVsNormalTime", "15000"));
			PVP_PVP_TIME = Integer.parseInt(pvpSettings.getProperty("PvPVsPvPTime", "30000"));
			ALT_GAME_KARMA_PLAYER_CAN_BE_KILLED_IN_PEACEZONE = Boolean.valueOf(pvpSettings.getProperty("AltKarmaPlayerCanBeKilledInPeaceZone", "false"));
			ALT_GAME_KARMA_PLAYER_CAN_SHOP = Boolean.valueOf(pvpSettings.getProperty("AltKarmaPlayerCanShop", "true"));
			ALT_GAME_KARMA_PLAYER_CAN_USE_GK = Boolean.valueOf(pvpSettings.getProperty("AltKarmaPlayerCanUseGK", "false"));
			ALT_GAME_KARMA_PLAYER_CAN_TELEPORT = Boolean.valueOf(pvpSettings.getProperty("AltKarmaPlayerCanTeleport", "true"));
			ALT_GAME_KARMA_PLAYER_CAN_TRADE = Boolean.valueOf(pvpSettings.getProperty("AltKarmaPlayerCanTrade", "true"));
			ALT_GAME_KARMA_PLAYER_CAN_USE_WAREHOUSE = Boolean.valueOf(pvpSettings.getProperty("AltKarmaPlayerCanUseWareHouse", "true"));
			ALT_KARMA_TELEPORT_TO_FLORAN = Boolean.valueOf(pvpSettings.getProperty("AltKarmaTeleportToFloran", "true"));
			
			FLAGED_PLAYER_CAN_USE_GK = Boolean.parseBoolean(pvpSettings.getProperty("FlaggedPlayerCanUseGK", "false"));
			PVPEXPSP_SYSTEM = Boolean.parseBoolean(pvpSettings.getProperty("AllowAddExpSpAtPvP", "False"));
			ADD_EXP = Integer.parseInt(pvpSettings.getProperty("AddExpAtPvp", "0"));
			ADD_SP = Integer.parseInt(pvpSettings.getProperty("AddSpAtPvp", "0"));
			ALLOW_SOE_IN_PVP = Boolean.parseBoolean(pvpSettings.getProperty("AllowSoEInPvP", "true"));
			ALLOW_POTS_IN_PVP = Boolean.parseBoolean(pvpSettings.getProperty("AllowPotsInPvP", "True"));
			PVP_AND_PK_INFO_ENABLED = Boolean.valueOf(pvpSettings.getProperty("PvPAndPKInfoEnabled", "false"));
			
			ANNOUNCE_PVP_KILL = Boolean.parseBoolean(pvpSettings.getProperty("AnnouncePvPKill", "False"));
			ANNOUNCE_PK_KILL = Boolean.parseBoolean(pvpSettings.getProperty("AnnouncePkKill", "False"));
			
			DUEL_SPAWN_X = Integer.parseInt(pvpSettings.getProperty("DuelSpawnX", "-102495"));
			DUEL_SPAWN_Y = Integer.parseInt(pvpSettings.getProperty("DuelSpawnY", "-209023"));
			DUEL_SPAWN_Z = Integer.parseInt(pvpSettings.getProperty("DuelSpawnZ", "-3326"));
			PVP_PK_TITLE = Boolean.parseBoolean(pvpSettings.getProperty("PvpPkTitle", "False"));
			PVP_TITLE_PREFIX = pvpSettings.getProperty("PvPTitlePrefix", " ");
			PK_TITLE_PREFIX = pvpSettings.getProperty("PkTitlePrefix", " | ");
			
			WAR_LEGEND_AURA = Boolean.parseBoolean(pvpSettings.getProperty("WarLegendAura", "False"));
			KILLS_TO_GET_WAR_LEGEND_AURA = Integer.parseInt(pvpSettings.getProperty("KillsToGetWarLegendAura", "30"));
			
			ANTI_FARM_ENABLED = Boolean.parseBoolean(pvpSettings.getProperty("AntiFarmEnabled", "False"));
			ANTI_FARM_CLAN_ALLY_ENABLED = Boolean.parseBoolean(pvpSettings.getProperty("AntiFarmClanAlly", "False"));
			ANTI_FARM_LVL_DIFF_ENABLED = Boolean.parseBoolean(pvpSettings.getProperty("AntiFarmLvlDiff", "False"));
			ANTI_FARM_MAX_LVL_DIFF = Integer.parseInt(pvpSettings.getProperty("AntiFarmMaxLvlDiff", "40"));
			ANTI_FARM_PDEF_DIFF_ENABLED = Boolean.parseBoolean(pvpSettings.getProperty("AntiFarmPdefDiff", "False"));
			ANTI_FARM_MAX_PDEF_DIFF = Integer.parseInt(pvpSettings.getProperty("AntiFarmMaxPdefDiff", "300"));
			ANTI_FARM_PATK_DIFF_ENABLED = Boolean.parseBoolean(pvpSettings.getProperty("AntiFarmPatkDiff", "False"));
			ANTI_FARM_MAX_PATK_DIFF = Integer.parseInt(pvpSettings.getProperty("AntiFarmMaxPatkDiff", "300"));
			ANTI_FARM_PARTY_ENABLED = Boolean.parseBoolean(pvpSettings.getProperty("AntiFarmParty", "False"));
			ANTI_FARM_IP_ENABLED = Boolean.parseBoolean(pvpSettings.getProperty("AntiFarmIP", "False"));
			ANTI_FARM_SUMMON = Boolean.parseBoolean(pvpSettings.getProperty("AntiFarmSummon", "False"));
		}
		catch (Exception e)
		{
			LOGGER.error("Config : Failed to load " + filePath + " file.", e);
		}
	}
	
	// ============================================================
	
	public static int ALT_OLY_NUMBER_HEROS_EACH_CLASS;
	public static boolean ALT_OLY_LOG_FIGHTS;
	public static boolean ALT_OLY_SHOW_MONTHLY_WINNERS;
	public static boolean ALT_OLY_ANNOUNCE_GAMES;
	public static List<Integer> LIST_OLY_RESTRICTED_SKILLS = new ArrayList<>();
	public static boolean ALT_OLY_AUGMENT_ALLOW;
	public static int ALT_OLY_TELEPORT_COUNTDOWN;
	
	public static boolean ALLOW_DUALBOX_OLY;
	public static int ALT_OLY_ENCHANT_LIMIT;
	public static int ALT_OLY_START_TIME;
	public static int ALT_OLY_MIN;
	public static long ALT_OLY_CPERIOD;
	public static long ALT_OLY_BATTLE;
	
	public static long ALT_OLY_WPERIOD;
	public static long ALT_OLY_VPERIOD;
	public static int ALT_OLY_CLASSED;
	public static int ALT_OLY_NONCLASSED;
	public static int ALT_OLY_BATTLE_REWARD_ITEM;
	public static int ALT_OLY_CLASSED_RITEM_C;
	public static int ALT_OLY_NONCLASSED_RITEM_C;
	
	public static int ALT_OLY_GP_PER_POINT;
	public static int ALT_OLY_MIN_POINT_FOR_EXCH;
	public static int ALT_OLY_HERO_POINTS;
	public static String ALT_OLY_RESTRICTED_ITEMS;
	
	public static List<Integer> LIST_OLY_RESTRICTED_ITEMS = new ArrayList<>();
	public static boolean ALLOW_EVENTS_DURING_OLY;
	public static boolean ALT_OLY_RECHARGE_SKILLS;
	
	public static int ALT_OLY_COMP_RITEM;
	public static boolean REMOVE_SUMMON_AND_CUBIC_OLYMPIAD;
	
	public static boolean ALT_OLY_USE_CUSTOM_PERIOD_SETTINGS;
	public static OlympiadPeriod ALT_OLY_PERIOD;
	public static int ALT_OLY_PERIOD_MULTIPLIER;
	
	// ============================================================
	
	public static void loadOlympConfig()
	{
		String filePath = "config/head/olympiad.properties";
		
		try
		{
			L2Properties OLYMPSetting = new L2Properties(filePath);
			
			ALLOW_DUALBOX_OLY = Boolean.valueOf(OLYMPSetting.getProperty("AllowDualBoxInOly", "True"));
			ALT_OLY_ENCHANT_LIMIT = Integer.parseInt(OLYMPSetting.getProperty("AltOlyMaxEnchant", "-1"));
			ALT_OLY_START_TIME = Integer.parseInt(OLYMPSetting.getProperty("AltOlyStartTime", "18"));
			ALT_OLY_MIN = Integer.parseInt(OLYMPSetting.getProperty("AltOlyMin", "00"));
			ALT_OLY_CPERIOD = Long.parseLong(OLYMPSetting.getProperty("AltOlyCPeriod", "21600000"));
			ALT_OLY_BATTLE = Long.parseLong(OLYMPSetting.getProperty("AltOlyBattle", "360000"));
			ALT_OLY_WPERIOD = Long.parseLong(OLYMPSetting.getProperty("AltOlyWPeriod", "604800000"));
			ALT_OLY_VPERIOD = Long.parseLong(OLYMPSetting.getProperty("AltOlyVPeriod", "86400000"));
			ALT_OLY_CLASSED = Integer.parseInt(OLYMPSetting.getProperty("AltOlyClassedParticipants", "5"));
			ALT_OLY_NONCLASSED = Integer.parseInt(OLYMPSetting.getProperty("AltOlyNonClassedParticipants", "9"));
			ALT_OLY_BATTLE_REWARD_ITEM = Integer.parseInt(OLYMPSetting.getProperty("AltOlyBattleRewItem", "6651"));
			ALT_OLY_CLASSED_RITEM_C = Integer.parseInt(OLYMPSetting.getProperty("AltOlyClassedRewItemCount", "50"));
			ALT_OLY_NONCLASSED_RITEM_C = Integer.parseInt(OLYMPSetting.getProperty("AltOlyNonClassedRewItemCount", "30"));
			ALT_OLY_COMP_RITEM = Integer.parseInt(OLYMPSetting.getProperty("AltOlyCompRewItem", "6651"));
			ALT_OLY_GP_PER_POINT = Integer.parseInt(OLYMPSetting.getProperty("AltOlyGPPerPoint", "1000"));
			ALT_OLY_MIN_POINT_FOR_EXCH = Integer.parseInt(OLYMPSetting.getProperty("AltOlyMinPointForExchange", "50"));
			ALT_OLY_HERO_POINTS = Integer.parseInt(OLYMPSetting.getProperty("AltOlyHeroPoints", "100"));
			ALT_OLY_RESTRICTED_ITEMS = OLYMPSetting.getProperty("AltOlyRestrictedItems", "0");
			LIST_OLY_RESTRICTED_ITEMS = new ArrayList<>();
			for (final String id : ALT_OLY_RESTRICTED_ITEMS.split(","))
			{
				LIST_OLY_RESTRICTED_ITEMS.add(Integer.parseInt(id));
			}
			ALLOW_EVENTS_DURING_OLY = Boolean.parseBoolean(OLYMPSetting.getProperty("AllowEventsDuringOly", "False"));
			
			ALT_OLY_RECHARGE_SKILLS = Boolean.parseBoolean(OLYMPSetting.getProperty("AltOlyRechargeSkills", "False"));
			
			REMOVE_SUMMON_AND_CUBIC_OLYMPIAD = Boolean.parseBoolean(OLYMPSetting.getProperty("RemoveSummonAndCubicOlympiad", "True"));
			
			ALT_OLY_NUMBER_HEROS_EACH_CLASS = Integer.parseInt(OLYMPSetting.getProperty("AltOlyNumberHerosEachClass", "1"));
			ALT_OLY_LOG_FIGHTS = Boolean.parseBoolean(OLYMPSetting.getProperty("AlyOlyLogFights", "false"));
			ALT_OLY_SHOW_MONTHLY_WINNERS = Boolean.parseBoolean(OLYMPSetting.getProperty("AltOlyShowMonthlyWinners", "true"));
			ALT_OLY_ANNOUNCE_GAMES = Boolean.parseBoolean(OLYMPSetting.getProperty("AltOlyAnnounceGames", "true"));
			LIST_OLY_RESTRICTED_SKILLS = new ArrayList<>();
			for (final String id : OLYMPSetting.getProperty("AltOlyRestrictedSkills", "0").split(","))
			{
				LIST_OLY_RESTRICTED_SKILLS.add(Integer.parseInt(id));
			}
			ALT_OLY_AUGMENT_ALLOW = Boolean.parseBoolean(OLYMPSetting.getProperty("AltOlyAugmentAllow", "true"));
			ALT_OLY_TELEPORT_COUNTDOWN = Integer.parseInt(OLYMPSetting.getProperty("AltOlyTeleportCountDown", "120"));
			
			ALT_OLY_USE_CUSTOM_PERIOD_SETTINGS = Boolean.parseBoolean(OLYMPSetting.getProperty("AltOlyUseCustomPeriodSettings", "false"));
			ALT_OLY_PERIOD = OlympiadPeriod.valueOf(OLYMPSetting.getProperty("AltOlyPeriod", "MONTH"));
			ALT_OLY_PERIOD_MULTIPLIER = Integer.parseInt(OLYMPSetting.getProperty("AltOlyPeriodMultiplier", "1"));
			
		}
		catch (Exception e)
		{
			LOGGER.error("Config : Failed to load " + filePath + " file.", e);
		}
	}
	
	// ============================================================
	// Enchant map
	public static Map<Integer, Integer> NORMAL_WEAPON_ENCHANT_LEVEL = new HashMap<>();
	public static Map<Integer, Integer> BLESS_WEAPON_ENCHANT_LEVEL = new HashMap<>();
	public static Map<Integer, Integer> CRYSTAL_WEAPON_ENCHANT_LEVEL = new HashMap<>();
	
	public static Map<Integer, Integer> NORMAL_ARMOR_ENCHANT_LEVEL = new HashMap<>();
	public static Map<Integer, Integer> BLESS_ARMOR_ENCHANT_LEVEL = new HashMap<>();
	public static Map<Integer, Integer> CRYSTAL_ARMOR_ENCHANT_LEVEL = new HashMap<>();
	
	public static Map<Integer, Integer> NORMAL_JEWELRY_ENCHANT_LEVEL = new HashMap<>();
	public static Map<Integer, Integer> BLESS_JEWELRY_ENCHANT_LEVEL = new HashMap<>();
	public static Map<Integer, Integer> CRYSTAL_JEWELRY_ENCHANT_LEVEL = new HashMap<>();
	
	public static int ENCHANT_SAFE_MAX;
	public static int ENCHANT_SAFE_MAX_FULL;
	public static int ENCHANT_WEAPON_MAX;
	public static int ENCHANT_ARMOR_MAX;
	public static int ENCHANT_JEWELRY_MAX;
	
	public static int CRYSTAL_ENCHANT_MAX;
	public static int CRYSTAL_ENCHANT_MIN;
	
	public static boolean SCROLL_ENCHANT_STACKABLE;
	
	// Dwarf bonus
	public static boolean ENABLE_DWARF_ENCHANT_BONUS;
	public static int DWARF_ENCHANT_MIN_LEVEL;
	public static int DWARF_ENCHANT_BONUS;
	
	public static boolean LIFE_STONE_IS_STACKABLE;
	
	// Augment chance
	public static int AUGMENTATION_NG_SKILL_CHANCE;
	public static int AUGMENTATION_MID_SKILL_CHANCE;
	public static int AUGMENTATION_HIGH_SKILL_CHANCE;
	public static int AUGMENTATION_TOP_SKILL_CHANCE;
	public static int AUGMENTATION_BASESTAT_CHANCE;
	// Augment glow
	public static int AUGMENTATION_NG_GLOW_CHANCE;
	public static int AUGMENTATION_MID_GLOW_CHANCE;
	public static int AUGMENTATION_HIGH_GLOW_CHANCE;
	public static int AUGMENTATION_TOP_GLOW_CHANCE;
	
	public static boolean DELETE_AUGM_PASSIVE_ON_CHANGE;
	public static boolean DELETE_AUGM_ACTIVE_ON_CHANGE;
	
	// Enchant hero weapon
	public static boolean ENCHANT_HERO_WEAPON;
	// Soul crystal
	public static int SOUL_CRYSTAL_BREAK_CHANCE;
	public static int SOUL_CRYSTAL_LEVEL_CHANCE;
	public static int SOUL_CRYSTAL_MAX_LEVEL;
	// Count enchant
	public static int CUSTOM_ENCHANT_VALUE;
	public static int ENCHANT_LEVEL_WHEN_BLESSED_SCROLL_FAIL;
	
	public static int MAX_ENCHANT_LEVEL;
	
	// ============================================================
	public static void loadEnchantConfig()
	{
		String filePath = "config/head/enchant.properties";
		
		try
		{
			L2Properties ENCHANTSetting = new L2Properties(filePath);
			
			String[] propertySplit = ENCHANTSetting.getProperty("NormalWeaponEnchantLevel", "").split(";");
			for (final String readData : propertySplit)
			{
				final String[] writeData = readData.split(",");
				if (writeData.length != 2)
				{
					LOGGER.info("invalid config property");
				}
				else
				{
					try
					{
						NORMAL_WEAPON_ENCHANT_LEVEL.put(Integer.parseInt(writeData[0]), Integer.parseInt(writeData[1]));
					}
					catch (NumberFormatException e)
					{
						LOGGER.error("Config.loadEnchantConfig.NormalWeaponEnchantLevel : Failed to load ", e);
					}
				}
			}
			
			propertySplit = ENCHANTSetting.getProperty("BlessWeaponEnchantLevel", "").split(";");
			for (final String readData : propertySplit)
			{
				final String[] writeData = readData.split(",");
				if (writeData.length != 2)
				{
					LOGGER.info("invalid config property");
				}
				else
				{
					try
					{
						BLESS_WEAPON_ENCHANT_LEVEL.put(Integer.parseInt(writeData[0]), Integer.parseInt(writeData[1]));
					}
					catch (NumberFormatException e)
					{
						LOGGER.error("Config.loadEnchantConfig.BlessWeaponEnchantLevel : Failed to load ", e);
					}
				}
			}
			
			propertySplit = ENCHANTSetting.getProperty("CrystalWeaponEnchantLevel", "").split(";");
			for (final String readData : propertySplit)
			{
				final String[] writeData = readData.split(",");
				if (writeData.length != 2)
				{
					LOGGER.info("invalid config property");
				}
				else
				{
					try
					{
						CRYSTAL_WEAPON_ENCHANT_LEVEL.put(Integer.parseInt(writeData[0]), Integer.parseInt(writeData[1]));
					}
					catch (NumberFormatException e)
					{
						LOGGER.error("Config.loadEnchantConfig.CrystalWeaponEnchantLevel : Failed to load ", e);
					}
				}
			}
			
			propertySplit = ENCHANTSetting.getProperty("NormalArmorEnchantLevel", "").split(";");
			for (final String readData : propertySplit)
			{
				final String[] writeData = readData.split(",");
				if (writeData.length != 2)
				{
					LOGGER.info("invalid config property");
				}
				else
				{
					try
					{
						NORMAL_ARMOR_ENCHANT_LEVEL.put(Integer.parseInt(writeData[0]), Integer.parseInt(writeData[1]));
					}
					catch (NumberFormatException e)
					{
						LOGGER.error("Config.loadEnchantConfig.NormalArmorEnchantLevel : Failed to load ", e);
					}
				}
			}
			
			propertySplit = ENCHANTSetting.getProperty("BlessArmorEnchantLevel", "").split(";");
			for (final String readData : propertySplit)
			{
				final String[] writeData = readData.split(",");
				if (writeData.length != 2)
				{
					LOGGER.info("invalid config property");
				}
				else
				{
					try
					{
						BLESS_ARMOR_ENCHANT_LEVEL.put(Integer.parseInt(writeData[0]), Integer.parseInt(writeData[1]));
					}
					catch (NumberFormatException e)
					{
						LOGGER.error("Config.loadEnchantConfig.BlessArmorEnchantLevel : Failed to load ", e);
					}
				}
			}
			
			propertySplit = ENCHANTSetting.getProperty("CrystalArmorEnchantLevel", "").split(";");
			for (final String readData : propertySplit)
			{
				final String[] writeData = readData.split(",");
				if (writeData.length != 2)
				{
					LOGGER.info("invalid config property");
				}
				else
				{
					try
					{
						CRYSTAL_ARMOR_ENCHANT_LEVEL.put(Integer.parseInt(writeData[0]), Integer.parseInt(writeData[1]));
					}
					catch (NumberFormatException e)
					{
						LOGGER.error("Config.loadEnchantConfig.CrystalArmorEnchantLevel : Failed to load ", e);
					}
				}
			}
			
			propertySplit = ENCHANTSetting.getProperty("NormalJewelryEnchantLevel", "").split(";");
			for (final String readData : propertySplit)
			{
				final String[] writeData = readData.split(",");
				if (writeData.length != 2)
				{
					LOGGER.info("invalid config property");
				}
				else
				{
					try
					{
						NORMAL_JEWELRY_ENCHANT_LEVEL.put(Integer.parseInt(writeData[0]), Integer.parseInt(writeData[1]));
					}
					catch (NumberFormatException e)
					{
						LOGGER.error("Config.loadEnchantConfig.NormalJewelryEnchantLevel : Failed to load ", e);
					}
				}
			}
			
			propertySplit = ENCHANTSetting.getProperty("BlessJewelryEnchantLevel", "").split(";");
			for (final String readData : propertySplit)
			{
				final String[] writeData = readData.split(",");
				if (writeData.length != 2)
				{
					LOGGER.info("invalid config property");
				}
				else
				{
					try
					{
						BLESS_JEWELRY_ENCHANT_LEVEL.put(Integer.parseInt(writeData[0]), Integer.parseInt(writeData[1]));
					}
					catch (NumberFormatException e)
					{
						LOGGER.error("Config.loadEnchantConfig.BlessJewelryEnchantLevel : Failed to load ", e);
					}
				}
			}
			
			propertySplit = ENCHANTSetting.getProperty("CrystalJewelryEnchantLevel", "").split(";");
			for (final String readData : propertySplit)
			{
				final String[] writeData = readData.split(",");
				if (writeData.length != 2)
				{
					LOGGER.info("invalid config property");
				}
				else
				{
					try
					{
						CRYSTAL_JEWELRY_ENCHANT_LEVEL.put(Integer.parseInt(writeData[0]), Integer.parseInt(writeData[1]));
					}
					catch (NumberFormatException e)
					{
						LOGGER.error("Config.loadEnchantConfig.CrystalJewelryEnchantLevel : Failed to load ", e);
					}
				}
			}
			
			/** limit of safe enchant normal **/
			ENCHANT_SAFE_MAX = Integer.parseInt(ENCHANTSetting.getProperty("EnchantSafeMax", "3"));
			
			/** limit of safe enchant full **/
			ENCHANT_SAFE_MAX_FULL = Integer.parseInt(ENCHANTSetting.getProperty("EnchantSafeMaxFull", "4"));
			
			/** limit of max enchant **/
			ENCHANT_WEAPON_MAX = Integer.parseInt(ENCHANTSetting.getProperty("EnchantWeaponMax", "25"));
			ENCHANT_ARMOR_MAX = Integer.parseInt(ENCHANTSetting.getProperty("EnchantArmorMax", "25"));
			ENCHANT_JEWELRY_MAX = Integer.parseInt(ENCHANTSetting.getProperty("EnchantJewelryMax", "25"));
			
			/** CRYSTAL SCROLL enchant limits **/
			CRYSTAL_ENCHANT_MIN = Integer.parseInt(ENCHANTSetting.getProperty("CrystalEnchantMin", "20"));
			CRYSTAL_ENCHANT_MAX = Integer.parseInt(ENCHANTSetting.getProperty("CrystalEnchantMax", "0"));
			
			SCROLL_ENCHANT_STACKABLE = Boolean.parseBoolean(ENCHANTSetting.getProperty("ScrollEnchantStackable", "False"));
			
			/** bonus for dwarf **/
			ENABLE_DWARF_ENCHANT_BONUS = Boolean.parseBoolean(ENCHANTSetting.getProperty("EnableDwarfEnchantBonus", "False"));
			DWARF_ENCHANT_MIN_LEVEL = Integer.parseInt(ENCHANTSetting.getProperty("DwarfEnchantMinLevel", "80"));
			DWARF_ENCHANT_BONUS = Integer.parseInt(ENCHANTSetting.getProperty("DwarfEnchantBonus", "15"));
			
			LIFE_STONE_IS_STACKABLE = Boolean.parseBoolean(ENCHANTSetting.getProperty("LifeStoneIsStackable", "False"));
			
			/** augmentation chance **/
			AUGMENTATION_NG_SKILL_CHANCE = Integer.parseInt(ENCHANTSetting.getProperty("AugmentationNGSkillChance", "15"));
			AUGMENTATION_MID_SKILL_CHANCE = Integer.parseInt(ENCHANTSetting.getProperty("AugmentationMidSkillChance", "30"));
			AUGMENTATION_HIGH_SKILL_CHANCE = Integer.parseInt(ENCHANTSetting.getProperty("AugmentationHighSkillChance", "45"));
			AUGMENTATION_TOP_SKILL_CHANCE = Integer.parseInt(ENCHANTSetting.getProperty("AugmentationTopSkillChance", "60"));
			AUGMENTATION_BASESTAT_CHANCE = Integer.parseInt(ENCHANTSetting.getProperty("AugmentationBaseStatChance", "1"));
			
			/** augmentation glow **/
			AUGMENTATION_NG_GLOW_CHANCE = Integer.parseInt(ENCHANTSetting.getProperty("AugmentationNGGlowChance", "0"));
			AUGMENTATION_MID_GLOW_CHANCE = Integer.parseInt(ENCHANTSetting.getProperty("AugmentationMidGlowChance", "40"));
			AUGMENTATION_HIGH_GLOW_CHANCE = Integer.parseInt(ENCHANTSetting.getProperty("AugmentationHighGlowChance", "70"));
			AUGMENTATION_TOP_GLOW_CHANCE = Integer.parseInt(ENCHANTSetting.getProperty("AugmentationTopGlowChance", "100"));
			
			/** augmentation configs **/
			DELETE_AUGM_PASSIVE_ON_CHANGE = Boolean.parseBoolean(ENCHANTSetting.getProperty("DeleteAgmentPassiveEffectOnChangeWep", "true"));
			DELETE_AUGM_ACTIVE_ON_CHANGE = Boolean.parseBoolean(ENCHANTSetting.getProperty("DeleteAgmentActiveEffectOnChangeWep", "true"));
			
			/** enchant hero weapon **/
			ENCHANT_HERO_WEAPON = Boolean.parseBoolean(ENCHANTSetting.getProperty("EnableEnchantHeroWeapons", "False"));
			
			/** soul crystal **/
			SOUL_CRYSTAL_BREAK_CHANCE = Integer.parseInt(ENCHANTSetting.getProperty("SoulCrystalBreakChance", "10"));
			SOUL_CRYSTAL_LEVEL_CHANCE = Integer.parseInt(ENCHANTSetting.getProperty("SoulCrystalLevelChance", "32"));
			SOUL_CRYSTAL_MAX_LEVEL = Integer.parseInt(ENCHANTSetting.getProperty("SoulCrystalMaxLevel", "13"));
			
			/** count enchant **/
			CUSTOM_ENCHANT_VALUE = Integer.parseInt(ENCHANTSetting.getProperty("CustomEnchantValue", "1"));
			
			ENCHANT_LEVEL_WHEN_BLESSED_SCROLL_FAIL = Integer.valueOf(ENCHANTSetting.getProperty("EnchantLevelWhenBlessedScrollFail", "0"));
			
			MAX_ENCHANT_LEVEL = Integer.parseInt(ENCHANTSetting.getProperty("MaxEnchantLevel", "0"));
			
		}
		catch (Exception e)
		{
			LOGGER.error("Config : Failed to load " + filePath + " file.", e);
		}
	}
	
	// --------------------------------------------------
	// FloodProtector Settings
	// --------------------------------------------------
	public static FloodProtectorConfig FLOOD_PROTECTOR_USE_ITEM;
	public static FloodProtectorConfig FLOOD_PROTECTOR_ROLL_DICE;
	public static FloodProtectorConfig FLOOD_PROTECTOR_FIREWORK;
	public static FloodProtectorConfig FLOOD_PROTECTOR_ITEM_PET_SUMMON;
	public static FloodProtectorConfig FLOOD_PROTECTOR_HERO_VOICE;
	public static FloodProtectorConfig FLOOD_PROTECTOR_GLOBAL_CHAT;
	public static FloodProtectorConfig FLOOD_PROTECTOR_SUBCLASS;
	public static FloodProtectorConfig FLOOD_PROTECTOR_DROP_ITEM;
	public static FloodProtectorConfig FLOOD_PROTECTOR_SERVER_BYPASS;
	public static FloodProtectorConfig FLOOD_PROTECTOR_MULTISELL;
	public static FloodProtectorConfig FLOOD_PROTECTOR_TRANSACTION;
	public static FloodProtectorConfig FLOOD_PROTECTOR_MANUFACTURE;
	public static FloodProtectorConfig FLOOD_PROTECTOR_MANOR;
	public static FloodProtectorConfig FLOOD_PROTECTOR_CHARACTER_SELECT;
	
	public static FloodProtectorConfig FLOOD_PROTECTOR_UNKNOWN_PACKETS;
	public static FloodProtectorConfig FLOOD_PROTECTOR_PARTY_INVITATION;
	public static FloodProtectorConfig FLOOD_PROTECTOR_SAY_ACTION;
	public static FloodProtectorConfig FLOOD_PROTECTOR_MOVE_ACTION;
	public static FloodProtectorConfig FLOOD_PROTECTOR_GENERIC_ACTION;
	public static FloodProtectorConfig FLOOD_PROTECTOR_MACRO;
	public static FloodProtectorConfig FLOOD_PROTECTOR_POTION;
	
	// ============================================================
	public static void loadFloodConfig()
	{
		String filePath = "config/protected/flood.properties";
		
		try
		{
			FLOOD_PROTECTOR_USE_ITEM = new FloodProtectorConfig("UseItemFloodProtector");
			FLOOD_PROTECTOR_ROLL_DICE = new FloodProtectorConfig("RollDiceFloodProtector");
			FLOOD_PROTECTOR_FIREWORK = new FloodProtectorConfig("FireworkFloodProtector");
			FLOOD_PROTECTOR_ITEM_PET_SUMMON = new FloodProtectorConfig("ItemPetSummonFloodProtector");
			FLOOD_PROTECTOR_HERO_VOICE = new FloodProtectorConfig("HeroVoiceFloodProtector");
			FLOOD_PROTECTOR_GLOBAL_CHAT = new FloodProtectorConfig("GlobalChatFloodProtector");
			FLOOD_PROTECTOR_SUBCLASS = new FloodProtectorConfig("SubclassFloodProtector");
			FLOOD_PROTECTOR_DROP_ITEM = new FloodProtectorConfig("DropItemFloodProtector");
			FLOOD_PROTECTOR_SERVER_BYPASS = new FloodProtectorConfig("ServerBypassFloodProtector");
			FLOOD_PROTECTOR_MULTISELL = new FloodProtectorConfig("MultiSellFloodProtector");
			FLOOD_PROTECTOR_TRANSACTION = new FloodProtectorConfig("TransactionFloodProtector");
			FLOOD_PROTECTOR_MANUFACTURE = new FloodProtectorConfig("ManufactureFloodProtector");
			FLOOD_PROTECTOR_MANOR = new FloodProtectorConfig("ManorFloodProtector");
			FLOOD_PROTECTOR_CHARACTER_SELECT = new FloodProtectorConfig("CharacterSelectFloodProtector");
			
			FLOOD_PROTECTOR_UNKNOWN_PACKETS = new FloodProtectorConfig("UnknownPacketsFloodProtector");
			FLOOD_PROTECTOR_PARTY_INVITATION = new FloodProtectorConfig("PartyInvitationFloodProtector");
			FLOOD_PROTECTOR_SAY_ACTION = new FloodProtectorConfig("SayActionFloodProtector");
			FLOOD_PROTECTOR_MOVE_ACTION = new FloodProtectorConfig("MoveActionFloodProtector");
			FLOOD_PROTECTOR_GENERIC_ACTION = new FloodProtectorConfig("GenericActionFloodProtector", true);
			FLOOD_PROTECTOR_MACRO = new FloodProtectorConfig("MacroFloodProtector", true);
			FLOOD_PROTECTOR_POTION = new FloodProtectorConfig("PotionFloodProtector", true);
			
			// Load FloodProtector L2Properties file
			try
			{
				L2Properties security = new L2Properties(filePath);
				
				loadFloodProtectorConfigs(security);
				
			}
			catch (Exception e)
			{
				LOGGER.error("Config : Failed to load " + filePath + " file.", e);
			}
			
		}
		catch (Exception e)
		{
			LOGGER.error("Config : Failed to load " + filePath + " file.", e);
		}
	}
	
	/**
	 * Loads flood protector configurations.
	 * @param properties
	 */
	private static void loadFloodProtectorConfigs(final L2Properties properties)
	{
		loadFloodProtectorConfig(properties, FLOOD_PROTECTOR_USE_ITEM, "UseItem", "1");
		loadFloodProtectorConfig(properties, FLOOD_PROTECTOR_ROLL_DICE, "RollDice", "42");
		loadFloodProtectorConfig(properties, FLOOD_PROTECTOR_FIREWORK, "Firework", "42");
		loadFloodProtectorConfig(properties, FLOOD_PROTECTOR_ITEM_PET_SUMMON, "ItemPetSummon", "16");
		loadFloodProtectorConfig(properties, FLOOD_PROTECTOR_HERO_VOICE, "HeroVoice", "100");
		loadFloodProtectorConfig(properties, FLOOD_PROTECTOR_GLOBAL_CHAT, "GlobalChat", "5");
		loadFloodProtectorConfig(properties, FLOOD_PROTECTOR_SUBCLASS, "Subclass", "20");
		loadFloodProtectorConfig(properties, FLOOD_PROTECTOR_DROP_ITEM, "DropItem", "10");
		loadFloodProtectorConfig(properties, FLOOD_PROTECTOR_SERVER_BYPASS, "ServerBypass", "5");
		loadFloodProtectorConfig(properties, FLOOD_PROTECTOR_MULTISELL, "MultiSell", "1");
		loadFloodProtectorConfig(properties, FLOOD_PROTECTOR_TRANSACTION, "Transaction", "10");
		loadFloodProtectorConfig(properties, FLOOD_PROTECTOR_MANUFACTURE, "Manufacture", "3");
		loadFloodProtectorConfig(properties, FLOOD_PROTECTOR_MANOR, "Manor", "30");
		loadFloodProtectorConfig(properties, FLOOD_PROTECTOR_CHARACTER_SELECT, "CharacterSelect", "30");
		
		loadFloodProtectorConfig(properties, FLOOD_PROTECTOR_UNKNOWN_PACKETS, "UnknownPackets", "5");
		loadFloodProtectorConfig(properties, FLOOD_PROTECTOR_PARTY_INVITATION, "PartyInvitation", "30");
		loadFloodProtectorConfig(properties, FLOOD_PROTECTOR_SAY_ACTION, "SayAction", "100");
		loadFloodProtectorConfig(properties, FLOOD_PROTECTOR_MOVE_ACTION, "MoveAction", "30");
		
		loadFloodProtectorConfig(properties, FLOOD_PROTECTOR_GENERIC_ACTION, "GenericAction", "5");
		loadFloodProtectorConfig(properties, FLOOD_PROTECTOR_MACRO, "Macro", "10");
		loadFloodProtectorConfig(properties, FLOOD_PROTECTOR_POTION, "Potion", "4");
	}
	
	/**
	 * Loads single flood protector configuration.
	 * @param properties      L2Properties file reader
	 * @param config          flood protector configuration instance
	 * @param configString    flood protector configuration string that determines for which flood protector configuration should be read
	 * @param defaultInterval default flood protector interval
	 */
	private static void loadFloodProtectorConfig(final L2Properties properties, final FloodProtectorConfig config, final String configString, final String defaultInterval)
	{
		config.FLOOD_PROTECTION_INTERVAL = Float.parseFloat(properties.getProperty(StringUtil.concat("FloodProtector", configString, "Interval"), defaultInterval));
		config.LOG_FLOODING = Boolean.parseBoolean(properties.getProperty(StringUtil.concat("FloodProtector", configString, "LogFlooding"), "False"));
		config.PUNISHMENT_LIMIT = Integer.parseInt(properties.getProperty(StringUtil.concat("FloodProtector", configString, "PunishmentLimit"), "0"));
		config.PUNISHMENT_TYPE = properties.getProperty(StringUtil.concat("FloodProtector", configString, "PunishmentType"), "none");
		config.PUNISHMENT_TIME = Integer.parseInt(properties.getProperty(StringUtil.concat("FloodProtector", configString, "PunishmentTime"), "0"));
	}
	
	// ============================================================
	public static boolean ENABLE_UNK_PACKET_PROTECTION;
	public static int MAX_UNKNOWN_PACKETS;
	public static int UNKNOWN_PACKETS_PUNiSHMENT;
	public static boolean DEBUG_UNKNOWN_PACKETS;
	
	public static boolean DEBUG_PACKETS;
	
	// ============================================================
	public static void loadPacketConfig()
	{
		String filePath = "config/protected/packets.properties";
		
		try
		{
			L2Properties PacketSetting = new L2Properties(filePath);
			
			ENABLE_UNK_PACKET_PROTECTION = Boolean.parseBoolean(PacketSetting.getProperty("UnknownPacketProtection", "true"));
			MAX_UNKNOWN_PACKETS = Integer.parseInt(PacketSetting.getProperty("UnknownPacketsBeforeBan", "5"));
			UNKNOWN_PACKETS_PUNiSHMENT = Integer.parseInt(PacketSetting.getProperty("UnknownPacketsPunishment", "2"));
			DEBUG_PACKETS = Boolean.parseBoolean(PacketSetting.getProperty("DebugPackets", "false"));
			DEBUG_UNKNOWN_PACKETS = Boolean.parseBoolean(PacketSetting.getProperty("UnknownDebugPackets", "false"));
			
		}
		catch (Exception e)
		{
			LOGGER.error("Config : Failed to load " + filePath + " file.", e);
		}
	}
	
	// ============================================================
	public static boolean CHECK_SKILLS_ON_ENTER;
	public static List<Integer> ALLOWED_SKILLS_LIST;
	public static boolean CHECK_NAME_ON_LOGIN;
	public static boolean L2WALKER_PROTEC;
	
	public static int DUALBOX_AMOUNT_ALLOWED;
	
	// ============================================================
	public static void loadPOtherConfig()
	{
		String filePath = "config/protected/other.properties";
		
		try
		{
			L2Properties POtherSetting = new L2Properties(filePath);
			
			CHECK_NAME_ON_LOGIN = Boolean.parseBoolean(POtherSetting.getProperty("CheckNameOnEnter", "True"));
			CHECK_SKILLS_ON_ENTER = Boolean.parseBoolean(POtherSetting.getProperty("CheckSkillsOnEnter", "True"));
			
			String ALLOWED_SKILLS = POtherSetting.getProperty("AllowedSkills", "541,542,543,544,545,546,547,548,549,550,551,552,553,554,555,556,557,558,617,618,619");
			
			ALLOWED_SKILLS_LIST = new ArrayList<>();
			for (String id : ALLOWED_SKILLS.split(","))
			{
				ALLOWED_SKILLS_LIST.add(Integer.parseInt(id.trim()));
			}
			
			/** l2walker protection **/
			L2WALKER_PROTEC = Boolean.parseBoolean(POtherSetting.getProperty("L2WalkerProtection", "False"));
			
			DUALBOX_AMOUNT_ALLOWED = Integer.parseInt(POtherSetting.getProperty("DualBoxAmountAllowed", "99"));
		}
		catch (Exception e)
		{
			LOGGER.error("Config : Failed to load " + filePath + " file.", e);
		}
	}
	
	// ============================================================
	public static int BLOW_ATTACK_FRONT;
	public static int BLOW_ATTACK_SIDE;
	public static int BLOW_ATTACK_BEHIND;
	
	public static int BACKSTAB_ATTACK_FRONT;
	public static int BACKSTAB_ATTACK_SIDE;
	public static int BACKSTAB_ATTACK_BEHIND;
	
	public static int MAX_PATK_SPEED;
	public static int MAX_MATK_SPEED;
	
	public static int MAX_PCRIT_RATE;
	public static int MAX_MCRIT_RATE;
	public static float MCRIT_RATE_MUL;
	
	public static int RUN_SPD_BOOST;
	public static int MAX_RUN_SPEED;
	
	public static float ALT_MAGES_PHYSICAL_DAMAGE_MULTI;
	public static float ALT_MAGES_MAGICAL_DAMAGE_MULTI;
	public static float ALT_FIGHTERS_PHYSICAL_DAMAGE_MULTI;
	public static float ALT_FIGHTERS_MAGICAL_DAMAGE_MULTI;
	public static float ALT_PETS_PHYSICAL_DAMAGE_MULTI;
	public static float ALT_PETS_MAGICAL_DAMAGE_MULTI;
	public static float ALT_NPC_PHYSICAL_DAMAGE_MULTI;
	public static float ALT_NPC_MAGICAL_DAMAGE_MULTI;
	// Alternative damage for dagger skills VS heavy
	public static float ALT_DAGGER_DMG_VS_HEAVY;
	// Alternative damage for dagger skills VS robe
	public static float ALT_DAGGER_DMG_VS_ROBE;
	// Alternative damage for dagger skills VS light
	public static float ALT_DAGGER_DMG_VS_LIGHT;
	
	public static boolean ALLOW_RAID_LETHAL, ALLOW_LETHAL_PROTECTION_MOBS;
	
	public static String LETHAL_PROTECTED_MOBS;
	public static List<Integer> LIST_LETHAL_PROTECTED_MOBS = new ArrayList<>();
	
	public static float MAGIC_CRITICAL_POWER;
	
	public static float STUN_CHANCE_MODIFIER;
	public static float BLEED_CHANCE_MODIFIER;
	public static float POISON_CHANCE_MODIFIER;
	public static float PARALYZE_CHANCE_MODIFIER;
	public static float ROOT_CHANCE_MODIFIER;
	public static float SLEEP_CHANCE_MODIFIER;
	public static float FEAR_CHANCE_MODIFIER;
	public static float CONFUSION_CHANCE_MODIFIER;
	public static float DEBUFF_CHANCE_MODIFIER;
	public static float BUFF_CHANCE_MODIFIER;
	public static boolean SEND_SKILLS_CHANCE_TO_PLAYERS;
	
	/* Remove equip during subclass change */
	public static boolean REMOVE_WEAPON_SUBCLASS;
	public static boolean REMOVE_CHEST_SUBCLASS;
	public static boolean REMOVE_LEG_SUBCLASS;
	
	public static boolean ENABLE_CLASS_DAMAGES;
	public static boolean ENABLE_CLASS_DAMAGES_IN_OLY;
	public static boolean ENABLE_CLASS_DAMAGES_LOGGER;
	
	public static boolean ALT_RAIDS_STATS_BONUS;
	
	// ============================================================
	public static void loadPHYSICSConfig()
	{
		String filePath = "config/functions/physics.properties";
		
		try
		{
			L2Properties physicSettings = new L2Properties(filePath);
			
			ENABLE_CLASS_DAMAGES = Boolean.parseBoolean(physicSettings.getProperty("EnableClassDamagesSettings", "true"));
			ENABLE_CLASS_DAMAGES_IN_OLY = Boolean.parseBoolean(physicSettings.getProperty("EnableClassDamagesSettingsInOly", "true"));
			ENABLE_CLASS_DAMAGES_LOGGER = Boolean.parseBoolean(physicSettings.getProperty("EnableClassDamagesLogger", "true"));
			
			BLOW_ATTACK_FRONT = Integer.parseInt(physicSettings.getProperty("BlowAttackFront", "50"));
			BLOW_ATTACK_SIDE = Integer.parseInt(physicSettings.getProperty("BlowAttackSide", "60"));
			BLOW_ATTACK_BEHIND = Integer.parseInt(physicSettings.getProperty("BlowAttackBehind", "70"));
			
			BACKSTAB_ATTACK_FRONT = Integer.parseInt(physicSettings.getProperty("BackstabAttackFront", "0"));
			BACKSTAB_ATTACK_SIDE = Integer.parseInt(physicSettings.getProperty("BackstabAttackSide", "0"));
			BACKSTAB_ATTACK_BEHIND = Integer.parseInt(physicSettings.getProperty("BackstabAttackBehind", "70"));
			
			// Max patk speed and matk speed
			MAX_PATK_SPEED = Integer.parseInt(physicSettings.getProperty("MaxPAtkSpeed", "1500"));
			MAX_MATK_SPEED = Integer.parseInt(physicSettings.getProperty("MaxMAtkSpeed", "1999"));
			
			if (MAX_PATK_SPEED < 1)
			{
				MAX_PATK_SPEED = Integer.MAX_VALUE;
			}
			
			if (MAX_MATK_SPEED < 1)
			{
				MAX_MATK_SPEED = Integer.MAX_VALUE;
			}
			
			MAX_PCRIT_RATE = Integer.parseInt(physicSettings.getProperty("MaxPCritRate", "500"));
			MAX_MCRIT_RATE = Integer.parseInt(physicSettings.getProperty("MaxMCritRate", "300"));
			MCRIT_RATE_MUL = Float.parseFloat(physicSettings.getProperty("McritMulDif", "1"));
			
			MAGIC_CRITICAL_POWER = Float.parseFloat(physicSettings.getProperty("MagicCriticalPower", "3.0"));
			
			STUN_CHANCE_MODIFIER = Float.parseFloat(physicSettings.getProperty("StunChanceModifier", "1.0"));
			BLEED_CHANCE_MODIFIER = Float.parseFloat(physicSettings.getProperty("BleedChanceModifier", "1.0"));
			POISON_CHANCE_MODIFIER = Float.parseFloat(physicSettings.getProperty("PoisonChanceModifier", "1.0"));
			PARALYZE_CHANCE_MODIFIER = Float.parseFloat(physicSettings.getProperty("ParalyzeChanceModifier", "1.0"));
			ROOT_CHANCE_MODIFIER = Float.parseFloat(physicSettings.getProperty("RootChanceModifier", "1.0"));
			SLEEP_CHANCE_MODIFIER = Float.parseFloat(physicSettings.getProperty("SleepChanceModifier", "1.0"));
			FEAR_CHANCE_MODIFIER = Float.parseFloat(physicSettings.getProperty("FearChanceModifier", "1.0"));
			CONFUSION_CHANCE_MODIFIER = Float.parseFloat(physicSettings.getProperty("ConfusionChanceModifier", "1.0"));
			DEBUFF_CHANCE_MODIFIER = Float.parseFloat(physicSettings.getProperty("DebuffChanceModifier", "1.0"));
			BUFF_CHANCE_MODIFIER = Float.parseFloat(physicSettings.getProperty("BuffChanceModifier", "1.0"));
			
			ALT_MAGES_PHYSICAL_DAMAGE_MULTI = Float.parseFloat(physicSettings.getProperty("AltPDamageMages", "1.00"));
			ALT_MAGES_MAGICAL_DAMAGE_MULTI = Float.parseFloat(physicSettings.getProperty("AltMDamageMages", "1.00"));
			ALT_FIGHTERS_PHYSICAL_DAMAGE_MULTI = Float.parseFloat(physicSettings.getProperty("AltPDamageFighters", "1.00"));
			ALT_FIGHTERS_MAGICAL_DAMAGE_MULTI = Float.parseFloat(physicSettings.getProperty("AltMDamageFighters", "1.00"));
			ALT_PETS_PHYSICAL_DAMAGE_MULTI = Float.parseFloat(physicSettings.getProperty("AltPDamagePets", "1.00"));
			ALT_PETS_MAGICAL_DAMAGE_MULTI = Float.parseFloat(physicSettings.getProperty("AltMDamagePets", "1.00"));
			ALT_NPC_PHYSICAL_DAMAGE_MULTI = Float.parseFloat(physicSettings.getProperty("AltPDamageNpc", "1.00"));
			ALT_NPC_MAGICAL_DAMAGE_MULTI = Float.parseFloat(physicSettings.getProperty("AltMDamageNpc", "1.00"));
			ALT_DAGGER_DMG_VS_HEAVY = Float.parseFloat(physicSettings.getProperty("DaggerVSHeavy", "2.50"));
			ALT_DAGGER_DMG_VS_ROBE = Float.parseFloat(physicSettings.getProperty("DaggerVSRobe", "1.80"));
			ALT_DAGGER_DMG_VS_LIGHT = Float.parseFloat(physicSettings.getProperty("DaggerVSLight", "2.00"));
			RUN_SPD_BOOST = Integer.parseInt(physicSettings.getProperty("RunSpeedBoost", "0"));
			MAX_RUN_SPEED = Integer.parseInt(physicSettings.getProperty("MaxRunSpeed", "250"));
			
			ALLOW_RAID_LETHAL = Boolean.parseBoolean(physicSettings.getProperty("AllowLethalOnRaids", "False"));
			
			ALLOW_LETHAL_PROTECTION_MOBS = Boolean.parseBoolean(physicSettings.getProperty("AllowLethalProtectionMobs", "False"));
			
			LETHAL_PROTECTED_MOBS = physicSettings.getProperty("LethalProtectedMobs", "");
			
			LIST_LETHAL_PROTECTED_MOBS = new ArrayList<>();
			for (final String id : LETHAL_PROTECTED_MOBS.split(","))
			{
				LIST_LETHAL_PROTECTED_MOBS.add(Integer.parseInt(id));
			}
			
			SEND_SKILLS_CHANCE_TO_PLAYERS = Boolean.parseBoolean(physicSettings.getProperty("SendSkillsChanceToPlayers", "False"));
			
			/* Remove equip during subclass change */
			REMOVE_WEAPON_SUBCLASS = Boolean.parseBoolean(physicSettings.getProperty("RemoveWeaponSubclass", "False"));
			REMOVE_CHEST_SUBCLASS = Boolean.parseBoolean(physicSettings.getProperty("RemoveChestSubclass", "False"));
			REMOVE_LEG_SUBCLASS = Boolean.parseBoolean(physicSettings.getProperty("RemoveLegSubclass", "False"));
			
			DISABLE_BOW_CLASSES_STRING = physicSettings.getProperty("DisableBowForClasses", "");
			DISABLE_BOW_CLASSES = new ArrayList<>();
			for (final String class_id : DISABLE_BOW_CLASSES_STRING.split(","))
			{
				if (!class_id.equals(""))
				{
					DISABLE_BOW_CLASSES.add(Integer.parseInt(class_id));
				}
			}
		}
		catch (Exception e)
		{
			LOGGER.error("Config : Failed to load " + filePath + " file.", e);
		}
	}
	
	// ============================================================
	public static int GEODATA;
	public static boolean GEODATA_CELLFINDING;
	public static boolean ALLOW_PLAYERS_PATHNODE;
	public static boolean FORCE_GEODATA;
	
	public static enum CorrectSpawnsZ
	{
		TOWN,
		MONSTER,
		ALL,
		NONE
	}
	
	public static CorrectSpawnsZ GEO_CORRECT_Z;
	
	public static boolean ACCEPT_GEOEDITOR_CONN;
	public static int GEOEDITOR_PORT;
	
	public static int WORLD_SIZE_MIN_X;
	public static int WORLD_SIZE_MAX_X;
	public static int WORLD_SIZE_MIN_Y;
	public static int WORLD_SIZE_MAX_Y;
	public static int WORLD_SIZE_MIN_Z;
	public static int WORLD_SIZE_MAX_Z;
	
	public static int COORD_SYNCHRONIZE;
	
	public static boolean FALL_DAMAGE;
	public static boolean ALLOW_WATER;
	
	// ============================================================
	public static void loadgeodataConfig()
	{
		String filePath = "config/head/geodata.properties";
		
		try
		{
			L2Properties geodataSetting = new L2Properties(filePath);
			
			GEODATA = Integer.parseInt(geodataSetting.getProperty("GeoData", "0"));
			GEODATA_CELLFINDING = Boolean.parseBoolean(geodataSetting.getProperty("CellPathFinding", "False"));
			
			ALLOW_PLAYERS_PATHNODE = Boolean.parseBoolean(geodataSetting.getProperty("AllowPlayersPathnode", "False"));
			
			FORCE_GEODATA = Boolean.parseBoolean(geodataSetting.getProperty("ForceGeoData", "True"));
			final String correctZ = geodataSetting.getProperty("GeoCorrectZ", "ALL");
			GEO_CORRECT_Z = CorrectSpawnsZ.valueOf(correctZ.toUpperCase());
			
			ACCEPT_GEOEDITOR_CONN = Boolean.parseBoolean(geodataSetting.getProperty("AcceptGeoeditorConn", "False"));
			GEOEDITOR_PORT = Integer.parseInt(geodataSetting.getProperty("GeoEditorPort", "9011"));
			
			WORLD_SIZE_MIN_X = Integer.parseInt(geodataSetting.getProperty("WorldSizeMinX", "-131072"));
			WORLD_SIZE_MAX_X = Integer.parseInt(geodataSetting.getProperty("WorldSizeMaxX", "228608"));
			WORLD_SIZE_MIN_Y = Integer.parseInt(geodataSetting.getProperty("WorldSizeMinY", "-262144"));
			WORLD_SIZE_MAX_Y = Integer.parseInt(geodataSetting.getProperty("WorldSizeMaxY", "262144"));
			WORLD_SIZE_MIN_Z = Integer.parseInt(geodataSetting.getProperty("WorldSizeMinZ", "-15000"));
			WORLD_SIZE_MAX_Z = Integer.parseInt(geodataSetting.getProperty("WorldSizeMaxZ", "15000"));
			
			COORD_SYNCHRONIZE = Integer.valueOf(geodataSetting.getProperty("CoordSynchronize", "-1"));
			
			FALL_DAMAGE = Boolean.parseBoolean(geodataSetting.getProperty("FallDamage", "False"));
			ALLOW_WATER = Boolean.valueOf(geodataSetting.getProperty("AllowWater", "False"));
		}
		catch (Exception e)
		{
			LOGGER.error("Config : Failed to load " + filePath + " file.", e);
		}
		
	}
	
	// ============================================================
	public static boolean ANNOUNCE_TO_ALL_SPAWN_RB;
	public static boolean ALLOW_RAID_BOSS_PETRIFIED;
	public static boolean AUTO_LOOT_BOSS;
	public static double RAID_HP_REGEN_MULTIPLIER;
	public static double RAID_MP_REGEN_MULTIPLIER;
	public static double RAID_P_DEFENSE_MULTIPLIER;
	public static double RAID_M_DEFENSE_MULTIPLIER;
	public static double RAID_MINION_RESPAWN_TIMER;
	public static float RAID_MIN_RESPAWN_MULTIPLIER;
	public static float RAID_MAX_RESPAWN_MULTIPLIER;
	public static boolean PLAYERS_CAN_HEAL_RB;
	public static int RBLOCKRAGE;
	
	public static HashMap<Integer, Integer> RBS_SPECIFIC_LOCK_RAGE;
	
	public static boolean ALLOW_DIRECT_TP_TO_BOSS_ROOM;
	public static int RAID_RANKING_1ST;
	public static int RAID_RANKING_2ND;
	public static int RAID_RANKING_3RD;
	public static int RAID_RANKING_4TH;
	public static int RAID_RANKING_5TH;
	public static int RAID_RANKING_6TH;
	public static int RAID_RANKING_7TH;
	public static int RAID_RANKING_8TH;
	public static int RAID_RANKING_9TH;
	public static int RAID_RANKING_10TH;
	public static int RAID_RANKING_UP_TO_50TH;
	public static int RAID_RANKING_UP_TO_100TH;
	public static List<Integer> RAID_INFO_IDS_LIST;
	public static boolean ANTHARAS_OLD;
	public static int ANTHARAS_CLOSE;
	public static int ANTHARAS_DESPAWN_TIME;
	public static int ANTHARAS_RESP_FIRST;
	public static int ANTHARAS_RESP_SECOND;
	public static int ANTHARAS_WAIT_TIME;
	public static float ANTHARAS_POWER_MULTIPLIER;
	
	public static int BAIUM_SLEEP;
	public static int BAIUM_RESP_FIRST;
	public static int BAIUM_RESP_SECOND;
	public static float BAIUM_POWER_MULTIPLIER;
	
	public static int CORE_RESP_MINION;
	public static int CORE_RESP_FIRST;
	public static int CORE_RESP_SECOND;
	public static int CORE_LEVEL;
	public static int CORE_RING_CHANCE;
	public static float CORE_POWER_MULTIPLIER;
	
	public static int QA_RESP_NURSE;
	public static int QA_RESP_ROYAL;
	public static int QA_RESP_FIRST;
	public static int QA_RESP_SECOND;
	public static int QA_LEVEL;
	public static int QA_RING_CHANCE;
	public static float QA_POWER_MULTIPLIER;
	
	public static float LEVEL_DIFF_MULTIPLIER_MINION;
	
	public static int HPH_FIXINTERVALOFHALTER;
	public static int HPH_RANDOMINTERVALOFHALTER;
	public static int HPH_APPTIMEOFHALTER;
	public static int HPH_ACTIVITYTIMEOFHALTER;
	public static int HPH_FIGHTTIMEOFHALTER;
	public static int HPH_CALLROYALGUARDHELPERCOUNT;
	public static int HPH_CALLROYALGUARDHELPERINTERVAL;
	public static int HPH_INTERVALOFDOOROFALTER;
	public static int HPH_TIMEOFLOCKUPDOOROFALTAR;
	
	public static int ZAKEN_RESP_FIRST;
	public static int ZAKEN_RESP_SECOND;
	public static int ZAKEN_LEVEL;
	public static int ZAKEN_EARRING_CHANCE;
	public static float ZAKEN_POWER_MULTIPLIER;
	
	public static int ORFEN_RESP_FIRST;
	public static int ORFEN_RESP_SECOND;
	public static int ORFEN_LEVEL;
	public static int ORFEN_EARRING_CHANCE;
	public static float ORFEN_POWER_MULTIPLIER;
	
	public static int VALAKAS_RESP_FIRST;
	public static int VALAKAS_RESP_SECOND;
	public static int VALAKAS_WAIT_TIME;
	public static int VALAKAS_DESPAWN_TIME;
	public static float VALAKAS_POWER_MULTIPLIER;
	
	public static int FRINTEZZA_RESP_FIRST;
	public static int FRINTEZZA_RESP_SECOND;
	public static float FRINTEZZA_POWER_MULTIPLIER;
	
	public static boolean BYPASS_FRINTEZZA_PARTIES_CHECK;
	public static int FRINTEZZA_MIN_PARTIES;
	public static int FRINTEZZA_MAX_PARTIES;
	
	// ============================================================
	public static void loadBossConfig()
	{
		String filePath = "config/head/boss.properties";
		
		try
		{
			L2Properties bossSettings = new L2Properties(filePath);
			
			ALT_RAIDS_STATS_BONUS = Boolean.parseBoolean(bossSettings.getProperty("AltRaidsStatsBonus", "True"));
			
			RBLOCKRAGE = Integer.parseInt(bossSettings.getProperty("RBlockRage", "5000"));
			
			if (RBLOCKRAGE > 0 && RBLOCKRAGE < 100)
			{
				LOGGER.info("ATTENTION: RBlockRage, if enabled (>0), must be >=100");
				LOGGER.info("	-- RBlockRage setted to 100 by default");
				RBLOCKRAGE = 100;
			}
			
			RBS_SPECIFIC_LOCK_RAGE = new HashMap<>();
			
			final String RBS_SPECIFIC_LOCK_RAGE_String = bossSettings.getProperty("RaidBossesSpecificLockRage", "");
			
			if (!RBS_SPECIFIC_LOCK_RAGE_String.equals(""))
			{
				
				final String[] locked_bosses = RBS_SPECIFIC_LOCK_RAGE_String.split(";");
				
				for (final String actual_boss_rage : locked_bosses)
				{
					final String[] boss_rage = actual_boss_rage.split(",");
					
					int specific_rage = Integer.parseInt(boss_rage[1]);
					
					if (specific_rage > 0 && specific_rage < 100)
					{
						LOGGER.info("ATTENTION: RaidBossesSpecificLockRage Value for boss " + boss_rage[0] + ", if enabled (>0), must be >=100");
						LOGGER.info("	-- RaidBossesSpecificLockRage Value for boss " + boss_rage[0] + " setted to 100 by default");
						specific_rage = 100;
					}
					
					RBS_SPECIFIC_LOCK_RAGE.put(Integer.parseInt(boss_rage[0]), specific_rage);
				}
				
			}
			
			ANNOUNCE_TO_ALL_SPAWN_RB = Boolean.valueOf(bossSettings.getProperty("AnnounceToAllSpawnRb", "False"));
			ALLOW_RAID_BOSS_PETRIFIED = Boolean.valueOf(bossSettings.getProperty("AllowRaidBossPetrified", "True"));
			AUTO_LOOT_BOSS = Boolean.valueOf(bossSettings.getProperty("AutoLootBoss", "True"));
			RAID_HP_REGEN_MULTIPLIER = Double.parseDouble(bossSettings.getProperty("RaidHpRegenMultiplier", "100")) / 100;
			RAID_MP_REGEN_MULTIPLIER = Double.parseDouble(bossSettings.getProperty("RaidMpRegenMultiplier", "100")) / 100;
			RAID_P_DEFENSE_MULTIPLIER = Double.parseDouble(bossSettings.getProperty("RaidPhysicalDefenseMultiplier", "100")) / 100;
			RAID_M_DEFENSE_MULTIPLIER = Double.parseDouble(bossSettings.getProperty("RaidMagicalDefenseMultiplier", "100")) / 100;
			RAID_MINION_RESPAWN_TIMER = Integer.parseInt(bossSettings.getProperty("RaidMinionRespawnTime", "300000"));
			RAID_MIN_RESPAWN_MULTIPLIER = Float.parseFloat(bossSettings.getProperty("RaidMinRespawnMultiplier", "1.0"));
			RAID_MAX_RESPAWN_MULTIPLIER = Float.parseFloat(bossSettings.getProperty("RaidMaxRespawnMultiplier", "1.0"));
			PLAYERS_CAN_HEAL_RB = Boolean.valueOf(bossSettings.getProperty("PlayersCanHealRb", "True"));
			ALLOW_DIRECT_TP_TO_BOSS_ROOM = Boolean.valueOf(bossSettings.getProperty("AllowDirectTeleportToBossRoom", "False"));
			RAID_RANKING_1ST = Integer.parseInt(bossSettings.getProperty("1stRaidRankingPoints", "1250"));
			RAID_RANKING_2ND = Integer.parseInt(bossSettings.getProperty("2ndRaidRankingPoints", "900"));
			RAID_RANKING_3RD = Integer.parseInt(bossSettings.getProperty("3rdRaidRankingPoints", "700"));
			RAID_RANKING_4TH = Integer.parseInt(bossSettings.getProperty("4thRaidRankingPoints", "600"));
			RAID_RANKING_5TH = Integer.parseInt(bossSettings.getProperty("5thRaidRankingPoints", "450"));
			RAID_RANKING_6TH = Integer.parseInt(bossSettings.getProperty("6thRaidRankingPoints", "350"));
			RAID_RANKING_7TH = Integer.parseInt(bossSettings.getProperty("7thRaidRankingPoints", "300"));
			RAID_RANKING_8TH = Integer.parseInt(bossSettings.getProperty("8thRaidRankingPoints", "200"));
			RAID_RANKING_9TH = Integer.parseInt(bossSettings.getProperty("9thRaidRankingPoints", "150"));
			RAID_RANKING_10TH = Integer.parseInt(bossSettings.getProperty("10thRaidRankingPoints", "100"));
			RAID_RANKING_UP_TO_50TH = Integer.parseInt(bossSettings.getProperty("UpTo50thRaidRankingPoints", "25"));
			RAID_RANKING_UP_TO_100TH = Integer.parseInt(bossSettings.getProperty("UpTo100thRaidRankingPoints", "12"));
			
			String raidInfoIds = bossSettings.getProperty("RaidInfoIDs", "");
			
			RAID_INFO_IDS_LIST = new ArrayList<>();
			
			for (String id : raidInfoIds.split(","))
			{
				try
				{
					RAID_INFO_IDS_LIST.add(Integer.parseInt(id));
				}
				catch (NumberFormatException e)
				{
					LOGGER.error("Config.loadBossConfig : Invalid number format for: " + id, e);
				}
			}
			
			// Antharas
			ANTHARAS_OLD = Boolean.valueOf(bossSettings.getProperty("AntharasOldScript", "true"));
			ANTHARAS_CLOSE = Integer.parseInt(bossSettings.getProperty("AntharasClose", "1200"));
			ANTHARAS_DESPAWN_TIME = Integer.parseInt(bossSettings.getProperty("AntharasDespawnTime", "240"));
			ANTHARAS_RESP_FIRST = Integer.parseInt(bossSettings.getProperty("AntharasRespFirst", "192"));
			ANTHARAS_RESP_SECOND = Integer.parseInt(bossSettings.getProperty("AntharasRespSecond", "145"));
			ANTHARAS_WAIT_TIME = Integer.parseInt(bossSettings.getProperty("AntharasWaitTime", "30"));
			ANTHARAS_POWER_MULTIPLIER = Float.parseFloat(bossSettings.getProperty("AntharasPowerMultiplier", "1.0"));
			// ============================================================
			// Baium
			BAIUM_SLEEP = Integer.parseInt(bossSettings.getProperty("BaiumSleep", "1800"));
			BAIUM_RESP_FIRST = Integer.parseInt(bossSettings.getProperty("BaiumRespFirst", "121"));
			BAIUM_RESP_SECOND = Integer.parseInt(bossSettings.getProperty("BaiumRespSecond", "8"));
			BAIUM_POWER_MULTIPLIER = Float.parseFloat(bossSettings.getProperty("BaiumPowerMultiplier", "1.0"));
			// ============================================================
			// Core
			CORE_RESP_MINION = Integer.parseInt(bossSettings.getProperty("CoreRespMinion", "60"));
			CORE_RESP_FIRST = Integer.parseInt(bossSettings.getProperty("CoreRespFirst", "37"));
			CORE_RESP_SECOND = Integer.parseInt(bossSettings.getProperty("CoreRespSecond", "42"));
			CORE_LEVEL = Integer.parseInt(bossSettings.getProperty("CoreLevel", "0"));
			CORE_RING_CHANCE = Integer.parseInt(bossSettings.getProperty("CoreRingChance", "0"));
			CORE_POWER_MULTIPLIER = Float.parseFloat(bossSettings.getProperty("CorePowerMultiplier", "1.0"));
			// ============================================================
			// Queen Ant
			QA_RESP_NURSE = Integer.parseInt(bossSettings.getProperty("QueenAntRespNurse", "60"));
			QA_RESP_ROYAL = Integer.parseInt(bossSettings.getProperty("QueenAntRespRoyal", "120"));
			QA_RESP_FIRST = Integer.parseInt(bossSettings.getProperty("QueenAntRespFirst", "19"));
			QA_RESP_SECOND = Integer.parseInt(bossSettings.getProperty("QueenAntRespSecond", "35"));
			QA_LEVEL = Integer.parseInt(bossSettings.getProperty("QALevel", "0"));
			QA_RING_CHANCE = Integer.parseInt(bossSettings.getProperty("QARingChance", "0"));
			QA_POWER_MULTIPLIER = Float.parseFloat(bossSettings.getProperty("QueenAntPowerMultiplier", "1.0"));
			// ============================================================
			// ZAKEN
			ZAKEN_RESP_FIRST = Integer.parseInt(bossSettings.getProperty("ZakenRespFirst", "60"));
			ZAKEN_RESP_SECOND = Integer.parseInt(bossSettings.getProperty("ZakenRespSecond", "8"));
			ZAKEN_LEVEL = Integer.parseInt(bossSettings.getProperty("ZakenLevel", "0"));
			ZAKEN_EARRING_CHANCE = Integer.parseInt(bossSettings.getProperty("ZakenEarringChance", "0"));
			ZAKEN_POWER_MULTIPLIER = Float.parseFloat(bossSettings.getProperty("ZakenPowerMultiplier", "1.0"));
			// ============================================================
			// ORFEN
			ORFEN_RESP_FIRST = Integer.parseInt(bossSettings.getProperty("OrfenRespFirst", "20"));
			ORFEN_RESP_SECOND = Integer.parseInt(bossSettings.getProperty("OrfenRespSecond", "8"));
			ORFEN_LEVEL = Integer.parseInt(bossSettings.getProperty("OrfenLevel", "0"));
			ORFEN_EARRING_CHANCE = Integer.parseInt(bossSettings.getProperty("OrfenEarringChance", "0"));
			ORFEN_POWER_MULTIPLIER = Float.parseFloat(bossSettings.getProperty("OrfenPowerMultiplier", "1.0"));
			// ============================================================
			// VALAKAS
			VALAKAS_RESP_FIRST = Integer.parseInt(bossSettings.getProperty("ValakasRespFirst", "192"));
			VALAKAS_RESP_SECOND = Integer.parseInt(bossSettings.getProperty("ValakasRespSecond", "44"));
			VALAKAS_WAIT_TIME = Integer.parseInt(bossSettings.getProperty("ValakasWaitTime", "30"));
			VALAKAS_POWER_MULTIPLIER = Float.parseFloat(bossSettings.getProperty("ValakasPowerMultiplier", "1.0"));
			VALAKAS_DESPAWN_TIME = Integer.parseInt(bossSettings.getProperty("ValakasDespawnTime", "15"));
			// ============================================================
			// FRINTEZZA
			FRINTEZZA_RESP_FIRST = Integer.parseInt(bossSettings.getProperty("FrintezzaRespFirst", "48"));
			FRINTEZZA_RESP_SECOND = Integer.parseInt(bossSettings.getProperty("FrintezzaRespSecond", "8"));
			FRINTEZZA_POWER_MULTIPLIER = Float.parseFloat(bossSettings.getProperty("FrintezzaPowerMultiplier", "1.0"));
			
			BYPASS_FRINTEZZA_PARTIES_CHECK = Boolean.valueOf(bossSettings.getProperty("BypassPartiesCheck", "false"));
			FRINTEZZA_MIN_PARTIES = Integer.parseInt(bossSettings.getProperty("FrintezzaMinParties", "4"));
			FRINTEZZA_MAX_PARTIES = Integer.parseInt(bossSettings.getProperty("FrintezzaMaxParties", "5"));
			// ============================================================
			
			LEVEL_DIFF_MULTIPLIER_MINION = Float.parseFloat(bossSettings.getProperty("LevelDiffMultiplierMinion", "0.5"));
			
			// High Priestess van Halter
			HPH_FIXINTERVALOFHALTER = Integer.parseInt(bossSettings.getProperty("FixIntervalOfHalter", "172800"));
			if (HPH_FIXINTERVALOFHALTER < 300 || HPH_FIXINTERVALOFHALTER > 864000)
			{
				HPH_FIXINTERVALOFHALTER = 172800;
			}
			HPH_FIXINTERVALOFHALTER *= 6000;
			
			HPH_RANDOMINTERVALOFHALTER = Integer.parseInt(bossSettings.getProperty("RandomIntervalOfHalter", "86400"));
			if (HPH_RANDOMINTERVALOFHALTER < 300 || HPH_RANDOMINTERVALOFHALTER > 864000)
			{
				HPH_RANDOMINTERVALOFHALTER = 86400;
			}
			HPH_RANDOMINTERVALOFHALTER *= 6000;
			
			HPH_APPTIMEOFHALTER = Integer.parseInt(bossSettings.getProperty("AppTimeOfHalter", "20"));
			if (HPH_APPTIMEOFHALTER < 5 || HPH_APPTIMEOFHALTER > 60)
			{
				HPH_APPTIMEOFHALTER = 20;
			}
			HPH_APPTIMEOFHALTER *= 6000;
			
			HPH_ACTIVITYTIMEOFHALTER = Integer.parseInt(bossSettings.getProperty("ActivityTimeOfHalter", "21600"));
			if (HPH_ACTIVITYTIMEOFHALTER < 7200 || HPH_ACTIVITYTIMEOFHALTER > 86400)
			{
				HPH_ACTIVITYTIMEOFHALTER = 21600;
			}
			HPH_ACTIVITYTIMEOFHALTER *= 1000;
			
			HPH_FIGHTTIMEOFHALTER = Integer.parseInt(bossSettings.getProperty("FightTimeOfHalter", "7200"));
			if (HPH_FIGHTTIMEOFHALTER < 7200 || HPH_FIGHTTIMEOFHALTER > 21600)
			{
				HPH_FIGHTTIMEOFHALTER = 7200;
			}
			HPH_FIGHTTIMEOFHALTER *= 6000;
			
			HPH_CALLROYALGUARDHELPERCOUNT = Integer.parseInt(bossSettings.getProperty("CallRoyalGuardHelperCount", "6"));
			if (HPH_CALLROYALGUARDHELPERCOUNT < 1 || HPH_CALLROYALGUARDHELPERCOUNT > 6)
			{
				HPH_CALLROYALGUARDHELPERCOUNT = 6;
			}
			
			HPH_CALLROYALGUARDHELPERINTERVAL = Integer.parseInt(bossSettings.getProperty("CallRoyalGuardHelperInterval", "10"));
			if (HPH_CALLROYALGUARDHELPERINTERVAL < 1 || HPH_CALLROYALGUARDHELPERINTERVAL > 60)
			{
				HPH_CALLROYALGUARDHELPERINTERVAL = 10;
			}
			HPH_CALLROYALGUARDHELPERINTERVAL *= 6000;
			
			HPH_INTERVALOFDOOROFALTER = Integer.parseInt(bossSettings.getProperty("IntervalOfDoorOfAlter", "5400"));
			if (HPH_INTERVALOFDOOROFALTER < 60 || HPH_INTERVALOFDOOROFALTER > 5400)
			{
				HPH_INTERVALOFDOOROFALTER = 5400;
			}
			HPH_INTERVALOFDOOROFALTER *= 6000;
			
			HPH_TIMEOFLOCKUPDOOROFALTAR = Integer.parseInt(bossSettings.getProperty("TimeOfLockUpDoorOfAltar", "180"));
			if (HPH_TIMEOFLOCKUPDOOROFALTAR < 60 || HPH_TIMEOFLOCKUPDOOROFALTAR > 600)
			{
				HPH_TIMEOFLOCKUPDOOROFALTAR = 180;
			}
			HPH_TIMEOFLOCKUPDOOROFALTAR *= 6000;
			
		}
		catch (Exception e)
		{
			LOGGER.error("Config : Failed to load " + filePath + " file.", e);
		}
	}
	
	// ============================================================
	public static boolean SCRIPT_DEBUG;
	public static boolean SCRIPT_ALLOW_COMPILATION;
	public static boolean SCRIPT_CACHE;
	public static boolean SCRIPT_ERROR_LOG;
	
	// ============================================================
	public static void loadScriptConfig()
	{
		String filePath = "config/others/script.properties";
		
		try
		{
			L2Properties scriptSetting = new L2Properties(filePath);
			
			SCRIPT_DEBUG = Boolean.valueOf(scriptSetting.getProperty("EnableScriptDebug", "false"));
			SCRIPT_ALLOW_COMPILATION = Boolean.valueOf(scriptSetting.getProperty("AllowCompilation", "true"));
			SCRIPT_CACHE = Boolean.valueOf(scriptSetting.getProperty("UseCache", "true"));
			SCRIPT_ERROR_LOG = Boolean.valueOf(scriptSetting.getProperty("EnableScriptErrorLog", "true"));
		}
		catch (Exception e)
		{
			LOGGER.error("Config : Failed to load " + filePath + " file.", e);
		}
	}
	
	// ============================================================
	public static boolean CHARACTER_REPAIR;
	public static boolean ENABLE_SAY_SOCIAL_ACTIONS;
	
	// ============================================================
	
	public static void loadPowerPak()
	{
		String filePath = "config/powerpak.properties";
		
		try
		{
			L2Properties powerPakProperties = new L2Properties(filePath);
			
			CHARACTER_REPAIR = Boolean.parseBoolean(powerPakProperties.getProperty("CharacterRepair", "false"));
			ENABLE_SAY_SOCIAL_ACTIONS = Boolean.parseBoolean(powerPakProperties.getProperty("EnableSocialSayActions", "false"));
		}
		catch (Exception e)
		{
			LOGGER.error("Failed to load " + filePath + " file", e);
		}
	}
	
	// ============================================================
	public static Map<String, List<String>> EXTENDERS;
	
	// ============================================================
	public static void loadExtendersConfig()
	{
		String filePath = "config/extender.properties";
		
		EXTENDERS = new HashMap<>();
		File f = new File(filePath);
		
		if (f.exists())
		{
			try (LineNumberReader lineReader = new LineNumberReader(new BufferedReader(new FileReader(f))))
			{
				String line;
				while ((line = lineReader.readLine()) != null)
				{
					int iPos = line.indexOf("#");
					
					if (iPos != -1)
					{
						line = line.substring(0, iPos);
					}
					
					if (line.trim().length() == 0)
					{
						continue;
					}
					
					iPos = line.indexOf("=");
					if (iPos != -1)
					{
						final String baseName = line.substring(0, iPos).trim();
						final String className = line.substring(iPos + 1).trim();
						
						if (EXTENDERS.get(baseName) == null)
						{
							EXTENDERS.put(baseName, new ArrayList<>());
						}
						
						EXTENDERS.get(baseName).add(className);
					}
				}
			}
			catch (Exception e)
			{
				LOGGER.error("Config : Failed to load " + filePath + " file.", e);
			}
		}
	}
	
	// ============================================================
	public static long CHECK_CONNECTION_INACTIVITY_TIME;
	public static long CHECK_CONNECTION_INITIAL_TIME;
	public static long CHECK_CONNECTION_DELAY_TIME;
	public static long CLEANDB_INITIAL_TIME;
	public static long CLEANDB_DELAY_TIME;
	public static long CHECK_TELEPORT_ZOMBIE_DELAY_TIME;
	public static long DEADLOCKCHECK_INTIAL_TIME;
	public static long DEADLOCKCHECK_DELAY_TIME;
	
	// ============================================================
	public static void loadDaemonsConf()
	{
		String filePath = "config/others/daemons.properties";
		
		try
		{
			L2Properties properties = new L2Properties(filePath);
			
			CHECK_CONNECTION_INITIAL_TIME = Long.parseLong(properties.getProperty("CheckConnectionInitial", "300000"));
			CHECK_CONNECTION_DELAY_TIME = Long.parseLong(properties.getProperty("CheckConnectionDelay", "900000"));
			CHECK_CONNECTION_INACTIVITY_TIME = Long.parseLong(properties.getProperty("CheckConnectionInactivityTime", "90000"));
			CLEANDB_INITIAL_TIME = Long.parseLong(properties.getProperty("CleanDBInitial", "300000"));
			CLEANDB_DELAY_TIME = Long.parseLong(properties.getProperty("CleanDBDelay", "900000"));
			CHECK_TELEPORT_ZOMBIE_DELAY_TIME = Long.parseLong(properties.getProperty("CheckTeleportZombiesDelay", "90000"));
			DEADLOCKCHECK_INTIAL_TIME = Long.parseLong(properties.getProperty("DeadLockCheck", "0"));
			DEADLOCKCHECK_DELAY_TIME = Long.parseLong(properties.getProperty("DeadLockDelay", "0"));
		}
		catch (Exception e)
		{
			LOGGER.error("Config : Failed to load " + filePath + " file.", e);
		}
	}
	
	// ==============================================================
	public static void loadFilter()
	{
		String filePath = "config/chatfilter.txt";
		
		File filter_file = new File(filePath);
		if (!filter_file.exists())
		{
			return;
		}
		
		try (LineNumberReader lnr = new LineNumberReader(new BufferedReader(new FileReader(filter_file))))
		{
			String line = null;
			while ((line = lnr.readLine()) != null)
			{
				if (line.trim().length() == 0 || line.startsWith("#"))
				{
					continue;
				}
				FILTER_LIST.add(line.trim());
			}
			
		}
		catch (Exception e)
		{
			LOGGER.error("Config : Failed to load " + filePath + " file.", e);
		}
	}
	
	// ============================================================
	private static final String HEXID_FILE = "config/hexid.txt";
	public static int SERVER_ID;
	public static byte[] HEX_ID;
	
	// ============================================================
	public static void loadHexid()
	{
		try
		{
			L2Properties Settings = new L2Properties(HEXID_FILE);
			SERVER_ID = Integer.parseInt(Settings.getProperty("ServerID"));
			HEX_ID = new BigInteger(Settings.getProperty("HexID"), 16).toByteArray();
		}
		catch (Exception e)
		{
			LOGGER.error("Could not load HexID file (" + HEXID_FILE + "). Remember register the gameserver.");
			System.exit(1);
		}
	}
	
	// ============================================================
	public static int PORT_LOGIN;
	public static String LOGIN_BIND_ADDRESS;
	public static int LOGIN_TRY_BEFORE_BAN;
	public static int LOGIN_BLOCK_AFTER_BAN;
	public static File DATAPACK_ROOT;
	public static int GAME_SERVER_LOGIN_PORT;
	public static String GAME_SERVER_LOGIN_HOST;
	public static String INTERNAL_HOSTNAME;
	public static String EXTERNAL_HOSTNAME;
	public static boolean REMOVE_BUFFS_ON_DIE;
	public static boolean SHOW_LICENCE;
	public static boolean FORCE_GGAUTH;
	public static boolean FLOOD_PROTECTION;
	public static int FAST_CONNECTION_LIMIT;
	public static int NORMAL_CONNECTION_TIME;
	public static int FAST_CONNECTION_TIME;
	public static int MAX_CONNECTION_PER_IP;
	public static boolean AUTO_CREATE_ACCOUNTS;
	public static String NETWORK_IP_LIST;
	public static long SESSION_TTL;
	public static int MAX_LOGINSESSIONS;
	
	// ============================================================
	public static void loadLoginStartConfig()
	{
		String filePath = "config/network/loginserver.properties";
		
		try
		{
			L2Properties serverSettings = new L2Properties(filePath);
			
			GAME_SERVER_LOGIN_HOST = serverSettings.getProperty("LoginHostname", "*");
			GAME_SERVER_LOGIN_PORT = Integer.parseInt(serverSettings.getProperty("LoginPort", "9013"));
			
			LOGIN_BIND_ADDRESS = serverSettings.getProperty("LoginserverHostname", "*");
			PORT_LOGIN = Integer.parseInt(serverSettings.getProperty("LoginserverPort", "2106"));
			
			LOGIN_TRY_BEFORE_BAN = Integer.parseInt(serverSettings.getProperty("LoginTryBeforeBan", "10"));
			LOGIN_BLOCK_AFTER_BAN = Integer.parseInt(serverSettings.getProperty("LoginBlockAfterBan", "600"));
			
			INTERNAL_HOSTNAME = serverSettings.getProperty("InternalHostname", "127.0.0.1");
			EXTERNAL_HOSTNAME = serverSettings.getProperty("ExternalHostname", "127.0.0.1");
			
			// ------------- LoginServer
			DATABASE_LOGINSERVER_HOST = serverSettings.getProperty("DatabaseLoginServerHost", "127.0.0.1");
			DATABASE_LOGINSERVER_NAME = serverSettings.getProperty("DatabaseLoginServerName", "frozen");
			DATABASE_URL = "jdbc:mariadb://" + DATABASE_LOGINSERVER_HOST + "/" + DATABASE_LOGINSERVER_NAME; // MariaDB JDBC URL
			// ------------- LoginServer
			
			DATABASE_USER = serverSettings.getProperty("DatabaseUser", "root");
			DATABASE_PASSWORD = serverSettings.getProperty("DatabasePassword", "");
			
			// Anti Brute force attack on login
			BRUT_AVG_TIME = Integer.parseInt(serverSettings.getProperty("BrutAvgTime", "30")); // in Seconds
			BRUT_LOGON_ATTEMPTS = Integer.parseInt(serverSettings.getProperty("BrutLogonAttempts", "15"));
			BRUT_BAN_IP_TIME = Integer.parseInt(serverSettings.getProperty("BrutBanIpTime", "900")); // in Seconds
			
			SHOW_LICENCE = Boolean.parseBoolean(serverSettings.getProperty("ShowLicence", "false"));
			FORCE_GGAUTH = Boolean.parseBoolean(serverSettings.getProperty("ForceGGAuth", "false"));
			
			AUTO_CREATE_ACCOUNTS = Boolean.parseBoolean(serverSettings.getProperty("AutoCreateAccounts", "True"));
			
			FLOOD_PROTECTION = Boolean.parseBoolean(serverSettings.getProperty("EnableFloodProtection", "True"));
			FAST_CONNECTION_LIMIT = Integer.parseInt(serverSettings.getProperty("FastConnectionLimit", "15"));
			NORMAL_CONNECTION_TIME = Integer.parseInt(serverSettings.getProperty("NormalConnectionTime", "700"));
			FAST_CONNECTION_TIME = Integer.parseInt(serverSettings.getProperty("FastConnectionTime", "350"));
			MAX_CONNECTION_PER_IP = Integer.parseInt(serverSettings.getProperty("MaxConnectionPerIP", "50"));
			DEBUG = Boolean.parseBoolean(serverSettings.getProperty("Debug", "false"));
			DEVELOPER = Boolean.parseBoolean(serverSettings.getProperty("Developer", "false"));
			
			NETWORK_IP_LIST = serverSettings.getProperty("NetworkList", "");
			SESSION_TTL = Long.parseLong(serverSettings.getProperty("SessionTTL", "25000"));
			MAX_LOGINSESSIONS = Integer.parseInt(serverSettings.getProperty("MaxSessions", "200"));
			
			DEBUG_PACKETS = Boolean.parseBoolean(serverSettings.getProperty("DebugPackets", "false"));
			
		}
		catch (Exception e)
		{
			LOGGER.error("Config.loadLoginStartConfig : Could not load " + filePath, e);
		}
	}
	
	/** Enumeration for type of ID Factory */
	public static enum IdFactoryType
	{
		Compaction,
		BitSet,
		Stack
	}
	
	/** Enumeration for type of maps object */
	public static enum ObjectMapType
	{
		WorldObjectTree,
		WorldObjectMap
	}
	
	/** Enumeration for type of set object */
	public static enum ObjectSetType
	{
		L2ObjectHashSet,
		WorldObjectSet
	}
	
	public static void load()
	{
		if (ServerType.serverMode == ServerType.MODE_GAMESERVER)
		{
			loadAccessLevelConfig();
			
			loadHexid();
			
			// Load network
			loadServerConfig();
			
			// Load system
			loadIdFactoryConfig();
			
			// Load developer parameters
			loadDevConfig();
			
			// Head
			loadOptionsConfig();
			loadOtherConfig();
			loadRatesConfig();
			loadAltConfig();
			load7sConfig();
			loadCHConfig();
			loadElitCHConfig();
			loadOlympConfig();
			loadEnchantConfig();
			loadBossConfig();
			
			// Head functions
			loadL2JFrozenConfig();
			loadPHYSICSConfig();
			loadAccessConfig();
			loadPvpConfig();
			loadCraftConfig();
			
			// Frozen config
			loadTWConfig();
			
			// Protect
			loadFloodConfig();
			loadPacketConfig();
			loadPOtherConfig();
			
			// Geo&path
			loadgeodataConfig();
			
			// Fun
			loadWeddingConfig();
			loadAWAYConfig();
			loadBankingConfig();
			loadPCBPointConfig();
			loadPowerPak();
			
			// Other
			loadExtendersConfig();
			loadDaemonsConf();
			loadAccessConfig();
			loadConfigMonsterItem();
			
			if (Config.USE_SAY_FILTER)
			{
				loadFilter();
			}
			
		}
		else if (ServerType.serverMode == ServerType.MODE_LOGINSERVER)
		{
			loadLoginStartConfig();
		}
		else
		{
			LOGGER.error("Could not Load Config: server mode was not set");
		}
	}
	
	public static void saveHexid(int serverId, String string)
	{
		Config.saveHexid(serverId, string, HEXID_FILE);
	}
	
	public static void saveHexid(int serverId, String hexId, String fileName)
	{
		try
		{
			L2Properties hexSetting = new L2Properties();
			File file = new File(fileName);
			
			if (file.createNewFile())
			{
				try (OutputStream out = new FileOutputStream(file))
				{
					hexSetting.setProperty("ServerID", String.valueOf(serverId));
					hexSetting.setProperty("HexID", hexId);
					hexSetting.store(out, "the hexID to auth into login");
				}
				
			}
		}
		catch (Exception e)
		{
			LOGGER.error("Config : Failed to save hex id to " + fileName + " file.", e);
		}
	}
	
	public static void unallocateFilterBuffer()
	{
		LOGGER.info("Cleaning Chat Filter..");
		FILTER_LIST.clear();
	}
	
	public static void loadAccessLevelConfig()
	{
		try
		{
			File fXmlFile = new File("./config/access_level/GM.xml");
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(fXmlFile);
			
			// optional, but recommended
			// read this - http://stackoverflow.com/questions/13786607/normalization-in-dom-parsing-with-java-how-does-it-work
			doc.getDocumentElement().normalize();
			
			NodeList nList = doc.getElementsByTagName("character");
			
			for (int temp = 0; temp < nList.getLength(); temp++)
			{
				Node nNode = nList.item(temp);
				
				if (nNode.getNodeType() == Node.ELEMENT_NODE)
				{
					Element eElement = (Element) nNode;
					
					int character_obj_id = Integer.parseInt(eElement.getAttribute("obj_id"));
					int accesslevel = Integer.parseInt(eElement.getAttribute("access_level"));
					
					GM_PLAYERS.put(character_obj_id, accesslevel);
				}
			}
		}
		catch (Exception e)
		{
			LOGGER.error("Could not read config/access_level/GM.xml " + e);
		}
	}
	
}
