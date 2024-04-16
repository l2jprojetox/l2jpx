package com.px.gameserver.scripting.script.ai.individual.Monster.WarriorBase.Warrior.WarriorPhysicalSpecial;

import com.px.commons.random.Rnd;

import com.px.gameserver.enums.actors.NpcSkillType;
import com.px.gameserver.model.actor.Creature;
import com.px.gameserver.model.actor.Npc;
import com.px.gameserver.model.actor.Playable;
import com.px.gameserver.model.group.Party;
import com.px.gameserver.skills.L2Skill;

public class WarriorSaintTransform extends WarriorPhysicalSpecial
{
	public WarriorSaintTransform()
	{
		super("ai/individual/Monster/WarriorBase/Warrior/WarriorPhysicalSpecial");
	}
	
	public WarriorSaintTransform(String descr)
	{
		super(descr);
	}
	
	protected final int[] _npcIds =
	{
		21534,
		21528,
		21522,
		21538
	};
	
	@Override
	public void onCreated(Npc npc)
	{
		npc._i_ai0 = 0;
		super.onCreated(npc);
	}
	
	@Override
	public void onAttacked(Npc npc, Creature attacker, int damage, L2Skill skill)
	{
		Party party = attacker.getParty();
		if (party != null)
		{
			if (Rnd.get(100) < 33)
			{
				L2Skill selfRangeDDMagic = getNpcSkillByType(npc, NpcSkillType.SELF_RANGE_DD_MAGIC);
				if (npc.getCast().meetsHpMpConditions(npc, selfRangeDDMagic))
				{
					npc.getAI().addCastDesire(npc, selfRangeDDMagic, 1000000);
				}
				else
				{
					npc._i_ai0 = 1;
				}
			}
		}
		if (attacker instanceof Playable)
		{
			Creature mostHated = npc.getAI().getAggroList().getMostHatedCreature();
			Creature finalTarget = mostHated != null ? mostHated : attacker;
			L2Skill dispell = getNpcSkillByType(npc, NpcSkillType.DISPELL);
			
			if (Rnd.get(100) < 90 && ((npc.getStatus().getHp() / npc.getStatus().getMaxHp()) * 100) > 90 && npc._i_ai0 == 0)
			{
				if (npc.getCast().meetsHpMpConditions(finalTarget, dispell))
				{
					npc.getAI().addCastDesire(finalTarget, dispell, 1000000);
				}
				else
				{
					npc.getAI().addAttackDesire(finalTarget, 1000);
				}
				npc._i_ai0 = 1;
			}
			else if (Rnd.get(100) < 80 && ((npc.getStatus().getHp() / npc.getStatus().getMaxHp()) * 100) > 40 && ((npc.getStatus().getHp() / npc.getStatus().getMaxHp()) * 100) < 50 && npc._i_ai0 < 1)
			{
				if (npc.getCast().meetsHpMpConditions(finalTarget, dispell))
				{
					npc.getAI().addCastDesire(finalTarget, dispell, 1000000);
				}
				else
				{
					npc.getAI().addAttackDesire(finalTarget, 1000);
				}
				npc._i_ai0 = 2;
			}
		}
		
		super.onAttacked(npc, attacker, damage, skill);
	}
}
