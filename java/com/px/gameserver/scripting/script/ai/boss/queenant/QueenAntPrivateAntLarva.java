package com.px.gameserver.scripting.script.ai.boss.queenant;

import com.px.gameserver.data.SkillTable;
import com.px.gameserver.model.actor.Creature;
import com.px.gameserver.model.actor.Npc;
import com.px.gameserver.model.actor.Player;
import com.px.gameserver.scripting.script.ai.individual.DefaultNpc;
import com.px.gameserver.skills.L2Skill;

public class QueenAntPrivateAntLarva extends DefaultNpc
{
	public QueenAntPrivateAntLarva()
	{
		super("ai/boss/queenant");
	}
	
	public QueenAntPrivateAntLarva(String descr)
	{
		super(descr);
	}
	
	protected final int[] _npcIds =
	{
		29002 // queen_ant_larva
	};
	
	@Override
	public void onCreated(Npc npc)
	{
		startQuestTimerAtFixedRate("2001", npc, null, 5000, 5000);
		
		super.onCreated(npc);
	}
	
	@Override
	public String onTimer(String name, Npc npc, Player player)
	{
		if (name.equalsIgnoreCase("2001"))
			if (!npc.hasMaster() || npc.getMaster().isDead())
			{
				npc.deleteMe();
				cancelQuestTimers(npc);
			}
		
		return super.onTimer(name, npc, player);
	}
	
	@Override
	public void onAttacked(Npc npc, Creature attacker, int damage, L2Skill skill)
	{
		if (attacker.getStatus().getLevel() > (npc.getStatus().getLevel() + 8))
		{
			final L2Skill raidCurse = SkillTable.getInstance().getInfo(4515, 1);
			
			npc.getAI().addCastDesire(attacker, raidCurse, 1000000);
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
