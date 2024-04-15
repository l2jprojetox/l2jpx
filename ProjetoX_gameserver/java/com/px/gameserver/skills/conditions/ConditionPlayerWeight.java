package com.px.gameserver.skills.conditions;

import com.px.gameserver.model.actor.Player;
import com.px.gameserver.skills.Env;

/**
 * The Class ConditionPlayerWeight.
 * @author Kerberos
 */
public class ConditionPlayerWeight extends Condition
{
	private final int _weight;
	
	/**
	 * Instantiates a new condition player weight.
	 * @param weight the weight
	 */
	public ConditionPlayerWeight(int weight)
	{
		_weight = weight;
	}
	
	@Override
	public boolean testImpl(Env env)
	{
		final Player player = env.getPlayer();
		if (player != null && player.getMaxLoad() > 0)
		{
			int weightproc = player.getCurrentLoad() * 100 / player.getMaxLoad();
			return weightproc < _weight;
		}
		return true;
	}
}