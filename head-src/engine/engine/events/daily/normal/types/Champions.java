package engine.engine.events.daily.normal.types;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import engine.data.memory.ObjectData;
import engine.data.properties.ConfigData;
import engine.engine.events.daily.AbstractEvent;
import engine.enums.ChampionType;
import engine.enums.ExpSpType;
import engine.enums.ItemDropType;
import engine.enums.TeamType;
import engine.holders.objects.CharacterHolder;
import engine.holders.objects.NpcHolder;
import engine.holders.objects.PlayerHolder;
import engine.instances.NpcDropsInstance;
import engine.instances.NpcExpInstance;
import engine.util.Util;
import net.l2jpx.gameserver.model.L2Attackable;
import net.l2jpx.gameserver.model.actor.instance.L2GrandBossInstance;
import net.l2jpx.gameserver.model.actor.instance.L2MonsterInstance;
import net.l2jpx.gameserver.model.actor.instance.L2PcInstance;
import net.l2jpx.gameserver.model.actor.instance.L2RaidBossInstance;
import net.l2jpx.gameserver.model.holder.RewardHolder;
import net.l2jpx.gameserver.skills.Stats;
import net.l2jpx.util.random.Rnd;

/**
 * Champions event class for enhancing NPCs with special statuses, rewards, and stats.
 */
public class Champions extends AbstractEvent
{
	public static class ChampionInfoHolder
	{
		ChampionType type;
		int chanceToSpawn;
		Map<Stats, Double> allStats = new HashMap<>();
		List<RewardHolder> rewards;
		
		public ChampionInfoHolder(ChampionType type, int chanceToSpawn, Map<Stats, Double> allStats, List<RewardHolder> rewards)
		{
			this.type = type;
			this.chanceToSpawn = chanceToSpawn;
			this.allStats = allStats;
			this.rewards = rewards;
		}
	}
	
	private static final Map<ChampionType, ChampionInfoHolder> CHAMPIONS_INFO = new HashMap<>();
	
	static
	{
		initializeChampionsInfo();
	}
	
	public Champions()
	{
		registerEvent(ConfigData.ENABLE_Champions, ConfigData.CHAMPION_ENABLE_DAY);
	}
	
	private static void initializeChampionsInfo()
	{
		CHAMPIONS_INFO.put(ChampionType.WEAK_CHAMPION, new ChampionInfoHolder(ChampionType.WEAK_CHAMPION, ConfigData.CHANCE_SPAWN_WEAK, ConfigData.CHAMPION_STAT_WEAK, ConfigData.CHAMPION_REWARD_WEAK));
		CHAMPIONS_INFO.put(ChampionType.SUPER_CHAMPION, new ChampionInfoHolder(ChampionType.SUPER_CHAMPION, ConfigData.CHANCE_SPAWN_SUPER, ConfigData.CHAMPION_STAT_SUPER, ConfigData.CHAMPION_REWARD_SUPER));
		CHAMPIONS_INFO.put(ChampionType.HARD_CHAMPION, new ChampionInfoHolder(ChampionType.HARD_CHAMPION, ConfigData.CHANCE_SPAWN_HARD, ConfigData.CHAMPION_STAT_HARD, ConfigData.CHAMPION_REWARD_HARD));
	}
	
	@Override
	public void onModState()
	{
		switch (getState())
		{
			case START:
				
				break;
			case END:
				
				ObjectData.getAll(NpcHolder.class).stream().filter(npc -> (npc.getInstance() != null) && npc.isChampion()).forEach(npc ->
				{
					
					npc.setChampionType(ChampionType.NONE);
					
					npc.setTeam(TeamType.NONE);
					
					npc.getInstance().setTitle("");
					
					npc.getInstance().setCurrentHpMp(npc.getInstance().getMaxHp(), npc.getInstance().getMaxMp());
				});
				break;
		}
	}
	
	@Override
	public void onSpawn(NpcHolder npc)
	{
		if (!isEligibleForChampionStatus(npc))
		{
			return;
		}
		
		CHAMPIONS_INFO.values().forEach(info ->
		{
			if (Rnd.get(100) < info.chanceToSpawn)
			{
				applyChampionStatus(npc, info);
			}
		});
	}
	
	@Override
	public void onKill(CharacterHolder killer, CharacterHolder victim, boolean isPet)
	{
		if (!(victim instanceof NpcHolder))
		{
			return;
		}
		
		NpcHolder npc = (NpcHolder) victim;
		ChampionInfoHolder championInfo = CHAMPIONS_INFO.get(npc.getChampionType());
		
		if (championInfo == null)
		{
			return;
		}
		
		championInfo.rewards.stream().filter(reward -> Rnd.get(100) < reward.getRewardChance()).forEach(reward ->
		{
			((L2Attackable) npc.getInstance()).dropItem((L2PcInstance) killer.getInstance(), reward.getRewardId(), reward.getRewardCount());
		});
		
		resetNpcStatus(npc);
	}
	
	@Override
	public double onStats(Stats stat, CharacterHolder character, double value)
	{
		if (!(character instanceof NpcHolder))
		{
			return value;
		}
		
		NpcHolder npc = (NpcHolder) character;
		ChampionInfoHolder championInfo = CHAMPIONS_INFO.get(npc.getChampionType());
		
		if (championInfo != null && championInfo.allStats.containsKey(stat))
		{
			return value * championInfo.allStats.get(stat);
		}
		
		return value;
	}
	
	@Override
	public void onNpcExpSp(PlayerHolder killer, NpcHolder npc, NpcExpInstance instance)
	{
		if (npc.isChampion())
		{
			instance.increaseRate(ExpSpType.EXP, ConfigData.CHAMPION_BONUS_RATE_EXP);
			instance.increaseRate(ExpSpType.SP, ConfigData.CHAMPION_BONUS_RATE_SP);
		}
	}
	
	@Override
	public void onNpcDrop(PlayerHolder killer, NpcHolder npc, NpcDropsInstance instance)
	{
		if (npc.isChampion())
		{
			instance.increaseDrop(ItemDropType.NORMAL, ConfigData.CHAMPION_BONUS_DROP, ConfigData.CHAMPION_BONUS_DROP);
			instance.increaseDrop(ItemDropType.SPOIL, ConfigData.CHAMPION_BONUS_SPOIL, ConfigData.CHAMPION_BONUS_SPOIL);
			instance.increaseDrop(ItemDropType.SEED, ConfigData.CHAMPION_BONUS_SEED, ConfigData.CHAMPION_BONUS_SEED);
		}
	}
	
	private boolean isEligibleForChampionStatus(NpcHolder npc)
	{
		return npc.getInstance() instanceof L2MonsterInstance && !(npc.getInstance() instanceof L2RaidBossInstance) && !(npc.getInstance() instanceof L2GrandBossInstance);
	}
	
	private void applyChampionStatus(NpcHolder npc, ChampionInfoHolder info)
	{
		npc.setChampionType(info.type);
		npc.setTeam(TeamType.RED);
		npc.getInstance().setTitle(info.type.name().replace("_", " "));
		npc.getInstance().setCurrentHpMp(npc.getInstance().getStat().getMaxHp() * info.allStats.get(Stats.MAX_HP), npc.getInstance().getStat().getMaxMp() * info.allStats.get(Stats.MAX_MP));
	}
	
	private void resetNpcStatus(NpcHolder npc)
	{
		npc.setChampionType(ChampionType.NONE);
		npc.setTeam(TeamType.NONE);
		npc.getInstance().setTitle(npc.getInstance().getTemplate().title);
	}
	
	@SuppressWarnings("unused")
	private static boolean checkNpcType(NpcHolder obj)
	{
		return Util.areObjectType(L2MonsterInstance.class, obj) && !Util.areObjectType(L2RaidBossInstance.class, obj) && !Util.areObjectType(L2GrandBossInstance.class, obj);
	}
}
