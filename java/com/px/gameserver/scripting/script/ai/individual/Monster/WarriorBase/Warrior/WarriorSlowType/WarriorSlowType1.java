package com.px.gameserver.scripting.script.ai.individual.Monster.WarriorBase.Warrior.WarriorSlowType;

import com.px.commons.random.Rnd;

import com.px.gameserver.enums.IntentionType;
import com.px.gameserver.enums.actors.NpcSkillType;
import com.px.gameserver.model.actor.Creature;
import com.px.gameserver.model.actor.Npc;
import com.px.gameserver.model.actor.Playable;
import com.px.gameserver.skills.L2Skill;

public class WarriorSlowType1 extends WarriorSlowType
{
	public WarriorSlowType1()
	{
		super("ai/individual/Monster/WarriorBase/Warrior/WarriorSlowType");
	}
	
	public WarriorSlowType1(String descr)
	{
		super(descr);
	}
	
	protected final int[] _npcIds =
	{
		20101,
		20378,
		20379,
		20380,
		20526,
		21128,
		21131,
		20346,
		20566,
		20565,
		20083,
		20368,
		20333,
		21103,
		20511,
		20559,
		20016,
		20591,
		20597,
		20521
	};
	
	@Override
	public void onAttacked(Npc npc, Creature attacker, int damage, L2Skill skill)
	{
		Creature mostHated = npc.getAI().getAggroList().getMostHatedCreature();
		
		if (mostHated != null)
		{
			if (npc.getAI().getCurrentIntention().getType() == IntentionType.ATTACK && attacker != mostHated)
			{
				if (npc.distance2D(mostHated) > 100 && npc.distance2D(attacker) < 100)
				{
					if (Rnd.get(100) < 80)
					{
						npc.removeAllAttackDesire();
						if (attacker instanceof Playable)
						{
							double f0 = getHateRatio(npc, attacker);
							f0 = (((1.0 * damage) / (npc.getStatus().getLevel() + 7)) + ((f0 / 100) * ((1.0 * damage) / (npc.getStatus().getLevel() + 7))));
							npc.getAI().addAttackDesire(attacker, f0 * 30);
						}
					}
				}
			}
		}
		if (npc.distance2D(attacker) < 200 && Rnd.get(100) < 10)
		{
			L2Skill DDMagicSlow = getNpcSkillByType(npc, NpcSkillType.DD_MAGIC_SLOW);
			
			npc.getAI().addCastDesire(mostHated, DDMagicSlow, 1000000);
		}
		
		super.onAttacked(npc, attacker, damage, skill);
	}
}
