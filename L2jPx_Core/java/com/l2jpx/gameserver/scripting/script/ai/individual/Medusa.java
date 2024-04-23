package com.l2jpx.gameserver.scripting.script.ai.individual;

import com.l2jpx.commons.random.Rnd;

import com.l2jpx.gameserver.data.SkillTable;
import com.l2jpx.gameserver.model.actor.Attackable;
import com.l2jpx.gameserver.model.actor.Creature;
import com.l2jpx.gameserver.model.actor.Npc;
import com.l2jpx.gameserver.network.NpcStringId;
import com.l2jpx.gameserver.scripting.script.ai.AttackableAIScript;
import com.l2jpx.gameserver.skills.L2Skill;

/**
 * Specific AI for Medusa. She casts a fatal poison on low life.
 */
public class Medusa extends AttackableAIScript
{
	private static final L2Skill POISON = SkillTable.getInstance().getInfo(4320, 3);
	
	public Medusa()
	{
		super("ai/individual");
	}
	
	@Override
	protected void registerNpcs()
	{
		addAttackId(20158);
	}
	
	@Override
	public String onAttack(Npc npc, Creature attacker, int damage, L2Skill skill)
	{
		final Attackable medusa = (Attackable) npc;
		
		// Chance to cast is 3%, when Medusa's HP is below 20%. Also attacker must be the most hated.
		if (Rnd.get(100) < 3 && npc.getStatus().getHpRatio() < 0.2 && medusa.getAggroList().getMostHatedCreature() == attacker)
		{
			medusa.broadcastNpcSay(NpcStringId.ID_1000452);
			medusa.getAI().tryToCast(attacker, POISON);
		}
		
		return super.onAttack(npc, attacker, damage, skill);
	}
}