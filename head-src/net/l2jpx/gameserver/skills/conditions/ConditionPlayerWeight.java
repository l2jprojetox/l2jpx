package net.l2jpx.gameserver.skills.conditions;

import net.l2jpx.gameserver.model.actor.instance.L2PcInstance;
import net.l2jpx.gameserver.skills.Env;
import net.l2jpx.gameserver.skills.Stats;

/**
 * The Class ConditionPlayerWeight.
 * @author Kerberos
 */
public class ConditionPlayerWeight extends Condition
{
	private final int weight;
	
	/**
	 * Instantiates a new condition player weight.
	 * @param weight the weight
	 */
	public ConditionPlayerWeight(final int weight)
	{
		this.weight = weight;
	}
	
	@Override
	public boolean testImpl(final Env env)
	{
		final L2PcInstance player = env.getPlayer();
		if ((player != null) && (player.getMaxLoad() > 0))
		{
			final int weightproc = (int) (((player.getCurrentLoad() - player.calcStat(Stats.WEIGHT_PENALTY, 1, player, null)) * 100) / player.getMaxLoad());
			return (weightproc < weight) || player.getDietMode();
		}
		return true;
	}
}