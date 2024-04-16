package com.px.gameserver.scripting.script.ai.individual.Monster.WarriorBase.Warrior.WarriorCastSplash;

import com.px.gameserver.model.actor.Creature;
import com.px.gameserver.model.actor.Npc;
import com.px.gameserver.model.actor.Playable;

public class WarriorCastSplashAggressive extends WarriorCastSplash
{
	public WarriorCastSplashAggressive()
	{
		super("ai/individual/Monster/WarriorBase/Warrior/WarriorCastSplash");
	}
	
	public WarriorCastSplashAggressive(String descr)
	{
		super(descr);
	}
	
	protected final int[] _npcIds =
	{
		21284,
		21035,
		21291,
		21428,
		20815,
		20212,
		20294,
		20512,
		20549,
		21141,
		21147,
		21149,
		21153,
		21159,
		21164
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