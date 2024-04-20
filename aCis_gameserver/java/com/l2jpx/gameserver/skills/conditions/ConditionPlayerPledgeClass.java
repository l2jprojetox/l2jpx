package com.l2jpx.gameserver.skills.conditions;

import com.l2jpx.gameserver.model.actor.Creature;
import com.l2jpx.gameserver.model.actor.Player;
import com.l2jpx.gameserver.model.item.kind.Item;
import com.l2jpx.gameserver.skills.L2Skill;

public final class ConditionPlayerPledgeClass extends Condition
{
	private final int _pledgeClass;
	
	public ConditionPlayerPledgeClass(int pledgeClass)
	{
		_pledgeClass = pledgeClass;
	}
	
	@Override
	public boolean testImpl(Creature effector, Creature effected, L2Skill skill, Item item)
	{
		if (!(effector instanceof Player))
			return false;
		
		final Player player = (Player) effector;
		
		if (player.getClan() == null)
			return false;
		
		if (_pledgeClass == -1)
			return player.isClanLeader();
		
		return player.getPledgeClass() >= _pledgeClass;
	}
}