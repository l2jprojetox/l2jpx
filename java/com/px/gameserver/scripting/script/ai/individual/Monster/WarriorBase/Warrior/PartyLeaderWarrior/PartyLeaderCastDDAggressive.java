package com.px.gameserver.scripting.script.ai.individual.Monster.WarriorBase.Warrior.PartyLeaderWarrior;

import com.px.gameserver.model.actor.Creature;
import com.px.gameserver.model.actor.Npc;
import com.px.gameserver.model.actor.Playable;

public class PartyLeaderCastDDAggressive extends PartyLeaderCastDD
{
	public PartyLeaderCastDDAggressive()
	{
		super("ai/individual/Monster/WarriorBase/Warrior/PartyLeaderWarrior");
	}
	
	public PartyLeaderCastDDAggressive(String descr)
	{
		super(descr);
	}
	
	protected final int[] _npcIds =
	{
		20963,
		22123,
		20749,
		20969,
		22135,
		20944,
		20761,
		20747,
		20753,
		20771,
		20977
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