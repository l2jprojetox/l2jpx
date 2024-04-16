package com.px.gameserver.scripting.script.ai.individual.Monster.WarriorBase.Warrior.PartyPrivateWarrior;

import com.px.commons.random.Rnd;

import com.px.gameserver.enums.IntentionType;
import com.px.gameserver.enums.actors.NpcSkillType;
import com.px.gameserver.model.actor.Attackable;
import com.px.gameserver.model.actor.Creature;
import com.px.gameserver.model.actor.Npc;
import com.px.gameserver.model.actor.Playable;
import com.px.gameserver.skills.L2Skill;

public class PartyPrivateCast3SkillsMagical extends PartyPrivateWarrior
{
	public PartyPrivateCast3SkillsMagical()
	{
		super("ai/individual/Monster/WarriorBase/Warrior/PartyPrivateWarrior");
	}
	
	public PartyPrivateCast3SkillsMagical(String descr)
	{
		super(descr);
	}
	
	protected final int[] _npcIds =
	{
		22131
	};
	
	@Override
	public void onAttacked(Npc npc, Creature attacker, int damage, L2Skill skill)
	{
		if (attacker instanceof Playable)
		{
			if (npc.distance2D(attacker) > 100)
			{
				Creature mostHated = npc.getAI().getAggroList().getMostHatedCreature();
				if (mostHated != null)
				{
					if (mostHated == attacker)
					{
						L2Skill ddMagic1 = getNpcSkillByType(npc, NpcSkillType.DD_MAGIC1);
						L2Skill ddMagic2 = getNpcSkillByType(npc, NpcSkillType.DD_MAGIC2);
						L2Skill debuff = getNpcSkillByType(npc, NpcSkillType.DEBUFF);
						
						if (Rnd.get(100) < 33)
						{
							npc.getAI().addCastDesire(attacker, ddMagic1, 1000000);
						}
						if (Rnd.get(100) < 33)
						{
							npc.getAI().addCastDesire(attacker, ddMagic2, 1000000);
						}
						if (Rnd.get(100) < 33 && getAbnormalLevel(attacker, debuff) <= 0)
						{
							npc.getAI().addCastDesire(attacker, debuff, 1000000);
						}
					}
				}
			}
		}
		super.onAttacked(npc, attacker, damage, skill);
	}
	
	@Override
	public void onClanAttacked(Attackable caller, Attackable called, Creature attacker, int damage, L2Skill skill)
	{
		if (called.getAI().getLifeTime() > 7 && attacker instanceof Playable && called.getAI().getCurrentIntention().getType() != IntentionType.ATTACK)
		{
			L2Skill ddMagic1 = getNpcSkillByType(called, NpcSkillType.DD_MAGIC1);
			L2Skill ddMagic2 = getNpcSkillByType(called, NpcSkillType.DD_MAGIC2);
			L2Skill debuff = getNpcSkillByType(called, NpcSkillType.DEBUFF);
			
			if (called.distance2D(attacker) > 100)
			{
				if (Rnd.get(100) < 33)
				{
					called.getAI().addCastDesire(attacker, ddMagic1, 1000000);
				}
				if (Rnd.get(100) < 33)
				{
					called.getAI().addCastDesire(attacker, ddMagic2, 1000000);
				}
			}
			if (Rnd.get(100) < 33 && getAbnormalLevel(attacker, debuff) <= 0)
			{
				called.getAI().addCastDesire(attacker, debuff, 1000000);
			}
		}
		super.onClanAttacked(caller, called, attacker, damage, skill);
	}
}
