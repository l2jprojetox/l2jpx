package com.px.gameserver.scripting.script.ai.individual.Monster.WarriorBase.Warrior.WarriorCorpseGhostDDMagic;

import com.px.commons.random.Rnd;

import com.px.gameserver.enums.IntentionType;
import com.px.gameserver.enums.actors.NpcSkillType;
import com.px.gameserver.model.actor.Creature;
import com.px.gameserver.model.actor.Npc;
import com.px.gameserver.model.actor.Playable;
import com.px.gameserver.skills.L2Skill;

public class WarriorCorpseGhostDDMagicAggressive extends WarriorCorpseGhostDDMagic
{
	public WarriorCorpseGhostDDMagicAggressive()
	{
		super("ai/individual/Monster/WarriorBase/Warrior/WarriorCorpseGhostDDMagic");
	}
	
	public WarriorCorpseGhostDDMagicAggressive(String descr)
	{
		super(descr);
	}
	
	protected final int[] _npcIds =
	{
		21562
	};
	
	@Override
	public void onSeeCreature(Npc npc, Creature creature)
	{
		if (!(creature instanceof Playable))
			return;
		
		if (npc.getAI().getLifeTime() > 7 && npc.getAI().getCurrentIntention().getType() != IntentionType.ATTACK && npc.isInMyTerritory())
		{
			if (Rnd.get(100) < 33 && npc.distance2D(creature) > 100)
			{
				final L2Skill DDMagic = getNpcSkillByType(npc, NpcSkillType.DD_MAGIC);
				npc.getAI().addCastDesire(creature, DDMagic, 1000000);
			}
		}
		
		tryToAttack(npc, creature);
		
		super.onSeeCreature(npc, creature);
	}
}