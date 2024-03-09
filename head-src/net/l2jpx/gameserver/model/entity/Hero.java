package net.l2jpx.gameserver.model.entity;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import net.l2jpx.Config;
import net.l2jpx.gameserver.datatables.sql.ClanTable;
import net.l2jpx.gameserver.model.L2Clan;
import net.l2jpx.gameserver.model.L2World;
import net.l2jpx.gameserver.model.actor.instance.L2ItemInstance;
import net.l2jpx.gameserver.model.actor.instance.L2PcInstance;
import net.l2jpx.gameserver.model.entity.olympiad.Olympiad;
import net.l2jpx.gameserver.network.SystemMessageId;
import net.l2jpx.gameserver.network.serverpackets.InventoryUpdate;
import net.l2jpx.gameserver.network.serverpackets.PledgeShowInfoUpdate;
import net.l2jpx.gameserver.network.serverpackets.SystemMessage;
import net.l2jpx.gameserver.network.serverpackets.UserInfo;
import net.l2jpx.gameserver.templates.L2Item;
import net.l2jpx.gameserver.templates.StatsSet;
import net.l2jpx.util.database.L2DatabaseFactory;

/**
 * @author godson
 * @author ReynalDev
 */
public class Hero
{
	private static final Logger LOGGER = Logger.getLogger(Hero.class);
	
	private static Hero instance;
	private static final String SELECT_HEROES = "SELECT charId, char_name, class_id, count, played FROM heroes";
	private static final String SELECT_HEROES_BY_PLAYED = "SELECT charId, char_name, class_id, count, played FROM heroes WHERE played=1";
	private static final String UPDATE_HEROES_SET_PLAYED_0 = "UPDATE heroes SET played=0";
	private static final String INSERT_HERO = "INSERT INTO heroes VALUES (?,?,?,?,?)";
	private static final String UPDATE_HERO_BY_CHAR_ID = "UPDATE heroes SET count = ?, played = ?  WHERE charId = ?";
	private static final String SELECT_CLAN_AND_ALLY = "SELECT characters.clanid AS clanid, coalesce(clan_data.ally_Id, 0) AS allyId FROM characters LEFT JOIN clan_data ON clan_data.clan_id = characters.clanid WHERE characters.obj_Id = ?";
	private static final String SELECT_CLAN_NAME = "SELECT clan_name FROM clan_data WHERE clan_id = (SELECT clanid FROM characters WHERE char_name = ?)";
	private static final String DELETE_HERO_ITEMS = "DELETE FROM items WHERE item_id IN (6842, 6611, 6612, 6613, 6614, 6615, 6616, 6617, 6618, 6619, 6620, 6621) AND owner_id NOT IN (SELECT obj_id FROM characters WHERE accesslevel > 0)";
	private static final List<Integer> HERO_ITEMS = Arrays.asList(6842, 6611, 6612, 6613, 6614, 6615, 6616, 6617, 6618, 6619, 6620, 6621);
	private static Map<Integer, StatsSet> heroesMap = new HashMap<>();
	private static Map<Integer, StatsSet> completeHeroes = new HashMap<>();
	
	public static final String COUNT = "count";
	public static final String PLAYED = "played";
	public static final String CLAN_NAME = "clan_name";
	public static final String CLAN_CREST = "clan_crest";
	public static final String ALLY_NAME = "ally_name";
	public static final String ALLY_CREST = "ally_crest";
	
	public static Hero getInstance()
	{
		if (instance == null)
		{
			instance = new Hero();
		}
		return instance;
	}
	
	public Hero()
	{
		init();
	}
	
	private void init()
	{
		try(Connection con = L2DatabaseFactory.getInstance().getConnection())
		{
			try(PreparedStatement selectHeroes = con.prepareStatement(SELECT_HEROES_BY_PLAYED);
				ResultSet resultSelectHeroes = selectHeroes.executeQuery())
			{
				while (resultSelectHeroes.next())
				{
					StatsSet hero = new StatsSet();
					int charId = resultSelectHeroes.getInt(Olympiad.CHAR_ID);
					hero.set(Olympiad.CHAR_NAME, resultSelectHeroes.getString(Olympiad.CHAR_NAME));
					hero.set(Olympiad.CLASS_ID, resultSelectHeroes.getInt(Olympiad.CLASS_ID));
					hero.set(COUNT, resultSelectHeroes.getInt(COUNT));
					hero.set(PLAYED, resultSelectHeroes.getInt(PLAYED));
					
					try(PreparedStatement selectClanAndAlly = con.prepareStatement(SELECT_CLAN_AND_ALLY))
					{
						selectClanAndAlly.setInt(1, charId);
						
						try(ResultSet resultSelectClanAndAlly = selectClanAndAlly.executeQuery())
						{
							if (resultSelectClanAndAlly.next())
							{
								int clanId = resultSelectClanAndAlly.getInt("clanid");
								int allyId = resultSelectClanAndAlly.getInt("allyId");
								String clanName = "";
								String allyName = "";
								int clanCrest = 0;
								int allyCrest = 0;
								
								if (clanId > 0)
								{
									clanName = ClanTable.getInstance().getClan(clanId).getName();
									clanCrest = ClanTable.getInstance().getClan(clanId).getCrestId();
									if (allyId > 0)
									{
										allyName = ClanTable.getInstance().getClan(clanId).getAllyName();
										allyCrest = ClanTable.getInstance().getClan(clanId).getAllyCrestId();
									}
								}
								
								hero.set(CLAN_CREST, clanCrest);
								hero.set(CLAN_NAME, clanName);
								hero.set(ALLY_CREST, allyCrest);
								hero.set(ALLY_NAME, allyName);
							}
							heroesMap.put(charId, hero);
						}
					}
				}
			}
			
			try(PreparedStatement selectAllHeroes = con.prepareStatement(SELECT_HEROES);
				ResultSet resultSelectAllHeroes = selectAllHeroes.executeQuery())
			{
				while (resultSelectAllHeroes.next())
				{
					StatsSet hero = new StatsSet();
					int charId = resultSelectAllHeroes.getInt(Olympiad.CHAR_ID);
					
					String charName = resultSelectAllHeroes.getString(Olympiad.CHAR_NAME);
					
					hero.set(Olympiad.CHAR_NAME, charName);
					hero.set(Olympiad.CLASS_ID, resultSelectAllHeroes.getInt(Olympiad.CLASS_ID));
					hero.set(COUNT, resultSelectAllHeroes.getInt(COUNT));
					hero.set(PLAYED, resultSelectAllHeroes.getInt(PLAYED));
					
					try(PreparedStatement selectClanAndAlly = con.prepareStatement(SELECT_CLAN_AND_ALLY))
					{
						selectClanAndAlly.setInt(1, charId);
						
						try(ResultSet resultSelectClanAndAlly = selectClanAndAlly.executeQuery())
						{
							if (resultSelectClanAndAlly.next())
							{
								int clanId = resultSelectClanAndAlly.getInt("clanid");
								int allyId = resultSelectClanAndAlly.getInt("allyId");
								String clanName = "";
								String allyName = "";
								int clanCrest = 0;
								int allyCrest = 0;
								
								if (clanId > 0)
								{
									L2Clan clan = ClanTable.getInstance().getClan(clanId);
									if (clan != null)
									{
										clanName = clan.getName();
										clanCrest = clan.getCrestId();
										if (allyId > 0)
										{
											allyName = clan.getAllyName();
											allyCrest = clan.getAllyCrestId();
										}
									}
									else
									{
										LOGGER.error("Hero System: Player " + charName + " has clan id " + clanId + " that is not present inside clanTable..");
									}
									
								}
								hero.set(CLAN_CREST, clanCrest);
								hero.set(CLAN_NAME, clanName);
								hero.set(ALLY_CREST, allyCrest);
								hero.set(ALLY_NAME, allyName);
							}
							completeHeroes.put(charId, hero);
						}
					}
				}
			}
		}
		catch (Exception e)
		{
			LOGGER.error("Hero System: Couldnt load Heroes", e);
		}
		
		if (!heroesMap.isEmpty())
		{
			LOGGER.info("Loaded " + heroesMap.size() + " Heroes.");
		}
		
		if (!completeHeroes.isEmpty())
		{
			LOGGER.info(" Loaded " + completeHeroes.size() + " all time Heroes.");
		}
	}
	
	public void putHero(final L2PcInstance player, final boolean isComplete)
	{
		try
		{
			if (Config.DEBUG)
			{
				LOGGER.info("Adding new hero");
				LOGGER.info("Name:" + player.getName());
				LOGGER.info("ClassId:" + player.getClassId().getId());
			}
			final StatsSet newHero = new StatsSet();
			newHero.set(Olympiad.CHAR_NAME, player.getName());
			newHero.set(Olympiad.CLASS_ID, player.getClassId().getId());
			newHero.set(COUNT, 1);
			newHero.set(PLAYED, 1);
			heroesMap.put(player.getObjectId(), newHero);
			if (isComplete)
			{
				completeHeroes.put(player.getObjectId(), newHero);
			}
		}
		catch (final Exception e)
		{
			/*   */
		}
	}
	
	public void deleteHero(final L2PcInstance player, final boolean isComplete)
	{
		final int objId = player.getObjectId();
		if (heroesMap.containsKey(objId))
		{
			heroesMap.remove(objId);
		}
		if (isComplete)
		{
			if (completeHeroes.containsKey(objId))
			{
				completeHeroes.remove(objId);
			}
		}
	}
	
	public Map<Integer, StatsSet> getHeroes()
	{
		return heroesMap;
	}
	
	public synchronized void computeNewHeroes(final List<StatsSet> newHeroes)
	{
		updateHeroes(true);
		L2ItemInstance[] items;
		InventoryUpdate iu;
		if (heroesMap.size() != 0)
		{
			for (final StatsSet hero : heroesMap.values())
			{
				final String name = hero.getString(Olympiad.CHAR_NAME);
				final L2PcInstance player = L2World.getInstance().getPlayerByName(name);
				if (player == null)
				{
					continue;
				}
				try
				{
					player.setHero(false);
					items = player.getInventory().unEquipItemInBodySlotAndRecord(L2Item.SLOT_LR_HAND);
					iu = new InventoryUpdate();
					for (final L2ItemInstance item : items)
					{
						iu.addModifiedItem(item);
					}
					player.sendPacket(iu);
					items = player.getInventory().unEquipItemInBodySlotAndRecord(L2Item.SLOT_R_HAND);
					iu = new InventoryUpdate();
					for (final L2ItemInstance item : items)
					{
						iu.addModifiedItem(item);
					}
					player.sendPacket(iu);
					items = player.getInventory().unEquipItemInBodySlotAndRecord(L2Item.SLOT_HAIR);
					iu = new InventoryUpdate();
					for (final L2ItemInstance item : items)
					{
						iu.addModifiedItem(item);
					}
					player.sendPacket(iu);
					items = player.getInventory().unEquipItemInBodySlotAndRecord(L2Item.SLOT_FACE);
					iu = new InventoryUpdate();
					for (final L2ItemInstance item : items)
					{
						iu.addModifiedItem(item);
					}
					player.sendPacket(iu);
					items = player.getInventory().unEquipItemInBodySlotAndRecord(L2Item.SLOT_DHAIR);
					iu = new InventoryUpdate();
					for (final L2ItemInstance item : items)
					{
						iu.addModifiedItem(item);
					}
					player.sendPacket(iu);
					for (final L2ItemInstance item : player.getInventory().getAvailableItems(false))
					{
						if (item == null)
						{
							continue;
						}
						if (!HERO_ITEMS.contains(item.getItemId()))
						{
							continue;
						}
						player.destroyItem("Hero", item, null, true);
						iu = new InventoryUpdate();
						iu.addRemovedItem(item);
						player.sendPacket(iu);
					}
					player.sendPacket(new UserInfo(player));
					player.broadcastUserInfo();
				}
				catch (final NullPointerException e)
				{
					/**/
				}
			}
		}
		if (newHeroes.size() == 0)
		{
			heroesMap.clear();
			return;
		}
		
		final Map<Integer, StatsSet> heroes = new HashMap<>();
		
		for (final StatsSet hero : newHeroes)
		{
			final int charId = hero.getInteger(Olympiad.CHAR_ID);
			if (completeHeroes != null && completeHeroes.containsKey(charId))
			{
				final StatsSet oldHero = completeHeroes.get(charId);
				final int count = oldHero.getInteger(COUNT);
				oldHero.set(COUNT, count + 1);
				oldHero.set(PLAYED, 1);
				heroes.put(charId, oldHero);
			}
			else
			{
				final StatsSet newHero = new StatsSet();
				newHero.set(Olympiad.CHAR_NAME, hero.getString(Olympiad.CHAR_NAME));
				newHero.set(Olympiad.CLASS_ID, hero.getInteger(Olympiad.CLASS_ID));
				newHero.set(COUNT, 1);
				newHero.set(PLAYED, 1);
				heroes.put(charId, newHero);
			}
		}
		deleteItemsInDb();
		heroesMap.clear();
		heroesMap.putAll(heroes);
		heroes.clear();
		updateHeroes(false);
		for (final StatsSet hero : heroesMap.values())
		{
			final String name = hero.getString(Olympiad.CHAR_NAME);
			final L2PcInstance player = L2World.getInstance().getPlayerByName(name);
			if (player != null)
			{
				player.setHero(true);
				final L2Clan clan = player.getClan();
				if (clan != null)
				{
					clan.setReputationScore(clan.getReputationScore() + 1000, true);
					clan.broadcastToOnlineMembers(new PledgeShowInfoUpdate(clan));
					final SystemMessage sm = new SystemMessage(SystemMessageId.CLAN_MEMBER_S1_BECAME_HERO_AND_GAINED_S2_REPUTATION_POINTS);
					sm.addString(name);
					sm.addNumber(1000);
					clan.broadcastToOnlineMembers(sm);
				}
				player.sendPacket(new UserInfo(player));
				player.broadcastUserInfo();
			}
			else
			{
				try (Connection con = L2DatabaseFactory.getInstance().getConnection();
					PreparedStatement statement = con.prepareStatement(SELECT_CLAN_NAME))
				{
					statement.setString(1, name);
					
					try (ResultSet rset = statement.executeQuery())
					{
						if (rset.next())
						{
							String clanName = rset.getString("clan_name");
							if (clanName != null)
							{
								L2Clan clan = ClanTable.getInstance().getClanByName(clanName);
								if (clan != null)
								{
									clan.setReputationScore(clan.getReputationScore() + 1000, true);
									clan.broadcastToOnlineMembers(new PledgeShowInfoUpdate(clan));
									SystemMessage sm = new SystemMessage(SystemMessageId.CLAN_MEMBER_S1_BECAME_HERO_AND_GAINED_S2_REPUTATION_POINTS);
									sm.addString(name);
									sm.addNumber(1000);
									clan.broadcastToOnlineMembers(sm);
								}
							}
						}
					}
				}
				catch (Exception e)
				{
					LOGGER.error("could not get clan name of " + name + ": " + e);
				}
			}
		}
	}
	
	public void updateHeroes(boolean setDefault)
	{
		try(Connection con = L2DatabaseFactory.getInstance().getConnection())
		{
			if (setDefault)
			{
				try(PreparedStatement statement = con.prepareStatement(UPDATE_HEROES_SET_PLAYED_0))
				{
					statement.executeUpdate();
				}
			}
			else
			{
				for (Integer heroId : heroesMap.keySet())
				{
					StatsSet hero = heroesMap.get(heroId);
					if (completeHeroes == null || !completeHeroes.containsKey(heroId))
					{
						try(PreparedStatement statement = con.prepareStatement(INSERT_HERO))
						{
							statement.setInt(1, heroId);
							statement.setString(2, hero.getString(Olympiad.CHAR_NAME));
							statement.setInt(3, hero.getInteger(Olympiad.CLASS_ID));
							statement.setInt(4, hero.getInteger(COUNT));
							statement.setInt(5, hero.getInteger(PLAYED));
							statement.executeUpdate();
						}
						
						try(PreparedStatement statement2 = con.prepareStatement(SELECT_CLAN_AND_ALLY))
						{
							statement2.setInt(1, heroId);
							
							try(ResultSet rset2 = statement2.executeQuery())
							{
								if (rset2.next())
								{
									int clanId = rset2.getInt("clanid");
									int allyId = rset2.getInt("allyId");
									String clanName = "";
									String allyName = "";
									int clanCrest = 0;
									int allyCrest = 0;
									
									if (clanId > 0)
									{
										clanName = ClanTable.getInstance().getClan(clanId).getName();
										clanCrest = ClanTable.getInstance().getClan(clanId).getCrestId();
										if (allyId > 0)
										{
											allyName = ClanTable.getInstance().getClan(clanId).getAllyName();
											allyCrest = ClanTable.getInstance().getClan(clanId).getAllyCrestId();
										}
									}
									
									hero.set(CLAN_CREST, clanCrest);
									hero.set(CLAN_NAME, clanName);
									hero.set(ALLY_CREST, allyCrest);
									hero.set(ALLY_NAME, allyName);
								}
							}
						}
						
						heroesMap.put(heroId, hero);
						completeHeroes.put(heroId, hero);
					}
					else
					{
						try(PreparedStatement statement = con.prepareStatement(UPDATE_HERO_BY_CHAR_ID))
						{
							statement.setInt(1, hero.getInteger(COUNT));
							statement.setInt(2, hero.getInteger(PLAYED));
							statement.setInt(3, heroId);
							statement.executeUpdate();
						}
					}
				}
			}
		}
		catch (Exception e)
		{
			LOGGER.error("Hero System: Couldnt update Heroes", e);
		}
	}
	
	public List<Integer> getHeroItems()
	{
		return HERO_ITEMS;
	}
	
	private void deleteItemsInDb()
	{
		try (Connection con = L2DatabaseFactory.getInstance().getConnection();
			PreparedStatement statement = con.prepareStatement(DELETE_HERO_ITEMS))
		{
			statement.executeUpdate();
		}
		catch (SQLException e)
		{
			LOGGER.error("Hero.deleteItemsInDb : Could not delete items in db", e);
		}
	}
}