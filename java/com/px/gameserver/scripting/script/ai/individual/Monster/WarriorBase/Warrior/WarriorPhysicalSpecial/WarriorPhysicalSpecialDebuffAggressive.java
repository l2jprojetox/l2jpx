package com.px.gameserver.scripting.script.ai.individual.Monster.WarriorBase.Warrior.WarriorPhysicalSpecial;

import com.px.commons.random.Rnd;

import com.px.gameserver.enums.IntentionType;
import com.px.gameserver.enums.actors.NpcSkillType;
import com.px.gameserver.model.actor.Attackable;
import com.px.gameserver.model.actor.Creature;
import com.px.gameserver.model.actor.Npc;
import com.px.gameserver.model.actor.Playable;
import com.px.gameserver.model.actor.Player;
import com.px.gameserver.skills.L2Skill;

public class WarriorPhysicalSpecialDebuffAggressive extends WarriorPhysicalSpecial
{
	public WarriorPhysicalSpecialDebuffAggressive()
	{
		super("ai/individual/Monster/WarriorBase/Warrior/WarriorPhysicalSpecial");
	}
	
	public WarriorPhysicalSpecialDebuffAggressive(String descr)
	{
		super(descr);
	}
	
	protected final int[] _npcIds =
	{
		21383,
		21407,
		21380,
		21413
	};
	
	@Override
	public void onCreated(Npc npc)
	{
		npc._i_ai1 = 0;
		npc._i_ai2 = 0;
		
		super.onCreated(npc);
	}
	
	@Override
	public void onSeeCreature(Npc npc, Creature creature)
	{
		if (!(creature instanceof Playable))
			return;
		
		tryToAttack(npc, creature);
		
		super.onSeeCreature(npc, creature);
	}
	
	@Override
	public void onAttacked(Npc npc, Creature attacker, int damage, L2Skill skill)
	{
		final Creature mostHated = npc.getAI().getAggroList().getMostHatedCreature();
		
		if (npc._i_ai1 == 1)
		{
			if (mostHated == attacker)
				npc._i_ai2 = 1;
		}
		else
		{
			startQuestTimer("2001", npc, null, 5000);
			
			npc._i_ai1 = 1;
		}
		
		if (attacker instanceof Playable && mostHated == attacker && Rnd.get(100) < 33)
		{
			final L2Skill debuff = getNpcSkillByType(npc, NpcSkillType.DEBUFF);
			if (getAbnormalLevel(attacker, debuff) <= 0)
				npc.getAI().addCastDesire(attacker, debuff, 1000000);
		}
		super.onAttacked(npc, attacker, damage, skill);
	}
	
	@Override
	public void onClanAttacked(Attackable caller, Attackable called, Creature attacker, int damage, L2Skill skill)
	{
		if (attacker instanceof Playable && called.getAI().getLifeTime() > 7 && Rnd.get(100) < 33)
		{
			final L2Skill debuff = getNpcSkillByType(called, NpcSkillType.DEBUFF);
			if (getAbnormalLevel(attacker, debuff) <= 0)
				called.getAI().addCastDesire(attacker, debuff, 1000000);
		}
		super.onClanAttacked(caller, called, attacker, damage, skill);
	}
	
	@Override
	public String onTimer(String name, Npc npc, Player player)
	{
		if (name.equalsIgnoreCase("2001"))
		{
			if (npc.getAI().getCurrentIntention().getType() != IntentionType.ATTACK && npc.getAI().getCurrentIntention().getType() != IntentionType.CAST)
			{
				npc._i_ai1 = 0;
				npc._i_ai2 = 0;
				
				return super.onTimer(name, npc, player);
			}
			
			if (npc._i_ai2 == 0)
			{
				final Creature mostHated = npc.getAI().getAggroList().getMostHatedCreature();
				if (mostHated != null && Rnd.get(100) < 50)
				{
					final L2Skill debuff = getNpcSkillByType(npc, NpcSkillType.DEBUFF);
					if (getAbnormalLevel(mostHated, debuff) <= 0)
						npc.getAI().addCastDesire(mostHated, debuff, 1000000);
				}
			}
			
			startQuestTimer("2001", npc, null, 5000);
			
			npc._i_ai2 = 0;
		}
		return super.onTimer(name, npc, player);
	}
}