package com.px.gameserver.scripting.script.ai.boss.orfen;

import com.px.commons.random.Rnd;

import com.px.gameserver.data.SkillTable;
import com.px.gameserver.model.actor.Attackable;
import com.px.gameserver.model.actor.Creature;
import com.px.gameserver.model.actor.Npc;
import com.px.gameserver.model.actor.Player;
import com.px.gameserver.scripting.script.ai.individual.DefaultNpc;
import com.px.gameserver.skills.L2Skill;

public class OrfenPrivateRibaIren extends DefaultNpc
{
	public OrfenPrivateRibaIren()
	{
		super("ai/boss/orfen");
	}
	
	public OrfenPrivateRibaIren(String descr)
	{
		super(descr);
	}
	
	protected final int[] _npcIds =
	{
		29018 // riba_iren
	};
	
	@Override
	public void onNoDesire(Npc npc)
	{
		npc.getAI().addWanderDesire(5, 5);
	}
	
	@Override
	public void onAttacked(Npc npc, Creature attacker, int damage, L2Skill skill)
	{
		if (attacker.getStatus().getLevel() > (npc.getStatus().getLevel() + 8))
		{
			final L2Skill raidCurse = SkillTable.getInstance().getInfo(4515, 1);
			npc.getAI().addCastDesire(attacker, raidCurse, 1000000);
		}
		
		if (npc.getStatus().getHpRatio() < 0.5)
		{
			final L2Skill heal = SkillTable.getInstance().getInfo(4516, 1);
			npc.getAI().addCastDesire(npc, heal, 1000000);
		}
	}
	
	@Override
	public void onClanAttacked(Attackable caller, Attackable called, Creature attacker, int damage, L2Skill skill)
	{
		if (caller.getNpcId() == 29018)
			return;
		
		if (caller.getStatus().getHpRatio() < 0.5)
		{
			if (Rnd.get(100) < ((caller.getNpcId() == 29014) ? 90 : 10))
			{
				final L2Skill heal = SkillTable.getInstance().getInfo(4516, 1);
				called.getAI().addCastDesire(caller, heal, 1000000);
			}
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