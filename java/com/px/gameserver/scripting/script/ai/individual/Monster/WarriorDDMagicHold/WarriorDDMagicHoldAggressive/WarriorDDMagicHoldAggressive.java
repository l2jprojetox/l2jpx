package com.px.gameserver.scripting.script.ai.individual.Monster.WarriorDDMagicHold.WarriorDDMagicHoldAggressive;

import com.px.gameserver.enums.IntentionType;
import com.px.gameserver.enums.actors.NpcSkillType;
import com.px.gameserver.model.actor.Creature;
import com.px.gameserver.model.actor.Npc;
import com.px.gameserver.model.actor.Playable;
import com.px.gameserver.scripting.script.ai.individual.Monster.WarriorDDMagicHold.WarriorDDMagicHold;

public class WarriorDDMagicHoldAggressive extends WarriorDDMagicHold
{
	public WarriorDDMagicHoldAggressive()
	{
		super("ai/individual/Monster/WarriorDDMagicHold/WarriorDDMagicHoldAggressive");
	}
	
	public WarriorDDMagicHoldAggressive(String descr)
	{
		super(descr);
	}
	
	protected final int[] _npcIds =
	{
		21394, // lavasaurus
		21395, // elder_lavasaurus
	};
	
	@Override
	public void onSeeCreature(Npc npc, Creature creature)
	{
		final IntentionType currentIntentionType = npc.getAI().getCurrentIntention().getType();
		if (currentIntentionType != IntentionType.ATTACK && currentIntentionType != IntentionType.CAST)
			npc.getAI().addCastDesireHold(creature, getNpcSkillByType(npc, NpcSkillType.DD_MAGIC), 1000000);
		
		if (creature instanceof Playable)
			npc.getAI().addAttackDesireHold(creature, 50);
	}
}