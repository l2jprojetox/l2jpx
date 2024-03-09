package net.l2jpx.gameserver.model.entity.olympiad;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;

import org.apache.log4j.Logger;

import net.l2jpx.Config;
import net.l2jpx.gameserver.managers.OlympiadStadiaManager;
import net.l2jpx.gameserver.model.L2World;
import net.l2jpx.gameserver.model.actor.instance.L2PcInstance;
import net.l2jpx.gameserver.model.entity.Announcements;
import net.l2jpx.gameserver.model.entity.Hero;
import net.l2jpx.gameserver.model.spawn.L2Spawn;
import net.l2jpx.gameserver.network.SystemMessageId;
import net.l2jpx.gameserver.network.clientpackets.Say2;
import net.l2jpx.gameserver.network.serverpackets.CreatureSay;
import net.l2jpx.gameserver.network.serverpackets.NpcHtmlMessage;
import net.l2jpx.gameserver.network.serverpackets.SystemMessage;
import net.l2jpx.gameserver.templates.StatsSet;
import net.l2jpx.gameserver.thread.ThreadPoolManager;
import net.l2jpx.util.L2FastList;
import net.l2jpx.util.database.L2DatabaseFactory;

/**
 * @author godson
 * @author ReynalDev
 */
public class Olympiad
{
	protected static final Logger LOGGER = Logger.getLogger(Olympiad.class);
	private static Olympiad instance;
	
	private static Map<Integer, StatsSet> nobles = new ConcurrentHashMap<>();
	private static Map<Integer, StatsSet> nobles_eom = new ConcurrentHashMap<>();
	protected static List<StatsSet> heroesToBe = new ArrayList<>();
	private static L2FastList<L2PcInstance> nonClassBasedRegisters;
	private static Map<Integer, L2FastList<L2PcInstance>> classBasedRegisters;
	public static final int OLY_MANAGER = 31688;
	public static List<L2Spawn> olymanagers = new ArrayList<>();
	
	public static final String OLYMPIAD_HTML_PATH = "data/html/olympiad/";
	
	private static final String SELECT_OLYMPIAD_DATA = "SELECT current_cycle, period, olympiad_end, olympiad_validation_end, next_weekly_change FROM olympiad_data";
	private static final String DELETE_OLYMPIAD_DATA = "DELETE FROM olympiad_data";
	private static final String INSERT_OLYMPIAD_DATA = "INSERT INTO olympiad_data (current_cycle, period, olympiad_end, olympiad_validation_end, next_weekly_change) VALUES (?,?,?,?,?)";
	
	private static final String SELECT_OLYMPIAD_NOBLES = "SELECT charId, characters.char_name, class_id, olympiad_points, competitions_done, competitions_won, competitions_lost, competitions_drawn FROM olympiad_nobles LEFT JOIN characters ON charId=characters.obj_Id";
	private static final String INSERT_OLYMPIAD_NOBLES = "INSERT INTO olympiad_nobles (`charId`,`class_id`,`olympiad_points`,`competitions_done`,`competitions_won`,`competitions_lost`,`competitions_drawn`) VALUES (?,?,?,?,?,?,?)";
	private static final String UPDATE_OLYMPIAD_NOBLES = "UPDATE olympiad_nobles SET olympiad_points = ?, competitions_done = ?, competitions_won = ?, competitions_lost = ?, competitions_drawn = ? WHERE charId=?";
	
	private static final String SELECT_CANDIDATES_TO_HERO = "SELECT charId, characters.char_name FROM olympiad_nobles LEFT JOIN characters ON charId=characters.obj_Id WHERE class_id = ? AND competitions_done >= 0 AND competitions_won >= 0 ORDER BY olympiad_points DESC, competitions_won/competitions_done DESC, competitions_done DESC LIMIT ?";
	
	private static final String SELECT_CLASS_LEADER_BY_CLASS_ID = "SELECT characters.char_name FROM olympiad_nobles_eom LEFT JOIN characters ON charId = characters.obj_Id WHERE class_id = ? AND competitions_done >= 9 ORDER BY olympiad_points DESC, competitions_done DESC LIMIT 10";
	private static final String SELECT_CURRENT_CLASS_LEADER_BY_CLASS_ID = "SELECT characters.char_name FROM olympiad_nobles LEFT JOIN characters ON charId=characters.obj_Id WHERE class_id = ? AND competitions_done >= 9 ORDER BY olympiad_points DESC, competitions_done DESC LIMIT 10";
	
	private static final String TRUNCATE_OLYMPIAD_NOBLES = "TRUNCATE olympiad_nobles";
	private static final String TRUNCATE_OLYMPIAD_NOBLES_EOM = "TRUNCATE olympiad_nobles_eom";
	
	private static final String SELECT_OLYMPIAD_NOBLES_EOM = "SELECT charId, characters.char_name, class_id, olympiad_points, competitions_done, competitions_won, competitions_lost, competitions_drawn FROM olympiad_nobles_eom LEFT JOIN characters ON charId=characters.obj_Id";
	private static final String INSERT_OLYMPIAD_NOBLES_DATA_INTO_OLYMPIAD_NOBLES_EOM = "INSERT INTO olympiad_nobles_eom SELECT * FROM olympiad_nobles";
	private static final String UPDATE_OLYMPIAD_NOBLES_EOM = "UPDATE olympiad_nobles_eom SET olympiad_points = ?, competitions_done = ?, competitions_won = ?, competitions_lost = ?, competitions_drawn = ? WHERE charId = ?";
	
	private static final SimpleDateFormat dateFormat = new SimpleDateFormat("E dd/MM/yyyy HH:mm:ss");
	private static final SimpleDateFormat hourFormat = new SimpleDateFormat("HH:mm:ss");
	
	private static final int[] HERO_IDS =
	{
		88,
		89,
		90,
		91,
		92,
		93,
		94,
		95,
		96,
		97,
		98,
		99,
		100,
		101,
		102,
		103,
		104,
		105,
		106,
		107,
		108,
		109,
		110,
		111,
		112,
		113,
		114,
		115,
		116,
		117,
		118
	};
	
	private static final int COMP_START_HOUR = Config.ALT_OLY_START_TIME; // 6PM
	private static final int COMP_START_MINUTE = Config.ALT_OLY_MIN; // 00 mins
	private static final long COMP_PERIOD = Config.ALT_OLY_CPERIOD; // 6 hours
	protected static final long WEEKLY_PERIOD = Config.ALT_OLY_WPERIOD; // 1 week
	protected static final long VALIDATION_PERIOD = Config.ALT_OLY_VPERIOD; // 24 hours
	
	private static final int DEFAULT_POINTS = 18;
	protected static final int WEEKLY_POINTS = 3;
	
	public static final String CHAR_ID = "charId";
	public static final String CLASS_ID = "class_id";
	public static final String CHAR_NAME = "char_name";
	public static final String POINTS = "olympiad_points";
	public static final String COMP_DONE = "competitions_done";
	public static final String COMP_WON = "competitions_won";
	public static final String COMP_LOST = "competitions_lost";
	public static final String COMP_DRAWN = "competitions_drawn";
	
	protected static long olympiadEnd;
	protected static long olympiadValidationEnd;
	
	/**
	 * The current period of the olympiad.<br>
	 * <b>0 -</b> Competition period<br>
	 * <b>1 -</b> Validation Period
	 */
	protected static int period;
	protected long nextWeeklyChange;
	protected int currentCycle;
	private long compEnd;
	private Calendar compStart;
	protected static boolean inCompPeriod;
	protected static boolean compStarted = false;
	protected ScheduledFuture<?> scheduledCompStart;
	protected ScheduledFuture<?> scheduledCompEnd;
	protected ScheduledFuture<?> scheduledOlympiadEnd;
	protected ScheduledFuture<?> scheduledWeeklyTask;
	protected ScheduledFuture<?> scheduledValdationTask;
	
	protected static enum COMP_TYPE
	{
		CLASSED,
		NON_CLASSED
	}
	
	public static Olympiad getInstance()
	{
		if (instance == null)
		{
			instance = new Olympiad();
		}
		return instance;
	}
	
	public Olympiad()
	{
		load();
		
		if (period == 0)
		{
			init();
		}
	}
	
	public static Integer getStadiumCount()
	{
		return OlympiadManager.STADIUMS.length;
	}
	
	private void load()
	{
		currentCycle = 1;
		period = 0;
		olympiadEnd = 0;
		olympiadValidationEnd = 0;
		nextWeeklyChange = 0;
		
		try (Connection con = L2DatabaseFactory.getInstance().getConnection();
			PreparedStatement pst = con.prepareStatement(SELECT_OLYMPIAD_DATA);
			ResultSet rs = pst.executeQuery())
		{
			if (rs.next())
			{
				currentCycle = rs.getInt("current_cycle");
				period = rs.getInt("period");
				olympiadEnd = rs.getLong("olympiad_end");
				olympiadValidationEnd = rs.getLong("olympiad_validation_end");
				nextWeeklyChange = rs.getLong("next_weekly_change");
			}
		}
		catch (Exception e)
		{
			LOGGER.error("Olympiad.load : Could not select data from olympiad_data table in database ", e);
		}
		
		switch (period)
		{
			case 0:
				if (olympiadEnd < Calendar.getInstance().getTimeInMillis())
				{
					setNewOlympiadEnd();
				}
				else
				{
					scheduleWeeklyChange();
				}
				break;
			case 1:
				if (olympiadValidationEnd > Calendar.getInstance().getTimeInMillis())
				{
					scheduledValdationTask = ThreadPoolManager.getInstance().scheduleGeneral(new ValidationEndTask(), getMillisToValidationEnd());
				}
				else
				{
					currentCycle++;
					period = 0;
					deleteNobles();
					setNewOlympiadEnd();
				}
				break;
			default:
				LOGGER.warn("Olympiad System: Omg something went wrong in loading!! Period = " + period);
				return;
		}
		
		try (Connection con = L2DatabaseFactory.getInstance().getConnection();
			PreparedStatement statement = con.prepareStatement(SELECT_OLYMPIAD_NOBLES);
			ResultSet rset = statement.executeQuery())
		{
			while (rset.next())
			{
				StatsSet nobleStatsData = new StatsSet();
				int charId = rset.getInt(CHAR_ID);
				nobleStatsData.set(CHAR_ID, rset.getInt(CHAR_ID));
				nobleStatsData.set(CLASS_ID, rset.getInt(CLASS_ID));
				nobleStatsData.set(CHAR_NAME, rset.getString(CHAR_NAME));
				nobleStatsData.set(POINTS, rset.getInt(POINTS));
				nobleStatsData.set(COMP_DONE, rset.getInt(COMP_DONE));
				nobleStatsData.set(COMP_WON, rset.getInt(COMP_WON));
				nobleStatsData.set(COMP_LOST, rset.getInt(COMP_LOST));
				nobleStatsData.set(COMP_DRAWN, rset.getInt(COMP_DRAWN));
				nobleStatsData.set("to_save", false);
				
				nobles.put(charId, nobleStatsData);
			}
		}
		catch (Exception e)
		{
			LOGGER.warn("Olympiad.load : Error loading noblesse data from database: ", e);
		}
		
		try (Connection con = L2DatabaseFactory.getInstance().getConnection();
			PreparedStatement statement = con.prepareStatement(SELECT_OLYMPIAD_NOBLES_EOM);
			ResultSet rset = statement.executeQuery())
		{
			while (rset.next())
			{
				StatsSet nobleStatsData = new StatsSet();
				int charId = rset.getInt(CHAR_ID);
				nobleStatsData.set(CHAR_ID, rset.getInt(CHAR_ID));
				nobleStatsData.set(CLASS_ID, rset.getInt(CLASS_ID));
				nobleStatsData.set(CHAR_NAME, rset.getString(CHAR_NAME));
				nobleStatsData.set(POINTS, rset.getInt(POINTS));
				nobleStatsData.set(COMP_DONE, rset.getInt(COMP_DONE));
				nobleStatsData.set(COMP_WON, rset.getInt(COMP_WON));
				nobleStatsData.set(COMP_LOST, rset.getInt(COMP_LOST));
				nobleStatsData.set(COMP_DRAWN, rset.getInt(COMP_DRAWN));
				nobleStatsData.set("to_save", false);
				
				nobles_eom.put(charId, nobleStatsData);
			}
		}
		catch (Exception e)
		{
			LOGGER.error("Olympiad.load : Error loading noblesse data from database: ", e);
		}
		
		LOGGER.info("Olympiad cycle: " + currentCycle);
		
		if (period == 0) // normal
		{
			LOGGER.info("Olympiad period: NORMAL");
		}
		else if (period == 1) // validation
		{
			LOGGER.info("Olympiad period: VALIDATION");
		}
		else
			LOGGER.info("Olympiad period: UNKNOW");
		
		LOGGER.info("Olympiad end: " + dateFormat.format(new Date(olympiadEnd)));
		
		if (period == 1) // validation
		{
			LOGGER.info("Olympiad validation end : " + dateFormat.format(new Date(olympiadValidationEnd)));
		}
		
		LOGGER.info("Olympiad weekly change: " + dateFormat.format(new Date(nextWeeklyChange)));
	}
	
	protected final void init()
	{
		if (period == 1)
		{
			return;
		}
		
		nonClassBasedRegisters = new L2FastList<>();
		classBasedRegisters = new HashMap<>();
		
		compStart = Calendar.getInstance();
		compStart.set(Calendar.HOUR_OF_DAY, COMP_START_HOUR);
		compStart.set(Calendar.MINUTE, COMP_START_MINUTE);
		compStart.set(Calendar.SECOND, 0);
		compStart.set(Calendar.MILLISECOND, 0);
		compEnd = compStart.getTimeInMillis() + COMP_PERIOD;
		
		if (scheduledOlympiadEnd != null)
		{
			scheduledOlympiadEnd.cancel(true);
		}
		
		scheduledOlympiadEnd = ThreadPoolManager.getInstance().scheduleGeneral(new OlympiadEndTask(), getMillisToOlympiadEnd());
		
		updateCompStatus();
	}
	
	protected class OlympiadEndTask implements Runnable
	{
		@Override
		public void run()
		{
			SystemMessage sm = new SystemMessage(SystemMessageId.OLYMPIAD_PERIOD_S1_HAS_ENDED);
			sm.addNumber(currentCycle);
			
			Announcements.getInstance().announceToAll(sm);
			Announcements.getInstance().announceToAll("Olympiad Validation Period has began");
			
			if (scheduledWeeklyTask != null)
			{
				scheduledWeeklyTask.cancel(true);
			}
			
			saveNobleData();
			
			period = 1;
			sortHerosToBe();
			giveHeroBonus();
			Hero.getInstance().computeNewHeroes(heroesToBe);
			
			saveOlympiadStatus();
			updateMonthlyData();
			
			Calendar validationEnd = Calendar.getInstance();
			olympiadValidationEnd = validationEnd.getTimeInMillis() + VALIDATION_PERIOD;
			
			scheduledValdationTask = ThreadPoolManager.getInstance().scheduleGeneral(new ValidationEndTask(), getMillisToValidationEnd());
		}
	}
	
	protected class ValidationEndTask implements Runnable
	{
		@Override
		public void run()
		{
			Announcements.getInstance().announceToAll("Olympiad Validation Period has ended");
			period = 0;
			currentCycle++;
			deleteNobles();
			setNewOlympiadEnd();
			init();
		}
	}
	
	public boolean registerNoble(L2PcInstance noble, boolean classBased)
	{
		SystemMessage sm;
		
		/** Begin Olympiad Restrictions */
		
		if (noble.isDead())
		{
			sm = new SystemMessage(SystemMessageId.CANNOT_PARTICIPATE_OLYMPIAD_WHILE_DEAD);
			noble.sendPacket(sm);
			return false;
		}
		
		if (noble.inObserverMode())
		{
			noble.sendMessage("You can not participate to Olympiad. You are in Observer Mode, try to restart!");
			return false;
		}
		
		if (!inCompPeriod)
		{
			sm = new SystemMessage(SystemMessageId.THE_OLYMPIAD_GAME_IS_NOT_CURRENTLY_IN_PROGRESS);
			noble.sendPacket(sm);
			return false;
		}
		
		if (!noble.isNoble())
		{
			sm = new SystemMessage(SystemMessageId.ONLY_NOBLESS_CAN_PARTICIPATE_IN_THE_OLYMPIAD);
			noble.sendPacket(sm);
			return false;
		}
		
		if (noble.getBaseClass() != noble.getClassId().getId())
		{
			sm = new SystemMessage(SystemMessageId.YOU_CANT_JOIN_THE_OLYMPIAD_WITH_A_SUB_JOB_CHARACTER);
			noble.sendPacket(sm);
			return false;
		}
		
		if (noble.isCursedWeaponEquiped())
		{
			sm = new SystemMessage(SystemMessageId.CANNOT_JOIN_OLYMPIAD_POSSESSING_S1);
			sm.addItemName(noble.getCursedWeaponEquipedId());
			noble.sendPacket(sm);
			return false;
		}
		
		if (noble.getInventoryLimit() * 0.8 <= noble.getInventory().getSize())
		{
			sm = new SystemMessage(SystemMessageId.SINCE_80_PERCENT_OR_MORE_OF_YOUR_INVENTORY_SLOTS_ARE_FULL_YOU_CANNOT_PARTICIPATE_IN_THE_OLYMPIAD);
			noble.sendPacket(sm);
			return false;
		}
		
		if (getMillisToCompEnd() < 600000)
		{
			sm = new SystemMessage(SystemMessageId.GAME_REQUEST_CANNOT_BE_MADE);
			noble.sendPacket(sm);
			return false;
		}
		
		// Olympiad dualbox protection
		if (!Config.ALLOW_DUALBOX_OLY)
		{
			Iterator<L2PcInstance> iterator = L2World.getInstance().getAllPlayers().iterator();
			
			while (iterator.hasNext())
			{
				L2PcInstance player = iterator.next();
				
				if (player.getObjectId() == noble.getObjectId())
					continue;
				
				if (player.getIPAddress().equalsIgnoreCase(noble.getIPAddress()))
				{
					if (player.isInOlympiadMode())
					{
						noble.sendMessage("You are already participating in Olympiad with another character!");
						return false;
					}
				}
			}
		}
		
		/** End Olympiad Restrictions */
		
		if (classBasedRegisters.containsKey(noble.getClassId().getId()))
		{
			L2FastList<L2PcInstance> classed = classBasedRegisters.get(noble.getClassId().getId());
			
			for (L2PcInstance participant : classed)
			{
				if (participant.getObjectId() == noble.getObjectId())
				{
					sm = new SystemMessage(SystemMessageId.YOU_ARE_ALREADY_ON_THE_WAITING_LIST_TO_PARTICIPATE_IN_THE_GAME_FOR_YOUR_CLASS);
					noble.sendPacket(sm);
					return false;
				}
			}
		}
		
		if (isRegisteredInComp(noble))
		{
			sm = new SystemMessage(SystemMessageId.YOU_ARE_ALREADY_ON_THE_WAITING_LIST_FOR_ALL_CLASSES_WAITING_TO_PARTICIPATE_IN_THE_GAME);
			noble.sendPacket(sm);
			return false;
		}
		
		if (!nobles.containsKey(noble.getObjectId()))
		{
			StatsSet nobleStatsData = new StatsSet();
			nobleStatsData.set(CHAR_ID, noble.getObjectId());
			nobleStatsData.set(CLASS_ID, noble.getClassId().getId());
			nobleStatsData.set(CHAR_NAME, noble.getName());
			nobleStatsData.set(POINTS, DEFAULT_POINTS);
			nobleStatsData.set(COMP_DONE, 0);
			nobleStatsData.set(COMP_WON, 0);
			nobleStatsData.set(COMP_LOST, 0);
			nobleStatsData.set(COMP_DRAWN, 0);
			nobleStatsData.set("to_save", true);
			
			nobles.put(noble.getObjectId(), nobleStatsData);
		}
		
		if (classBased && getNoblePoints(noble.getObjectId()) < 3)
		{
			noble.sendMessage("Cant register when you have less than 3 points");
			return false;
		}
		if (!classBased && getNoblePoints(noble.getObjectId()) < 5)
		{
			noble.sendMessage("Cant register when you have less than 5 points");
			return false;
		}
		
		if (classBased)
		{
			if (classBasedRegisters.containsKey(noble.getClassId().getId()))
			{
				L2FastList<L2PcInstance> classed = classBasedRegisters.get(noble.getClassId().getId());
				classed.add(noble);
				
				classBasedRegisters.remove(noble.getClassId().getId());
				classBasedRegisters.put(noble.getClassId().getId(), classed);
			}
			else
			{
				L2FastList<L2PcInstance> classed = new L2FastList<>();
				classed.add(noble);
				
				classBasedRegisters.put(noble.getClassId().getId(), classed);
			}
			sm = new SystemMessage(SystemMessageId.YOU_HAVE_BEEN_REGISTERED_IN_A_WAITING_LIST_OF_CLASSIFIED_GAMES);
			noble.sendPacket(sm);
		}
		else
		{
			nonClassBasedRegisters.add(noble);
			sm = new SystemMessage(SystemMessageId.YOU_HAVE_BEEN_REGISTERED_IN_A_WAITING_LIST_OF_NO_CLASS_GAMES);
			noble.sendPacket(sm);
		}
		
		noble.setIsInOlympiadMode(true);
		
		return true;
	}
	
	protected static int getNobleCount()
	{
		return nobles.size();
	}
	
	protected StatsSet getNobleStats(int playerId)
	{
		return nobles.get(playerId);
	}
	
	protected void updateNobleStats(int playerId, StatsSet stats)
	{
		nobles.put(playerId, stats);
	}
	
	protected void updateNobleEomStats(int playerId, StatsSet stats)
	{
		nobles_eom.put(playerId, stats);
		saveOldNobleData(playerId);
	}
	
	protected static L2FastList<L2PcInstance> getRegisteredNonClassBased()
	{
		return nonClassBasedRegisters;
	}
	
	protected static Map<Integer, L2FastList<L2PcInstance>> getRegisteredClassBased()
	{
		return classBasedRegisters;
	}
	
	protected static L2FastList<Integer> hasEnoughRegisteredClassed()
	{
		L2FastList<Integer> result = new L2FastList<>();
		
		for (Integer classList : getRegisteredClassBased().keySet())
		{
			if (getRegisteredClassBased().get(classList).size() >= Config.ALT_OLY_CLASSED)
			{
				result.add(classList);
			}
		}
		
		if (!result.isEmpty())
		{
			return result;
		}
		return null;
	}
	
	protected static boolean hasEnoughRegisteredNonClassed()
	{
		return Olympiad.getRegisteredNonClassBased().size() >= Config.ALT_OLY_NONCLASSED;
	}
	
	protected static void clearRegistered()
	{
		nonClassBasedRegisters.clear();
		classBasedRegisters.clear();
	}
	
	public boolean isRegistered(L2PcInstance noble)
	{
		boolean result = false;
		
		if (nonClassBasedRegisters != null && nonClassBasedRegisters.contains(noble))
		{
			result = true;
		}
		else if (classBasedRegisters != null && classBasedRegisters.containsKey(noble.getClassId().getId()))
		{
			L2FastList<L2PcInstance> classed = classBasedRegisters.get(noble.getClassId().getId());
			if (classed != null && classed.contains(noble))
			{
				result = true;
			}
		}
		
		return result;
	}
	
	public boolean unRegisterNoble(L2PcInstance noble)
	{
		SystemMessage sm;
		
		if (!inCompPeriod)
		{
			sm = new SystemMessage(SystemMessageId.THE_OLYMPIAD_GAME_IS_NOT_CURRENTLY_IN_PROGRESS);
			noble.sendPacket(sm);
			return false;
		}
		
		if (!noble.isNoble())
		{
			sm = new SystemMessage(SystemMessageId.ONLY_NOBLESS_CAN_PARTICIPATE_IN_THE_OLYMPIAD);
			noble.sendPacket(sm);
			return false;
		}
		
		if (!isRegistered(noble))
		{
			sm = new SystemMessage(SystemMessageId.YOU_HAVE_NOT_BEEN_REGISTERED_IN_A_WAITING_LIST_OF_A_GAME);
			noble.sendPacket(sm);
			return false;
		}
		
		for (OlympiadGame game : OlympiadManager.getInstance().getOlympiadGames().values())
		{
			if (game == null)
			{
				continue;
			}
			
			if ((game.playerOne != null && game.playerOne.getObjectId() == noble.getObjectId()) || (game.playerTwo != null && game.playerTwo.getObjectId() == noble.getObjectId()))
			{
				noble.sendMessage("Can't deregister whilst you are already selected for a game");
				return false;
			}
		}
		
		if (nonClassBasedRegisters.contains(noble))
		{
			nonClassBasedRegisters.remove(noble);
		}
		else
		{
			L2FastList<L2PcInstance> classed = classBasedRegisters.get(noble.getClassId().getId());
			classed.remove(noble);
			
			classBasedRegisters.remove(noble.getClassId().getId());
			classBasedRegisters.put(noble.getClassId().getId(), classed);
		}
		
		sm = new SystemMessage(SystemMessageId.YOU_HAVE_BEEN_DELETED_FROM_THE_WAITING_LIST_OF_A_GAME);
		noble.sendPacket(sm);
		
		noble.setIsInOlympiadMode(false);
		
		return true;
	}
	
	public void removeDisconnectedCompetitor(L2PcInstance player)
	{
		if (OlympiadManager.getInstance().getOlympiadGame(player.getOlympiadGameId()) != null)
		{
			OlympiadManager.getInstance().getOlympiadGame(player.getOlympiadGameId()).handleDisconnect(player);
		}
		
		L2FastList<L2PcInstance> classed = classBasedRegisters.get(player.getClassId().getId());
		
		if (nonClassBasedRegisters.contains(player))
		{
			nonClassBasedRegisters.remove(player);
		}
		else if (classed != null && classed.contains(player))
		{
			classed.remove(player);
			
			classBasedRegisters.remove(player.getClassId().getId());
			classBasedRegisters.put(player.getClassId().getId(), classed);
		}
	}
	
	public void notifyCompetitorDamage(L2PcInstance player, int damage, int gameId)
	{
		if (OlympiadManager.getInstance().getOlympiadGames().get(gameId) != null)
		{
			OlympiadManager.getInstance().getOlympiadGames().get(gameId).addDamage(player, damage);
		}
	}
	
	private void updateCompStatus()
	{
		if (Calendar.getInstance().getTimeInMillis() > compStart.getTimeInMillis())
		{
			LOGGER.info("Olympiad game started: " + hourFormat.format(compStart.getTime()));
		}
		else
		{
			LOGGER.info("Olympiad game starts: " + hourFormat.format(compStart.getTime()));
		}
		
		scheduledCompStart = ThreadPoolManager.getInstance().scheduleGeneral(() ->
		{
			if (isOlympiadEnd())
			{
				return;
			}
			
			inCompPeriod = true;
			final OlympiadManager om = OlympiadManager.getInstance();
			
			Announcements.getInstance().announceToAll(new SystemMessage(SystemMessageId.THE_OLYMPIAD_GAME_HAS_STARTED));
			LOGGER.info("Olympiad System: Olympiad Game Started");
			
			final Thread olyCycle = new Thread(om);
			olyCycle.start();
			
			final long regEnd = getMillisToCompEnd() - 600000;
			if (regEnd > 0)
			{
				ThreadPoolManager.getInstance().scheduleGeneral(() -> Announcements.getInstance().announceToAll(new SystemMessage(SystemMessageId.OLYMPIAD_REGISTRATION_PERIOD_ENDED)), regEnd);
			}
			
			scheduledCompEnd = ThreadPoolManager.getInstance().scheduleGeneral(() ->
			{
				if (isOlympiadEnd())
				{
					return;
				}
				inCompPeriod = false;
				Announcements.getInstance().announceToAll(new SystemMessage(SystemMessageId.THE_OLYMPIAD_GAME_HAS_ENDED));
				LOGGER.info("Olympiad System: Olympiad Game Ended");
				
				while (OlympiadGame.battleStarted)
				{
					try
					{
						// wait 1 minutes for end of pendings games
						Thread.sleep(60000);
					}
					catch (final InterruptedException e)
					{
					}
				}
				saveOlympiadStatus();
				
				init();
			}, getMillisToCompEnd());
		}, getMillisToCompBegin());
	}
	
	private static long getMillisToOlympiadEnd()
	{
		return (olympiadEnd - Calendar.getInstance().getTimeInMillis());
	}
	
	public void manualSelectHeroes()
	{
		if (scheduledOlympiadEnd != null)
		{
			scheduledOlympiadEnd.cancel(true);
		}
		
		scheduledOlympiadEnd = ThreadPoolManager.getInstance().scheduleGeneral(new OlympiadEndTask(), 0);
	}
	
	protected static long getMillisToValidationEnd()
	{
		if (olympiadValidationEnd > Calendar.getInstance().getTimeInMillis())
		{
			return (olympiadValidationEnd - Calendar.getInstance().getTimeInMillis());
		}
		return 10L;
	}
	
	public boolean isOlympiadEnd()
	{
		return (period != 0);
	}
	
	protected void setNewOlympiadEnd()
	{
		if (Config.ALT_OLY_USE_CUSTOM_PERIOD_SETTINGS)
		{
			setNewOlympiadEndCustom();
			return;
		}
		
		SystemMessage sm = new SystemMessage(SystemMessageId.OLYMPIAD_PERIOD_S1_HAS_STARTED);
		sm.addNumber(currentCycle);
		
		Announcements.getInstance().announceToAll(sm);
		
		Calendar currentTime = Calendar.getInstance();
		currentTime.add(Calendar.MONTH, 1);
		currentTime.set(Calendar.DAY_OF_MONTH, 1);
		currentTime.set(Calendar.HOUR_OF_DAY, 12);
		currentTime.set(Calendar.MINUTE, 0);
		currentTime.set(Calendar.SECOND, 0);
		currentTime.set(Calendar.MILLISECOND, 0);
		olympiadEnd = currentTime.getTimeInMillis();
		
		Calendar nextChange = Calendar.getInstance();
		nextChange.set(Calendar.MILLISECOND, 0);
		nextWeeklyChange = nextChange.getTimeInMillis() + WEEKLY_PERIOD;
		scheduleWeeklyChange();
	}
	
	public boolean inCompPeriod()
	{
		return inCompPeriod;
	}
	
	private long getMillisToCompBegin()
	{
		if (compStart.getTimeInMillis() < Calendar.getInstance().getTimeInMillis() && compEnd > Calendar.getInstance().getTimeInMillis())
		{
			return 10L;
		}
		
		if (compStart.getTimeInMillis() > Calendar.getInstance().getTimeInMillis())
		{
			return (compStart.getTimeInMillis() - Calendar.getInstance().getTimeInMillis());
		}
		
		return setNewCompBegin();
	}
	
	private long setNewCompBegin()
	{
		compStart = Calendar.getInstance();
		compStart.set(Calendar.HOUR_OF_DAY, COMP_START_HOUR);
		compStart.set(Calendar.MINUTE, COMP_START_MINUTE);
		compStart.set(Calendar.SECOND, 0);
		compStart.set(Calendar.MILLISECOND, 0);
		compStart.add(Calendar.HOUR_OF_DAY, 24);
		compEnd = compStart.getTimeInMillis() + COMP_PERIOD;
		
		LOGGER.info("Olympiad System: New Schedule @ " + compStart.getTime());
		
		return (compStart.getTimeInMillis() - Calendar.getInstance().getTimeInMillis());
	}
	
	protected long getMillisToCompEnd()
	{
		return (compEnd - Calendar.getInstance().getTimeInMillis());
	}
	
	private long getMillisToWeekChange()
	{
		if (nextWeeklyChange > Calendar.getInstance().getTimeInMillis())
		{
			return (nextWeeklyChange - Calendar.getInstance().getTimeInMillis());
		}
		return 10L;
	}
	
	private void scheduleWeeklyChange()
	{
		if (Config.ALT_OLY_USE_CUSTOM_PERIOD_SETTINGS)
		{
			schedulePointsRestoreCustom();
			return;
		}
		
		scheduledWeeklyTask = ThreadPoolManager.getInstance().scheduleGeneralAtFixedRate(() ->
		{
			addWeeklyPoints();
			LOGGER.info("Olympiad System: Added weekly points to nobles");
			
			final Calendar nextChange = Calendar.getInstance();
			nextWeeklyChange = nextChange.getTimeInMillis() + WEEKLY_PERIOD;
		}, getMillisToWeekChange(), WEEKLY_PERIOD);
	}
	
	protected synchronized void addWeeklyPoints()
	{
		if (period == 1)
		{
			return;
		}
		
		for (Integer nobleId : nobles.keySet())
		{
			StatsSet nobleInfo = nobles.get(nobleId);
			int currentPoints = nobleInfo.getInteger(POINTS);
			currentPoints += WEEKLY_POINTS;
			nobleInfo.set(POINTS, currentPoints);
			
			updateNobleStats(nobleId, nobleInfo);
		}
	}
	
	public Map<Integer, String> getMatchList()
	{
		return OlympiadManager.getInstance().getAllTitles();
	}
	
	// returns the players for the given olympiad game Id
	public L2PcInstance[] getPlayers(int Id)
	{
		if (OlympiadManager.getInstance().getOlympiadGame(Id) == null)
		{
			return null;
		}
		return OlympiadManager.getInstance().getOlympiadGame(Id).getPlayers();
	}
	
	public int getCurrentCycle()
	{
		return currentCycle;
	}
	
	public static void addSpectator(int id, L2PcInstance spectator, boolean storeCoords)
	{
		if (getInstance().isRegisteredInComp(spectator))
		{
			spectator.sendPacket(new SystemMessage(SystemMessageId.WHILE_YOU_ARE_ON_THE_WAITING_LIST_YOU_ARE_NOT_ALLOWED_TO_WATCH_THE_GAME));
			return;
		}
		
		OlympiadManager.STADIUMS[id].addSpectator(id, spectator, storeCoords);
		if (OlympiadManager.getInstance().getOlympiadGame(id) != null)
		{
			OlympiadManager.getInstance().getOlympiadGame(id).sendPlayersStatus(spectator);
		}
	}
	
	public static int getSpectatorArena(L2PcInstance player)
	{
		for (int i = 0; i < OlympiadManager.STADIUMS.length; i++)
		{
			if (OlympiadManager.STADIUMS[i].getSpectators().contains(player))
			{
				return i;
			}
		}
		return -1;
	}
	
	public static void removeSpectator(int id, L2PcInstance spectator)
	{
		OlympiadManager.STADIUMS[id].removeSpectator(spectator);
	}
	
	public List<L2PcInstance> getSpectators(int olympiadGameId)
	{
		if (OlympiadManager.getInstance().getOlympiadGame(olympiadGameId) == null)
		{
			return null;
		}
		return OlympiadManager.STADIUMS[olympiadGameId].getSpectators();
	}
	
	public Map<Integer, OlympiadGame> getOlympiadGames()
	{
		return OlympiadManager.getInstance().getOlympiadGames();
	}
	
	public boolean playerInStadia(final L2PcInstance player)
	{
		return (OlympiadStadiaManager.getInstance().getStadium(player) != null);
	}
	
	public int[] getWaitingList()
	{
		final int[] array = new int[2];
		
		if (!inCompPeriod())
		{
			return null;
		}
		
		int classCount = 0;
		
		if (!classBasedRegisters.isEmpty())
		{
			for (L2FastList<L2PcInstance> classed : classBasedRegisters.values())
			{
				classCount += classed.size();
			}
		}
		
		array[0] = classCount;
		array[1] = nonClassBasedRegisters.size();
		
		return array;
	}
	
	/**
	 * Save noblesse data to database
	 */
	protected synchronized void saveNobleData()
	{
		if (nobles == null || nobles.isEmpty())
		{
			return;
		}
		
		try (Connection con = L2DatabaseFactory.getInstance().getConnection())
		{
			for (int nobleId : nobles.keySet())
			{
				StatsSet nobleInfo = nobles.get(nobleId);
				
				int charId = nobleId;
				int classId = nobleInfo.getInteger(CLASS_ID);
				int points = nobleInfo.getInteger(POINTS);
				int compDone = nobleInfo.getInteger(COMP_DONE);
				int compWon = nobleInfo.getInteger(COMP_WON);
				int compLost = nobleInfo.getInteger(COMP_LOST);
				int compDrawn = nobleInfo.getInteger(COMP_DRAWN);
				boolean toSave = nobleInfo.getBool("to_save");
				
				if (toSave)
				{
					try (PreparedStatement statement = con.prepareStatement(INSERT_OLYMPIAD_NOBLES))
					{
						statement.setInt(1, charId);
						statement.setInt(2, classId);
						statement.setInt(3, points);
						statement.setInt(4, compDone);
						statement.setInt(5, compWon);
						statement.setInt(6, compLost);
						statement.setInt(7, compDrawn);
						
						nobleInfo.set("to_save", false);
						
						updateNobleStats(nobleId, nobleInfo);
						
						statement.executeUpdate();
					}
				}
				else
				{
					try (PreparedStatement statement = con.prepareStatement(UPDATE_OLYMPIAD_NOBLES))
					{
						statement.setInt(1, points);
						statement.setInt(2, compDone);
						statement.setInt(3, compWon);
						statement.setInt(4, compLost);
						statement.setInt(5, compDrawn);
						statement.setInt(6, charId);
						
						statement.executeUpdate();
					}
				}
			}
		}
		catch (Exception e)
		{
			LOGGER.error("Olympiad.saveNobleData : Failed to save noblesse data to database: ", e);
		}
	}
	
	/**
	 * Save noblesse data to database
	 * @param nobleId
	 */
	protected synchronized void saveOldNobleData(int nobleId)
	{
		try (Connection con = L2DatabaseFactory.getInstance().getConnection();
			PreparedStatement statement = con.prepareStatement(UPDATE_OLYMPIAD_NOBLES_EOM))
		{
			StatsSet nobleInfo = nobles_eom.get(nobleId);
			
			int charId = nobleId;
			int points = nobleInfo.getInteger(POINTS);
			int compDone = nobleInfo.getInteger(COMP_DONE);
			int compWon = nobleInfo.getInteger(COMP_WON);
			int compLost = nobleInfo.getInteger(COMP_LOST);
			int compDrawn = nobleInfo.getInteger(COMP_DRAWN);
			
			statement.setInt(1, points);
			statement.setInt(2, compDone);
			statement.setInt(3, compWon);
			statement.setInt(4, compLost);
			statement.setInt(5, compDrawn);
			statement.setInt(6, charId);
			statement.executeUpdate();
		}
		catch (Exception e)
		{
			LOGGER.error("Olympiad.saveOldNobleData : Failed to save old noblesse data to database: ", e);
		}
	}
	
	/**
	 * Save olympiad.properties file with current olympiad status and update noblesse table in database
	 */
	public void saveOlympiadStatus()
	{
		saveNobleData();
		saveOlympiadData();
		
		LOGGER.info("Olympiad System: Data saved!!");
	}
	
	private void saveOlympiadData()
	{
		try (Connection con = L2DatabaseFactory.getInstance().getConnection();
			PreparedStatement pstDelete = con.prepareStatement(DELETE_OLYMPIAD_DATA);
			PreparedStatement pstInsert = con.prepareStatement(INSERT_OLYMPIAD_DATA))
		{
			pstDelete.executeUpdate();
			
			pstInsert.setInt(1, currentCycle);
			pstInsert.setInt(2, period);
			pstInsert.setLong(3, olympiadEnd);
			pstInsert.setLong(4, olympiadValidationEnd);
			pstInsert.setLong(5, nextWeeklyChange);
			pstInsert.executeUpdate();
		}
		catch (Exception e)
		{
			LOGGER.error("Olympiad.saveOlympiadStatus : Could not insert data to olympiad_data table in database ", e);
		}
	}
	
	protected void updateMonthlyData()
	{
		try (Connection con = L2DatabaseFactory.getInstance().getConnection())
		{
			try (PreparedStatement statement = con.prepareStatement(TRUNCATE_OLYMPIAD_NOBLES_EOM))
			{
				statement.execute();
			}
			
			try (PreparedStatement statement = con.prepareStatement(INSERT_OLYMPIAD_NOBLES_DATA_INTO_OLYMPIAD_NOBLES_EOM))
			{
				statement.executeUpdate();
			}
		}
		catch (Exception e)
		{
			LOGGER.error("Olympiad.updateMonthlyData : Failed to update monthly noblese data", e);
		}
	}
	
	protected void sortHerosToBe()
	{
		if (period != 1)
		{
			return;
		}
		
		if (nobles != null)
		{
			for (Integer nobleId : nobles.keySet())
			{
				StatsSet nobleInfo = nobles.get(nobleId);
				
				int charId = nobleId;
				int classId = nobleInfo.getInteger(CLASS_ID);
				String charName = nobleInfo.getString(CHAR_NAME);
				int points = nobleInfo.getInteger(POINTS);
				int compDone = nobleInfo.getInteger(COMP_DONE);
				
				logResult(charName, "", Double.valueOf(charId), Double.valueOf(classId), compDone, points, "noble-charId-classId-compdone-points", 0, "");
			}
		}
		
		try (Connection con = L2DatabaseFactory.getInstance().getConnection())
		{
			for (int classId : HERO_IDS)
			{
				try (PreparedStatement statement = con.prepareStatement(SELECT_CANDIDATES_TO_HERO))
				{
					statement.setInt(1, classId);
					statement.setInt(2, Config.ALT_OLY_NUMBER_HEROS_EACH_CLASS);
					
					try (ResultSet rset = statement.executeQuery())
					{
						while (rset.next())
						{
							StatsSet hero = new StatsSet();
							hero.set(CHAR_ID, rset.getInt(CHAR_ID));
							hero.set(CHAR_NAME, rset.getString(CHAR_NAME));
							hero.set(CLASS_ID, classId);
							
							logResult(hero.getString(CHAR_NAME), "", hero.getDouble(CHAR_ID), hero.getDouble(CLASS_ID), 0, 0, "awarded hero", 0, "");
							heroesToBe.add(hero);
						}
					}
				}
			}
		}
		catch (Exception e)
		{
			LOGGER.error("Olympiad.sortHerosToBe : Couldnt load heros from DB", e);
		}
	}
	
	public List<String> getClassLeaderBoard(int classId)
	{
		List<String> names = new ArrayList<>();
		String sql = SELECT_CURRENT_CLASS_LEADER_BY_CLASS_ID;
		
		if (Config.ALT_OLY_SHOW_MONTHLY_WINNERS)
		{
			sql = SELECT_CLASS_LEADER_BY_CLASS_ID;
		}
		
		try (Connection con = L2DatabaseFactory.getInstance().getConnection();
			PreparedStatement statement = con.prepareStatement(sql))
		{
			statement.setInt(1, classId);
			
			try (ResultSet rset = statement.executeQuery())
			{
				while (rset.next())
				{
					names.add(rset.getString(CHAR_NAME));
				}
			}
		}
		catch (Exception e)
		{
			LOGGER.warn("Olympiad.getClassLeaderBoard : Couldnt load olympiad leaders from DB", e);
		}
		
		return names;
	}
	
	protected void giveHeroBonus()
	{
		if (heroesToBe.size() == 0)
		{
			return;
		}
		
		for (StatsSet hero : heroesToBe)
		{
			int charId = hero.getInteger(CHAR_ID);
			
			StatsSet noble = nobles.get(charId);
			int currentPoints = noble.getInteger(POINTS);
			currentPoints += Config.ALT_OLY_HERO_POINTS;
			noble.set(POINTS, currentPoints);
			
			updateNobleStats(charId, noble);
		}
	}
	
	public int getNoblessePasses(int objId)
	{
		if (period == 1)
		{
			if (nobles.isEmpty())
			{
				return 0;
			}
			
			StatsSet noble = nobles.get(objId);
			
			int points = noble.getInteger(POINTS);
			
			if (points <= Config.ALT_OLY_MIN_POINT_FOR_EXCH)
			{
				return 0;
			}
			
			noble.set(POINTS, 0);
			updateNobleStats(objId, noble);
			
			points *= Config.ALT_OLY_GP_PER_POINT;
			
			return points;
		}
		
		if (nobles_eom.isEmpty())
		{
			return 0;
		}
		
		StatsSet noble = nobles_eom.get(objId);
		
		if (noble == null)
		{
			return 0;
		}
		
		int points = noble.getInteger(POINTS);
		
		if (points <= Config.ALT_OLY_MIN_POINT_FOR_EXCH)
		{
			return 0;
		}
		
		noble.set(POINTS, 0);
		updateNobleEomStats(objId, noble);
		
		points *= Config.ALT_OLY_GP_PER_POINT;
		
		return points;
	}
	
	public boolean isRegisteredInComp(L2PcInstance player)
	{
		boolean result = isRegistered(player);
		
		if (inCompPeriod)
		{
			for (final OlympiadGame game : OlympiadManager.getInstance().getOlympiadGames().values())
			{
				if ((game.playerOne != null && game.playerOne.getObjectId() == player.getObjectId()) || (game.playerTwo != null && game.playerTwo.getObjectId() == player.getObjectId()))
				{
					result = true;
					break;
				}
			}
		}
		
		return result;
	}
	
	public int getNoblePoints(int objId)
	{
		if (nobles.isEmpty())
		{
			return 0;
		}
		
		StatsSet noble = nobles.get(objId);
		
		int points = noble.getInteger(POINTS);
		
		return points;
	}
	
	public int getCompetitionDone(int objId)
	{
		if (nobles.isEmpty())
		{
			return 0;
		}
		
		StatsSet noble = nobles.get(objId);
		
		int points = noble.getInteger(COMP_DONE);
		
		return points;
	}
	
	public int getCompetitionWon(int objId)
	{
		if (nobles.isEmpty())
		{
			return 0;
		}
		
		StatsSet noble = nobles.get(objId);
		
		int points = noble.getInteger(COMP_WON);
		
		return points;
	}
	
	public int getCompetitionLost(int objId)
	{
		if (nobles.isEmpty())
		{
			return 0;
		}
		
		StatsSet noble = nobles.get(objId);
		
		int points = noble.getInteger(COMP_LOST);
		
		return points;
	}
	
	protected void deleteNobles()
	{
		try (Connection con = L2DatabaseFactory.getInstance().getConnection();
			PreparedStatement statement = con.prepareStatement(TRUNCATE_OLYMPIAD_NOBLES))
		{
			statement.execute();
		}
		catch (Exception e)
		{
			LOGGER.error("Olympiad.deleteNobles : Couldnt delete nobles from DB", e);
		}
		
		nobles_eom.clear();
		nobles_eom = nobles;
		nobles.clear();
	}
	
	/**
	 * Logs result of Olympiad to a csv file.
	 * @param playerOne
	 * @param playerTwo
	 * @param p1hp
	 * @param p2hp
	 * @param p1dmg
	 * @param p2dmg
	 * @param result
	 * @param points
	 * @param classed
	 */
	public static synchronized void logResult(final String playerOne, final String playerTwo, final Double p1hp, final Double p2hp, final int p1dmg, final int p2dmg, final String result, final int points, final String classed)
	{
		if (!Config.ALT_OLY_LOG_FIGHTS)
		{
			return;
		}
		
		SimpleDateFormat formatter;
		formatter = new SimpleDateFormat("dd/MM/yyyy H:mm:ss");
		final String date = formatter.format(new Date());
		FileWriter save = null;
		try
		{
			final File file = new File("log/olympiad.csv");
			
			boolean writeHead = false;
			if (!file.exists())
			{
				writeHead = true;
			}
			
			save = new FileWriter(file, true);
			
			if (writeHead)
			{
				final String header = "Date,Player1,Player2,Player1 HP,Player2 HP,Player1 Damage,Player2 Damage,Result,Points,Classed\r\n";
				save.write(header);
			}
			
			final String out = date + "," + playerOne + "," + playerTwo + "," + p1hp + "," + p2hp + "," + p1dmg + "," + p2dmg + "," + result + "," + points + "," + classed + "\r\n";
			save.write(out);
		}
		catch (final IOException e)
		{
			LOGGER.warn("Olympiad System: Olympiad LOGGER could not be saved: ", e);
		}
		finally
		{
			try
			{
				if (save != null)
				{
					save.close();
				}
			}
			catch (final Exception e)
			{
			}
		}
	}
	
	public static void sendMatchList(L2PcInstance player)
	{
		NpcHtmlMessage message = new NpcHtmlMessage(0);
		StringBuilder replyMSG = new StringBuilder("<html><body>");
		replyMSG.append("<center><br>Grand Olympiad Game View<table width=270 border=0 bgcolor=\"000000\">");
		replyMSG.append("<tr><td fixwidth=30>NO.</td><td fixwidth=60>Status</td><td>Player1 / Player2</td></tr>");
		
		Map<Integer, String> matches = getInstance().getMatchList();
		for (int i = 0; i < Olympiad.getStadiumCount(); i++)
		{
			int arenaID = i + 1;
			String players = "&nbsp;";
			String state = "Initial State";
			if (matches.containsKey(i))
			{
				state = "In Progress";
				players = matches.get(i);
			}
			replyMSG.append("<tr><td fixwidth=30><a action=\"bypass -h OlympiadArenaChange " + i + "\">" + arenaID + "</a></td><td fixwidth=60>" + state + "</td><td>" + players + "</td></tr>");
		}
		replyMSG.append("</table></center></body></html>");
		
		message.setHtml(replyMSG.toString());
		player.sendPacket(message);
	}
	
	public static void bypassChangeArena(final String command, final L2PcInstance player)
	{
		final String[] commands = command.split(" ");
		final int id = Integer.parseInt(commands[1]);
		final int arena = getSpectatorArena(player);
		if (arena >= 0)
		{
			Olympiad.removeSpectator(arena, player);
		}
		Olympiad.addSpectator(id, player, false);
	}
	
	protected void setNewOlympiadEndCustom()
	{
		SystemMessage sm = new SystemMessage(SystemMessageId.OLYMPIAD_PERIOD_S1_HAS_STARTED);
		sm.addNumber(currentCycle);
		
		Announcements.getInstance().announceToAll(sm);
		
		Calendar currentTime = Calendar.getInstance();
		currentTime.set(Calendar.AM_PM, Calendar.AM);
		currentTime.set(Calendar.HOUR, 12);
		currentTime.set(Calendar.MINUTE, 0);
		currentTime.set(Calendar.SECOND, 0);
		
		Calendar nextChange = Calendar.getInstance();
		
		switch (Config.ALT_OLY_PERIOD)
		{
			case DAY:
			{
				currentTime.add(Calendar.DAY_OF_MONTH, Config.ALT_OLY_PERIOD_MULTIPLIER);
				currentTime.add(Calendar.DAY_OF_MONTH, -1); // last day is for validation
				
				if (Config.ALT_OLY_PERIOD_MULTIPLIER >= 14)
				{
					nextWeeklyChange = nextChange.getTimeInMillis() + WEEKLY_PERIOD;
				}
				else if (Config.ALT_OLY_PERIOD_MULTIPLIER >= 7)
				{
					nextWeeklyChange = nextChange.getTimeInMillis() + (WEEKLY_PERIOD / 2);
				}
				else
				{
					// nothing to do, too low period
				}
				
			}
				break;
			case WEEK:
			{
				currentTime.add(Calendar.WEEK_OF_MONTH, Config.ALT_OLY_PERIOD_MULTIPLIER);
				currentTime.add(Calendar.DAY_OF_MONTH, -1); // last day is for validation
				
				if (Config.ALT_OLY_PERIOD_MULTIPLIER > 1)
				{
					nextWeeklyChange = nextChange.getTimeInMillis() + WEEKLY_PERIOD;
				}
				else
				{
					nextWeeklyChange = nextChange.getTimeInMillis() + (WEEKLY_PERIOD / 2);
				}
				
			}
				break;
			case MONTH:
			{
				currentTime.add(Calendar.MONTH, Config.ALT_OLY_PERIOD_MULTIPLIER);
				currentTime.add(Calendar.DAY_OF_MONTH, -1); // last day is for validation
				
				nextWeeklyChange = nextChange.getTimeInMillis() + WEEKLY_PERIOD;
				
			}
				break;
		}
		
		olympiadEnd = currentTime.getTimeInMillis();
		
		scheduleWeeklyChange();
	}
	
	private void schedulePointsRestoreCustom()
	{
		long final_change_period = WEEKLY_PERIOD;
		
		switch (Config.ALT_OLY_PERIOD)
		{
			case DAY:
			{
				
				if (Config.ALT_OLY_PERIOD_MULTIPLIER < 10)
				{
					
					final_change_period = WEEKLY_PERIOD / 2;
					
				}
				
			}
				break;
			case WEEK:
			{
				
				if (Config.ALT_OLY_PERIOD_MULTIPLIER == 1)
				{
					final_change_period = WEEKLY_PERIOD / 2;
				}
				
			}
				break;
		}
		
		scheduledWeeklyTask = ThreadPoolManager.getInstance().scheduleGeneralAtFixedRate(new OlympiadPointsRestoreTask(final_change_period), getMillisToWeekChange(), final_change_period);
	}
	
	public static void olympiadEnd(L2PcInstance player)
	{
		long milliToEnd;
		if (period == 0)
		{
			milliToEnd = getMillisToOlympiadEnd();
		}
		else
		{
			milliToEnd = getMillisToValidationEnd();
		}
		
		double numSecs = milliToEnd / 1000 % 60;
		double countDown = (milliToEnd / 1000 - numSecs) / 60;
		int numMins = (int) Math.floor(countDown % 60);
		countDown = (countDown - numMins) / 60;
		int numHours = (int) Math.floor(countDown % 24);
		int numDays = (int) Math.floor((countDown - numHours) / 24);
		
		CreatureSay cs = new CreatureSay(0, Say2.ANNOUNCEMENT, "", "Olympiad period ends in " + numDays + " days, " + numHours + " hours and " + numMins + " mins.");
		player.sendPacket(cs);
	}
	
	class OlympiadPointsRestoreTask implements Runnable
	{
		private final long restoreTime;
		
		public OlympiadPointsRestoreTask(final long restoreTime)
		{
			this.restoreTime = restoreTime;
		}
		
		@Override
		public void run()
		{
			addWeeklyPoints();
			LOGGER.info("Olympiad System: Added points to nobles");
			
			final Calendar nextChange = Calendar.getInstance();
			nextWeeklyChange = nextChange.getTimeInMillis() + restoreTime;
		}
		
	}
}
