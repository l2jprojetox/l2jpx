package com.px.gameserver.scripting.script.ai.individual.Monster.WarriorBase.Warrior.WarriorCast3SkillsMagical4;

import com.px.commons.random.Rnd;

import com.px.gameserver.enums.IntentionType;
import com.px.gameserver.enums.actors.NpcSkillType;
import com.px.gameserver.model.actor.Attackable;
import com.px.gameserver.model.actor.Creature;
import com.px.gameserver.model.actor.Npc;
import com.px.gameserver.model.actor.Playable;
import com.px.gameserver.scripting.script.ai.individual.Monster.WarriorBase.Warrior.Warrior;
import com.px.gameserver.skills.L2Skill;

public class WarriorCast3SkillsMagical4 extends Warrior
{
	public WarriorCast3SkillsMagical4()
	{
		super("ai/individual/Monster/WarriorBase/Warrior/WarriorCast3SkillsMagical4");
	}
	
	public WarriorCast3SkillsMagical4(String descr)
	{
		super(descr);
	}
	
	protected final int[] _npcIds =
	{
		21197,
		21200,
		21203
	};
	
	@Override
	public void onCreated(Npc npc)
	{
		npc._i_ai0 = 0;
		npc._i_ai1 = 0;
		npc._i_ai2 = 0;
		
		super.onCreated(npc);
	}
	
	@Override
	public void onAttacked(Npc npc, Creature attacker, int damage, L2Skill skill)
	{
		int i0 = (int) (((npc.getStatus().getHp() / npc.getStatus().getMaxHp()) * 100));
		if (Rnd.get(100) < 33 && i0 < 70)
		{
			L2Skill magicHeal = getNpcSkillByType(npc, NpcSkillType.MAGIC_HEAL);
			
			npc.getAI().addCastDesire(npc, magicHeal, 1000000);
		}
		if (attacker instanceof Playable)
		{
			if (npc._i_ai1 == 0 && Rnd.get(100) < 33 && i0 > 50)
			{
				L2Skill buff = getNpcSkillByType(npc, NpcSkillType.BUFF);
				
				npc.getAI().addCastDesire(npc, buff, 1000000);
			}
			else if (Rnd.get(200) < 1 && npc._i_ai2 == 0 && i0 > 60)
			{
				Creature mostHated = npc.getAI().getAggroList().getMostHatedCreature();
				
				if (mostHated != null)
				{
					L2Skill checkMagic = getNpcSkillByType(npc, NpcSkillType.CHECK_MAGIC);
					L2Skill checkMagic1 = getNpcSkillByType(npc, NpcSkillType.CHECK_MAGIC1);
					L2Skill checkMagic2 = getNpcSkillByType(npc, NpcSkillType.CHECK_MAGIC2);
					L2Skill cancelMagic = getNpcSkillByType(npc, NpcSkillType.CANCEL_MAGIC);
					
					if ((checkMagic == null || getAbnormalLevel(attacker, checkMagic) <= 0) && ((checkMagic1 == null || getAbnormalLevel(attacker, checkMagic1) <= 0) && (checkMagic2 == null || getAbnormalLevel(attacker, checkMagic2) <= 0)))
					{
						npc.getAI().addCastDesire(attacker, cancelMagic, 1000000);
						npc._i_ai2 = 1;
					}
				}
			}
			npc._i_ai1 = 1;
		}
		super.onAttacked(npc, attacker, damage, skill);
	}
	
	@Override
	public void onClanAttacked(Attackable caller, Attackable called, Creature attacker, int damage, L2Skill skill)
	{
		if (Rnd.get(100) < 33 && called.getAI().getCurrentIntention().getType() != IntentionType.ATTACK)
		{
			L2Skill magicHeal = getNpcSkillByType(called, NpcSkillType.MAGIC_HEAL);
			
			called.getAI().addCastDesire(caller, magicHeal, 1000000);
			
		}
		super.onClanAttacked(caller, called, attacker, damage, skill);
	}
}
