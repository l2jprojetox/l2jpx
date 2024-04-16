package com.px.gameserver.scripting.script.ai.individual.Monster.WarriorBase.Warrior.WarriorCastingHeal;

import com.px.gameserver.model.actor.Creature;
import com.px.gameserver.model.actor.Npc;
import com.px.gameserver.model.actor.Playable;

public class WarriorCastingHealAggressive extends WarriorCastingHeal
{
	public WarriorCastingHealAggressive()
	{
		super("ai/individual/Monster/WarriorBase/Warrior/WarriorCastingHeal");
	}
	
	public WarriorCastingHealAggressive(String descr)
	{
		super(descr);
	}
	
	protected final int[] _npcIds =
	{
		22132
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