package com.px.gameserver.scripting.script.ai.individual.Monster.WizardBase.Wizard.WizardDDMagic2;

import com.px.gameserver.model.actor.Creature;
import com.px.gameserver.model.actor.Npc;
import com.px.gameserver.model.actor.Playable;

public class WizardHealerAggressive extends WizardHealer
{
	public WizardHealerAggressive()
	{
		super("ai/individual/Monster/WizardBase/Wizard/WizardDDMagic2");
	}
	
	public WizardHealerAggressive(String descr)
	{
		super(descr);
	}
	
	protected final int[] _npcIds =
	{
		21412
	};
	
	@Override
	public void onSeeCreature(Npc npc, Creature creature)
	{
		if (!(creature instanceof Playable))
			return;
		
		tryToAttack(npc, creature);
		
		super.onSeeCreature(npc, creature);
	}
}