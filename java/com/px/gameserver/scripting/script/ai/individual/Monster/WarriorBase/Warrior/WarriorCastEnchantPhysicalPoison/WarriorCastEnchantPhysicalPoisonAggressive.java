package com.px.gameserver.scripting.script.ai.individual.Monster.WarriorBase.Warrior.WarriorCastEnchantPhysicalPoison;

import com.px.gameserver.model.actor.Creature;
import com.px.gameserver.model.actor.Npc;
import com.px.gameserver.model.actor.Playable;

public class WarriorCastEnchantPhysicalPoisonAggressive extends WarriorCastEnchantPhysicalPoison
{
	public WarriorCastEnchantPhysicalPoisonAggressive()
	{
		super("ai/individual/Monster/WarriorBase/Warrior/WarriorCastEnchantPhysicalPoison");
	}
	
	public WarriorCastEnchantPhysicalPoisonAggressive(String descr)
	{
		super(descr);
	}
	
	protected final int[] _npcIds =
	{
		21316
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