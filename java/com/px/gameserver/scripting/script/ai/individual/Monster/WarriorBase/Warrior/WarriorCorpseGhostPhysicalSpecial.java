package com.px.gameserver.scripting.script.ai.individual.Monster.WarriorBase.Warrior;

import com.px.commons.random.Rnd;

import com.px.gameserver.enums.IntentionType;
import com.px.gameserver.enums.actors.NpcSkillType;
import com.px.gameserver.model.actor.Attackable;
import com.px.gameserver.model.actor.Creature;
import com.px.gameserver.model.actor.Npc;
import com.px.gameserver.model.actor.Playable;
import com.px.gameserver.model.actor.Player;
import com.px.gameserver.skills.L2Skill;

public class WarriorCorpseGhostPhysicalSpecial extends Warrior
{
	public WarriorCorpseGhostPhysicalSpecial()
	{
		super("ai/individual/Monster/WarriorBase/Warrior");
	}
	
	public WarriorCorpseGhostPhysicalSpecial(String descr)
	{
		super(descr);
	}
	
	protected final int[] _npcIds =
	{
		21571
	};
	
	@Override
	public void onCreated(Npc npc)
	{
		startQuestTimerAtFixedRate("2001", npc, null, 10000, 10000);
		
		final L2Skill selfBuff = getNpcSkillByType(npc, NpcSkillType.SELF_BUFF);
		npc.getAI().addCastDesire(npc, selfBuff, 1000000);
		
		super.onCreated(npc);
	}
	
	@Override
	public void onSeeCreature(Npc npc, Creature creature)
	{
		if (!(creature instanceof Playable))
		{
			super.onSeeCreature(npc, creature);
			return;
		}
		
		if (npc.getAI().getLifeTime() > 7 && npc.isInMyTerritory() && npc.getAI().getCurrentIntention().getType() == IntentionType.ATTACK)
		{
			final Creature mostHated = npc.getAI().getAggroList().getMostHatedCreature();
			if (mostHated == creature && Rnd.get(100) < 33)
			{
				final L2Skill physicalSpecial = getNpcSkillByType(npc, NpcSkillType.PHYSICAL_SPECIAL);
				npc.getAI().addCastDesire(mostHated, physicalSpecial, 1000000);
			}
		}
		
		tryToAttack(npc, creature);
		
		super.onSeeCreature(npc, creature);
	}
	
	@Override
	public void onAttacked(Npc npc, Creature attacker, int damage, L2Skill skill)
	{
		if (attacker instanceof Playable)
		{
			final Creature mostHated = npc.getAI().getAggroList().getMostHatedCreature();
			if (mostHated == attacker && Rnd.get(100) < 33)
			{
				final L2Skill physicalSpecial = getNpcSkillByType(npc, NpcSkillType.PHYSICAL_SPECIAL);
				npc.getAI().addCastDesire(attacker, physicalSpecial, 1000000);
			}
		}
		super.onAttacked(npc, attacker, damage, skill);
	}
	
	@Override
	public void onClanAttacked(Attackable caller, Attackable called, Creature attacker, int damage, L2Skill skill)
	{
		if (attacker instanceof Playable && called.getAI().getLifeTime() > 7 && called.getAI().getCurrentIntention().getType() != IntentionType.ATTACK)
		{
			if (Rnd.get(100) < 33)
			{
				final L2Skill physicalSpecial = getNpcSkillByType(called, NpcSkillType.PHYSICAL_SPECIAL);
				called.getAI().addCastDesire(attacker, physicalSpecial, 1000000);
			}
		}
		super.onClanAttacked(caller, called, attacker, damage, skill);
	}
	
	@Override
	public String onTimer(String name, Npc npc, Player player)
	{
		if (name.equalsIgnoreCase("2001"))
		{
			if (npc.getAI().getCurrentIntention().getType() != IntentionType.ATTACK)
			{
				final L2Skill selfBuff = getNpcSkillByType(npc, NpcSkillType.SELF_BUFF);
				if (getAbnormalLevel(npc, selfBuff) <= 0)
					npc.getAI().addCastDesire(npc, selfBuff, 1000000);
			}
		}
		return super.onTimer(name, npc, player);
	}
}