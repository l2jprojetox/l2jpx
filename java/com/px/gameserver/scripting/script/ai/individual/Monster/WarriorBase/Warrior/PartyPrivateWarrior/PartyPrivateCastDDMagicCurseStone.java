package com.px.gameserver.scripting.script.ai.individual.Monster.WarriorBase.Warrior.PartyPrivateWarrior;

import com.px.commons.random.Rnd;

import com.px.gameserver.enums.IntentionType;
import com.px.gameserver.enums.actors.NpcSkillType;
import com.px.gameserver.model.actor.Attackable;
import com.px.gameserver.model.actor.Creature;
import com.px.gameserver.model.actor.Npc;
import com.px.gameserver.model.actor.Playable;
import com.px.gameserver.model.actor.Player;
import com.px.gameserver.skills.L2Skill;

public class PartyPrivateCastDDMagicCurseStone extends PartyPrivateWarrior
{
	public PartyPrivateCastDDMagicCurseStone()
	{
		super("ai/individual/Monster/WarriorBase/Warrior/PartyPrivateWarrior");
	}
	
	public PartyPrivateCastDDMagicCurseStone(String descr)
	{
		super(descr);
	}
	
	protected final int[] _npcIds =
	{
		21349,
		21375
	};
	
	@Override
	public void onAttacked(Npc npc, Creature attacker, int damage, L2Skill skill)
	{
		if (attacker instanceof Playable)
		{
			if (maybeCastPetrify(npc, attacker))
				return;
			
			final Creature mostHated = npc.getAI().getAggroList().getMostHatedCreature();
			if (mostHated != null)
			{
				final L2Skill debuff = getNpcSkillByType(npc, NpcSkillType.DEBUFF);
				if (Rnd.get(100) < 33 && getAbnormalLevel(attacker, debuff) <= 0 && mostHated == attacker)
					npc.getAI().addCastDesire(attacker, debuff, 1000000);
			}
		}
		
		if (Rnd.get(100) < 33)
			npc.getAI().addCastDesire(attacker, getNpcSkillByType(npc, NpcSkillType.DD_MAGIC), 1000000);
		
		super.onAttacked(npc, attacker, damage, skill);
	}
	
	@Override
	public void onClanAttacked(Attackable caller, Attackable called, Creature attacker, int damage, L2Skill skill)
	{
		if (maybeCastPetrify(called, attacker))
			return;
		
		super.onClanAttacked(caller, called, attacker, damage, skill);
	}
	
	@Override
	public void onPartyAttacked(Npc caller, Npc called, Creature target, int damage)
	{
		if (Rnd.get(100) < 33 && called.getAI().getCurrentIntention().getType() != IntentionType.ATTACK)
			called.getAI().addCastDesire(target, getNpcSkillByType(called, NpcSkillType.DD_MAGIC), 1000000);
		
		if (target instanceof Playable)
		{
			final Creature mostHated = called.getAI().getAggroList().getMostHatedCreature();
			if (mostHated != null)
			{
				final L2Skill debuff = getNpcSkillByType(called, NpcSkillType.DEBUFF);
				if (Rnd.get(100) < 33 && getAbnormalLevel(target, debuff) <= 0 && mostHated == target)
					called.getAI().addCastDesire(target, debuff, 1000000);
			}
		}
		super.onPartyAttacked(caller, called, target, damage);
	}
	
	@Override
	public void onSeeSpell(Npc npc, Player caster, L2Skill skill, Creature[] targets, boolean isPet)
	{
		if (skill.getAggroPoints() > 0 && !skill.isOffensive() && maybeCastPetrify(npc, caster))
			return;
		
		super.onSeeSpell(npc, caster, skill, targets, isPet);
	}
}