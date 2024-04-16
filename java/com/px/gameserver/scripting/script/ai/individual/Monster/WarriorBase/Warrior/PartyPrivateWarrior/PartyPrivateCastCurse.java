package com.px.gameserver.scripting.script.ai.individual.Monster.WarriorBase.Warrior.PartyPrivateWarrior;

import com.px.commons.random.Rnd;

import com.px.gameserver.enums.actors.NpcSkillType;
import com.px.gameserver.model.actor.Attackable;
import com.px.gameserver.model.actor.Creature;
import com.px.gameserver.model.actor.Npc;
import com.px.gameserver.model.actor.Playable;
import com.px.gameserver.skills.L2Skill;

public class PartyPrivateCastCurse extends PartyPrivateWarrior
{
	public PartyPrivateCastCurse()
	{
		super("ai/individual/Monster/WarriorBase/Warrior/PartyPrivateWarrior");
	}
	
	public PartyPrivateCastCurse(String descr)
	{
		super(descr);
	}
	
	protected final int[] _npcIds =
	{
		20982
	};
	
	@Override
	public void onAttacked(Npc npc, Creature attacker, int damage, L2Skill skill)
	{
		if (attacker instanceof Playable)
		{
			Creature mostHated = npc.getAI().getAggroList().getMostHatedCreature();
			
			if (mostHated != null)
			{
				L2Skill debuff = getNpcSkillByType(npc, NpcSkillType.DEBUFF);
				
				if (Rnd.get(100) < 33 && getAbnormalLevel(attacker, debuff) <= 0 && mostHated == attacker)
				{
					npc.getAI().addCastDesire(attacker, debuff, 1000000);
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
			L2Skill debuff = getNpcSkillByType(called, NpcSkillType.DEBUFF);
			
			if (Rnd.get(100) < 33 && getAbnormalLevel(attacker, debuff) <= 0)
			{
				called.getAI().addCastDesire(attacker, debuff, 1000000);
			}
		}
		super.onClanAttacked(caller, called, attacker, damage, skill);
	}
}
