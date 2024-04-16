package com.px.gameserver.scripting.script.ai.individual.Monster.WarriorBase.Warrior.WarriorCastingHeal;

import com.px.commons.random.Rnd;

import com.px.gameserver.enums.IntentionType;
import com.px.gameserver.enums.actors.NpcSkillType;
import com.px.gameserver.model.actor.Attackable;
import com.px.gameserver.model.actor.Creature;
import com.px.gameserver.model.actor.Npc;
import com.px.gameserver.scripting.script.ai.individual.Monster.WarriorBase.Warrior.Warrior;
import com.px.gameserver.skills.L2Skill;

public class WarriorCastingHeal extends Warrior
{
	public WarriorCastingHeal()
	{
		super("ai/individual/Monster/WarriorBase/Warrior/WarriorCastingHeal");
	}
	
	public WarriorCastingHeal(String descr)
	{
		super(descr);
	}
	
	protected final int[] _npcIds =
	{
		20686
	};
	
	@Override
	public void onAttacked(Npc npc, Creature attacker, int damage, L2Skill skill)
	{
		if (Rnd.get(100) < 33 && ((npc.getStatus().getHp() / npc.getStatus().getMaxHp()) * 100) < 70)
		{
			L2Skill magicHeal = getNpcSkillByType(npc, NpcSkillType.MAGIC_HEAL);
			
			npc.getAI().addCastDesire(npc, magicHeal, 1000000);
		}
		super.onAttacked(npc, attacker, damage, skill);
	}
	
	@Override
	public void onClanAttacked(Attackable caller, Attackable called, Creature attacker, int damage, L2Skill skill)
	{
		if (Rnd.get(100) < 33 && called.getAI().getCurrentIntention().getType() != IntentionType.ATTACK)
		{
			L2Skill magicHeal = getNpcSkillByType(called, NpcSkillType.MAGIC_HEAL);
			
			called.getAI().addCastDesire(caller, magicHeal, 1000000);
		}
		super.onClanAttacked(caller, called, attacker, damage, skill);
	}
}
