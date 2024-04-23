package com.l2jpx.gameserver.skills.conditions;

import java.util.List;

import com.l2jpx.gameserver.model.actor.Creature;
import com.l2jpx.gameserver.model.actor.Npc;
import com.l2jpx.gameserver.model.actor.instance.Door;
import com.l2jpx.gameserver.model.item.kind.Item;
import com.l2jpx.gameserver.skills.L2Skill;

public class ConditionTargetNpcId extends Condition
{
	private final List<Integer> _npcIds;
	
	public ConditionTargetNpcId(List<Integer> npcIds)
	{
		_npcIds = npcIds;
	}
	
	@Override
	public boolean testImpl(Creature effector, Creature effected, L2Skill skill, Item item)
	{
		if (effected instanceof Npc)
			return _npcIds.contains(((Npc) effected).getNpcId());
		
		if (effected instanceof Door)
			return _npcIds.contains(((Door) effected).getDoorId());
		
		return false;
	}
}