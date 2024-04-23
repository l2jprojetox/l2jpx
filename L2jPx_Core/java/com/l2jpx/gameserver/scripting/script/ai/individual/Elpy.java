package com.l2jpx.gameserver.scripting.script.ai.individual;

import com.l2jpx.Config;
import com.l2jpx.gameserver.model.actor.Creature;
import com.l2jpx.gameserver.model.actor.Npc;
import com.l2jpx.gameserver.scripting.script.ai.AttackableAIScript;
import com.l2jpx.gameserver.skills.L2Skill;

/**
 * A fleeing NPC.<br>
 * <br>
 * His behavior is to always flee, and never attack.
 */
public class Elpy extends AttackableAIScript
{
	public Elpy()
	{
		super("ai/individual");
	}
	
	@Override
	protected void registerNpcs()
	{
		addAttackId(20432);
	}
	
	@Override
	public String onAttack(Npc npc, Creature attacker, int damage, L2Skill skill)
	{
		npc.disableCoreAi(true);
		
		// Wait the NPC to be immobile to move him again.
		if (!npc.isMoving())
			npc.fleeFrom(attacker, Config.MAX_DRIFT_RANGE);
		
		return super.onAttack(npc, attacker, damage, skill);
	}
}