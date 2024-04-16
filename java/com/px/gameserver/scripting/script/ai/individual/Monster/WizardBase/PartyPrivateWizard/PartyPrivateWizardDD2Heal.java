package com.px.gameserver.scripting.script.ai.individual.Monster.WizardBase.PartyPrivateWizard;

import com.px.commons.random.Rnd;

import com.px.gameserver.enums.actors.NpcSkillType;
import com.px.gameserver.model.actor.Attackable;
import com.px.gameserver.model.actor.Creature;
import com.px.gameserver.model.actor.Npc;
import com.px.gameserver.model.actor.Playable;
import com.px.gameserver.model.actor.container.attackable.HateList;
import com.px.gameserver.skills.L2Skill;

public class PartyPrivateWizardDD2Heal extends PartyPrivateWizardDD2
{
	public PartyPrivateWizardDD2Heal()
	{
		super("ai/individual/Monster/WizardBase/PartyPrivateWizard");
	}
	
	public PartyPrivateWizardDD2Heal(String descr)
	{
		super(descr);
	}
	
	protected final int[] _npcIds =
	{
		21543,
		21546,
		21823
	};
	
	@Override
	public void onAttacked(Npc npc, Creature attacker, int damage, L2Skill skill)
	{
		super.onAttacked(npc, attacker, damage, skill);
		if (attacker instanceof Playable)
		{
			if (Rnd.get(100) < 33 && ((npc.getStatus().getHp() / npc.getStatus().getMaxHp()) * 100) < 70)
			{
				L2Skill magicHeal = getNpcSkillByType(npc, NpcSkillType.MAGIC_HEAL);
				
				npc.getAI().addCastDesire(npc, magicHeal, 1000000);
			}
		}
	}
	
	@Override
	public void onPartyAttacked(Npc caller, Npc called, Creature target, int damage)
	{
		super.onPartyAttacked(caller, called, target, damage);
		if (target instanceof Playable)
		{
			if (Rnd.get(100) < 33 && ((caller.getStatus().getHp() / caller.getStatus().getMaxHp()) * 100) < 70)
			{
				L2Skill magicHeal = getNpcSkillByType(called, NpcSkillType.MAGIC_HEAL);
				
				called.getAI().addCastDesire(caller, magicHeal, 1000000);
			}
		}
	}
	
	@Override
	public void onClanAttacked(Attackable caller, Attackable called, Creature attacker, int damage, L2Skill skill)
	{
		HateList hateList = called.getAI().getHateList();
		
		if ((called.getAI().getLifeTime() > 7 && attacker instanceof Playable) && hateList.isEmpty() && Rnd.get(100) < 33)
		{
			L2Skill magicHeal = getNpcSkillByType(called, NpcSkillType.MAGIC_HEAL);
			
			called.getAI().addCastDesire(caller, magicHeal, 1000000);
		}
		super.onClanAttacked(caller, called, attacker, damage, skill);
	}
}
