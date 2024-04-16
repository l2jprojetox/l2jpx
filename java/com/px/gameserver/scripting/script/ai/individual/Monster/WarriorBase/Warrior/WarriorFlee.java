package com.px.gameserver.scripting.script.ai.individual.Monster.WarriorBase.Warrior;

import com.px.Config;
import com.px.gameserver.model.actor.Creature;
import com.px.gameserver.model.actor.Npc;
import com.px.gameserver.skills.L2Skill;

public class WarriorFlee extends Warrior
{
	public WarriorFlee()
	{
		super("ai/individual/Monster/WarriorBase/Warrior");
	}
	
	public WarriorFlee(String descr)
	{
		super(descr);
	}
	
	protected final int[] _npcIds =
	{
		20002,
		20432
	};
	
	@Override
	public void onNoDesire(Npc npc)
	{
		npc.getAI().addWanderDesire(5, 20);
	}
	
	@Override
	public void onAttacked(Npc npc, Creature attacker, int damage, L2Skill skill)
	{
		npc.getAI().addFleeDesire(attacker, Config.MAX_DRIFT_RANGE, 30);
	}
}
