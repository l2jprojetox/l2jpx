package com.l2jpx.gameserver.scripting.script.ai.group;

import com.l2jpx.commons.random.Rnd;

import com.l2jpx.gameserver.data.SkillTable;
import com.l2jpx.gameserver.model.actor.Creature;
import com.l2jpx.gameserver.model.actor.Npc;
import com.l2jpx.gameserver.network.NpcStringId;
import com.l2jpx.gameserver.scripting.script.ai.AttackableAIScript;
import com.l2jpx.gameserver.skills.L2Skill;

/**
 * Frenzy behavior for 5 types of orcs. They cast a buff on low life.
 */
public class FrenzyOnAttack extends AttackableAIScript
{
	private static final L2Skill ULTIMATE_BUFF = SkillTable.getInstance().getInfo(4318, 1);
	
	private static final NpcStringId[] ORCS_WORDS =
	{
		NpcStringId.ID_1000290,
		NpcStringId.ID_1000395,
		NpcStringId.ID_1000396,
		NpcStringId.ID_1000397,
	};
	
	public FrenzyOnAttack()
	{
		super("ai/group");
	}
	
	@Override
	protected void registerNpcs()
	{
		addAttackId(20270, 20495, 20588, 20778, 21116);
	}
	
	@Override
	public String onAttack(Npc npc, Creature attacker, int damage, L2Skill skill)
	{
		// Chance to cast is 10%, when orc's HP is below 30%. Also the orc must not already be under the buff.
		if (Rnd.get(100) < 10 && npc.getStatus().getHpRatio() < 0.3 && npc.getFirstEffect(ULTIMATE_BUFF) == null)
		{
			npc.broadcastNpcSay(Rnd.get(ORCS_WORDS));
			npc.getAI().tryToCast(npc, ULTIMATE_BUFF);
		}
		
		return super.onAttack(npc, attacker, damage, skill);
	}
}