package com.px.gameserver.scripting.script.ai.individual.Monster.WarriorBase.Warrior.WarriorCorpseZombie;

import com.px.commons.random.Rnd;

import com.px.gameserver.enums.IntentionType;
import com.px.gameserver.enums.actors.NpcSkillType;
import com.px.gameserver.model.actor.Attackable;
import com.px.gameserver.model.actor.Creature;
import com.px.gameserver.model.actor.Npc;
import com.px.gameserver.model.actor.Playable;
import com.px.gameserver.skills.L2Skill;

public class WarriorCorpseZombiePhysicalSpecial extends WarriorCorpseZombie
{
	public WarriorCorpseZombiePhysicalSpecial()
	{
		super("ai/individual/Monster/WarriorBase/Warrior/WarriorCorpseZombie");
	}
	
	public WarriorCorpseZombiePhysicalSpecial(String descr)
	{
		super(descr);
	}
	
	protected final int[] _npcIds =
	{
		21578,
		21549,
		21547,
		21551,
		21598,
		21601
	};
	
	@Override
	public void onAttacked(Npc npc, Creature attacker, int damage, L2Skill skill)
	{
		if (attacker instanceof Playable)
		{
			Creature mostHated = npc.getAI().getAggroList().getMostHatedCreature();
			
			if (mostHated != null)
			{
				if (mostHated == attacker && Rnd.get(100) < 33)
				{
					L2Skill physicalSpecial = getNpcSkillByType(npc, NpcSkillType.PHYSICAL_SPECIAL);
					npc.getAI().addCastDesire(attacker, physicalSpecial, 1000000);
				}
			}
		}
		super.onAttacked(npc, attacker, damage, skill);
	}
	
	@Override
	public void onClanAttacked(Attackable caller, Attackable called, Creature attacker, int damage, L2Skill skill)
	{
		if ((called.getAI().getLifeTime() > 7 && (attacker instanceof Playable)) && called.getAI().getCurrentIntention().getType() != IntentionType.ATTACK)
		{
			if (Rnd.get(100) < 33)
			{
				L2Skill physicalSpecial = getNpcSkillByType(called, NpcSkillType.PHYSICAL_SPECIAL);
				called.getAI().addCastDesire(attacker, physicalSpecial, 1000000);
			}
		}
		super.onClanAttacked(caller, called, attacker, damage, skill);
	}
}
