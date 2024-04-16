package com.px.gameserver.scripting.script.ai.individual.Monster.WarriorBase.Warrior.WarriorCorpseZombie;

import com.px.commons.random.Rnd;

import com.px.gameserver.enums.IntentionType;
import com.px.gameserver.enums.actors.NpcSkillType;
import com.px.gameserver.model.actor.Attackable;
import com.px.gameserver.model.actor.Creature;
import com.px.gameserver.model.actor.Npc;
import com.px.gameserver.model.actor.Playable;
import com.px.gameserver.skills.L2Skill;

public class WarriorCorpseZombieDDMagic extends WarriorCorpseZombie
{
	public WarriorCorpseZombieDDMagic()
	{
		super("ai/individual/Monster/WarriorBase/Warrior/WarriorCorpseZombie");
	}
	
	public WarriorCorpseZombieDDMagic(String descr)
	{
		super(descr);
	}
	
	protected final int[] _npcIds =
	{
		21553,
		21597,
		21600
	};
	
	@Override
	public void onAttacked(Npc npc, Creature attacker, int damage, L2Skill skill)
	{
		if (attacker instanceof Playable)
		{
			if (npc.distance2D(attacker) > 100)
			{
				final Creature topDesireTarget = npc.getAI().getTopDesireTarget();
				if (topDesireTarget != null && topDesireTarget == attacker)
				{
					if (Rnd.get(100) < 33)
					{
						final L2Skill DDMagic = getNpcSkillByType(npc, NpcSkillType.DD_MAGIC);
						npc.getAI().addCastDesire(attacker, DDMagic, 1000000);
					}
				}
			}
		}
		super.onAttacked(npc, attacker, damage, skill);
	}
	
	@Override
	public void onClanAttacked(Attackable caller, Attackable called, Creature attacker, int damage, L2Skill skill)
	{
		if (attacker instanceof Playable && called.getAI().getLifeTime() > 7 && called.getAI().getCurrentIntention().getType() != IntentionType.ATTACK)
		{
			if (called.distance2D(attacker) > 100 && Rnd.get(100) < 33)
			{
				final L2Skill DDMagic = getNpcSkillByType(called, NpcSkillType.DD_MAGIC);
				called.getAI().addCastDesire(attacker, DDMagic, 1000000);
			}
		}
		super.onClanAttacked(caller, called, attacker, damage, skill);
	}
}