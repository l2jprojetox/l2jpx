package com.px.gameserver.scripting.script.ai.individual.Monster.WizardBase.Wizard.WizardDDMagic2.WizardCorpseVampireBasic;

import com.px.commons.random.Rnd;

import com.px.gameserver.enums.IntentionType;
import com.px.gameserver.enums.actors.NpcSkillType;
import com.px.gameserver.model.actor.Creature;
import com.px.gameserver.model.actor.Npc;
import com.px.gameserver.model.actor.Playable;
import com.px.gameserver.scripting.script.ai.individual.Monster.WizardBase.Wizard.WizardDDMagic2.WizardDDMagic2;
import com.px.gameserver.skills.L2Skill;

public class WizardCorpseVampireBasic extends WizardDDMagic2
{
	public WizardCorpseVampireBasic()
	{
		super("ai/individual/Monster/WizardBase/Wizard/WizardDDMagic2/WizardCorpseVampireBasic");
	}
	
	public WizardCorpseVampireBasic(String descr)
	{
		super(descr);
	}
	
	protected final int[] _npcIds =
	{
		21576,
		21422,
		21590
	};
	
	@Override
	public void onCreated(Npc npc)
	{
		npc._i_ai0 = 0;
		
		super.onCreated(npc);
	}
	
	@Override
	public void onAttacked(Npc npc, Creature attacker, int damage, L2Skill skill)
	{
		if (attacker instanceof Playable)
		{
			final Creature topDesireTarget = npc.getAI().getTopDesireTarget();
			if (topDesireTarget != null)
			{
				final double hpRatio = npc.getStatus().getHpRatio();
				
				if (hpRatio > 0.9 && npc.getAI().getCurrentIntention().getType() != IntentionType.ATTACK)
				{
					final L2Skill debuff = getNpcSkillByType(npc, NpcSkillType.DEBUFF);
					npc.getAI().addCastDesire(attacker, debuff, 1000000);
				}
				
				if (topDesireTarget == attacker)
				{
					if (npc._i_ai0 == 0 && Rnd.get(100) < 33 && hpRatio < 0.5)
					{
						final L2Skill DDMagic = getNpcSkillByType(npc, NpcSkillType.DD_MAGIC);
						npc.getAI().addCastDesire(attacker, DDMagic, 1000000);
						
						npc._i_ai0 = 1;
					}
				}
			}
		}
		super.onAttacked(npc, attacker, damage, skill);
	}
}