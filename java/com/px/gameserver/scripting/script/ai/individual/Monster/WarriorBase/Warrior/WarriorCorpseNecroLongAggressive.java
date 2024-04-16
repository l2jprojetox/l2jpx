package com.px.gameserver.scripting.script.ai.individual.Monster.WarriorBase.Warrior;

import com.px.commons.random.Rnd;

import com.px.gameserver.enums.IntentionType;
import com.px.gameserver.enums.actors.NpcSkillType;
import com.px.gameserver.model.actor.Attackable;
import com.px.gameserver.model.actor.Creature;
import com.px.gameserver.model.actor.Npc;
import com.px.gameserver.model.actor.Playable;
import com.px.gameserver.skills.L2Skill;

public class WarriorCorpseNecroLongAggressive extends Warrior
{
	public WarriorCorpseNecroLongAggressive()
	{
		super("ai/individual/Monster/WarriorBase/Warrior");
	}
	
	public WarriorCorpseNecroLongAggressive(String descr)
	{
		super(descr);
	}
	
	protected final int[] _npcIds =
	{
		21574,
		21567
	};
	
	@Override
	public void onSeeCreature(Npc npc, Creature creature)
	{
		
		if (!(creature instanceof Playable))
		{
			super.onSeeCreature(npc, creature);
			return;
		}
		
		if (npc.getAI().getLifeTime() > 7 && npc.isInMyTerritory() && npc.getAI().getCurrentIntention().getType() != IntentionType.ATTACK)
		{
			if (npc.distance2D(creature) > 100 && Rnd.get(100) < 33)
			{
				L2Skill physicalSpecial = getNpcSkillByType(npc, NpcSkillType.PHYSICAL_SPECIAL);
				
				npc.getAI().addCastDesire(creature, physicalSpecial, 1000000);
			}
		}
		
		if (creature.isDead())
		{
			if (npc.getAI().getCurrentIntention().getType() != IntentionType.ATTACK && Rnd.get(100) < 33 && npc.distance2D(creature) < 100)
			{
				Creature mostHated = npc.getAI().getAggroList().getMostHatedCreature();
				
				if (mostHated != null)
				{
					L2Skill DDMagic2 = getNpcSkillByType(npc, NpcSkillType.DD_MAGIC2);
					
					npc.getAI().addCastDesire(creature, DDMagic2, 1000000);
				}
			}
		}
		super.onSeeCreature(npc, creature);
	}
	
	@Override
	public void onAttacked(Npc npc, Creature attacker, int damage, L2Skill skill)
	{
		if (attacker instanceof Playable)
		{
			Creature mostHated = npc.getAI().getAggroList().getMostHatedCreature();
			
			if (mostHated != null)
			{
				if (mostHated == attacker)
				{
					if (npc.distance2D(attacker) > 100)
					{
						L2Skill physicalSpecial = getNpcSkillByType(npc, NpcSkillType.PHYSICAL_SPECIAL);
						
						npc.getAI().addCastDesire(attacker, physicalSpecial, 1000000);
					}
					if (Rnd.get(100) < 33 && (((npc.getStatus().getHp() / npc.getStatus().getMaxHp()) * 100)) < 40)
					{
						L2Skill DDMagic1 = getNpcSkillByType(npc, NpcSkillType.DD_MAGIC1);
						
						npc.getAI().addCastDesire(attacker, DDMagic1, 1000000);
					}
				}
			}
		}
		super.onAttacked(npc, attacker, damage, skill);
	}
	
	@Override
	public void onClanAttacked(Attackable caller, Attackable called, Creature attacker, int damage, L2Skill skill)
	{
		if ((called.getAI().getLifeTime() > 7 && attacker instanceof Playable) && called.getAI().getCurrentIntention().getType() != IntentionType.ATTACK)
		{
			if (called.distance2D(attacker) > 100)
			{
				L2Skill physicalSpecial = getNpcSkillByType(called, NpcSkillType.PHYSICAL_SPECIAL);
				
				called.getAI().addCastDesire(attacker, physicalSpecial, 1000000);
			}
		}
		super.onClanAttacked(caller, called, attacker, damage, skill);
	}
}
