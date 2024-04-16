package com.px.gameserver.scripting.script.ai.individual.Monster.WarriorBase.Warrior.WarriorCastSummonPC;

import com.px.commons.random.Rnd;

import com.px.gameserver.enums.actors.NpcSkillType;
import com.px.gameserver.model.actor.Creature;
import com.px.gameserver.model.actor.Npc;
import com.px.gameserver.model.actor.Playable;
import com.px.gameserver.model.actor.Player;
import com.px.gameserver.scripting.script.ai.individual.Monster.WarriorBase.Warrior.Warrior;
import com.px.gameserver.skills.L2Skill;

public class WarriorCastSummonPC extends Warrior
{
	public WarriorCastSummonPC()
	{
		super("ai/individual/Monster/WarriorBase/Warrior/WarriorCastSummonPC");
	}
	
	public WarriorCastSummonPC(String descr)
	{
		super(descr);
	}
	
	protected final int[] _npcIds =
	{
		20213
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
			int i6 = Rnd.get(100);
			
			npc._c_ai0 = attacker;
			
			if (npc._i_ai0 == 0)
			{
				final double dist = npc.distance2D(attacker);
				if (dist > 300)
				{
					if (i6 < 50)
					{
						final L2Skill summonMagic = getNpcSkillByType(npc, NpcSkillType.SUMMON_MAGIC);
						if (npc.getCast().meetsHpMpDisabledConditions(attacker, summonMagic))
						{
							npc.getAI().addCastDesire(attacker, summonMagic, 1000000);
							
							npc._i_ai0 = 1;
							npc._i_ai1 = 1;
						}
					}
				}
				else if (dist > 100)
				{
					final Creature mostHated = npc.getAI().getAggroList().getMostHatedCreature();
					if (mostHated != null)
					{
						if ((mostHated == attacker && i6 < 50) || i6 < 10)
						{
							final L2Skill summonMagic = getNpcSkillByType(npc, NpcSkillType.SUMMON_MAGIC);
							if (npc.getCast().meetsHpMpDisabledConditions(attacker, summonMagic))
							{
								npc.getAI().addCastDesire(attacker, summonMagic, 1000000);
								
								npc._i_ai0 = 1;
								npc._i_ai1 = 1;
							}
						}
					}
				}
			}
		}
		
		super.onAttacked(npc, attacker, damage, skill);
	}
	
	@Override
	public void onUseSkillFinished(Npc npc, Player player, L2Skill skill, boolean success)
	{
		if (npc._c_ai0 != null && success && npc._i_ai1 == 1)
		{
			npc.abortAll(false);
			npc.instantTeleportTo(npc._c_ai0.getPosition(), 0);
			
			npc._i_ai1 = 0;
		}
		
		final Creature mostHated = npc.getAI().getAggroList().getMostHatedCreature();
		if (mostHated != null)
		{
			if (mostHated == npc._c_ai0 && Rnd.get(100) < 33)
			{
				final L2Skill physicalSpecial = getNpcSkillByType(npc, NpcSkillType.PHYSICAL_SPECIAL);
				if (physicalSpecial != null)
					npc.getAI().addCastDesire(npc._c_ai0, physicalSpecial, 1000000);
			}
		}
	}
}