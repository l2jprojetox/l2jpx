package com.px.gameserver.scripting.script.ai.individual.Monster.WarriorBase.Warrior.WarriorCast3SkillsMagical;

import com.px.commons.random.Rnd;

import com.px.gameserver.enums.IntentionType;
import com.px.gameserver.enums.actors.NpcSkillType;
import com.px.gameserver.model.actor.Attackable;
import com.px.gameserver.model.actor.Creature;
import com.px.gameserver.model.actor.Npc;
import com.px.gameserver.model.actor.Playable;
import com.px.gameserver.scripting.script.ai.individual.Monster.WarriorBase.Warrior.Warrior;
import com.px.gameserver.skills.L2Skill;

public class WarriorCast3SkillsMagical extends Warrior
{
	public WarriorCast3SkillsMagical()
	{
		super("ai/individual/Monster/WarriorBase/Warrior/WarriorCast3SkillsMagical");
	}
	
	public WarriorCast3SkillsMagical(String descr)
	{
		super(descr);
	}
	
	protected final int[] _npcIds =
	{
		20219,
		20610,
		20617,
		21176,
		21179,
		21182,
		21185,
		20797,
		20214,
		21003,
		20658,
		20639,
		20802,
		20672
	};
	
	@Override
	public void onAttacked(Npc npc, Creature attacker, int damage, L2Skill skill)
	{
		if (attacker instanceof Playable)
		{
			Creature mostHated = npc.getAI().getAggroList().getMostHatedCreature();
			
			if (mostHated == null)
			{
			}
			else if (mostHated != attacker)
			{
				if (npc._i_ai0 == 0)
				{
					npc._i_ai0 = 1;
				}
				else if (npc._i_ai0 == 1 && Rnd.get(100) < 30 && npc.getStatus().getHp() > (npc.getStatus().getMaxHp() / 10.0))
				{
					L2Skill sleepMagic = getNpcSkillByType(npc, NpcSkillType.SLEEP_MAGIC);
					npc.getAI().addCastDesire(attacker, sleepMagic, 1000000);
				}
			}
			else if (npc.distance2D(attacker) > 100)
			{
				if (mostHated == attacker && Rnd.get(100) < 33)
				{
					L2Skill DDMagic = getNpcSkillByType(npc, NpcSkillType.DD_MAGIC);
					
					npc.getAI().addCastDesire(attacker, DDMagic, 1000000);
				}
			}
			else if (Rnd.get(200) < 1 && npc._i_ai1 == 0 && npc.getStatus().getHp() > (npc.getStatus().getMaxHp() * 0.6))
			{
				L2Skill checkMagic = getNpcSkillByType(npc, NpcSkillType.CHECK_MAGIC);
				L2Skill checkMagic1 = getNpcSkillByType(npc, NpcSkillType.CHECK_MAGIC1);
				L2Skill checkMagic2 = getNpcSkillByType(npc, NpcSkillType.CHECK_MAGIC2);
				L2Skill cancelMagic = getNpcSkillByType(npc, NpcSkillType.CANCEL_MAGIC);
				L2Skill sleepMagic = getNpcSkillByType(npc, NpcSkillType.SLEEP_MAGIC);
				
				if ((sleepMagic == null || getAbnormalLevel(attacker, sleepMagic) <= 0) && (checkMagic == null || getAbnormalLevel(attacker, checkMagic) <= 0) && ((checkMagic1 == null || getAbnormalLevel(attacker, checkMagic1) <= 0) && (checkMagic2 == null || getAbnormalLevel(attacker, checkMagic2) <= 0)))
				{
					npc.getAI().addCastDesire(attacker, cancelMagic, 1000000);
					npc._i_ai1 = 1;
				}
			}
		}
		super.onAttacked(npc, attacker, damage, skill);
	}
	
	@Override
	public void onClanAttacked(Attackable caller, Attackable called, Creature attacker, int damage, L2Skill skill)
	{
		if ((called.getAI().getLifeTime() > 7 && attacker instanceof Playable) && called.getAI().getCurrentIntention().getType() != IntentionType.ATTACK)
		{
			if (called.distance2D(attacker) > 100 && Rnd.get(100) < 33)
			{
				L2Skill DDMagic = getNpcSkillByType(called, NpcSkillType.DD_MAGIC);
				
				called.getAI().addCastDesire(attacker, DDMagic, 1000000);
			}
		}
		super.onClanAttacked(caller, called, attacker, damage, skill);
	}
}
