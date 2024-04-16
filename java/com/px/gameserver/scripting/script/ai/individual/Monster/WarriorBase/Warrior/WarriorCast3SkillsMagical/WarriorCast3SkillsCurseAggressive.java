package com.px.gameserver.scripting.script.ai.individual.Monster.WarriorBase.Warrior.WarriorCast3SkillsMagical;

import com.px.gameserver.model.actor.Creature;
import com.px.gameserver.model.actor.Npc;
import com.px.gameserver.model.actor.Playable;

public class WarriorCast3SkillsCurseAggressive extends WarriorCast3SkillsCurse
{
	public WarriorCast3SkillsCurseAggressive()
	{
		super("ai/individual/Monster/WarriorBase/Warrior/WarriorCast3SkillsMagical");
	}
	
	public WarriorCast3SkillsCurseAggressive(String descr)
	{
		super(descr);
	}
	
	protected final int[] _npcIds =
	{
		21302
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