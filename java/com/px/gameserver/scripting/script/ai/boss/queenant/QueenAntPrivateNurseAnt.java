package com.px.gameserver.scripting.script.ai.boss.queenant;

import com.px.gameserver.data.SkillTable;
import com.px.gameserver.enums.IntentionType;
import com.px.gameserver.model.actor.Creature;
import com.px.gameserver.model.actor.Npc;
import com.px.gameserver.model.actor.Player;
import com.px.gameserver.scripting.script.ai.individual.DefaultNpc;
import com.px.gameserver.skills.L2Skill;

public class QueenAntPrivateNurseAnt extends DefaultNpc
{
	public QueenAntPrivateNurseAnt()
	{
		super("ai/boss/queenant");
	}
	
	public QueenAntPrivateNurseAnt(String descr)
	{
		super(descr);
	}
	
	protected final int[] _npcIds =
	{
		29003 // nurse_ant
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
		{
			if (!npc.hasMaster() || npc.getMaster().isDead())
			{
				npc.deleteMe();
				cancelQuestTimers(npc);
			}
		}
		return super.onTimer(name, npc, player);
	}
	
	@Override
	public void onNoDesire(Npc npc)
	{
		if (npc.hasMaster() && !npc.getMaster().isDead())
			npc.getAI().addFollowDesire(npc.getMaster(), 20);
		else
			npc.getAI().addWanderDesire(40, 20);
	}
	
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
	public void onPartyAttacked(Npc caller, Npc called, Creature target, int damage)
	{
		if (caller.getNpcId() == 29001)
		{
			final Creature topDesireTarget = called.getAI().getTopDesireTarget();
			if (called.distance2D(caller) > 2500 && called.getAI().getCurrentIntention().getType() == IntentionType.CAST && topDesireTarget != null && topDesireTarget instanceof Npc && ((Npc) topDesireTarget).getNpcId() == 29002)
				return;
			
			final L2Skill queenAntHeal = SkillTable.getInstance().getInfo(4020, 1);
			called.getAI().addCastDesire(caller, queenAntHeal, 1000000);
		}
		else if (called.getNpcId() == 29002)
		{
			final L2Skill queenAntHeal = SkillTable.getInstance().getInfo(4020, 1);
			called.getAI().addCastDesire(caller, queenAntHeal, 100);
			
			final L2Skill queenAntHeal2 = SkillTable.getInstance().getInfo(4024, 1);
			called.getAI().addCastDesire(caller, queenAntHeal2, 100);
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