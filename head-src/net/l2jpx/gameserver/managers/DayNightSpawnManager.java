package net.l2jpx.gameserver.managers;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import net.l2jpx.gameserver.controllers.GameTimeController;
import net.l2jpx.gameserver.datatables.SkillTable;
import net.l2jpx.gameserver.model.L2Skill;
import net.l2jpx.gameserver.model.L2World;
import net.l2jpx.gameserver.model.actor.instance.L2NpcInstance;
import net.l2jpx.gameserver.model.actor.instance.L2RaidBossInstance;
import net.l2jpx.gameserver.model.base.Race;
import net.l2jpx.gameserver.model.spawn.L2Spawn;
import net.l2jpx.gameserver.network.SystemMessageId;
import net.l2jpx.gameserver.network.serverpackets.SystemMessage;

/**
 * @author godson
 * @author ReynalDev
 */
public class DayNightSpawnManager
{
	private static final Logger LOGGER = Logger.getLogger(DayNightSpawnManager.class);
	
	private static DayNightSpawnManager instance;
	private static Map<L2Spawn, L2NpcInstance> dayCreatures = new HashMap<>();
	private static Map<L2Spawn, L2NpcInstance> nightCreatures = new HashMap<>();
	private static Map<L2Spawn, L2RaidBossInstance> bosses = new HashMap<>();
	
	private static final L2Skill SHADOW_SENSE = SkillTable.getInstance().getInfo(294, 1);
	
	private static final int DAY = 0;
	private static final int NIGHT = 1;
	
	public static DayNightSpawnManager getInstance()
	{
		if (instance == null)
		{
			instance = new DayNightSpawnManager();
		}
		
		return instance;
	}
	
	private DayNightSpawnManager()
	{
		LOGGER.info("DayNightSpawnManager: Day/Night handler initialised");
	}
	
	public void addDayCreature(L2Spawn spawnDat)
	{
		if (dayCreatures.containsKey(spawnDat))
		{
			LOGGER.warn("DayNightSpawnManager: Spawn already added into day map");
			return;
		}
		
		dayCreatures.put(spawnDat, null);
	}
	
	public void addNightCreature(L2Spawn spawnDat)
	{
		if (nightCreatures.containsKey(spawnDat))
		{
			LOGGER.warn("DayNightSpawnManager: Spawn already added into night map");
			return;
		}
		
		nightCreatures.put(spawnDat, null);
	}
	
	/**
	 * Spawn day creatures and despawn night creatures
	 */
	public void spawnDayCreatures()
	{
		spawnCreatures(nightCreatures, dayCreatures, "night", "day");
	}
	
	/**
	 * Spawn night creatures and despawn day creatures
	 */
	public void spawnNightCreatures()
	{
		spawnCreatures(dayCreatures, nightCreatures, "day", "night");
	}
	
	private void spawnCreatures(Map<L2Spawn, L2NpcInstance> creaturesToDespawn, Map<L2Spawn, L2NpcInstance> creaturesToSpawn, String despawnedCreatures, String spawnedCreatures)
	{
		try
		{
			if (creaturesToDespawn.size() != 0)
			{
				int counter = 0;
				
				for (L2NpcInstance dayCreature : creaturesToDespawn.values())
				{
					if (dayCreature == null)
					{
						continue;
					}
					
					dayCreature.getSpawn().stopRespawn();
					dayCreature.deleteMe();
					counter++;
				}
				
				LOGGER.info("DayNightSpawnManager: Despawning " + counter + " " + despawnedCreatures + " creatures");
			}
			
			int counter = 0;
			L2NpcInstance creature = null;
			
			for (L2Spawn spawnDat : creaturesToSpawn.keySet())
			{
				if (creaturesToSpawn.get(spawnDat) == null)
				{
					creature = spawnDat.doSpawn();
					
					if (creature == null)
					{
						continue;
					}
					
					creaturesToSpawn.put(spawnDat, creature);
					creature.setCurrentHp(creature.getMaxHp());
					creature.setCurrentMp(creature.getMaxMp());
					creature = creaturesToSpawn.get(spawnDat);
					creature.getSpawn().startRespawn();
				}
				else
				{
					creature = creaturesToSpawn.get(spawnDat);
					
					if (creature == null)
					{
						continue;
					}
					
					creature.getSpawn().startRespawn();
					creature.setCurrentHp(creature.getMaxHp());
					creature.setCurrentMp(creature.getMaxMp());
					creature.spawnMe();
				}
				
				counter++;
			}
			
			LOGGER.info("DayNightSpawnManager: Spawning " + counter + " " + spawnedCreatures + " creatures");
		}
		catch (Exception e)
		{
			LOGGER.error("DayNightSpawnManager.spawnCreatures : Something went wrong while despawning and spawning creatures", e);
		}
	}
	
	private void changeMode(int mode)
	{
		if (nightCreatures.size() == 0 && dayCreatures.size() == 0)
		{
			return;
		}
		
		switch (mode)
		{
			case DAY:
				spawnDayCreatures();
				specialNightBoss(DAY);
				sendShadowSenseMessage(DAY);
				break;
			case NIGHT:
				spawnNightCreatures();
				specialNightBoss(NIGHT);
				sendShadowSenseMessage(NIGHT);
				break;
			default:
				LOGGER.warn("DayNightSpawnManager: Wrong mode sent");
				break;
		}
	}
	
	public void notifyChangeMode()
	{
		try
		{
			if (GameTimeController.getInstance().isNowNight())
			{
				changeMode(NIGHT);
			}
			else
			{
				changeMode(DAY);
			}
		}
		catch (Exception e)
		{
			LOGGER.error("DayNightSpawnManager.notifyChangeMode : Error while changing mode ", e);
		}
	}
	
	public void cleanUp()
	{
		nightCreatures.clear();
		dayCreatures.clear();
		bosses.clear();
	}
	
	private void specialNightBoss(int mode)
	{
		try
		{
			for (L2Spawn spawn : bosses.keySet())
			{
				L2RaidBossInstance boss = bosses.get(spawn);
				
				if (boss == null && mode == NIGHT)
				{
					boss = (L2RaidBossInstance) spawn.doSpawn();
					RaidBossSpawnManager.getInstance().notifySpawnNightBoss(boss);
					bosses.put(spawn, boss);
					continue;
				}
				
				if (boss == null && mode == DAY)
				{
					continue;
				}
				
				if (boss != null && boss.getNpcId() == 25328 && boss.getRaidStatus().equals(RaidBossSpawnManager.StatusEnum.ALIVE))
				{
					handleHellmans(boss, mode);
				}
				
				return;
			}
		}
		catch (Exception e)
		{
			LOGGER.error("DayNightSpawnManager.specialNightBoss : Error while unspawn or spawn raidboss", e);
		}
	}
	
	private void handleHellmans(L2RaidBossInstance boss, int mode)
	{
		switch (mode)
		{
			case DAY:
				boss.deleteMe();
				LOGGER.info("DayNightSpawnManager: It has dawned (06:00), despawning Eilhalder Von Hellmann raidboss.");
				break;
			case NIGHT:
				boss.spawnMe();
				LOGGER.info("DayNightSpawnManager: It is MIDNIGHT (00:00), spawning Eilhalder Von Hellmann raidboss.");
				break;
		}
	}
	
	/**
	 * @param mode 0 = day, 1 = night
	 */
	private void sendShadowSenseMessage(int mode)
	{
		if (mode == NIGHT)
		{
			SystemMessageId msg = SystemMessageId.NIGHT_EFFECT_APPLIES;
			L2World.getInstance().getAllPlayers().forEach(player ->
			{
				if (player.getRace() == Race.darkelf)
				{
					player.addSkill(SHADOW_SENSE, false);
					player.sendSkillList();
					
					SystemMessage sm = SystemMessage.getSystemMessage(msg);
					sm.addSkillName(SHADOW_SENSE.getId());
					player.sendPacket(sm);
				}
			});
		}
		else if (mode == DAY)
		{
			SystemMessageId msg = SystemMessageId.DAY_EFFECT_DISAPPEARS;
			L2World.getInstance().getAllPlayers().forEach(player ->
			{
				if (player.getRace() == Race.darkelf && player.getSkillLevel(SHADOW_SENSE.getId()) > 0)
				{
					player.removeSkill(SHADOW_SENSE);
					player.sendSkillList();
					
					SystemMessage sm = SystemMessage.getSystemMessage(msg);
					sm.addSkillName(SHADOW_SENSE.getId());
					player.sendPacket(sm);
				}
			});
		}
	}
	
	public L2RaidBossInstance handleBoss(L2Spawn spawnDat)
	{
		if (bosses.containsKey(spawnDat))
		{
			return bosses.get(spawnDat);
		}
		
		if (GameTimeController.getInstance().isNowNight())
		{
			L2RaidBossInstance raidboss = (L2RaidBossInstance) spawnDat.doSpawn();
			bosses.put(spawnDat, raidboss);
			return raidboss;
		}
		
		bosses.put(spawnDat, null);
		return null;
	}
}
