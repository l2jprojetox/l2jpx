package com.px.gameserver.scripting.script.ai.individual.Monster.WarriorBase.Warrior.WarriorSlowType;

import com.px.commons.random.Rnd;

import com.px.gameserver.enums.IntentionType;
import com.px.gameserver.enums.actors.NpcSkillType;
import com.px.gameserver.model.actor.Creature;
import com.px.gameserver.model.actor.Npc;
import com.px.gameserver.model.actor.Playable;
import com.px.gameserver.skills.L2Skill;

public class WarriorSlowType2Aggressive extends WarriorSlowType2
{
	public WarriorSlowType2Aggressive()
	{
		super("ai/individual/Monster/WarriorBase/Warrior/WarriorSlowType");
	}
	
	public WarriorSlowType2Aggressive(String descr)
	{
		super(descr);
	}
	
	protected final int[] _npcIds =
	{
		20996,
		20458
	};
	
	@Override
	public void onSeeCreature(Npc npc, Creature creature)
	{
		if (!(creature instanceof Playable))
		{
			super.onSeeCreature(npc, creature);
			return;
		}
		
		if (npc.getAI().getLifeTime() > 7 && npc.getAI().getCurrentIntention().getType() != IntentionType.ATTACK && npc.isInMyTerritory())
		{
			if (npc.distance2D(creature) > 100 && Rnd.get(100) < 10)
			{
				final L2Skill DDMagicSlow = getNpcSkillByType(npc, NpcSkillType.DD_MAGIC_SLOW);
				npc.getAI().addCastDesire(creature, DDMagicSlow, 1000000);
			}
		}
		
		tryToAttack(npc, creature);
		
		super.onSeeCreature(npc, creature);
	}
}