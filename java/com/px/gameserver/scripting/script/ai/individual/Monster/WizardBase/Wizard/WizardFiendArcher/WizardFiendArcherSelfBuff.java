package com.px.gameserver.scripting.script.ai.individual.Monster.WizardBase.Wizard.WizardFiendArcher;

import com.px.gameserver.enums.IntentionType;
import com.px.gameserver.enums.actors.NpcSkillType;
import com.px.gameserver.model.actor.Creature;
import com.px.gameserver.model.actor.Npc;
import com.px.gameserver.model.actor.Playable;
import com.px.gameserver.skills.L2Skill;

public class WizardFiendArcherSelfBuff extends WizardFiendArcher
{
	public WizardFiendArcherSelfBuff()
	{
		super("ai/individual/Monster/WizardBase/Wizard/WizardFiendArcher");
	}
	
	public WizardFiendArcherSelfBuff(String descr)
	{
		super(descr);
	}
	
	@Override
	public void onCreated(Npc npc)
	{
		L2Skill selfBuff = getNpcSkillByType(npc, NpcSkillType.SELF_BUFF);
		
		npc.getAI().addCastDesire(npc, selfBuff, 1000000);
		
		super.onCreated(npc);
	}
	
	@Override
	public void onAttacked(Npc npc, Creature attacker, int damage, L2Skill skill)
	{
		if (attacker instanceof Playable)
		{
			if (npc.getAI().getCurrentIntention().getType() != IntentionType.ATTACK && npc.getAI().getCurrentIntention().getType() != IntentionType.CAST)
			{
				L2Skill selfBuff = getNpcSkillByType(npc, NpcSkillType.SELF_BUFF);
				
				if (getAbnormalLevel(npc, selfBuff) <= 0)
				{
					npc.getAI().addCastDesire(npc, selfBuff, 1000000);
				}
			}
		}
		super.onAttacked(npc, attacker, damage, skill);
	}
}
