package com.px.gameserver.skills.conditions;

import java.util.List;

import com.px.gameserver.model.actor.Creature;
import com.px.gameserver.model.actor.Player;
import com.px.gameserver.model.item.kind.Item;
import com.px.gameserver.model.pledge.Clan;
import com.px.gameserver.skills.L2Skill;

public final class ConditionPlayerHasClanHall extends Condition
{
	private final List<Integer> _clanHall;
	
	public ConditionPlayerHasClanHall(List<Integer> clanHall)
	{
		_clanHall = clanHall;
	}
	
	@Override
	public boolean testImpl(Creature effector, Creature effected, L2Skill skill, Item item)
	{
		if (!(effector instanceof Player))
			return false;
		
		final Clan clan = ((Player) effector).getClan();
		if (clan == null)
			return (_clanHall.size() == 1 && _clanHall.get(0) == 0);
		
		// All Clan Hall
		if (_clanHall.size() == 1 && _clanHall.get(0) == -1)
			return clan.hasClanHall();
		
		return _clanHall.contains(clan.getClanHallId());
	}
}