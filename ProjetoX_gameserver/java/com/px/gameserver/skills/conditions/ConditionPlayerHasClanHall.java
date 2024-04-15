package com.px.gameserver.skills.conditions;

import java.util.List;

import com.px.gameserver.model.pledge.Clan;
import com.px.gameserver.skills.Env;

/**
 * The Class ConditionPlayerHasClanHall.
 * @author MrPoke
 */
public final class ConditionPlayerHasClanHall extends Condition
{
	private final List<Integer> _clanHall;
	
	/**
	 * Instantiates a new condition player has clan hall.
	 * @param clanHall the clan hall
	 */
	public ConditionPlayerHasClanHall(List<Integer> clanHall)
	{
		_clanHall = clanHall;
	}
	
	/**
	 * @param env the env
	 * @return true, if successful
	 * @see com.px.gameserver.skills.conditions.Condition#testImpl(com.px.gameserver.skills.Env)
	 */
	@Override
	public boolean testImpl(Env env)
	{
		if (env.getPlayer() == null)
			return false;
		
		final Clan clan = env.getPlayer().getClan();
		if (clan == null)
			return (_clanHall.size() == 1 && _clanHall.get(0) == 0);
		
		// All Clan Hall
		if (_clanHall.size() == 1 && _clanHall.get(0) == -1)
			return clan.hasClanHall();
		
		return _clanHall.contains(clan.getClanHallId());
	}
}