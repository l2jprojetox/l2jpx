package com.px.gameserver.scripting.script.ai.individual.Monster.WarriorBase.Warrior.WarriorCastSplash;

import com.px.commons.random.Rnd;

import com.px.gameserver.enums.actors.NpcSkillType;
import com.px.gameserver.model.actor.Attackable;
import com.px.gameserver.model.actor.Creature;
import com.px.gameserver.model.actor.Npc;
import com.px.gameserver.model.actor.Playable;
import com.px.gameserver.scripting.script.ai.individual.Monster.WarriorBase.Warrior.Warrior;
import com.px.gameserver.skills.L2Skill;

public class WarriorCastSplash extends Warrior
{
	public WarriorCastSplash()
	{
		super("ai/individual/Monster/WarriorBase/Warrior/WarriorCastSplash");
	}
	
	public WarriorCastSplash(String descr)
	{
		super(descr);
	}
	
	protected final int[] _npcIds =
	{
		20449,
		20483,
		21287,
		21114,
		21107,
		21651
	};
	
	@Override
	public void onAttacked(Npc npc, Creature attacker, int damage, L2Skill skill)
	{
		
		if (attacker instanceof Playable)
		{
			Creature mostHated = npc.getAI().getAggroList().getMostHatedCreature();
			
			if (mostHated != null)
			{
				if (npc.distance2D(attacker) < 200 && Rnd.get(100) < 33 && mostHated == attacker)
				{
					L2Skill selfRangeDD = getNpcSkillByType(npc, NpcSkillType.SELF_RANGE_DD_MAGIC);
					npc.getAI().addCastDesire(npc, selfRangeDD, 1000000);
				}
			}
		}
		super.onAttacked(npc, attacker, damage, skill);
	}
	
	@Override
	public void onClanAttacked(Attackable caller, Attackable called, Creature attacker, int damage, L2Skill skill)
	{
		if (called.getAI().getLifeTime() > 7 && attacker instanceof Playable)
		{
			Creature mostHated = called.getAI().getAggroList().getMostHatedCreature();
			
			if (mostHated != null)
			{
				if (called.distance2D(attacker) < 200 && Rnd.get(100) < 33 && mostHated == attacker)
				{
					L2Skill selfRangeDD = getNpcSkillByType(called, NpcSkillType.SELF_RANGE_DD_MAGIC);
					called.getAI().addCastDesire(called, selfRangeDD, 1000000);
				}
			}
		}
		super.onClanAttacked(caller, called, attacker, damage, skill);
	}
}
