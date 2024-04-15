package com.px.gameserver.taskmanager;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.px.commons.concurrent.ThreadPool;

import com.px.gameserver.enums.GaugeColor;
import com.px.gameserver.enums.skills.Stats;
import com.px.gameserver.model.actor.Player;
import com.px.gameserver.network.SystemMessageId;
import com.px.gameserver.network.serverpackets.SetupGauge;
import com.px.gameserver.network.serverpackets.SystemMessage;

/**
 * Updates {@link Player} drown timer and reduces {@link Player} HP, when drowning.
 */
public final class WaterTaskManager implements Runnable
{
	private final Map<Player, Long> _players = new ConcurrentHashMap<>();
	
	protected WaterTaskManager()
	{
		// Run task each second.
		ThreadPool.scheduleAtFixedRate(this, 1000, 1000);
	}
	
	@Override
	public final void run()
	{
		// List is empty, skip.
		if (_players.isEmpty())
			return;
		
		// Get current time.
		final long time = System.currentTimeMillis();
		
		// Loop all players.
		for (Map.Entry<Player, Long> entry : _players.entrySet())
		{
			// Time has not passed yet, skip.
			if (time < entry.getValue())
				continue;
			
			// Get player.
			final Player player = entry.getKey();
			
			// Reduce 1% of HP per second.
			final double hp = player.getMaxHp() / 100.0;
			player.reduceCurrentHp(hp, player, false, false, null);
			player.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.DROWN_DAMAGE_S1).addNumber((int) hp));
		}
	}
	
	/**
	 * Adds {@link Player} to the WaterTask.
	 * @param player : {@link Player} to be added and checked.
	 */
	public final void add(Player player)
	{
		if (!player.isDead() && !_players.containsKey(player))
		{
			final int time = (int) player.calcStat(Stats.BREATH, 60000 * player.getRace().getBreathMultiplier(), player, null);
			
			_players.put(player, System.currentTimeMillis() + time);
			
			player.sendPacket(new SetupGauge(GaugeColor.CYAN, time));
		}
	}
	
	/**
	 * Removes {@link Player} from the WaterTask.
	 * @param player : Player to be removed.
	 */
	public final void remove(Player player)
	{
		if (_players.remove(player) != null)
			player.sendPacket(new SetupGauge(GaugeColor.CYAN, 0));
	}
	
	public static final WaterTaskManager getInstance()
	{
		return SingletonHolder.INSTANCE;
	}
	
	private static class SingletonHolder
	{
		protected static final WaterTaskManager INSTANCE = new WaterTaskManager();
	}
}