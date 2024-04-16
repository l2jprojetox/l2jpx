package com.px.gameserver.scripting.script.ai.individual.Monster.WarriorBase.Warrior.WarriorCast3SkillsMagical2;

import com.px.commons.random.Rnd;

import com.px.gameserver.enums.IntentionType;
import com.px.gameserver.enums.actors.NpcSkillType;
import com.px.gameserver.model.actor.Creature;
import com.px.gameserver.model.actor.Npc;
import com.px.gameserver.model.actor.Playable;
import com.px.gameserver.skills.L2Skill;

public class WarriorCast3SkillsMagical2Aggressive extends WarriorCast3SkillsMagical2
{
	public WarriorCast3SkillsMagical2Aggressive()
	{
		super("ai/individual/Monster/WarriorBase/Warrior/WarriorCast3SkillsMagical2");
	}
	
	public WarriorCast3SkillsMagical2Aggressive(String descr)
	{
		super(descr);
	}
	
	protected final int[] _npcIds =
	{
		18001,
		22024,
		22017,
		22128,
		20621,
		21006,
		22108,
		20825,
		22053,
		22054,
		22074,
		22060,
		22113,
		22109,
		20160,
		20198,
		22118,
		21066,
		22133,
		20612,
		21647,
		21026,
		22111,
		22121,
		20056,
		20117,
		20118,
		20352,
		20421,
		18002,
		22112,
		22120
	};
	
	@Override
	public void onSeeCreature(Npc npc, Creature creature)
	{
		if (!(creature instanceof Playable))
			return;
		
		if (npc.getAI().getLifeTime() > 7 && npc.isInMyTerritory())
		{
			if (npc.distance2D(creature) > 100)
			{
				if (Rnd.get(100) < 33)
				{
					final L2Skill DDMagic1 = getNpcSkillByType(npc, NpcSkillType.DD_MAGIC1);
					npc.getAI().addCastDesire(creature, DDMagic1, 1000000);
				}
				
				if (Rnd.get(100) < 33)
				{
					final L2Skill DDMagic2 = getNpcSkillByType(npc, NpcSkillType.DD_MAGIC2);
					npc.getAI().addCastDesire(creature, DDMagic2, 1000000);
				}
			}
			
			if (npc.getAI().getCurrentIntention().getType() == IntentionType.WANDER)
			{
				final L2Skill debuff = getNpcSkillByType(npc, NpcSkillType.DEBUFF);
				if (Rnd.get(100) < 33 && getAbnormalLevel(creature, debuff) <= 0)
					npc.getAI().addCastDesire(creature, debuff, 1000000);
			}
		}
		
		tryToAttack(npc, creature);
		
		super.onSeeCreature(npc, creature);
	}
}