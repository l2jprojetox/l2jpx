package com.px.gameserver.scripting.scripts.ai.individual;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import com.px.commons.util.StatsSet;
import com.px.gameserver.SchedulingPattern;
import com.px.gameserver.data.manager.GrandBossManager;
import com.px.gameserver.data.manager.ZoneManager;
import com.px.gameserver.enums.IntentionType;
import com.px.gameserver.model.L2Skill;
import com.px.gameserver.model.actor.Creature;
import com.px.gameserver.model.actor.Npc;
import com.px.gameserver.model.actor.Playable;
import com.px.gameserver.model.actor.Player;
import com.px.gameserver.model.actor.instance.GrandBoss;
import com.px.gameserver.model.location.SpawnLocation;
import com.px.gameserver.model.zone.type.BossZone;
import com.px.gameserver.network.serverpackets.MagicSkillUse;
import com.px.gameserver.network.serverpackets.SocialAction;
import com.px.gameserver.network.serverpackets.SpecialCamera;
import com.px.gameserver.scripting.scripts.ai.L2AttackableAIScript;
import com.px.gameserver.scripting.scripts.ai.boss.SailrenConfig;

public class Sailren extends L2AttackableAIScript
{
	private static final BossZone SAILREN_LAIR = ZoneManager.getInstance().getZoneById(110011, BossZone.class);
	
	public static final int SAILREN = 29065;
	
	public static final byte DORMANT = 0; // No one has entered yet. Entry is unlocked.
	public static final byte FIGHTING = 1; // A group entered in the nest. Entry is locked.
	public static final byte DEAD = 2; // Sailren has been killed. Entry is locked.
	
	private static final int VELOCIRAPTOR = 22223;
	private static final int PTEROSAUR = 22199;
	private static final int TREX = 22217;
	private static final int DUMMY = 32110;
	private static final int CUBE = 32107;
	
	private static final long INTERVAL_CHECK = 600000L; // 10 minutes
	
	private static final SpawnLocation SAILREN_LOC = new SpawnLocation(27549, -6638, -2008, 0);
	
	private final Set<Npc> _minions = ConcurrentHashMap.newKeySet();
	
	private long _timeTracker = 0;
	
	public Sailren()
	{
		super("ai/boss");
		SailrenConfig.ini();
		final StatsSet info = GrandBossManager.getInstance().getStatsSet(SAILREN);
		
		switch (GrandBossManager.getInstance().getBossStatus(SAILREN))
		{
			case DEAD: // Launch the timer to set DORMANT, or set DORMANT directly if timer expired while offline.
				final long temp = (info.getLong("respawn_time") - System.currentTimeMillis());
				if (temp > 0)
					startQuestTimer("unlock", temp, null, null, false);
				else
					GrandBossManager.getInstance().setBossStatus(SAILREN, DORMANT);
				break;
			
			case FIGHTING:
				final int loc_x = info.getInteger("loc_x");
				final int loc_y = info.getInteger("loc_y");
				final int loc_z = info.getInteger("loc_z");
				final int heading = info.getInteger("heading");
				final int hp = info.getInteger("currentHP");
				final int mp = info.getInteger("currentMP");
				
				final Npc sailren = addSpawn(SAILREN, loc_x, loc_y, loc_z, heading, false, 0, false);
				GrandBossManager.getInstance().addBoss((GrandBoss) sailren);
				_minions.add(sailren);
				
				sailren.getStatus().setCurrentHpMp(hp, mp);
				sailren.setRunning();
				
				// Don't need to edit _timeTracker, as it's initialized to 0.
				startQuestTimer("inactivity", INTERVAL_CHECK, null, null, true);
				break;
		}
	}
	
	@Override
	protected void registerNpcs()
	{
		addAttackId(VELOCIRAPTOR, PTEROSAUR, TREX, SAILREN);
		addKillId(VELOCIRAPTOR, PTEROSAUR, TREX, SAILREN);
	}
	
	public String onTimer(String name, Npc npc, Player player)
	{
		if (name.equalsIgnoreCase("beginning"))
		{
			_timeTracker = 0;
			
			for (int i = 0; i < 3; i++)
			{
				final Npc temp = addSpawn(VELOCIRAPTOR, SAILREN_LOC, true, 0, false);
				temp.getAI().setIntention(IntentionType.ACTIVE);
				temp.setRunning();
				_minions.add(temp);
			}
			startQuestTimer("inactivity", INTERVAL_CHECK, null, null, true);
		}
		else if (name.equalsIgnoreCase("spawn"))
		{
			// Dummy spawn used to cast the skill. Despawned after 26sec.
			final Npc temp = addSpawn(DUMMY, SAILREN_LOC, false, 26000, false);
			
			// Cast skill every 2,5sec.
			SAILREN_LAIR.broadcastPacket(new MagicSkillUse(npc, npc, 5090, 1, 2500, 0));
			startQuestTimer("skill", 2500, temp, null, true);
			
			SAILREN_LAIR.broadcastPacket(new SpecialCamera(temp.getObjectId(), 60, 110, 30, 4000, 4000, 0, 65, 1, 0)); // 4sec
	
		}
		else if (name.equalsIgnoreCase("skill"))
			SAILREN_LAIR.broadcastPacket(new MagicSkillUse(npc, npc, 5090, 1, 2500, 0));
		else if (name.equalsIgnoreCase("camera_4"))
		{
			
			final Npc temp = addSpawn(SAILREN, SAILREN_LOC, false, 0, false);
			GrandBossManager.getInstance().addBoss((GrandBoss) temp);
			_minions.add(temp);
			
			// Stop skill task.
			cancelQuestTimers("skill");
			SAILREN_LAIR.broadcastPacket(new MagicSkillUse(npc, npc, 5091, 1, 2500, 0));
			
			temp.broadcastPacket(new SocialAction(temp, 2));
		}
		else if (name.equalsIgnoreCase("unlock"))
			GrandBossManager.getInstance().setBossStatus(SAILREN, DORMANT);
		else if (name.equalsIgnoreCase("inactivity"))
		{
			// 10 minutes without any attack activity leads to a reset.
			if ((System.currentTimeMillis() - _timeTracker) >= INTERVAL_CHECK)
			{
				// Set it dormant.
				GrandBossManager.getInstance().setBossStatus(SAILREN, DORMANT);
				
				// Delete all monsters and clean the list.
				if (!_minions.isEmpty())
				{
					_minions.forEach(Npc::deleteMe);
					_minions.clear();
				}
				
				// Oust all players from area.
				SAILREN_LAIR.oustAllPlayers();
				
				// Cancel inactivity task.
				cancelQuestTimers("inactivity");
			}
		}
		else if (name.equalsIgnoreCase("oust"))
		{
			// Oust all players from area.
			SAILREN_LAIR.oustAllPlayers();
		}
		return null;
	}
	
	public void onAttacked(Npc npc, Creature attacker, int damage, L2Skill skill)
	{
		if (attacker instanceof Playable)
		{
			final Player player = attacker.getActingPlayer();
			if (player == null || !_minions.contains(npc) || !SAILREN_LAIR.getAllowedPlayers().contains(player.getObjectId()))
				return;
			
			// Curses
			if (testCursesOnAttack(npc, attacker, SAILREN))
				return;
			
			// Refresh timer on every hit.
			_timeTracker = System.currentTimeMillis();
		}
		super.onAttack(npc, attacker, damage, skill);
	}
	
	public void onMyDying(Npc npc, Creature killer)
	{
		if (killer instanceof Playable)
		{
			final Player player = killer.getActingPlayer();
			if (player == null || !_minions.contains(npc) || !SAILREN_LAIR.getAllowedPlayers().contains(player.getObjectId()))
				return;
		}
		
		switch (npc.getNpcId())
		{
			case VELOCIRAPTOR:
				// Once the 3 Velociraptors are dead, spawn a Pterosaur.
				if (_minions.remove(npc) && _minions.isEmpty())
				{
					final Npc temp = addSpawn(PTEROSAUR, SAILREN_LOC, false, 0, false);
					temp.setRunning();
					temp.getAI().setIntention(IntentionType.ATTACK, killer);
					_minions.add(temp);
				}
				break;
			
			case PTEROSAUR:
				// Pterosaur is dead, spawn a Trex.
				if (_minions.remove(npc))
				{
					final Npc temp = addSpawn(TREX, SAILREN_LOC, false, 0, false);
					temp.setRunning();
					temp.getAI().setIntention(IntentionType.ATTACK, killer);
					_minions.add(temp);
				}
				break;
			
			case TREX:
				// Trex is dead, wait 5min and spawn Sailren.
				if (_minions.remove(npc))
					startQuestTimer("spawn", SailrenConfig.WAIT_TIME_SAILREN, npc, null, false);
				break;
		}
		
		if (!SailrenConfig.FWA_FIXTIMEPATTERNOFSAILREN.isEmpty())
		{
			if (npc.getNpcId() == SAILREN)
			{
				if (_minions.remove(npc))
				{
					// Set Sailren as dead.
					GrandBossManager.getInstance().setBossStatus(SAILREN, DEAD);
					
					// Spawn the Teleport Cube for 10min.
					addSpawn(CUBE, npc, false, INTERVAL_CHECK, false);
					
					// Cancel inactivity task.
					cancelQuestTimers("inactivity");
					
					startQuestTimer("oust", INTERVAL_CHECK, null, null, false);
					
					startQuestTimer("unlock", getRespawnInterval(), null, null, false);
					
					// Save the respawn time so that the info is maintained past reboots.
					final StatsSet info = GrandBossManager.getInstance().getStatsSet(SAILREN);
					info.set("respawn_time", System.currentTimeMillis() + getRespawnInterval());
					GrandBossManager.getInstance().setStatsSet(SAILREN, info);
				}
			}
		}
		super.onKill(npc, killer);
	}
	
	private static long getRespawnInterval()
	{
		SchedulingPattern timePattern = null;
		
		long now = System.currentTimeMillis();
		try
		{
			timePattern = new SchedulingPattern(SailrenConfig.FWA_FIXTIMEPATTERNOFSAILREN);
			long delay = timePattern.next(now) - now;
			return Math.max(60000, delay);
		}
		catch (SchedulingPattern.InvalidPatternException ipe)
		{
			throw new RuntimeException("Invalid respawn data \"" + SailrenConfig.FWA_FIXTIMEPATTERNOFSAILREN + "\" in " + Sailren.class.getSimpleName(), ipe);
		}
	}
}