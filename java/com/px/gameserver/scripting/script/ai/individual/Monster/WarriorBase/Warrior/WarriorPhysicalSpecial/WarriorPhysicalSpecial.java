package com.px.gameserver.scripting.script.ai.individual.Monster.WarriorBase.Warrior.WarriorPhysicalSpecial;

import com.px.commons.random.Rnd;

import com.px.gameserver.enums.IntentionType;
import com.px.gameserver.enums.actors.NpcSkillType;
import com.px.gameserver.model.actor.Attackable;
import com.px.gameserver.model.actor.Creature;
import com.px.gameserver.model.actor.Npc;
import com.px.gameserver.model.actor.Playable;
import com.px.gameserver.scripting.script.ai.individual.Monster.WarriorBase.Warrior.Warrior;
import com.px.gameserver.skills.L2Skill;

public class WarriorPhysicalSpecial extends Warrior
{
	public WarriorPhysicalSpecial()
	{
		super("ai/individual/Monster/WarriorBase/Warrior/WarriorPhysicalSpecial");
	}
	
	public WarriorPhysicalSpecial(String descr)
	{
		super(descr);
	}
	
	protected final int[] _npcIds =
	{
		18337,
		20218,
		20606,
		20607,
		20613,
		20932,
		21016,
		21135,
		21166,
		21169,
		21172,
		21175,
		21178,
		21181,
		21184,
		21187,
		21190,
		21193,
		21196,
		21199,
		21202,
		21205,
		21278,
		21279,
		21280,
		21288,
		21659,
		21682,
		21705,
		21728,
		21751,
		21774,
		21431,
		20199,
		20084,
		21286,
		21397,
		20246,
		20238,
		21104,
		20242,
		20567,
		20667,
		21648,
		21411,
		21798,
		21112,
		21109,
		20641,
		20662,
		20661,
		20241,
		21415,
		20600,
		21274,
		21275,
		21276,
		20604,
		20654,
		20563,
		20230,
		20836,
		20788,
		20999,
		20240,
		20666,
		20665,
		20572,
		21294,
		20684
	};
	
	@Override
	public void onAttacked(Npc npc, Creature attacker, int damage, L2Skill skill)
	{
		if (attacker instanceof Playable)
		{
			Creature mostHated = npc.getAI().getAggroList().getMostHatedCreature();
			
			if (mostHated != null)
			{
				if (Rnd.get(100) < 33 && mostHated == attacker)
				{
					L2Skill physicalSpecial = getNpcSkillByType(npc, NpcSkillType.PHYSICAL_SPECIAL);
					
					npc.getAI().addCastDesire(attacker, physicalSpecial, 1000000);
				}
			}
		}
		super.onAttacked(npc, attacker, damage, skill);
	}
	
	@Override
	public void onClanAttacked(Attackable caller, Attackable called, Creature attacker, int damage, L2Skill skill)
	{
		if (called.getAI().getLifeTime() > 7 && (attacker instanceof Playable) && called.getAI().getCurrentIntention().getType() != IntentionType.ATTACK)
		{
			if (Rnd.get(100) < 33)
			{
				L2Skill physicalSpecial = getNpcSkillByType(called, NpcSkillType.PHYSICAL_SPECIAL);
				
				called.getAI().addCastDesire(attacker, physicalSpecial, 1000000);
			}
		}
		super.onClanAttacked(caller, called, attacker, damage, skill);
	}
}
