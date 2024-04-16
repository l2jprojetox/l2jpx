package com.px.gameserver.scripting.script.ai.individual.Monster.WarriorBase.Warrior.WarriorSlowType;

import com.px.commons.random.Rnd;

import com.px.gameserver.enums.IntentionType;
import com.px.gameserver.enums.actors.NpcSkillType;
import com.px.gameserver.model.actor.Attackable;
import com.px.gameserver.model.actor.Creature;
import com.px.gameserver.model.actor.Npc;
import com.px.gameserver.model.actor.Playable;
import com.px.gameserver.model.actor.Player;
import com.px.gameserver.scripting.script.ai.individual.Monster.WarriorBase.Warrior.Warrior;
import com.px.gameserver.skills.L2Skill;

public class WarriorSlowType extends Warrior
{
	public WarriorSlowType()
	{
		super("ai/individual/Monster/WarriorBase/Warrior/WarriorSlowType");
	}
	
	public WarriorSlowType(String descr)
	{
		super(descr);
	}
	
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
		if (npc._i_ai0 == 1)
		{
			final Creature mostHated = npc.getAI().getAggroList().getMostHatedCreature();
			if (mostHated != null && mostHated == attacker)
				npc._i_ai1 = 1;
		}
		else
		{
			startQuestTimer("2001", npc, null, 5000);
			
			npc._i_ai0 = 1;
		}
		super.onAttacked(npc, attacker, damage, skill);
	}
	
	@Override
	public String onTimer(String name, Npc npc, Player player)
	{
		if (name.equalsIgnoreCase("2001"))
		{
			if (npc.getAI().getCurrentIntention().getType() != IntentionType.ATTACK && npc.getAI().getCurrentIntention().getType() != IntentionType.CAST)
			{
				npc._i_ai0 = 0;
				npc._i_ai1 = 0;
				
				return super.onTimer(name, npc, player);
			}
			
			if (npc._i_ai1 == 0)
			{
				final Creature mostHated = npc.getAI().getAggroList().getMostHatedCreature();
				if (mostHated != null && Rnd.get(100) < 50)
				{
					final L2Skill DDMagicSlow = getNpcSkillByType(npc, NpcSkillType.DD_MAGIC_SLOW);
					npc.getAI().addCastDesire(mostHated, DDMagicSlow, 1000000);
				}
			}
			
			startQuestTimer("2001", npc, null, 5000);
			
			npc._i_ai1 = 0;
		}
		
		return super.onTimer(name, npc, player);
	}
	
	@Override
	public void onClanAttacked(Attackable caller, Attackable called, Creature attacker, int damage, L2Skill skill)
	{
		if (attacker instanceof Playable && called.getAI().getLifeTime() > 7 && called.getAI().getCurrentIntention().getType() != IntentionType.ATTACK)
		{
			if (Rnd.get(100) < 10)
			{
				final L2Skill DDMagicSlow = getNpcSkillByType(called, NpcSkillType.DD_MAGIC_SLOW);
				called.getAI().addCastDesire(attacker, DDMagicSlow, 1000000);
			}
		}
		super.onClanAttacked(caller, called, attacker, damage, skill);
	}
}