package com.px.gameserver.scripting.script.ai.individual.Monster.WizardBase.Wizard.WizardDDMagic2;

import com.px.commons.random.Rnd;

import com.px.gameserver.enums.actors.NpcSkillType;
import com.px.gameserver.model.actor.Attackable;
import com.px.gameserver.model.actor.Creature;
import com.px.gameserver.model.actor.Npc;
import com.px.gameserver.model.actor.Playable;
import com.px.gameserver.skills.L2Skill;

public class WizardDDMagic2Heal extends WizardDDMagic2
{
	public WizardDDMagic2Heal()
	{
		super("ai/individual/Monster/WizardBase/Wizard/WizardDDMagic2");
	}
	
	public WizardDDMagic2Heal(String descr)
	{
		super(descr);
	}
	
	protected final int[] _npcIds =
	{
		20292,
		20807,
		21414,
		20657,
		20923,
		20576,
		20633
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
	public void onClanAttacked(Attackable caller, Attackable called, Creature attacker, int damage, L2Skill skill)
	{
		if (((called.getAI().getLifeTime() > 7 && attacker instanceof Playable) && !called.getAI().getHateList().isEmpty()) && Rnd.get(100) < 33)
		{
			L2Skill magicHeal = getNpcSkillByType(called, NpcSkillType.MAGIC_HEAL);
			
			called.getAI().addCastDesire(caller, magicHeal, 1000000);
		}
		super.onClanAttacked(caller, called, attacker, damage, skill);
	}
}
