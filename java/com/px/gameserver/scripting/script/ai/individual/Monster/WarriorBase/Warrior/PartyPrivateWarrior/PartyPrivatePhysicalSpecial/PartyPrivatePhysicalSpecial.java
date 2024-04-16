package com.px.gameserver.scripting.script.ai.individual.Monster.WarriorBase.Warrior.PartyPrivateWarrior.PartyPrivatePhysicalSpecial;

import com.px.commons.random.Rnd;

import com.px.gameserver.enums.IntentionType;
import com.px.gameserver.enums.actors.NpcSkillType;
import com.px.gameserver.model.actor.Creature;
import com.px.gameserver.model.actor.Npc;
import com.px.gameserver.model.actor.Playable;
import com.px.gameserver.scripting.script.ai.individual.Monster.WarriorBase.Warrior.PartyPrivateWarrior.PartyPrivateWarrior;
import com.px.gameserver.skills.L2Skill;

public class PartyPrivatePhysicalSpecial extends PartyPrivateWarrior
{
	public PartyPrivatePhysicalSpecial()
	{
		super("ai/individual/Monster/WarriorBase/Warrior/PartyPrivateWarrior");
	}
	
	public PartyPrivatePhysicalSpecial(String descr)
	{
		super(descr);
	}
	
	protected final int[] _npcIds =
	{
		20971,
		20772,
		20961,
		20965,
		20759,
		21518,
		20955,
		20995,
		20954,
		20949,
		20957,
		20967,
		21436,
		20946,
		20937,
		20746,
		20752,
		20754,
		20938,
		20984,
		20992,
		20993,
		21073,
		21076,
		21079,
		21082,
		21091,
		21519
	};
	
	@Override
	public void onAttacked(Npc npc, Creature attacker, int damage, L2Skill skill)
	{
		if (attacker instanceof Playable)
		{
			final Creature topDesireTarget = npc.getAI().getTopDesireTarget();
			if (topDesireTarget != null && topDesireTarget == attacker)
			{
				if (Rnd.get(100) < 33)
				{
					final L2Skill physicalSpecial = getNpcSkillByType(npc, NpcSkillType.PHYSICAL_SPECIAL);
					npc.getAI().addCastDesire(attacker, physicalSpecial, 1000000);
				}
			}
		}
		super.onAttacked(npc, attacker, damage, skill);
	}
	
	@Override
	public void onPartyAttacked(Npc caller, Npc called, Creature target, int damage)
	{
		if (target instanceof Playable && called.getAI().getLifeTime() > 7 && called.getAI().getCurrentIntention().getType() != IntentionType.ATTACK)
		{
			if (Rnd.get(100) < 33)
			{
				final L2Skill physicalSpecial = getNpcSkillByType(called, NpcSkillType.PHYSICAL_SPECIAL);
				called.getAI().addCastDesire(target, physicalSpecial, 1000000);
			}
		}
		super.onPartyAttacked(caller, called, target, damage);
	}
}