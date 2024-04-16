package com.px.gameserver.scripting.script.ai.individual.Monster.WarriorBase.Warrior.WarriorCastingEnchant;

import com.px.commons.random.Rnd;

import com.px.gameserver.enums.actors.NpcSkillType;
import com.px.gameserver.model.actor.Creature;
import com.px.gameserver.model.actor.Npc;
import com.px.gameserver.model.actor.Playable;
import com.px.gameserver.scripting.script.ai.individual.Monster.WarriorBase.Warrior.Warrior;
import com.px.gameserver.skills.L2Skill;

public class WarriorCastingEnchant extends Warrior
{
	public WarriorCastingEnchant()
	{
		super("ai/individual/Monster/WarriorBase/Warrior/WarriorCastingEnchant");
	}
	
	public WarriorCastingEnchant(String descr)
	{
		super(descr);
	}
	
	protected final int[] _npcIds =
	{
		20681,
		21138,
		21168,
		21171,
		21174,
		21189,
		21192,
		21195,
		20569,
		20552,
		20550,
		20675,
		20586
	};
	
	@Override
	public void onCreated(Npc npc)
	{
		npc._i_ai0 = 0;
		npc._i_ai1 = 0;
		
		super.onCreated(npc);
	}
	
	@Override
	public void onAttacked(Npc npc, Creature attacker, int damage, L2Skill skill)
	{
		if (attacker instanceof Playable)
		{
			if (npc._i_ai1 == 0 && Rnd.get(100) < 33 && ((npc.getStatus().getHp() / npc.getStatus().getMaxHp()) * 100) > 50)
			{
				L2Skill buff = getNpcSkillByType(npc, NpcSkillType.BUFF);
				npc.getAI().addCastDesire(npc, buff, 1000000);
				npc._i_ai1 = 1;
			}
		}
		super.onAttacked(npc, attacker, damage, skill);
	}
}
