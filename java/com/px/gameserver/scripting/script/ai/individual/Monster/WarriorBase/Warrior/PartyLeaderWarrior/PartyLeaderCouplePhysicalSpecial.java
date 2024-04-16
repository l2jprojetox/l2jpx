package com.px.gameserver.scripting.script.ai.individual.Monster.WarriorBase.Warrior.PartyLeaderWarrior;

import com.px.commons.random.Rnd;

import com.px.gameserver.enums.actors.NpcSkillType;
import com.px.gameserver.model.actor.Attackable;
import com.px.gameserver.model.actor.Creature;
import com.px.gameserver.model.actor.Npc;
import com.px.gameserver.model.actor.Playable;
import com.px.gameserver.skills.L2Skill;

public class PartyLeaderCouplePhysicalSpecial extends PartyLeaderWarrior
{
	public PartyLeaderCouplePhysicalSpecial()
	{
		super("ai/individual/Monster/WarriorBase/Warrior/PartyLeaderWarrior");
	}
	
	public PartyLeaderCouplePhysicalSpecial(String descr)
	{
		super(descr);
	}
	
	protected final int[] _npcIds =
	{
		21432
	};
	
	@Override
	public void onAttacked(Npc npc, Creature attacker, int damage, L2Skill skill)
	{
		if (attacker instanceof Playable)
		{
			if (npc._i_ai0 == 0)
			{
				final Creature topDesireTarget = npc.getAI().getTopDesireTarget();
				if (topDesireTarget != null && topDesireTarget == attacker)
				{
					if (npc.getStatus().getHpRatio() < 0.2 && Rnd.get(100) < 33)
					{
						final L2Skill physicalSpecial = getNpcSkillByType(npc, NpcSkillType.PHYSICAL_SPECIAL);
						npc.getAI().addCastDesire(attacker, physicalSpecial, 1000000);
						
						npc._i_ai0 = 1;
					}
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
	
	@Override
	public void onPartyDied(Npc caller, Npc called)
	{
		if (called.isMaster())
		{
			if (caller.distance2D(called) < 100)
			{
				final L2Skill magicHeal = getNpcSkillByType(called, NpcSkillType.MAGIC_HEAL);
				called.getAI().addCastDesire(called, magicHeal, 1000000);
				
				final L2Skill selfBuff = getNpcSkillByType(called, NpcSkillType.SELF_BUFF);
				called.getAI().addCastDesire(called, selfBuff, 1000000);
				
				final Creature topDesireTarget = called.getAI().getTopDesireTarget();
				if (topDesireTarget != null)
				{
					called.removeAllAttackDesire();
					called.getAI().addAttackDesire(topDesireTarget, 1000);
				}
			}
		}
		super.onPartyDied(caller, called);
	}
}
