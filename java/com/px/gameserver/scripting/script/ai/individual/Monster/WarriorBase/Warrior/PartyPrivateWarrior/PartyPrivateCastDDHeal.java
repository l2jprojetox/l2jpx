package com.px.gameserver.scripting.script.ai.individual.Monster.WarriorBase.Warrior.PartyPrivateWarrior;

import com.px.commons.random.Rnd;

import com.px.gameserver.enums.actors.NpcSkillType;
import com.px.gameserver.model.actor.Creature;
import com.px.gameserver.model.actor.Npc;
import com.px.gameserver.model.actor.Playable;
import com.px.gameserver.skills.L2Skill;

public class PartyPrivateCastDDHeal extends PartyPrivateWarrior
{
	public PartyPrivateCastDDHeal()
	{
		super("ai/individual/Monster/WarriorBase/Warrior/PartyPrivateWarrior");
	}
	
	public PartyPrivateCastDDHeal(String descr)
	{
		super(descr);
	}
	
	protected final int[] _npcIds =
	{
		20948,
		20943,
		20987,
		20975,
		20764,
		20981,
		21039,
		21059,
		21074,
		21080
	};
	
	@Override
	public void onAttacked(Npc npc, Creature attacker, int damage, L2Skill skill)
	{
		if (Rnd.get(100) < 33 && npc.getStatus().getHpRatio() < 0.7)
		{
			final L2Skill magicHeal = getNpcSkillByType(npc, NpcSkillType.MAGIC_HEAL);
			npc.getAI().addCastDesire(npc, magicHeal, 1000000);
		}
		
		if (attacker instanceof Playable && npc.distance2D(attacker) > 100)
		{
			final Creature topDesireTarget = npc.getAI().getTopDesireTarget();
			if (topDesireTarget != null && topDesireTarget == attacker)
			{
				if (Rnd.get(100) < 33)
				{
					final L2Skill ddMagic = getNpcSkillByType(npc, NpcSkillType.DD_MAGIC);
					npc.getAI().addCastDesire(attacker, ddMagic, 1000000);
				}
			}
		}
		super.onAttacked(npc, attacker, damage, skill);
	}
	
	@Override
	public void onPartyAttacked(Npc caller, Npc called, Creature target, int damage)
	{
		if (caller != called && Rnd.get(100) < 33 && caller.getStatus().getHpRatio() < 0.7)
		{
			final L2Skill magicHeal = getNpcSkillByType(called, NpcSkillType.MAGIC_HEAL);
			called.getAI().addCastDesire(caller, magicHeal, 1000000);
		}
		
		if (target instanceof Playable && called.distance2D(target) > 100)
		{
			if (Rnd.get(100) < 33)
			{
				final L2Skill ddMagic = getNpcSkillByType(called, NpcSkillType.DD_MAGIC);
				called.getAI().addCastDesire(target, ddMagic, 1000000);
			}
		}
		super.onPartyAttacked(caller, called, target, damage);
	}
}