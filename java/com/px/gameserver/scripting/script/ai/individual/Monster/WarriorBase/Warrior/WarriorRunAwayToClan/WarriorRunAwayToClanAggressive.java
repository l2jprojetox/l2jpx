package com.px.gameserver.scripting.script.ai.individual.Monster.WarriorBase.Warrior.WarriorRunAwayToClan;

import com.px.gameserver.model.actor.Creature;
import com.px.gameserver.model.actor.Npc;
import com.px.gameserver.model.actor.Playable;

public class WarriorRunAwayToClanAggressive extends WarriorRunAwayToClan
{
	public WarriorRunAwayToClanAggressive()
	{
		super("ai/individual/Monster/WarriorBase/Warrior/WarriorRunAwayToClan");
	}
	
	public WarriorRunAwayToClanAggressive(String descr)
	{
		super(descr);
	}
	
	protected final int[] _npcIds =
	{
		20211,
		20438,
		20061,
		20497,
		20495
	};
	
	@Override
	public void onSeeCreature(Npc npc, Creature creature)
	{
		if (!(creature instanceof Playable))
			return;
		
		tryToAttack(npc, creature);
		
		super.onSeeCreature(npc, creature);
	}
}