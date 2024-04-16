package com.px.gameserver.scripting.script.ai.individual.Monster.WizardBase.Wizard.WizardFiendArcher;

import com.px.gameserver.enums.IntentionType;
import com.px.gameserver.enums.actors.NpcSkillType;
import com.px.gameserver.model.actor.Creature;
import com.px.gameserver.model.actor.Npc;
import com.px.gameserver.model.actor.Playable;
import com.px.gameserver.skills.L2Skill;

public class WizardFiendArcherSelfBuffAggressive extends WizardFiendArcherSelfBuff
{
	public WizardFiendArcherSelfBuffAggressive()
	{
		super("ai/individual/Monster/WizardBase/Wizard/WizardFiendArcher");
	}
	
	public WizardFiendArcherSelfBuffAggressive(String descr)
	{
		super(descr);
	}
	
	protected final int[] _npcIds =
	{
		21388
	};
	
	@Override
	public void onSeeCreature(Npc npc, Creature creature)
	{
		if (npc.getAI().getCurrentIntention().getType() != IntentionType.ATTACK && npc.getAI().getCurrentIntention().getType() != IntentionType.CAST)
		{
			final L2Skill selfBuff = getNpcSkillByType(npc, NpcSkillType.SELF_BUFF);
			if (getAbnormalLevel(creature, selfBuff) <= 0)
				npc.getAI().addCastDesire(npc, selfBuff, 1000000);
		}
		
		if (!(creature instanceof Playable))
			return;
		
		tryToAttack(npc, creature);
		
		super.onSeeCreature(npc, creature);
	}
}