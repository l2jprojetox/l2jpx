package com.px.gameserver.scripting.script.ai.individual.Monster.WizardBase.Wizard.WizardFiendArcher;

import com.px.gameserver.enums.actors.NpcSkillType;
import com.px.gameserver.model.actor.Attackable;
import com.px.gameserver.model.actor.Creature;
import com.px.gameserver.model.actor.Npc;
import com.px.gameserver.model.actor.Playable;
import com.px.gameserver.model.actor.Player;
import com.px.gameserver.model.actor.container.attackable.HateList;
import com.px.gameserver.scripting.script.ai.individual.Monster.WizardBase.Wizard.Wizard;
import com.px.gameserver.skills.L2Skill;

public class WizardFiendArcher extends Wizard
{
	public WizardFiendArcher()
	{
		super("ai/individual/Monster/WizardBase/Wizard/WizardFiendArcher");
	}
	
	public WizardFiendArcher(String descr)
	{
		super(descr);
	}
	
	@Override
	public void onAttacked(Npc npc, Creature attacker, int damage, L2Skill skill)
	{
		HateList hateList = npc.getAI().getHateList();
		
		if (attacker instanceof Playable)
		{
			double f0 = getHateRatio(npc, attacker);
			f0 = (((1.0 * damage) / (npc.getStatus().getLevel() + 7)) + ((f0 / 100) * ((1.0 * damage) / (npc.getStatus().getLevel() + 7))));
			
			if (hateList.isEmpty())
			{
				hateList.addHateInfo(attacker, (f0 * 100) + 300);
			}
			else
			{
				hateList.addHateInfo(attacker, f0 * 100);
			}
			
			L2Skill wFiendArcher = getNpcSkillByType(npc, NpcSkillType.W_FIEND_ARCHER);
			
			if (!hateList.isEmpty())
			{
				
				if (npc.getCast().meetsHpMpConditions(attacker, wFiendArcher))
				{
					npc.getAI().addCastDesire(attacker, wFiendArcher, 1000000, false);
				}
				else
				{
					npc._i_ai0 = 1;
					npc.getAI().addAttackDesire(attacker, 1000);
				}
			}
		}
		super.onAttacked(npc, attacker, damage, skill);
	}
	
	@Override
	public void onClanAttacked(Attackable caller, Attackable called, Creature attacker, int damage, L2Skill skill)
	{
		HateList hateList = called.getAI().getHateList();
		
		hateList.refresh();
		
		if (called.getAI().getLifeTime() > 7 && attacker instanceof Playable && hateList.isEmpty())
		{
			L2Skill wFiendArcher = getNpcSkillByType(caller, NpcSkillType.W_FIEND_ARCHER);
			
			if (caller.getCast().meetsHpMpConditions(attacker, wFiendArcher))
			{
				caller.getAI().addCastDesire(attacker, wFiendArcher, 1000000, false);
			}
			else
			{
				caller._i_ai0 = 1;
				caller.getAI().addAttackDesire(attacker, 1000);
			}
		}
		super.onClanAttacked(caller, called, attacker, damage, skill);
	}
	
	@Override
	public void onUseSkillFinished(Npc npc, Player player, L2Skill skill, boolean success)
	{
		Creature mostHatedHI = npc.getAI().getHateList().getMostHatedCreature();
		
		if (mostHatedHI != null)
		{
			L2Skill wFiendArcher = getNpcSkillByType(npc, NpcSkillType.W_FIEND_ARCHER);
			
			if (npc.getCast().meetsHpMpConditions(mostHatedHI, wFiendArcher))
			{
				npc.getAI().addCastDesire(mostHatedHI, wFiendArcher, 1000000, false);
			}
			else
			{
				npc._i_ai0 = 1;
				npc.getAI().addAttackDesire(mostHatedHI, 1000);
			}
		}
		super.onUseSkillFinished(npc, player, skill, success);
	}
}
