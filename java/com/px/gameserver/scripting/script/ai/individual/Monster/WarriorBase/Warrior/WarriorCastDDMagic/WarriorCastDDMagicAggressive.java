package com.px.gameserver.scripting.script.ai.individual.Monster.WarriorBase.Warrior.WarriorCastDDMagic;

import com.px.commons.random.Rnd;

import com.px.gameserver.enums.IntentionType;
import com.px.gameserver.enums.actors.NpcSkillType;
import com.px.gameserver.model.actor.Creature;
import com.px.gameserver.model.actor.Npc;
import com.px.gameserver.model.actor.Playable;
import com.px.gameserver.skills.L2Skill;

public class WarriorCastDDMagicAggressive extends WarriorCastDDMagic
{
	public WarriorCastDDMagicAggressive()
	{
		super("ai/individual/Monster/WarriorBase/Warrior/WarriorCastDDMagic");
	}
	
	public WarriorCastDDMagicAggressive(String descr)
	{
		super(descr);
	}
	
	protected final int[] _npcIds =
	{
		21421,
		20635,
		20670,
		21569,
		20664,
		20137,
		22011,
		22012,
		22013,
		22014,
		22015,
		22016,
		20668,
		21239,
		21243,
		22006,
		20146,
		22077,
		22065,
		22062,
		22058,
		21417,
		20656,
		20596,
		20680,
		20227,
		21223,
		21231,
		21227,
		20235,
		20279,
		22105,
		22116,
		22107,
		22117,
		22115,
		22106,
		21247,
		21255,
		21251,
		20110,
		20113,
		20115,
		21215,
		21219,
		21211,
		20935,
		20605,
		20176,
		20647,
		20194,
		20347,
		20354,
		20431,
		20453,
		20513,
		20546,
		21000,
		21143,
		21146,
		21155,
		21162,
		21235,
		22050,
		22052,
		22055,
		22056,
		22059,
		22063,
		22064,
		22076
	};
	
	@Override
	public void onSeeCreature(Npc npc, Creature creature)
	{
		if (!(creature instanceof Playable))
			return;
		
		if (npc.getAI().getLifeTime() > 7 && npc.getAI().getCurrentIntention().getType() != IntentionType.ATTACK && npc.isInMyTerritory())
		{
			if (Rnd.get(100) < 33 && npc.distance2D(creature) > 100)
			{
				final L2Skill DDMagic = getNpcSkillByType(npc, NpcSkillType.DD_MAGIC);
				npc.getAI().addCastDesire(creature, DDMagic, 1000000);
			}
		}
		
		tryToAttack(npc, creature);
		
		super.onSeeCreature(npc, creature);
	}
}