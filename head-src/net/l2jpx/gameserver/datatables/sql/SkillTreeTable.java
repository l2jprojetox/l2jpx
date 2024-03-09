package net.l2jpx.gameserver.datatables.sql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import net.l2jpx.gameserver.datatables.SkillTable;
import net.l2jpx.gameserver.model.L2EnchantSkillLearn;
import net.l2jpx.gameserver.model.L2PledgeSkillLearn;
import net.l2jpx.gameserver.model.L2Skill;
import net.l2jpx.gameserver.model.L2SkillLearn;
import net.l2jpx.gameserver.model.actor.instance.L2PcInstance;
import net.l2jpx.gameserver.model.base.ClassId;
import net.l2jpx.gameserver.skills.holders.ISkillsHolder;
import net.l2jpx.gameserver.skills.holders.PlayerSkillHolder;
import net.l2jpx.util.database.L2DatabaseFactory;

/**
 * @author ReynalDev
 */
public class SkillTreeTable
{
	private static final  Logger LOGGER = Logger.getLogger(SkillTreeTable.class);
	private static SkillTreeTable instance;
	
	private static final String SELECT_SKILL_TREES = "SELECT class_id, skill_id, level, name, sp, min_level FROM class_skill_trees where class_id=? ORDER BY skill_id, level";
	private static final String SELECT_FISHING_SKILL_TREES = "SELECT skill_id, level, name, sp, min_level, costid, cost, isfordwarf FROM fishing_skill_trees ORDER BY skill_id, level";
	private static final String SELECT_ENCHANT_SKILL_TREES = "SELECT skill_id, level, name, base_lvl, sp, min_skill_lvl, exp, success_rate76, success_rate77, success_rate78,success_rate79,success_rate80 FROM enchant_skill_trees ORDER BY skill_id, level";
	private static final String SELECT_CLAN_SKILL_TREES = "SELECT skill_id, level, name, clan_lvl, repCost, itemId FROM clan_skill_trees ORDER BY skill_id, level";
	
	private Map<ClassId, Map<Integer, L2SkillLearn>> skillTrees;
	private List<L2SkillLearn> fishingSkillTrees = new ArrayList<>(); // all common skills (teached by Fisherman)
	private List<L2SkillLearn> expandDwarfCraftSkillTrees = new ArrayList<>(); // list of special skill for dwarf (expand dwarf craft) learned by class teacher
	private List<L2EnchantSkillLearn> enchantSkillTrees = new ArrayList<>(); // enchant skill list
	private List<L2PledgeSkillLearn> pledgeSkillTrees = new ArrayList<>(); // pledge skill list
	
	private SkillTreeTable()
	{
		loadClassSkillTrees();
		loadFishingSkillTrees();
		loadEnchantSkillTrees();
		loadClanSkillTrees();
	}
	
	private void loadClassSkillTrees()
	{
		int classId = 0;
		int count = 0;
		
		try(Connection con = L2DatabaseFactory.getInstance().getConnection())
		{
			for (ClassId class_id : ClassId.getClassList())
			{
				Map<Integer, L2SkillLearn> map = new HashMap<>();
				int parentClassId = -1;
				
				if(class_id.getParent() != null)
				{
					parentClassId = class_id.getParent().getId();
				}
					
				classId = class_id.getId();
				
				try(PreparedStatement statement2 = con.prepareStatement(SELECT_SKILL_TREES))
				{
					statement2.setInt(1, classId);
					
					try(ResultSet skilltree = statement2.executeQuery())
					{
						if (parentClassId != -1)
						{
							Map<Integer, L2SkillLearn> parentMap = getSkillTrees().get(ClassId.values()[parentClassId]);
							map.putAll(parentMap);
						}
						
						int prevSkillId = -1;
						
						while (skilltree.next())
						{
							int id = skilltree.getInt("skill_id");
							int lvl = skilltree.getInt("level");
							String name = skilltree.getString("name");
							int minLvl = skilltree.getInt("min_level");
							int cost = skilltree.getInt("sp");
							
							if (prevSkillId != id)
							{
								prevSkillId = id;
							}
							
							L2SkillLearn skillLearn = new L2SkillLearn(id, lvl, minLvl, name, cost, 0, 0);
							map.put(SkillTable.getSkillHashCode(id, lvl), skillLearn);
						}
						
						getSkillTrees().put(ClassId.values()[classId], map);
						count += map.size();
					}
				}
			}
			
			LOGGER.info("SkillTreeTable: Loaded " + count + " class skills.");
		}
		catch (Exception e)
		{
			LOGGER.error("SkillTreeTable.SkillTreeTable: Error while creating skill tree (Class ID " + classId + ")", e);
		}
	}
	
	private void loadFishingSkillTrees()
	{
		try(Connection con = L2DatabaseFactory.getInstance().getConnection();
			PreparedStatement statement = con.prepareStatement(SELECT_FISHING_SKILL_TREES);
			ResultSet skilltree2 = statement.executeQuery())
		{
			int prevSkillId = -1;
			
			while (skilltree2.next())
			{
				int id = skilltree2.getInt("skill_id");
				int lvl = skilltree2.getInt("level");
				String name = skilltree2.getString("name");
				int minLvl = skilltree2.getInt("min_level");
				int cost = skilltree2.getInt("sp");
				int costId = skilltree2.getInt("costid");
				int costCount = skilltree2.getInt("cost");
				int isDwarven = skilltree2.getInt("isfordwarf");
				
				if (prevSkillId != id)
				{
					prevSkillId = id;
				}
				
				L2SkillLearn skill = new L2SkillLearn(id, lvl, minLvl, name, cost, costId, costCount);
				
				if (isDwarven == 0)
				{
					fishingSkillTrees.add(skill);
				}
				else
				{
					expandDwarfCraftSkillTrees.add(skill);
				}
			}
			
			LOGGER.info("SkillTreeTable: Loaded " + fishingSkillTrees.size() + " fishing skills.");
			LOGGER.info("SkillTreeTable: Loaded " + expandDwarfCraftSkillTrees.size() + " fishing dwarven skills.");
		}
		catch (Exception e)
		{
			LOGGER.error("Error while creating fishing skill table", e);
		}
	}
	
	private void loadEnchantSkillTrees()
	{
		try(Connection con = L2DatabaseFactory.getInstance().getConnection();
			PreparedStatement statement = con.prepareStatement(SELECT_ENCHANT_SKILL_TREES);
			ResultSet skilltree3 = statement.executeQuery())
		{
			int prevSkillId = -1;
			
			while (skilltree3.next())
			{
				int id = skilltree3.getInt("skill_id");
				int lvl = skilltree3.getInt("level");
				String name = skilltree3.getString("name");
				int baseLvl = skilltree3.getInt("base_lvl");
				int minSkillLvl = skilltree3.getInt("min_skill_lvl");
				int sp = skilltree3.getInt("sp");
				int exp = skilltree3.getInt("exp");
				byte rate76 = skilltree3.getByte("success_rate76");
				byte rate77 = skilltree3.getByte("success_rate77");
				byte rate78 = skilltree3.getByte("success_rate78");
				byte rate79 = skilltree3.getByte("success_rate79");
				byte rate80 = skilltree3.getByte("success_rate80");
				
				if (prevSkillId != id)
				{
					prevSkillId = id;
				}
				
				enchantSkillTrees.add(new L2EnchantSkillLearn(id, lvl, minSkillLvl, baseLvl, name, sp, exp, rate76, rate77, rate78, rate79, rate80));
			}
			
			LOGGER.info("SkillTreeTable: Loaded " + enchantSkillTrees.size() + " enchant skills.");
		}
		catch (Exception e)
		{
			LOGGER.error("Error while creating enchant skill table", e);
		}
	}
	
	private void loadClanSkillTrees()
	{
		try(Connection con = L2DatabaseFactory.getInstance().getConnection();
			PreparedStatement statement = con.prepareStatement(SELECT_CLAN_SKILL_TREES);
			ResultSet skilltree4 = statement.executeQuery())
		{
			int prevSkillId = -1;
			
			while (skilltree4.next())
			{
				int id = skilltree4.getInt("skill_id");
				int lvl = skilltree4.getInt("level");
				String name = skilltree4.getString("name");
				int baseLvl = skilltree4.getInt("clan_lvl");
				int sp = skilltree4.getInt("repCost");
				int itemId = skilltree4.getInt("itemId");
				
				if (prevSkillId != id)
				{
					prevSkillId = id;
				}
				
				pledgeSkillTrees.add(new L2PledgeSkillLearn(id, lvl, baseLvl, name, sp, itemId));
			}
			
			LOGGER.info("SkillTreeTable: Loaded " + pledgeSkillTrees.size() + " clan skills");
		}
		catch (Exception e)
		{
			LOGGER.error("Error while creating clan skill table", e);
		}
	}
	
	public static SkillTreeTable getInstance()
	{
		if (instance == null)
		{
			instance = new SkillTreeTable();
		}
		
		return instance;
	}
	
	/**
	 * Return the minimum level needed to have this Expertise.<BR>
	 * <BR>
	 * @param  grade The grade level searched
	 * @return
	 */
	public int getExpertiseLevel(final int grade)
	{
		if (grade <= 0)
		{
			return 0;
		}
		
		// since expertise comes at same level for all classes we use paladin for now
		final Map<Integer, L2SkillLearn> learnMap = getSkillTrees().get(ClassId.Paladin);
		
		final int skillHashCode = SkillTable.getSkillHashCode(239, grade);
		
		if (learnMap.containsKey(skillHashCode))
		{
			return learnMap.get(skillHashCode).getMinLevel();
		}
		
		return 0;
	}
	
	/**
	 * Each class receives new skill on certain levels, this methods allow the retrieval of the minimum character level of given class required to learn a given skill
	 * @param  skillId  The iD of the skill
	 * @param  classId  The classId of the character
	 * @param  skillLvl The SkillLvl
	 * @return          The min level
	 */
	public int getMinSkillLevel(final int skillId, final ClassId classId, final int skillLvl)
	{
		final Map<Integer, L2SkillLearn> map = getSkillTrees().get(classId);
		
		final int skillHashCode = SkillTable.getSkillHashCode(skillId, skillLvl);
		
		if (map.containsKey(skillHashCode))
		{
			return map.get(skillHashCode).getMinLevel();
		}
		
		return 0;
	}
	
	public int getMinSkillLevel(final int skillId, final int skillLvl)
	{
		final int skillHashCode = SkillTable.getSkillHashCode(skillId, skillLvl);
		
		// Look on all classes for this skill (takes the first one found)
		for (final Map<Integer, L2SkillLearn> map : getSkillTrees().values())
		{
			// checks if the current class has this skill
			if (map.containsKey(skillHashCode))
			{
				return map.get(skillHashCode).getMinLevel();
			}
		}
		
		return 0;
	}
	
	private Map<ClassId, Map<Integer, L2SkillLearn>> getSkillTrees()
	{
		if (skillTrees == null)
		{
			skillTrees = new HashMap<>();
		}
		
		return skillTrees;
	}
	
	public L2SkillLearn[] getAvailableSkills(final L2PcInstance player, final ClassId classId)
	{
		final List<L2SkillLearn> result = getAvailableSkills(player, classId, player);
		return result.toArray(new L2SkillLearn[result.size()]);
	}
	
	/**
	 * Gets the available skills.
	 * @param  player  the learning skill player.
	 * @param  classId the learning skill class ID.
	 * @param  holder
	 * @return         all available skills for a given {@code player}, {@code classId}, {@code includeByFs} and {@code includeAutoGet}.
	 */
	private List<L2SkillLearn> getAvailableSkills(final L2PcInstance player, final ClassId classId, final ISkillsHolder holder)
	{
		final List<L2SkillLearn> result = new ArrayList<>();
		final Collection<L2SkillLearn> skills = getSkillTrees().get(classId).values();
		
		if (skills.isEmpty())
		{
			// The Skill Tree for this class is undefined.
			LOGGER.warn(getClass().getSimpleName() + ": Skilltree for class " + classId + " is not defined!");
			return result;
		}
		
		for (final L2SkillLearn skill : skills)
		{
			if (skill.getMinLevel() <= player.getLevel())
			{
				final L2Skill oldSkill = holder.getKnownSkill(skill.getId());
				if (oldSkill != null)
				{
					if (oldSkill.getLevel() == (skill.getLevel() - 1))
					{
						result.add(skill);
					}
				}
				else if (skill.getLevel() == 1)
				{
					result.add(skill);
				}
			}
		}
		return result;
	}
	
	public L2SkillLearn[] getAvailableSkills(final L2PcInstance cha)
	{
		final List<L2SkillLearn> result = new ArrayList<>();
		final List<L2SkillLearn> skills = new ArrayList<>();
		
		skills.addAll(fishingSkillTrees);
		
		if (cha.hasDwarvenCraft() && expandDwarfCraftSkillTrees != null)
		{
			skills.addAll(expandDwarfCraftSkillTrees);
		}
		
		final L2Skill[] oldSkills = cha.getAllSkills();
		
		for (final L2SkillLearn temp : skills)
		{
			if (temp.getMinLevel() <= cha.getLevel())
			{
				boolean knownSkill = false;
				
				for (int j = 0; j < oldSkills.length && !knownSkill; j++)
				{
					if (oldSkills[j].getId() == temp.getId())
					{
						knownSkill = true;
						
						if (oldSkills[j].getLevel() == temp.getLevel() - 1)
						{
							// this is the next level of a skill that we know
							result.add(temp);
						}
					}
				}
				
				if (!knownSkill && temp.getLevel() == 1)
				{
					// this is a new skill
					result.add(temp);
				}
			}
		}
		
		return result.toArray(new L2SkillLearn[result.size()]);
	}
	
	public L2EnchantSkillLearn[] getAvailableEnchantSkills(final L2PcInstance cha)
	{
		final List<L2EnchantSkillLearn> result = new ArrayList<>();
		final List<L2EnchantSkillLearn> skills = new ArrayList<>();
		
		skills.addAll(enchantSkillTrees);
		
		final L2Skill[] oldSkills = cha.getAllSkills();
		
		if (cha.getLevel() < 76)
		{
			return result.toArray(new L2EnchantSkillLearn[result.size()]);
		}
		
		for (final L2EnchantSkillLearn skillLearn : skills)
		{
			boolean isKnownSkill = false;
			
			for (final L2Skill skill : oldSkills)
			{
				if (isKnownSkill)
				{
					continue;
				}
				if (skill.getId() == skillLearn.getId())
				{
					isKnownSkill = true;
					if (skill.getLevel() == skillLearn.getMinSkillLevel())
					{
						// this is the next level of a skill that we know
						result.add(skillLearn);
					}
				}
			}
		}
		return result.toArray(new L2EnchantSkillLearn[result.size()]);
	}
	
	public L2PledgeSkillLearn[] getAvailablePledgeSkills(final L2PcInstance cha)
	{
		final List<L2PledgeSkillLearn> result = new ArrayList<>();
		final List<L2PledgeSkillLearn> skills = pledgeSkillTrees;
		
		if (skills == null)
		{
			// the skilltree for this class is undefined, so we give an empty list
			LOGGER.warn("No clan skills defined!");
			return new L2PledgeSkillLearn[0];
		}
		
		final L2Skill[] oldSkills = cha.getClan().getAllSkills();
		
		for (final L2PledgeSkillLearn temp : skills)
		{
			if (temp.getBaseLevel() <= cha.getClan().getLevel())
			{
				boolean knownSkill = false;
				
				for (int j = 0; j < oldSkills.length && !knownSkill; j++)
				{
					if (oldSkills[j].getId() == temp.getId())
					{
						knownSkill = true;
						
						if (oldSkills[j].getLevel() == temp.getLevel() - 1)
						{
							// this is the next level of a skill that we know
							result.add(temp);
						}
					}
				}
				
				if (!knownSkill && temp.getLevel() == 1)
				{
					// this is a new skill
					result.add(temp);
				}
			}
		}
		
		return result.toArray(new L2PledgeSkillLearn[result.size()]);
	}
	
	/**
	 * Returns all allowed skills for a given class.
	 * @param  classId
	 * @return         all allowed skills for a given class.
	 */
	public Collection<L2SkillLearn> getAllowedSkills(final ClassId classId)
	{
		return getSkillTrees().get(classId).values();
	}
	
	public int getMinLevelForNewSkill(final L2PcInstance cha, final ClassId classId)
	{
		int minLevel = 0;
		final Collection<L2SkillLearn> skills = getSkillTrees().get(classId).values();
		
		for (final L2SkillLearn temp : skills)
		{
			if (temp.getMinLevel() > cha.getLevel() && temp.getSpCost() != 0)
			{
				if (minLevel == 0 || temp.getMinLevel() < minLevel)
				{
					minLevel = temp.getMinLevel();
				}
			}
		}
		
		return minLevel;
	}
	
	public int getMinLevelForNewSkill(final L2PcInstance cha)
	{
		int minLevel = 0;
		final List<L2SkillLearn> skills = new ArrayList<>();
		
		skills.addAll(fishingSkillTrees);
		
		if (cha.hasDwarvenCraft() && expandDwarfCraftSkillTrees != null)
		{
			skills.addAll(expandDwarfCraftSkillTrees);
		}
		
		for (final L2SkillLearn s : skills)
		{
			if (s.getMinLevel() > cha.getLevel())
			{
				if (minLevel == 0 || s.getMinLevel() < minLevel)
				{
					minLevel = s.getMinLevel();
				}
			}
		}
		
		return minLevel;
	}
	
	public int getSkillCost(final L2PcInstance player, final L2Skill skill)
	{
		int skillCost = 100000000;
		final ClassId classId = player.getSkillLearningClassId();
		final int skillHashCode = SkillTable.getSkillHashCode(skill);
		
		if (getSkillTrees().get(classId).containsKey(skillHashCode))
		{
			final L2SkillLearn skillLearn = getSkillTrees().get(classId).get(skillHashCode);
			if (skillLearn.getMinLevel() <= player.getLevel())
			{
				skillCost = skillLearn.getSpCost();
				if (!player.getClassId().equalsOrChildOf(classId))
				{
					if (skill.getCrossLearnAdd() < 0)
					{
						return skillCost;
					}
					
					skillCost += skill.getCrossLearnAdd();
					skillCost *= skill.getCrossLearnMul();
				}
				
				if (classId.getRace() != player.getRace() && !player.isSubClassActive())
				{
					skillCost *= skill.getCrossLearnRace();
				}
				
				if (classId.isMage() != player.getClassId().isMage())
				{
					skillCost *= skill.getCrossLearnProf();
				}
			}
		}
		
		return skillCost;
	}
	
	public int getSkillSpCost(final L2PcInstance player, final L2Skill skill)
	{
		int skillCost = 100000000;
		final L2EnchantSkillLearn[] enchantSkillLearnList = getAvailableEnchantSkills(player);
		
		for (final L2EnchantSkillLearn enchantSkillLearn : enchantSkillLearnList)
		{
			if (enchantSkillLearn.getId() != skill.getId())
			{
				continue;
			}
			
			if (enchantSkillLearn.getLevel() != skill.getLevel())
			{
				continue;
			}
			
			if (76 > player.getLevel())
			{
				continue;
			}
			
			skillCost = enchantSkillLearn.getSpCost();
		}
		return skillCost;
	}
	
	public int getSkillExpCost(final L2PcInstance player, final L2Skill skill)
	{
		int skillCost = 100000000;
		final L2EnchantSkillLearn[] enchantSkillLearnList = getAvailableEnchantSkills(player);
		
		for (final L2EnchantSkillLearn enchantSkillLearn : enchantSkillLearnList)
		{
			if (enchantSkillLearn.getId() != skill.getId())
			{
				continue;
			}
			
			if (enchantSkillLearn.getLevel() != skill.getLevel())
			{
				continue;
			}
			
			if (76 > player.getLevel())
			{
				continue;
			}
			
			skillCost = enchantSkillLearn.getExp();
		}
		
		return skillCost;
	}
	
	public byte getSkillRate(final L2PcInstance player, final L2Skill skill)
	{
		final L2EnchantSkillLearn[] enchantSkillLearnList = getAvailableEnchantSkills(player);
		
		for (final L2EnchantSkillLearn enchantSkillLearn : enchantSkillLearnList)
		{
			if (enchantSkillLearn.getId() != skill.getId())
			{
				continue;
			}
			
			if (enchantSkillLearn.getLevel() != skill.getLevel())
			{
				continue;
			}
			
			return enchantSkillLearn.getRate(player);
		}
		
		return 0;
	}
	
	/**
	 * @param  player
	 * @param  classId
	 * @return
	 */
	public Collection<L2Skill> getAllAvailableSkills(final L2PcInstance player, final ClassId classId)
	{
		// Get available skills
		int unLearnable = 0;
		final PlayerSkillHolder holder = new PlayerSkillHolder(player.getSkills());
		List<L2SkillLearn> learnable = getAvailableSkills(player, classId, holder);
		while (learnable.size() > unLearnable)
		{
			for (final L2SkillLearn s : learnable)
			{
				final L2Skill sk = SkillTable.getInstance().getInfo(s.getId(), s.getLevel());
				
				// if ((sk == null) || ((sk.getId() == L2Skill.SKILL_DIVINE_INSPIRATION) && !Config.AUTO_LEARN_DIVINE_INSPIRATION && !player.isGM()))
				if (sk == null)
				{
					unLearnable++;
					continue;
				}
				
				holder.addSkill(sk);
			}
			
			// Get new available skills, some skills depend of previous skills to be available.
			learnable = getAvailableSkills(player, classId, holder);
		}
		return holder.getSkills().values();
	}
}