package com.px.gameserver.scripting.script.ai.individual.Monster.WarriorBase.Warrior.WarriorCorpseGhostDDMagic;

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

public class WarriorCorpseGhostDDMagic extends Warrior
{
	public WarriorCorpseGhostDDMagic()
	{
		super("ai/individual/Monster/WarriorBase/Warrior/WarriorCorpseGhostDDMagic");
	}
	
	public WarriorCorpseGhostDDMagic(String descr)
	{
		super(descr);
	}
	
	protected final int[] _npcIds =
	{
		21556,
		21579
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
	public void onAttacked(Npc npc, Creature attacker, int damage, L2Skill skill)
	{
		if (attacker instanceof Playable)
		{
			if (npc.distance2D(attacker) > 100)
			{
				final Creature mostHated = npc.getAI().getAggroList().getMostHatedCreature();
				if (mostHated == attacker && Rnd.get(100) < 33)
				{
					final L2Skill DDMagic = getNpcSkillByType(npc, NpcSkillType.DD_MAGIC);
					npc.getAI().addCastDesire(attacker, DDMagic, 1000000);
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