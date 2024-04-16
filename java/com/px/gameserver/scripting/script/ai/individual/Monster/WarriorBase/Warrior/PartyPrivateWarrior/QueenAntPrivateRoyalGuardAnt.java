package com.px.gameserver.scripting.script.ai.individual.Monster.WarriorBase.Warrior.PartyPrivateWarrior;

import com.px.gameserver.data.SkillTable;
import com.px.gameserver.model.actor.Creature;
import com.px.gameserver.model.actor.Npc;
import com.px.gameserver.model.actor.Player;
import com.px.gameserver.skills.L2Skill;

public class QueenAntPrivateRoyalGuardAnt extends PartyPrivateWarrior
{
	public QueenAntPrivateRoyalGuardAnt()
	{
		super("ai/individual/Monster/WarriorBase/Warrior/PartyPrivateWarrior");
	}
	
	public QueenAntPrivateRoyalGuardAnt(String descr)
	{
		super(descr);
	}
	
	protected final int[] _npcIds =
	{
		29005, // royal_guard_ant
	};
	
	@Override
	public void onAttacked(Npc npc, Creature attacker, int damage, L2Skill skill)
	{
		if (attacker.getStatus().getLevel() > (npc.getStatus().getLevel() + 8))
		{
			final L2Skill raidCurse = SkillTable.getInstance().getInfo(4515, 1);
			npc.getAI().addCastDesire(attacker, raidCurse, 1000000);
			npc.getAI().getAggroList().stopHate(attacker);
		}
	}
	
	@Override
	public void onSeeSpell(Npc npc, Player caster, L2Skill skill, Creature[] targets, boolean isPet)
	{
		if (caster.getStatus().getLevel() > (npc.getStatus().getLevel() + 8))
		{
			final L2Skill raidMute = SkillTable.getInstance().getInfo(4215, 1);
			
			npc.getAI().addCastDesire(caster, raidMute, 1000000);
			
			return;
		}
		super.onSeeSpell(npc, caster, skill, targets, isPet);
	}
}
