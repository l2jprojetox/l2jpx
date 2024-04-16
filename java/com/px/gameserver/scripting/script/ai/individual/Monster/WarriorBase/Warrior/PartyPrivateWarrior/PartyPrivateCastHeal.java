package com.px.gameserver.scripting.script.ai.individual.Monster.WarriorBase.Warrior.PartyPrivateWarrior;

import com.px.commons.random.Rnd;

import com.px.gameserver.enums.actors.NpcSkillType;
import com.px.gameserver.model.actor.Creature;
import com.px.gameserver.model.actor.Npc;
import com.px.gameserver.skills.L2Skill;

public class PartyPrivateCastHeal extends PartyPrivateWarrior
{
	public PartyPrivateCastHeal()
	{
		super("ai/individual/Monster/WarriorBase/Warrior/PartyPrivateWarrior");
	}
	
	public PartyPrivateCastHeal(String descr)
	{
		super(descr);
	}
	
	protected final int[] _npcIds =
	{
		22122,
		20774,
		20990,
		20762
	};
	
	@Override
	public void onAttacked(Npc npc, Creature attacker, int damage, L2Skill skill)
	{
		int i0 = (int) ((npc.getStatus().getHp() / npc.getStatus().getMaxHp()) * 100);
		if (Rnd.get(100) < 33 && i0 < 70)
		{
			L2Skill magicHeal = getNpcSkillByType(npc, NpcSkillType.MAGIC_HEAL);
			
			npc.getAI().addCastDesire(npc, magicHeal, 1000000);
		}
		super.onAttacked(npc, attacker, damage, skill);
	}
	
	@Override
	public void onPartyAttacked(Npc caller, Npc called, Creature target, int damage)
	{
		int i0 = (int) ((caller.getStatus().getHp() / caller.getStatus().getMaxHp()) * 100);
		if (Rnd.get(100) < 33 && i0 < 70)
		{
			L2Skill magicHeal = getNpcSkillByType(called, NpcSkillType.MAGIC_HEAL);
			
			called.getAI().addCastDesire(caller, magicHeal, 1000000);
		}
		super.onPartyAttacked(caller, called, target, damage);
	}
}
