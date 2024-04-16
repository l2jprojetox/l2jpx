package com.px.gameserver.scripting.script.ai.individual.Monster.WarriorBase.Warrior.WarriorCastEnchantPhysicalPoison;

import com.px.commons.random.Rnd;

import com.px.gameserver.enums.actors.NpcSkillType;
import com.px.gameserver.model.actor.Creature;
import com.px.gameserver.model.actor.Npc;
import com.px.gameserver.model.actor.Playable;
import com.px.gameserver.skills.L2Skill;

public class WarriorCastEnchantRangePhysicalPoisonAggressive extends WarriorCastEnchantPhysicalPoisonAggressive
{
	public WarriorCastEnchantRangePhysicalPoisonAggressive()
	{
		super("ai/individual/Monster/WarriorBase/Warrior/WarriorCastEnchantPhysicalPoison");
	}
	
	public WarriorCastEnchantRangePhysicalPoisonAggressive(String descr)
	{
		super(descr);
	}
	
	protected final int[] _npcIds =
	{
		21319
	};
	
	@Override
	public void onAttacked(Npc npc, Creature attacker, int damage, L2Skill skill)
	{
		if (attacker instanceof Playable)
		{
			Creature mostHated = npc.getAI().getAggroList().getMostHatedCreature();
			
			if (mostHated != null)
			{
				if (Rnd.get(100) < 33 && mostHated != attacker)
				{
					L2Skill rangePhysicalSpecial = getNpcSkillByType(npc, NpcSkillType.RANGE_PHYSICAL_SPECIAL);
					L2Skill physicalSpecial = getNpcSkillByType(npc, NpcSkillType.PHYSICAL_SPECIAL);
					
					if (npc.getCast().meetsHpMpDisabledConditions(attacker, rangePhysicalSpecial))
					{
						npc.getAI().addCastDesire(attacker, rangePhysicalSpecial, 1000000);
					}
					else if (npc.getCast().meetsHpMpDisabledConditions(attacker, physicalSpecial))
					{
						npc.getAI().addCastDesire(attacker, physicalSpecial, 1000000);
					}
					else
					{
						npc._i_ai0 = 1;
						npc.getAI().addAttackDesire(attacker, 1000);
					}
				}
			}
		}
		super.onAttacked(npc, attacker, damage, skill);
	}
}
