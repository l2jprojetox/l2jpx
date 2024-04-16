package com.px.gameserver.scripting.script.ai.individual.Monster.WarriorBase.Warrior.PartyLeaderWarrior;

import com.px.commons.random.Rnd;

import com.px.gameserver.enums.actors.NpcSkillType;
import com.px.gameserver.model.actor.Attackable;
import com.px.gameserver.model.actor.Creature;
import com.px.gameserver.model.actor.Npc;
import com.px.gameserver.model.actor.Playable;
import com.px.gameserver.skills.L2Skill;

public class PartyLeaderPhysicalSpecial extends PartyLeaderWarrior
{
	public PartyLeaderPhysicalSpecial()
	{
		super("ai/individual/Monster/WarriorBase/Warrior/PartyLeaderWarrior");
	}
	
	public PartyLeaderPhysicalSpecial(String descr)
	{
		super(descr);
	}
	
	protected final int[] _npcIds =
	{
		20947,
		20989,
		20974,
		20767
	};
	
	@Override
	public void onAttacked(Npc npc, Creature attacker, int damage, L2Skill skill)
	{
		if (attacker instanceof Playable)
		{
			final Creature topDesireTarget = npc.getAI().getTopDesireTarget();
			if (topDesireTarget != null && topDesireTarget == attacker)
			{
				if (npc._i_ai0 == 0 && npc.getStatus().getHpRatio() < 0.2 && Rnd.get(100) < 33)
				{
					final L2Skill physicalSpecial = getNpcSkillByType(npc, NpcSkillType.PHYSICAL_SPECIAL);
					npc.getAI().addCastDesire(attacker, physicalSpecial, 1000000);
					
					npc._i_ai0 = 1;
				}
			}
		}
		super.onAttacked(npc, attacker, damage, skill);
	}
	
	@Override
	public void onClanAttacked(Attackable caller, Attackable called, Creature attacker, int damage, L2Skill skill)
	{
		if (attacker instanceof Playable && called.getAI().getLifeTime() > 7)
		{
			if (called._i_ai0 == 0 && Rnd.get(100) < 33)
			{
				final L2Skill physicalSpecial = getNpcSkillByType(called, NpcSkillType.PHYSICAL_SPECIAL);
				called.getAI().addCastDesire(attacker, physicalSpecial, 1000000);
				
				called._i_ai0 = 1;
			}
		}
		super.onClanAttacked(caller, called, attacker, damage, skill);
	}
}