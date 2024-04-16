package com.px.gameserver.scripting.script.ai.individual.Monster.WarriorBase.Warrior.WarriorPhysicalSpecial;

import com.px.commons.random.Rnd;

import com.px.gameserver.enums.IntentionType;
import com.px.gameserver.enums.actors.NpcSkillType;
import com.px.gameserver.model.actor.Creature;
import com.px.gameserver.model.actor.Npc;
import com.px.gameserver.model.actor.Playable;
import com.px.gameserver.model.actor.Player;
import com.px.gameserver.skills.L2Skill;

public class WarriorCorpseVampire extends WarriorPhysicalSpecial
{
	public WarriorCorpseVampire()
	{
		super("ai/individual/Monster/WarriorBase/Warrior/WarriorPhysicalSpecial");
	}
	
	public WarriorCorpseVampire(String descr)
	{
		super(descr);
	}
	
	protected final int[] _npcIds =
	{
		21424,
		21593
	};
	
	@Override
	public void onCreated(Npc npc)
	{
		npc._i_ai0 = 0;
		
		final L2Skill selfBuff = getNpcSkillByType(npc, NpcSkillType.SELF_BUFF);
		npc.getAI().addCastDesire(npc, selfBuff, 1000000);
		
		startQuestTimerAtFixedRate("2001", npc, null, 10000, 10000);
		
		super.onCreated(npc);
	}
	
	@Override
	public String onTimer(String name, Npc npc, Player player)
	{
		if (name.equalsIgnoreCase("2001"))
		{
			if (npc.getAI().getCurrentIntention().getType() != IntentionType.ATTACK)
			{
				final L2Skill selfBuff = getNpcSkillByType(npc, NpcSkillType.SELF_BUFF);
				npc.getAI().addCastDesire(npc, selfBuff, 1000000);
			}
		}
		return super.onTimer(name, npc, player);
	}
	
	@Override
	public void onAttacked(Npc npc, Creature attacker, int damage, L2Skill skill)
	{
		if (attacker instanceof Playable)
		{
			final Creature mostHated = npc.getAI().getAggroList().getMostHatedCreature();
			if (mostHated == attacker)
			{
				final L2Skill debuff = getNpcSkillByType(npc, NpcSkillType.DEBUFF);
				if (Rnd.get(100) < 33 && getAbnormalLevel(attacker, debuff) <= 0)
					npc.getAI().addCastDesire(attacker, debuff, 1000000);
				
				final L2Skill DDMagic1 = getNpcSkillByType(npc, NpcSkillType.DD_MAGIC1);
				if (Rnd.get(100) < 33 && npc.getStatus().getHpRatio() < 0.5 && npc._i_ai0 == 0)
					npc.getAI().addCastDesire(attacker, DDMagic1, 1000000);
			}
		}
		super.onAttacked(npc, attacker, damage, skill);
	}
}