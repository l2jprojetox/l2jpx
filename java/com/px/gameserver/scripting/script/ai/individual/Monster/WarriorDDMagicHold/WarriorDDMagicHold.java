package com.px.gameserver.scripting.script.ai.individual.Monster.WarriorDDMagicHold;

import com.px.gameserver.enums.IntentionType;
import com.px.gameserver.enums.actors.NpcSkillType;
import com.px.gameserver.model.actor.Attackable;
import com.px.gameserver.model.actor.Creature;
import com.px.gameserver.model.actor.Npc;
import com.px.gameserver.model.actor.Playable;
import com.px.gameserver.model.actor.Player;
import com.px.gameserver.scripting.script.ai.individual.Monster.MonsterAI;
import com.px.gameserver.skills.L2Skill;

public class WarriorDDMagicHold extends MonsterAI
{
	protected static final int SKILL_RANGE = 500;
	
	public WarriorDDMagicHold()
	{
		super("ai/individual/Monster/WarriorDDMagicHold");
	}
	
	public WarriorDDMagicHold(String descr)
	{
		super(descr);
	}
	
	@Override
	public void onAttacked(Npc npc, Creature attacker, int damage, L2Skill skill)
	{
		if (attacker instanceof Playable)
		{
			final IntentionType currentIntentionType = npc.getAI().getCurrentIntention().getType();
			if (currentIntentionType != IntentionType.ATTACK && currentIntentionType != IntentionType.CAST)
				npc.getAI().addCastDesireHold(attacker, getNpcSkillByType(npc, NpcSkillType.DD_MAGIC), 1000000);
			else
			{
				final Creature topDesireTarget = npc.getAI().getTopDesireTarget();
				if (topDesireTarget != null && attacker != topDesireTarget && npc.distance3D(topDesireTarget) > SKILL_RANGE)
				{
					npc.getAI().getAggroList().stopHate(topDesireTarget);
					npc.getAI().addCastDesireHold(attacker, getNpcSkillByType(npc, NpcSkillType.DD_MAGIC), 1000000);
				}
			}
			
			npc.getAI().addAttackDesireHold(attacker, 100);
		}
	}
	
	@Override
	public void onClanAttacked(Attackable caller, Attackable called, Creature attacker, int damage, L2Skill skill)
	{
		if (attacker instanceof Playable && called.getAI().getLifeTime() > 7)
		{
			final IntentionType currentIntentionType = called.getAI().getCurrentIntention().getType();
			if (currentIntentionType != IntentionType.ATTACK && currentIntentionType != IntentionType.CAST)
				called.getAI().addCastDesireHold(attacker, getNpcSkillByType(called, NpcSkillType.DD_MAGIC), 1000000);
			
			called.getAI().addAttackDesireHold(attacker, 50);
		}
	}
	
	@Override
	public void onUseSkillFinished(Npc npc, Player player, L2Skill skill, boolean success)
	{
		final Creature topDesireTarget = npc.getAI().getTopDesireTarget();
		if (topDesireTarget != null)
		{
			if (SKILL_RANGE < npc.distance3D(topDesireTarget))
			{
				npc.getAI().getAggroList().stopHate(topDesireTarget);
				
				startQuestTimer("2001", npc, player, 1000);
				
				return;
			}
			
			if (topDesireTarget instanceof Player)
				npc.getAI().addCastDesireHold(topDesireTarget, getNpcSkillByType(npc, NpcSkillType.DD_MAGIC), 1000000);
		}
	}
	
	@Override
	public String onTimer(String name, Npc npc, Player player)
	{
		if (name.equalsIgnoreCase("2001"))
		{
			final Creature topDesireTarget = npc.getAI().getTopDesireTarget();
			if (topDesireTarget != null)
			{
				if (SKILL_RANGE < npc.distance3D(topDesireTarget))
				{
					npc.getAI().getAggroList().stopHate(topDesireTarget);
					
					startQuestTimer("2001", npc, player, 1000);
					
					return super.onTimer(name, npc, player);
				}
				
				if (topDesireTarget instanceof Player)
					npc.getAI().addCastDesireHold(topDesireTarget, getNpcSkillByType(npc, NpcSkillType.DD_MAGIC), 1000000);
			}
		}
		return super.onTimer(name, npc, player);
	}
}