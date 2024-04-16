package com.px.gameserver.scripting.script.ai.individual.Monster.WarriorBase.Warrior.WarriorCast3SkillsApproach;

import com.px.commons.random.Rnd;

import com.px.gameserver.enums.IntentionType;
import com.px.gameserver.enums.actors.NpcSkillType;
import com.px.gameserver.model.actor.Creature;
import com.px.gameserver.model.actor.Npc;
import com.px.gameserver.model.actor.Playable;
import com.px.gameserver.skills.L2Skill;

public class WarriorCast3SkillsApproachAggressive extends WarriorCast3SkillsApproach
{
	public WarriorCast3SkillsApproachAggressive()
	{
		super("ai/individual/Monster/WarriorBase/Warrior/WarriorCast3SkillsApproach");
	}
	
	public WarriorCast3SkillsApproachAggressive(String descr)
	{
		super(descr);
	}
	
	protected final int[] _npcIds =
	{
		21309,
		20814,
		21273,
		20674,
		20811,
		21061,
		20820,
		20816,
		21116,
		20629,
		21402,
		21034,
		21641,
		21262,
		21263,
		21264,
		20829,
		20827,
		21649,
		20136,
		20853,
		20854,
		20861,
		21036,
		21087,
		21089
	};
	
	@Override
	public void onSeeCreature(Npc npc, Creature creature)
	{
		if (!(creature instanceof Playable))
			return;
		
		if (npc.getAI().getLifeTime() > 7 && npc.getAI().getCurrentIntention().getType() != IntentionType.ATTACK && npc.isInMyTerritory())
		{
			if (Rnd.get(100) < 33)
			{
				final L2Skill physicalSpecial = getNpcSkillByType(npc, NpcSkillType.PHYSICAL_SPECIAL);
				npc.getAI().addCastDesire(creature, physicalSpecial, 1000000);
			}
		}
		
		tryToAttack(npc, creature);
		
		super.onSeeCreature(npc, creature);
	}
}