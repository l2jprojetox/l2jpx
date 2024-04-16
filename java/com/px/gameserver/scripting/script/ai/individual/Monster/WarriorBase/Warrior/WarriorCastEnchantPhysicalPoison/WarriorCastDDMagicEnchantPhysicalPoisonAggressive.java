package com.px.gameserver.scripting.script.ai.individual.Monster.WarriorBase.Warrior.WarriorCastEnchantPhysicalPoison;

import com.px.commons.random.Rnd;

import com.px.gameserver.enums.IntentionType;
import com.px.gameserver.enums.actors.NpcSkillType;
import com.px.gameserver.model.actor.Attackable;
import com.px.gameserver.model.actor.Creature;
import com.px.gameserver.model.actor.Npc;
import com.px.gameserver.model.actor.Playable;
import com.px.gameserver.skills.L2Skill;

public class WarriorCastDDMagicEnchantPhysicalPoisonAggressive extends WarriorCastEnchantPhysicalPoisonAggressive
{
	public WarriorCastDDMagicEnchantPhysicalPoisonAggressive()
	{
		super("ai/individual/Monster/WarriorBase/Warrior/WarriorCastEnchantPhysicalPoison");
	}
	
	public WarriorCastDDMagicEnchantPhysicalPoisonAggressive(String descr)
	{
		super(descr);
	}
	
	protected final int[] _npcIds =
	{
		21321,
		21317,
		21322,
		21314
	};
	
	@Override
	public void onAttacked(Npc npc, Creature attacker, int damage, L2Skill skill)
	{
		if (attacker instanceof Playable)
		{
			if (npc.distance2D(attacker) > 100)
			{
				Creature mostHated = npc.getAI().getAggroList().getMostHatedCreature();
				if (mostHated != null)
				{
					if (mostHated == attacker && Rnd.get(100) < 33)
					{
						L2Skill ddMagic = getNpcSkillByType(npc, NpcSkillType.DD_MAGIC);
						npc.getAI().addCastDesire(attacker, ddMagic, 1000000);
					}
				}
			}
		}
		
		super.onAttacked(npc, attacker, damage, skill);
	}
	
	@Override
	public void onClanAttacked(Attackable caller, Attackable called, Creature attacker, int damage, L2Skill skill)
	{
		if (called.getAI().getLifeTime() > 7 && attacker instanceof Playable && called.getAI().getCurrentIntention().getType() != IntentionType.ATTACK)
		{
			if (called.distance2D(attacker) > 100 && Rnd.get(100) < 33)
			{
				L2Skill ddMagic = getNpcSkillByType(called, NpcSkillType.DD_MAGIC);
				called.getAI().addCastDesire(attacker, ddMagic, 1000000);
			}
		}
		super.onClanAttacked(caller, called, attacker, damage, skill);
	}
}
